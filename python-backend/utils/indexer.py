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


def build_index(doc_name: str, force: bool = False, data_dir: str = "", llm_choice: str = "groq") -> List[Dict]:
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
    import fitz
    import base64
    from utils.llm_nim import call_nim_vision
    import concurrent.futures

    images_target_dir = os.path.join(selected_pdfs_dir, "..", "images", doc_name.replace(".pdf", ""))
    os.makedirs(images_target_dir, exist_ok=True)
    
    # Store all asynchronous vision tasks so we can run them in parallel
    vision_tasks = []
    
    with fitz.open(pdf_path) as pdf_doc:
        for i, page in enumerate(pdf_doc, start=1):
            text = (page.get_text() or "").strip()
            
            page_images_data = []
            if llm_choice.lower() == "nim":
                image_list = page.get_images()
                for img_idx, img in enumerate(image_list):
                    xref = img[0]
                    try:
                        base_image = pdf_doc.extract_image(xref)
                        image_bytes = base_image["image"]
                        image_ext = base_image["ext"]
                        
                        img_filename = f"page{i}_img{img_idx}.{image_ext}"
                        img_filepath = os.path.join(images_target_dir, img_filename)
                        with open(img_filepath, "wb") as img_fp:
                            img_fp.write(image_bytes)
                            
                        b64 = base64.b64encode(image_bytes).decode("utf-8")
                        prompt = "Describe this diagram, chart, or visual so completely that somebody reading only text could fully recreate its insights and answer analytical questions about it. Be as detailed as possible."
                        
                        img_data = {
                            "path": f"images/{doc_name.replace('.pdf', '')}/{img_filename}",
                            "description": "Processing..."
                        }
                        page_images_data.append(img_data)
                        vision_tasks.append((prompt, b64, img_data))
                    except Exception:
                        continue

            if text or page_images_data:
                pages.append({"page": i, "text": text, "images": page_images_data})

    # Execute all NIM API calls concurrently
    if vision_tasks:
        with concurrent.futures.ThreadPoolExecutor(max_workers=5) as executor:
            future_to_img_data = {
                executor.submit(call_nim_vision, task[0], task[1]): task[2]
                for task in vision_tasks
            }
            for future in concurrent.futures.as_completed(future_to_img_data):
                img_data = future_to_img_data[future]
                try:
                    description = future.result()
                    img_data["description"] = description
                except Exception as e:
                    img_data["description"] = f"Image transcription failed: {str(e)}"

    with open(idx_path, "w", encoding="utf-8") as f:
        json.dump(pages, f, ensure_ascii=False, indent=2)

    return pages
