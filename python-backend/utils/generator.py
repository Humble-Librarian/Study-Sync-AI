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


def build_flashcards_prompt(retrieved_chunks, easy=3, medium=3, hard=2):
    context = _build_context(retrieved_chunks)
    total = easy + medium + hard
    return f"""You are an expert flashcard author. Generate exactly {total} flashcards from the context below.

DIFFICULTY:
E=easy: direct recall (definitions, facts, names)
M=medium: conceptual (explain why/how, cause/effect)
H=hard: synthesis (compare, apply, infer across ideas)

QUALITY:
- Questions must be self-contained and unambiguous
- Answers: 1-2 sentences, no filler
- No yes/no questions
- Do not copy sentences verbatim from the text

COUNT (must be exact): {easy}E {medium}M {hard}H

OUTPUT — TOON format, raw, no markdown, no preamble:
cards[{total}]{{question,answer,difficulty}}:
<question>,<answer>,<E|M|H>

CONTEXT:
{context}"""
