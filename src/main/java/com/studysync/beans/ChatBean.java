package com.studysync.beans;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "chatBean")
@SessionScoped
public class ChatBean implements Serializable {

    private String currentMessage;
    private boolean vivaMode = false;
    private List<ChatMessage> messages = new ArrayList<>();
    private String documentName = "Document";
    private int documentId;

    @PostConstruct
    public void init() {
        // Add welcome message
        messages.add(new ChatMessage(
                "Hello! I'm ready to help you learn from this document. Ask me anything!",
                false, new Date()));
    }

    public void sendMessage() {
        if (currentMessage == null || currentMessage.trim().isEmpty()) {
            return;
        }

        // Add user message
        messages.add(new ChatMessage(currentMessage, true, new Date()));

        // Generate AI response
        String response = generateResponse(currentMessage);
        messages.add(new ChatMessage(response, false, new Date()));

        // Clear input
        currentMessage = "";
    }

    private String generateResponse(String question) {
        String q = question.toLowerCase();

        if (vivaMode) {
            // Examiner-style follow-up questions
            if (q.contains("scheduling")) {
                return "Interesting. Can you explain the difference between preemptive and non-preemptive scheduling? Which would you choose for a real-time system and why?";
            } else if (q.contains("deadlock")) {
                return "You mentioned deadlock. What are the four necessary conditions for deadlock? Can you give a real-world example?";
            } else {
                return "That's a good point. Can you elaborate further? What are the practical implications of this concept?";
            }
        } else {
            // Normal helpful responses
            if (q.contains("scheduling")) {
                return "**Process Scheduling** is the activity of the process manager that handles the removal of the running process from the CPU and the selection of another process based on a particular strategy.\n\nCommon algorithms include:\n• First Come First Serve (FCFS)\n• Shortest Job First (SJF)\n• Round Robin (RR)\n• Priority Scheduling";
            } else if (q.contains("deadlock")) {
                return "**Deadlock** is a situation where a set of processes are blocked because each process is holding a resource and waiting for another resource held by another process.\n\n**Four necessary conditions:**\n1. Mutual Exclusion\n2. Hold and Wait\n3. No Preemption\n4. Circular Wait";
            } else if (q.contains("semaphore")) {
                return "A **Semaphore** is a synchronization primitive used to control access to a shared resource. It maintains a counter that is decremented when a process acquires the resource and incremented when released.\n\n**Types:**\n• Binary Semaphore (0 or 1)\n• Counting Semaphore (0 to N)";
            } else if (q.contains("hello") || q.contains("hi")) {
                return "Hello! I'm here to help you understand this document. What would you like to learn about?";
            } else {
                return "That's an interesting question about \"" + question
                        + "\". Based on the document, this concept relates to the core principles we discussed. Could you be more specific about which aspect you'd like me to explain?";
            }
        }
    }

    public void toggleVivaMode() {
        vivaMode = !vivaMode;
        String modeMessage = vivaMode
                ? "🎓 **Viva Mode ON** - I'll now ask you follow-up questions like an examiner."
                : "📚 **Viva Mode OFF** - Back to helpful explanation mode.";
        messages.add(new ChatMessage(modeMessage, false, new Date()));
    }

    // Getters and Setters
    public String getCurrentMessage() {
        return currentMessage;
    }

    public void setCurrentMessage(String currentMessage) {
        this.currentMessage = currentMessage;
    }

    public boolean isVivaMode() {
        return vivaMode;
    }

    public void setVivaMode(boolean vivaMode) {
        this.vivaMode = vivaMode;
    }

    public List<ChatMessage> getMessages() {
        return messages;
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

    // Inner class for messages
    public static class ChatMessage implements Serializable {
        private String content;
        private boolean fromUser;
        private Date timestamp;

        public ChatMessage(String content, boolean fromUser, Date timestamp) {
            this.content = content;
            this.fromUser = fromUser;
            this.timestamp = timestamp;
        }

        public String getContent() {
            return content;
        }

        public boolean isFromUser() {
            return fromUser;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }
}
