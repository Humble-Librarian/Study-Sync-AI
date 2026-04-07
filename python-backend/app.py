import os
from typing import Callable

from flask import Flask, jsonify, request

from config import DATA_DIR
from utils.generator import build_rag_prompt
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
        pages = build_index(doc_name, data_dir=data_dir)
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
