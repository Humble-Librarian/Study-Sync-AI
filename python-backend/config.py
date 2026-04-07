import os

from dotenv import load_dotenv

load_dotenv()

DATA_DIR = os.getenv("STUDYSYNC_DATA_DIR", "C:/StudySync_Data")
PDFS_DIR = os.path.join(DATA_DIR, "pdfs")
INDICES_DIR = os.path.join(DATA_DIR, "indices")

GROQ_API_KEY = os.getenv("GROQ_API_KEY")
GROQ_MODEL = os.getenv("GROQ_MODEL", "llama-3.3-70b-versatile")
NVIDIA_NIM_API_KEY = os.getenv("NVIDIA_NIM_API_KEY")
