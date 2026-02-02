package com.studysync.beans;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "flashcardBean")
@ViewScoped
public class FlashcardBean implements Serializable {

    private List<Flashcard> flashcards = new ArrayList<>();
    private String documentName = "Operating Systems Lecture";
    private int documentId;
    private boolean loading = false;

    @PostConstruct
    public void init() {
        generateFlashcards();
    }

    public void generateFlashcards() {
        loading = true;
        flashcards.clear();

        // Mock flashcard data
        flashcards.add(new Flashcard(1,
                "What is a Semaphore?",
                "A semaphore is a synchronization primitive used to control access to shared resources. It maintains a counter that is decremented when acquired and incremented when released.",
                "easy"));

        flashcards.add(new Flashcard(2,
                "Define Critical Section",
                "A critical section is a segment of code that accesses shared resources and must not be concurrently executed by more than one process to avoid race conditions.",
                "medium"));

        flashcards.add(new Flashcard(3,
                "Explain Banker's Algorithm",
                "Banker's algorithm is a deadlock avoidance algorithm that checks if granting a resource request will leave the system in a safe state before allowing the allocation.",
                "hard"));

        flashcards.add(new Flashcard(4,
                "What is Context Switching?",
                "Context switching is the process of storing the state of a process or thread so that it can be restored and executed later, and then loading the state of another process.",
                "medium"));

        flashcards.add(new Flashcard(5,
                "Process vs Thread",
                "A process is an executing program with its own memory space, while a thread is a lightweight unit of execution within a process that shares the process's resources.",
                "easy"));

        flashcards.add(new Flashcard(6,
                "What causes Deadlock?",
                "Deadlock occurs when four conditions are met simultaneously: Mutual Exclusion, Hold and Wait, No Preemption, and Circular Wait.",
                "hard"));

        loading = false;
    }

    public void regenerate() {
        generateFlashcards();
    }

    // Getters and Setters
    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public boolean isLoading() {
        return loading;
    }

    public int getCardCount() {
        return flashcards.size();
    }

    // Inner Flashcard class
    public static class Flashcard implements Serializable {
        private int id;
        private String question;
        private String answer;
        private String difficulty;

        public Flashcard(int id, String question, String answer, String difficulty) {
            this.id = id;
            this.question = question;
            this.answer = answer;
            this.difficulty = difficulty;
        }

        public int getId() {
            return id;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public String getDifficultyClass() {
            switch (difficulty) {
                case "easy":
                    return "badge-success";
                case "medium":
                    return "badge-warning";
                case "hard":
                    return "badge-danger";
                default:
                    return "badge-info";
            }
        }
    }
}
