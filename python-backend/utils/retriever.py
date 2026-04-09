from typing import Dict, List

import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity


def retrieve_top_k(query: str, pages: List[Dict], top_k: int = 3) -> List[Dict]:
    if not pages:
        return []

    top_k = max(1, int(top_k))
    corpus = []
    for p in pages:
        text = p.get("text", "")
        img_narratives = "\n".join([f"\n[Visual Component: {img.get('description', '')}]" for img in p.get("images", [])])
        corpus.append((text + img_narratives).strip())
    vectorizer = TfidfVectorizer(stop_words="english", ngram_range=(1, 2))

    try:
        tfidf_matrix = vectorizer.fit_transform(corpus + [query])
    except ValueError:
        return [{**p, "score": 0.0} for p in pages[:top_k]]

    doc_vectors = tfidf_matrix[:-1]
    query_vector = tfidf_matrix[-1]
    scores = cosine_similarity(query_vector, doc_vectors).flatten()
    top_indices = np.argsort(scores)[::-1][:top_k]

    results: List[Dict] = []
    for idx in top_indices:
        score = float(scores[idx])
        if score > 0.0:
            results.append(
                {
                    "page": pages[idx]["page"],
                    "text": corpus[idx],
                    "score": score,
                    "images": pages[idx].get("images", [])
                }
            )

    if not results:
        return [{**p, "score": 0.0} for p in pages[:top_k]]
    return results
