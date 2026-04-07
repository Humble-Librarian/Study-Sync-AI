from typing import Dict, List


def build_rag_prompt(query: str, retrieved_chunks: List[Dict]) -> str:
    context_parts = [
        f"[Page {chunk['page']}]\n{chunk['text']}" for chunk in retrieved_chunks
    ]
    context = "\n\n---\n\n".join(context_parts)

    return f"""You are a helpful academic assistant.
Answer the student's question using only the context below.
If the answer is not in the context, say "I couldn't find that in the document."

CONTEXT:
{context}

STUDENT QUESTION:
{query}

ANSWER:"""
