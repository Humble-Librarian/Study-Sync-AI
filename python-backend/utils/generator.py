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


def build_flashcards_prompt(retrieved_chunks: List[Dict], easy: int = 3, medium: int = 3, hard: int = 2) -> str:
    context_parts = [
        f"[Page {chunk['page']}]\n{chunk['text']}" for chunk in retrieved_chunks
    ]
    context = "\n\n---\n\n".join(context_parts)
    total = easy + medium + hard

    return f"""You are an expert AI teacher. Your task is to generate extremely high quality flashcards based on the provided text.

Generate EXACTLY {total} flashcards from the text chunks below. 
Specifically, generate exactly:
- {easy} "easy" flashcards
- {medium} "medium" flashcards
- {hard} "hard" flashcards

You MUST format your output as a valid JSON array of objects. 
Each object must have exactly three keys: "question", "answer", and "difficulty".
The "difficulty" must be exactly one of: "easy", "medium", "hard".

Do not wrap the JSON array in markdown formatting, just return raw JSON text starting with '['.

CONTEXT:
{context}
"""
