from typing import Optional

from openai import OpenAI

from config import get_nim_api_key

_client: Optional[OpenAI] = None
_client_key: Optional[str] = None

def _get_client() -> OpenAI:
    global _client, _client_key
    key = get_nim_api_key()
    if not key:
        raise EnvironmentError("NVIDIA_NIM_API_KEY is not set. Please configure it in Setup.")
    
    if _client is None or _client_key != key:
        _client = OpenAI(
            base_url="https://integrate.api.nvidia.com/v1",
            api_key=key,
        )
        _client_key = key
    return _client


def call_nim(
    prompt: str,
    model: str = "meta/llama3-70b-instruct",
    max_tokens: int = 512,
    temperature: float = 0.3,
) -> str:
    client = _get_client()
    response = client.chat.completions.create(
        model=model,
        messages=[{"role": "user", "content": prompt}],
        max_tokens=max_tokens,
        temperature=temperature,
    )
    return (response.choices[0].message.content or "").strip()

def call_nim_vision(prompt: str, base64_image: str) -> str:
    client = _get_client()
    try:
        response = client.chat.completions.create(
            model="meta/llama-3.2-11b-vision-instruct",
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
