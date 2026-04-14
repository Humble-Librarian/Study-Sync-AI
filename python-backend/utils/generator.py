from typing import Dict, List


def _build_context(retrieved_chunks):
    return "\n---\n".join(
        f"[p{chunk['page']}] {chunk['text'].strip()}"
        for chunk in retrieved_chunks
    )


def build_rag_prompt(query: str, retrieved_chunks: List[Dict]) -> str:
    context_parts = [
        f"[Page {chunk['page']}]\n{chunk['text']}" for chunk in retrieved_chunks
    ]
    context = "\n\n---\n\n".join(context_parts)

    return (
        "You are a helpful academic assistant.\n"
        "Answer the student's question using only the context below.\n"
        "If the answer is not in the context, say \"I couldn't find that in the document.\"\n\n"
        f"CONTEXT:\n{context}\n\n"
        f"STUDENT QUESTION:\n{query}\n\n"
        "ANSWER:"
    )


def build_flashcards_prompt(retrieved_chunks, easy=5, medium=5, hard=5):
    context = _build_context(retrieved_chunks)
    total = easy + medium + hard
    return f"""You are an expert academic flashcard author for higher education. 
Generate exactly {total} flashcards based on the context provided.

DIFFICULTY GUIDELINES:
- E (Easy): Direct recall of specific definitions, technical names, or core parameters.
- M (Medium): Conceptual checks. Requires explaining "why" or "how" or identifying relationships.
- H (Hard): Advanced synthesis. Requires applying a principle to a new scenario or inferring results across multiple sections.

STRICT CONTENT RULES:
1. FOCUS ONLY on technical, academic, or core subject matter.
2. ABSOLUTELY FORBIDDEN: Do not generate questions about faculty names, subject codes, grading, schedules, or departmental info.
3. If a section of text is purely administrative, skip it and focus on technical content elsewhere.
4. Each question MUST be unambiguous and self-contained.
5. ANSWERS must be 1-2 sentences of precise, high-quality explanation. No filler.

OUTPUT FORMAT:
Return a RAW JSON array of objects. No preamble, no markdown wrappers, just the data.
Required keys: "question", "answer", "difficulty" (use "E", "M", or "H")

JSON SCHEMA EXAMPLE:
[
  {{
    "question": "What is the primary function of a transformer in a power system?",
    "answer": "To step up or step down voltage levels while maintaining power frequency, facilitating efficient long-distance transmission.",
    "difficulty": "E"
  }}
]

CONTEXT:
{context}"""
