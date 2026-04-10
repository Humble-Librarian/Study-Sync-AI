from typing import Optional

from groq import Groq

from config import get_groq_api_key, GROQ_MODEL

_client: Optional[Groq] = None
_client_key: Optional[str] = None

def _get_client() -> Groq:
    global _client, _client_key
    key = get_groq_api_key()
    if not key:
        raise EnvironmentError("GROQ_API_KEY is not set. Please configure it in Setup.")
    
    if _client is None or _client_key != key:
        _client = Groq(api_key=key)
        _client_key = key
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

def call_groq_vision(prompt: str, base64_image: str) -> str:
    client = _get_client()
    try:
        response = client.chat.completions.create(
            model="llama-3.2-90b-vision-preview",
            messages=[{
                "role": "user",
                "content": [
                    {"type": "text", "text": prompt},
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:image/jpeg;base64,{base64_image}",
                        },
                    },
                ],
            }],
            max_tokens=512,
            temperature=0.2,
        )
        return (response.choices[0].message.content or "").strip()
    except Exception as e:
        return f"Image transcription failed: {str(e)}"
