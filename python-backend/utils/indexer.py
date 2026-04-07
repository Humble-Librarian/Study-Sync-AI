import json
import os
from typing import Dict, List

import pdfplumber

from config import DATA_DIR


def _candidate_base_dirs(data_dir: str = "") -> List[str]:
    raw_candidates = [
        (data_dir or "").strip(),
        (DATA_DIR or "").strip(),
    ]

    project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
    cwd = os.getcwd()
    expanded: List[str] = []
    for candidate in raw_candidates:
        if not candidate:
            continue
        expanded.append(candidate)
        expanded.append(os.path.abspath(candidate))
        expanded.append(os.path.abspath(os.path.join(cwd, candidate)))
        expanded.append(os.path.abspath(os.path.join(project_root, candidate)))

    deduped: List[str] = []
    seen = set()
    for path in expanded:
        normalized = os.path.abspath(path)
        if normalized not in seen:
            seen.add(normalized)
            deduped.append(normalized)
    return deduped


def _resolve_dirs(data_dir: str = ""):
    candidates = _candidate_base_dirs(data_dir)
    if not candidates:
        raise ValueError("Data directory is not configured.")

    base_dir = candidates[0]
    pdfs_dir = os.path.join(base_dir, "pdfs")
    indices_dir = os.path.join(base_dir, "indices")
    return pdfs_dir, indices_dir, candidates


def _index_path(doc_name: str, indices_dir: str) -> str:
    base = os.path.splitext(os.path.basename(doc_name))[0]
    return os.path.join(indices_dir, f"{base}.index.json")


def build_index(doc_name: str, force: bool = False, data_dir: str = "") -> List[Dict]:
    pdfs_dir, indices_dir, base_candidates = _resolve_dirs(data_dir)
    doc_file = os.path.basename(doc_name)

    selected_pdfs_dir = pdfs_dir
    selected_indices_dir = indices_dir
    pdf_path = os.path.join(selected_pdfs_dir, doc_file)

    if not os.path.exists(pdf_path):
        for base in base_candidates:
            candidate_pdf = os.path.join(base, "pdfs", doc_file)
            if os.path.exists(candidate_pdf):
                selected_pdfs_dir = os.path.join(base, "pdfs")
                selected_indices_dir = os.path.join(base, "indices")
                pdf_path = candidate_pdf
                break

    if not os.path.exists(pdf_path):
        tried = [os.path.join(base, "pdfs", doc_file) for base in base_candidates]
        raise FileNotFoundError(
            "PDF not found. Tried paths: " + " | ".join(tried[:6])
        )

    os.makedirs(selected_indices_dir, exist_ok=True)
    idx_path = _index_path(doc_name, selected_indices_dir)

    if os.path.exists(idx_path) and not force:
        with open(idx_path, "r", encoding="utf-8") as f:
            return json.load(f)

    pages: List[Dict] = []
    with pdfplumber.open(pdf_path) as pdf:
        for i, page in enumerate(pdf.pages, start=1):
            text = (page.extract_text() or "").strip()
            if text:
                pages.append({"page": i, "text": text})

    with open(idx_path, "w", encoding="utf-8") as f:
        json.dump(pages, f, ensure_ascii=False, indent=2)

    return pages
