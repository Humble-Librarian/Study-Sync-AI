package com.studysync.beans;

import com.studysync.services.ConfigService;
import com.studysync.services.DocumentService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean(name = "dashboardBean")
@SessionScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Pattern PAGE_PATTERN = Pattern.compile("\"page\"\\s*:");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm", Locale.ENGLISH);

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{configService}")
    private ConfigService configService;

    @ManagedProperty(value = "#{documentService}")
    private DocumentService documentService;

    private List<Document> documents;
    private List<String> subjects = new ArrayList<>();
    private String selectedSubject;
    private String setupMessage;
    private String pendingDeleteDocName;

    @PostConstruct
    public void init() {
        loadDocuments();
    }

    public void loadDocuments() {
        if (userSession == null || !userSession.isLoggedIn()) {
            documents = new ArrayList<>();
            return;
        }

        try {
            int userId = userSession.getUserId();
            String sharedDir = configService.resolveSharedDataDir();
            Path userPdfsDir = Paths.get(sharedDir, "pdfs", String.valueOf(userId));
            Path userIndicesDir = Paths.get(sharedDir, "indices", String.valueOf(userId));
            
            Files.createDirectories(userPdfsDir);
            Files.createDirectories(userIndicesDir);

            // Fetch metadata from DB
            List<DocumentService.DbDocument> dbDocs = documentService.getDocumentsForUser(userId);
            
            Set<String> uniqueSubjects = new LinkedHashSet<>();
            List<Document> loadedDocs = new ArrayList<>();

            for (DocumentService.DbDocument dbDoc : dbDocs) {
                String name = dbDoc.name;
                Path pdfPath = userPdfsDir.resolve(name);
                
                if (Files.exists(pdfPath)) {
                    // Enrich with file system data (page count from index)
                    int pageCount = 0;
                    String baseName = name.toLowerCase().endsWith(".pdf") ? name.substring(0, name.length() - 4) : name;
                    Path indexPath = userIndicesDir.resolve(baseName + ".index.json");
                    
                    if (Files.exists(indexPath)) {
                        String content = new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8);
                        Matcher matcher = PAGE_PATTERN.matcher(content);
                        while (matcher.find()) pageCount++;
                    }

                    String formattedDate = dbDoc.uploadDate.toLocalDateTime()
                            .atZone(ZoneId.systemDefault())
                            .format(DATE_FORMAT);

                    loadedDocs.add(new Document(
                        dbDoc.id,
                        name,
                        dbDoc.subject,
                        dbDoc.status,
                        pageCount,
                        formattedDate
                    ));

                    if (dbDoc.subject != null && !dbDoc.subject.trim().isEmpty()) {
                        uniqueSubjects.add(dbDoc.subject.trim());
                    }
                }
            }

            this.documents = loadedDocs;
            this.subjects = new ArrayList<>(uniqueSubjects);
            this.setupMessage = null;

        } catch (Exception e) {
            this.documents = new ArrayList<>();
            this.setupMessage = "Error loading documents: " + e.getMessage();
        }
    }

    public void deleteDocument(String docName) {
        if (docName == null || userSession == null) return;
        
        try {
            int userId = userSession.getUserId();
            String sharedDir = configService.resolveSharedDataDir();
            String safeName = docName.replaceAll("[^a-zA-Z0-9.-]", "_");
            
            System.out.println("[DashboardBean] Deleting document: " + docName + " for user: " + userId);
            
            // 1. Delete from DB
            documentService.deleteDocument(userId, docName);

            // 2. Physical Deletion
            Path userSpace = Paths.get(String.valueOf(userId));
            Path pdfPath = Paths.get(sharedDir, "pdfs").resolve(userSpace).resolve(docName);
            
            String baseNoExt = docName.toLowerCase().endsWith(".pdf") ? 
                              docName.substring(0, docName.length() - 4) : docName;
            
            Path indexPath = Paths.get(sharedDir, "indices").resolve(userSpace).resolve(baseNoExt + ".index.json");
            Path flashPath = Paths.get(sharedDir, "indices").resolve(userSpace).resolve(baseNoExt + ".flashcards.json");
            Path imagesDir = Paths.get(sharedDir, "images").resolve(userSpace).resolve(baseNoExt);

            // Log and Delete
            if (Files.deleteIfExists(pdfPath)) System.out.println("  - Deleted PDF: " + pdfPath);
            if (Files.deleteIfExists(indexPath)) System.out.println("  - Deleted Index: " + indexPath);
            if (Files.deleteIfExists(flashPath)) System.out.println("  - Deleted Flashcards Cache: " + flashPath);
            
            if (Files.exists(imagesDir)) {
                System.out.println("  - Cleaning images directory: " + imagesDir);
                try (Stream<Path> walk = Files.walk(imagesDir)) {
                    walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                        try { Files.delete(p); } catch (Exception ignored) {}
                    });
                }
            }

            addMessage(FacesMessage.SEVERITY_INFO, "Deleted", safeName + " and all associated data.");
        } catch (Exception e) {
            System.err.println("[DashboardBean] Delete error: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Delete failed", e.getMessage());
        }

        loadDocuments();
    }

    public void deleteSelectedDocument() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) return;

        String docName = safeTrim(pendingDeleteDocName);
        if (docName.isEmpty()) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            docName = safeTrim(params.get("docNameToDelete"));
        }

        pendingDeleteDocName = null;
        deleteDocument(docName);
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.addMessage(null, new FacesMessage(severity, summary, detail));
        }
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    // Bean Getters & Setters
    public List<Document> getDocuments() {
        return documents; // loadDocuments is called in init() and after mutations
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public String getSetupMessage() {
        return setupMessage;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public String getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(String selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    public boolean isConfigured() {
        return configService != null && configService.isSetupComplete();
    }

    public String getPendingDeleteDocName() {
        return pendingDeleteDocName;
    }

    public void setPendingDeleteDocName(String pendingDeleteDocName) {
        this.pendingDeleteDocName = pendingDeleteDocName;
    }

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

        public int getId() { return id; }
        public String getName() { return name; }
        public String getSubject() { return subject; }
        public String getStatus() { return status; }
        public int getPages() { return pages; }
        public String getDate() { return date; }
        public boolean isReady() { return "uploaded".equals(status); }
    }
}
