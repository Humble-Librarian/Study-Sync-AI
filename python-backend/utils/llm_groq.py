from typing import Optional

from groq import Groq

from config import GROQ_API_KEY, GROQ_MODEL

_client: Optional[Groq] = None


def _get_client() -> Groq:
    global _client
    if _client is None:
        if not GROQ_API_KEY:
            raise EnvironmentError("GROQ_API_KEY is not set in .env")
        _client = Groq(api_key=GROQ_API_KEY)
    return _client


def call_groq(
    prompt: str,
    model: Optional[str] = None,
    max_tokens: int = 512,
    temperature: float = 0.3,
) -> str:
    client = _get_client()
    selected_model = (model or GROQ_MODEL or "llama-3.3-70b-versatile").strip()
    response = client.chat.completions.create(
        model=selected_model,
        messages=[{"role": "user", "content": prompt}],
        max_tokens=max_tokens,
        temperature=temperature,
    )
    return (response.choices[0].message.content or "").strip()
