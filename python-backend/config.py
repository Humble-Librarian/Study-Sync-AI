import os

from dotenv import load_dotenv

load_dotenv()

DATA_DIR = os.getenv("STUDYSYNC_DATA_DIR", "C:/StudySync_Data")
PDFS_DIR = os.path.join(DATA_DIR, "pdfs")
INDICES_DIR = os.path.join(DATA_DIR, "indices")

def _read_app_config():
    cfg_path = os.path.expanduser("~/.studysync/app-config.properties")
    props = {}
    if os.path.exists(cfg_path):
        with open(cfg_path, "r", encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith("#") or line.startswith("!"):
                    continue
                if "=" in line:
                    k, v = line.split("=", 1)
                    if ":" in k: 
                        k = k.replace(":", "") # basic properties parsing
                    props[k.strip()] = v.strip()
    return props

def get_groq_api_key():
    props = _read_app_config()
    return props.get("groq_api_key") or os.getenv("GROQ_API_KEY")

def get_nim_api_key():
    props = _read_app_config()
    return props.get("nim_api_key") or os.getenv("NVIDIA_NIM_API_KEY")

GROQ_MODEL = os.getenv("GROQ_MODEL", "llama-3.3-70b-versatile")
