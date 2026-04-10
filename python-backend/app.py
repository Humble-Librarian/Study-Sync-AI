import os
import json
import random
from typing import Callable

from flask import Flask, jsonify, request, send_from_directory

from config import DATA_DIR
from utils.generator import build_rag_prompt, build_flashcards_prompt
from utils.parser import parse_toon_flashcards
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

    if provider == "nim":
        primary_name, primary_fn = "NIM", call_nim
        fallback_name, fallback_fn = "Groq", call_groq
    else:
        primary_name, primary_fn = "Groq", call_groq
        fallback_name, fallback_fn = "NIM", call_nim

    try:
        return primary_fn(prompt)
    except Exception as primary_err:
        try:
            return fallback_fn(prompt)
        except Exception as fallback_err:
            return (
                f"Both LLM providers failed. "
                f"{primary_name}: {primary_err} | {fallback_name}: {fallback_err}"
            )


@app.route("/validate-keys", methods=["POST"])
def validate_keys():
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
        answer = _answer_with_fallback(prompt, llm_choice)

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


@app.route("/flashcards", methods=["GET"])
def flashcards():
    doc_name = request.args.get("docName", "").strip()
    llm_choice = request.args.get("llm_choice", "groq")
    data_dir = _resolve_data_dir(request.args.get("data_dir", ""))

    if not doc_name:
        return jsonify({"success": False, "error": "Missing docName parameter."}), 400

    try:
        easy = int(request.args.get("easy", 3))
        medium = int(request.args.get("medium", 3))
        hard = int(request.args.get("hard", 2))
    except ValueError:
        easy, medium, hard = 3, 3, 2

    try:
        pages = build_index(doc_name, data_dir=data_dir, llm_choice=llm_choice)
        if not pages:
            return jsonify({"success": False, "error": "No text found in document."}), 404

        # Sample a few pages so the flashcards are diverse, but limit to 10 for context size
        sample_size = min(10, len(pages))
        sampled_pages = random.sample(pages, sample_size)
        sampled_pages.sort(key=lambda x: x["page"])

        prompt = build_flashcards_prompt(sampled_pages, easy=easy, medium=medium, hard=hard)
        raw = _answer_with_fallback(prompt, llm_choice)

        flashcards_data = parse_toon_flashcards(raw)
        if not flashcards_data:
            raise ValueError("Parser returned no flashcards from LLM output.")
        return jsonify(flashcards_data)

    except Exception as e:
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
