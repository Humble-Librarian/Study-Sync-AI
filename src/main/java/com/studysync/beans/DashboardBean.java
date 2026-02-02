package com.studysync.beans;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "dashboardBean")
@SessionScoped
public class DashboardBean implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private String selectedSubject;
    private List<String> subjects;
    private List<Document> documents;

    @PostConstruct
    public void init() {
        // Mock data
        subjects = new ArrayList<>();
        subjects.add("Operating Systems");
        subjects.add("Data Structures");
        subjects.add("Database Management");

        loadDocuments();
    }

    public void loadDocuments() {
        documents = new ArrayList<>();
        documents.add(new Document(1, "OS_Lecture_01.pdf", "Operating Systems", "indexed", 25, "Jan 20, 2026"));
        documents.add(new Document(2, "Process_Scheduling.pdf", "Operating Systems", "indexing", 18, "Jan 22, 2026"));
        documents.add(new Document(3, "Trees_and_Graphs.pdf", "Data Structures", "indexed", 32, "Jan 23, 2026"));
        documents.add(new Document(4, "SQL_Basics.pdf", "Database Management", "failed", 0, "Jan 24, 2026"));
    }

    public void onSubjectChange() {
        if (userSession != null) {
            userSession.setSelectedSubject(selectedSubject);
        }
    }

    public List<Document> getFilteredDocuments() {
        if (selectedSubject == null || selectedSubject.isEmpty()) {
            return documents;
        }
        List<Document> filtered = new ArrayList<>();
        for (Document doc : documents) {
            if (doc.getSubject().equals(selectedSubject)) {
                filtered.add(doc);
            }
        }
        return filtered;
    }

    // Getters and Setters
    public String getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(String selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public List<Document> getDocuments() {
        return getFilteredDocuments();
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    // Inner class for Document
    public static class Document implements Serializable {
        private int id;
        private String name;
        private String subject;
        private String status;
        private int pages;
        private String date;

        public Document(int id, String name, String subject, String status, int pages, String date) {
            this.id = id;
            this.name = name;
            this.subject = subject;
            this.status = status;
            this.pages = pages;
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSubject() {
            return subject;
        }

        public String getStatus() {
            return status;
        }

        public int getPages() {
            return pages;
        }

        public String getDate() {
            return date;
        }

        public boolean isReady() {
            return "indexed".equals(status);
        }
    }
}
