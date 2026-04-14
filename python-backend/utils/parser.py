import json
import re

_DIFFICULTY_MAP = {"E": "easy", "M": "medium", "H": "hard"}

def parse_json_flashcards(raw: str) -> list[dict]:
    """Parse JSON flashcard output into a list of dicts.
    
    Handles raw JSON or JSON wrapped in markdown code blocks.
    """
    try:
        # Clean up possible markdown code blocks
        clean_json = raw.strip()
        if "```" in clean_json:
            match = re.search(r"```(?:json)?\s*([\s\S]*?)\s*```", clean_json)
            if match:
                clean_json = match.group(1).strip()

        data = json.loads(clean_json)
        
        # Adjust for possible root wrappers (e.g., {"cards": [...]})
        if isinstance(data, dict):
            for key in ["cards", "flashcards"]:
                if key in data and isinstance(data[key], list):
                    data = data[key]
                    break
        
        if not isinstance(data, list):
            return []

        # Standardize fields and map difficulty
        standardized = []
        for item in data:
            q = item.get("question") or item.get("q")
            a = item.get("answer") or item.get("a")
            d = (item.get("difficulty") or item.get("d") or "M").upper()
            
            if q and a:
                standardized.append({
                    "question": q.strip(),
                    "answer": a.strip(),
                    "difficulty": _DIFFICULTY_MAP.get(d, "medium")
                })
        return standardized

    except Exception:
        return []
