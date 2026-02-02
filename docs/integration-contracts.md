# Backend Integration Contracts

This document defines the API contracts for integrating the Study-Sync AI frontend with a backend service.

## Base URL
```
https://api.studysync.com/v1
```

## Authentication

### POST `/auth/login`
Login and receive session token.

**Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200):**
```json
{
  "token": "jwt-token-string",
  "user": {
    "id": 123,
    "username": "student1",
    "fullName": "Student Name"
  }
}
```

**Response (401):** Invalid credentials

---

## Subjects

### GET `/subjects`
List all subjects for the logged-in user.

**Response (200):**
```json
[
  { "id": 1, "name": "Operating Systems" },
  { "id": 2, "name": "Data Structures" }
]
```

---

## Documents

### GET `/subjects/{subjectId}/documents`
List documents for a subject.

**Response (200):**
```json
[
  {
    "id": 101,
    "name": "OS_Lecture_01.pdf",
    "status": "indexed",
    "pages": 25,
    "uploadedAt": "2026-01-20T10:00:00Z"
  }
]
```

### POST `/documents/upload`
Upload a new document.

**Request:** `multipart/form-data`
- `file`: PDF file (max 20MB)
- `subjectId`: integer

**Response (202):**
```json
{
  "documentId": 105,
  "message": "Upload successful, indexing started"
}
```

---

## Chat

### POST `/chat/message`
Send a message and receive AI response.

**Request:**
```json
{
  "documentId": 101,
  "message": "What is process scheduling?",
  "vivaMode": false
}
```

**Response (200):**
```json
{
  "answer": "Process scheduling is...",
  "timestamp": "2026-01-25T12:00:00Z",
  "sources": [12, 14]
}
```

---

## Flashcards

### POST `/documents/{documentId}/flashcards/generate`
Generate flashcards from a document.

**Response (200):**
```json
[
  {
    "id": 501,
    "question": "What is a semaphore?",
    "answer": "A synchronization primitive...",
    "difficulty": "easy"
  }
]
```

---

## Error Responses

All endpoints may return:

| Code | Description |
|------|-------------|
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Token missing/invalid |
| 403 | Forbidden - Access denied |
| 404 | Not Found - Resource doesn't exist |
| 500 | Server Error |

**Error Format:**
```json
{
  "error": "Error message",
  "code": "ERROR_CODE"
}
```
