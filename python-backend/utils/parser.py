_DIFFICULTY_MAP = {"E": "easy", "M": "medium", "H": "hard"}


def parse_toon_flashcards(raw: str) -> list[dict]:
    """Parse TOON-format flashcard output into a list of dicts.

    Expected LLM output (after the header line):
        cards[N]{question,answer,difficulty}:
        <question>,<answer>,<E|M|H>
        ...

    Returns:
        List of dicts with keys: question, answer, difficulty
    """
    cards = []
    for line in raw.splitlines():
        line = line.strip()
        # Skip blank lines and the TOON header line
        if not line or line.startswith("cards["):
            continue
        parts = line.split(",", 2)
        if len(parts) != 3:
            continue
        question, answer, diff_code = (p.strip() for p in parts)
        difficulty = _DIFFICULTY_MAP.get(diff_code.upper())
        if difficulty is None:
            continue
        cards.append({"question": question, "answer": answer, "difficulty": difficulty})
    return cards
