import os
import json
import random
from typing import Callable

from flask import Flask, jsonify, request, send_from_directory

from config import DATA_DIR
from utils.generator import build_rag_prompt, build_flashcards_prompt
from utils.parser import parse_json_flashcards
from utils.indexer import build_index
from utils.llm_groq import call_groq
from utils.llm_nim import call_nim
from utils.retriever import retrieve_top_k

app = Flask(__name__)


def _resolve_data_dir(explicit_data_dir: str) -> str:
    chosen = (explicit_data_dir or DATA_DIR or "").strip()
    if not chosen:
        return ""
    return os.path.abspath(chosen)


def _answer_with_fallback(prompt: str, llm_choice: str) -> str:
    provider = (llm_choice or "groq").strip().lower()
    chat_model = "llama-3.3-70b-versatile"

    if provider == "nim":
        try:
            return call_nim(prompt)
        except Exception as nim_err:
            print(f"[FALLBACK LOG] NIM failed: {nim_err}. Using Groq 70B...")
            return call_groq(prompt, model=chat_model)
    else:
        try:
            return call_groq(prompt, model=chat_model)
        except Exception as groq_err:
            print(f"[FALLBACK LOG] Groq failed: {groq_err}. Using NIM...")
            return call_nim(prompt)


@app.route("/validate-keys", methods=["POST", "OPTIONS"])
def validate_keys():
    if request.method == "OPTIONS":
        response = jsonify({})
        response.headers["Access-Control-Allow-Origin"] = "*"
        response.headers["Access-Control-Allow-Methods"] = "POST, OPTIONS"
        response.headers["Access-Control-Allow-Headers"] = "Content-Type"
        return response

    data = request.json or {}
    groq_key = data.get("groq_key", "").strip()
    nim_key = data.get("nim_key", "").strip()

    results = {}
    
    if groq_key:
        try:
            from groq import Groq
            client = Groq(api_key=groq_key)
            client.models.list()
            results["groq"] = {"valid": True}
        except Exception as e:
            results["groq"] = {"valid": False, "error": str(e)}

    if nim_key:
        try:
            from openai import OpenAI
            client = OpenAI(base_url="https://integrate.api.nvidia.com/v1", api_key=nim_key)
            client.models.list()
            results["nim"] = {"valid": True}
        except Exception as e:
            results["nim"] = {"valid": False, "error": str(e)}

    # Add CORS headers for typical JSF dev server, or just jsonify with standard CORS
    response = jsonify(results)
    response.headers["Access-Control-Allow-Origin"] = "*"
    return response


@app.route("/chat", methods=["GET"])
def chat():
    doc_name = request.args.get("docName", "").strip()
    user_query = request.args.get("query", "").strip()
    llm_choice = request.args.get("llm_choice", "groq")
    data_dir = _resolve_data_dir(request.args.get("data_dir", ""))

    try:
        top_k = int(request.args.get("top_k", 3))
    except ValueError:
        top_k = 3

    if not doc_name or not user_query:
        return (
            jsonify(
                {
                    "success": False,
                    "error": "Missing docName or query parameter.",
                }
            ),
            400,
        )

    try:
        pages = build_index(doc_name, data_dir=data_dir, llm_choice=llm_choice)
        chunks = retrieve_top_k(user_query, pages, top_k=max(1, top_k))

        if not chunks:
            return jsonify(
                {
                    "success": True,
                    "answer": "I couldn't find any relevant content in the document for that question.",
                    "source_pages": [],
                }
            )

        prompt = build_rag_prompt(user_query, chunks)
        print(f"[CHAT LOG] Calling LLM ({llm_choice})...")
        import time
        t0 = time.time()
        answer = _answer_with_fallback(prompt, llm_choice)
        print(f"[CHAT LOG] Response received in {time.time()-t0:.2f}s")

        source_pages = [
            {
                "page": chunk["page"],
                "text": (
                    chunk["text"][:200] + "..."
                    if len(chunk["text"]) > 200
                    else chunk["text"]
                ),
                "score": chunk.get("score", 0.0),
                "images": [img.get("path", "") for img in chunk.get("images", [])]
            }
            for chunk in chunks
        ]

        return jsonify(
            {
                "success": True,
                "answer": answer,
                "source_pages": source_pages,
            }
        )
    except FileNotFoundError as e:
        return jsonify({"success": False, "error": str(e)}), 404
    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 500


def is_administrative(text: str) -> bool:
    """Heuristic to detect administrative/syllabus content."""
    keywords = [
        "faculty name", "subject code", "course code", "teaching department",
        "marking scheme", "internal marks", "external marks", "office hours",
        "prerequisite", "credits:", "l-t-p", "academic year", "grading policy",
        "syllabus cover", "course objectives", "reference books", "unit-wise syllabus"
    ]
    text_lower = text.lower()
    matches = sum(1 for kw in keywords if kw in text_lower)
    return matches >= 2 # If 2 or more keywords match, it's likely admin info

@app.route("/flashcards", methods=["GET"])
def flashcards():
    doc_name = request.args.get("docName", "").strip()
    llm_choice = request.args.get("llm_choice", "groq")
    data_dir = _resolve_data_dir(request.args.get("data_dir", ""))
    force = request.args.get("force", "false").lower() == "true"

    if not doc_name:
        return jsonify({"success": False, "error": "Missing docName parameter."}), 400

    # Determining cache path (preserving subdirectories for isolation)
    base = doc_name
    if base.lower().endswith(".pdf"):
        base = base[:-4]
        
    indices_dir = os.path.join(data_dir, "indices") if data_dir else os.path.join(os.path.abspath(DATA_DIR), "indices")
    cache_path = os.path.join(indices_dir, f"{base}.flashcards.json")

    # Return cached deck if available and not forced
    if os.path.exists(cache_path) and not force:
        try:
            with open(cache_path, "r", encoding="utf-8") as f:
                return jsonify(json.load(f))
        except Exception:
            pass # Fallback to generation if cache is corrupt

    try:
        pages = build_index(doc_name, data_dir=data_dir, llm_choice=llm_choice)
        if not pages:
            return jsonify({"success": False, "error": "No text found in document."}), 404

        # Adaptive syllabus skip: Skip first 15% or at least 3 pages, max 10
        skip_count = min(max(3, int(len(pages) * 0.15)), 10)
        meaningful_pages = pages[skip_count:] if len(pages) > skip_count else pages

        # Heuristic filtering: Drop any pages that look like admin info
        academic_pages = [p for p in meaningful_pages if not is_administrative(p['text'])]
        
        # Fallback to meaningful_pages if filtering was too aggressive
        if len(academic_pages) < 5:
            academic_pages = meaningful_pages

        step = max(1, len(academic_pages) // 15)
        sampled_pages = academic_pages[::step][:15]
        sampled_pages.sort(key=lambda x: x["page"])

        # Create balanced set: 5 Easy, 5 Medium, 5 Hard
        prompt = build_flashcards_prompt(sampled_pages, easy=5, medium=5, hard=5)
        
        # Flashcards require higher token counts for complex academic decks
        if llm_choice.lower() == "groq":
            raw = call_groq(prompt, max_tokens=2048, temperature=0.3, model="llama-3.1-8b-instant")
        else:
            raw = _answer_with_fallback(prompt, llm_choice)

        flashcards_data = parse_json_flashcards(raw)
        if not flashcards_data:
            return jsonify({"success": False, "error": "LLM failed to generate valid JSON flashcards. Please try again."}), 500
        
        # Save to cache
        os.makedirs(indices_dir, exist_ok=True)
        with open(cache_path, "w", encoding="utf-8") as f:
            json.dump(flashcards_data, f, indent=2)

        return jsonify(flashcards_data)

    except Exception as e:
        import traceback
        print(f"[INTERNAL ERROR] Flashcard generation failed: {str(e)}")
        traceback.print_exc()
        return jsonify({"success": False, "error": str(e)}), 500


@app.route("/pdfs/<path:filename>", methods=["GET"])
def serve_pdf(filename):
    try:
        data_dir = _resolve_data_dir(request.args.get("data_dir", ""))
        pdfs_dir = os.path.join(data_dir, "pdfs") if data_dir else os.path.join(os.path.abspath(DATA_DIR), "pdfs")
        
        filepath = os.path.join(pdfs_dir, filename)
        with open(filepath, "rb") as f:
            content = f.read()
            
        from flask import Response
        response = Response(content, mimetype="application/pdf")
        response.headers["Content-Disposition"] = "inline; filename*=UTF-8''" + filename
        response.headers["Access-Control-Allow-Origin"] = "*"
        response.headers["X-Frame-Options"] = "ALLOWALL"
        response.headers["Content-Security-Policy"] = "frame-ancestors *"
        return response
    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 404

@app.route("/images/<path:filename>", methods=["GET"])
def serve_image(filename):
    try:
        data_dir = _resolve_data_dir(request.args.get("data_dir", ""))
        images_dir = os.path.join(data_dir, "images") if data_dir else os.path.join(os.path.abspath(DATA_DIR), "images")
        
        response = send_from_directory(images_dir, filename)
        response.headers["Access-Control-Allow-Origin"] = "*"
        return response
    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 404


@app.route("/list-documents", methods=["GET"])
def list_documents():
    try:
        data_dir = _resolve_data_dir(request.args.get("data_dir", ""))
        pdfs_dir = os.path.join(data_dir, "pdfs") if data_dir else os.path.join(os.path.abspath(DATA_DIR), "pdfs")
        os.makedirs(pdfs_dir, exist_ok=True)
        docs = sorted(
            [name for name in os.listdir(pdfs_dir) if name.lower().endswith(".pdf")]
        )
        return jsonify({"documents": docs})
    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
