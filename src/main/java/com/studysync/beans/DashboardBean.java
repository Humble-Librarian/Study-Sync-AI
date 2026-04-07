package com.studysync.beans;

import com.studysync.services.ConfigService;

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
        documents = new ArrayList<>();
        subjects = new ArrayList<>();

        if (configService == null || !configService.isPathConfigured()) {
            setupMessage = "Shared folder is not configured yet. Complete setup before uploading files.";
            return;
        }

        setupMessage = "";
        Path pdfsDir = Paths.get(configService.resolveSharedDataDir(), "pdfs");
        Path indicesDir = Paths.get(configService.resolveSharedDataDir(), "indices");

        try {
            Files.createDirectories(pdfsDir);
            Files.createDirectories(indicesDir);

            List<Path> pdfFiles;
            try (Stream<Path> stream = Files.list(pdfsDir)) {
                pdfFiles = stream
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".pdf"))
                        .sorted(Comparator.comparing(this::lastModifiedSafe).reversed())
                        .collect(Collectors.toList());
            }

            int i = 1;
            Set<String> uniqueSubjects = new LinkedHashSet<>();
            for (Path pdfPath : pdfFiles) {
                String name = pdfPath.getFileName().toString();
                String baseName = removeExtension(name);
                Path indexPath = indicesDir.resolve(baseName + ".index.json");
                boolean indexed = Files.exists(indexPath);
                String subject = inferSubject(name);
                uniqueSubjects.add(subject);

                int pages = indexed ? countIndexedPages(indexPath) : 0;
                String status = indexed ? "indexed" : "uploaded";
                String date = DATE_FORMAT.format(lastModifiedSafe(pdfPath).toInstant().atZone(ZoneId.systemDefault()));

                documents.add(new Document(i++, name, subject, status, pages, date));
            }
            subjects = new ArrayList<>(uniqueSubjects);
        } catch (Exception e) {
            setupMessage = "Could not load documents: " + e.getMessage();
        }
    }

    private FileTime lastModifiedSafe(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (Exception ignored) {
            return FileTime.fromMillis(0);
        }
    }

    private int countIndexedPages(Path indexPath) {
        try {
            String json = Files.readString(indexPath, StandardCharsets.UTF_8);
            Matcher matcher = PAGE_PATTERN.matcher(json);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            return count;
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String removeExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex <= 0) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }

    private String inferSubject(String fileName) {
        String base = removeExtension(fileName);
        if (base.contains("_")) {
            String candidate = base.substring(0, base.indexOf('_')).trim();
            if (!candidate.isEmpty()) {
                return candidate;
            }
        }
        if (base.contains("-")) {
            String candidate = base.substring(0, base.indexOf('-')).trim();
            if (!candidate.isEmpty()) {
                return candidate;
            }
        }
        return "General";
    }

    public void onSubjectChange() {
        if (userSession != null) {
            userSession.setSelectedSubject(selectedSubject);
        }
    }

    public void deleteDocument(String docName) {
        if (configService == null || !configService.isPathConfigured()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Delete failed", "Shared folder is not configured.");
            return;
        }
        if (docName == null || docName.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Delete failed", "Invalid document name.");
            return;
        }

        try {
            String safeName = Paths.get(docName).getFileName().toString();
            Path pdfPath = Paths.get(configService.getPdfsDir(), safeName);
            Path indexPath = Paths.get(configService.getIndicesDir(), removeExtension(safeName) + ".index.json");

            boolean pdfDeleted = Files.deleteIfExists(pdfPath);
            Files.deleteIfExists(indexPath);

            if (pdfDeleted) {
                addMessage(FacesMessage.SEVERITY_INFO, "Deleted", safeName + " was deleted.");
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "Not found", safeName + " does not exist.");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Delete failed", e.getMessage());
        }

        loadDocuments();
    }

    public void deleteSelectedDocument() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Delete failed", "No request context available.");
            return;
        }

        String docName = safeTrim(pendingDeleteDocName);
        if (docName.isEmpty()) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            docName = safeTrim(params.get("docNameToDelete"));
            if (docName.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (entry.getKey() != null && entry.getKey().endsWith("docNameToDelete")) {
                        docName = safeTrim(entry.getValue());
                        break;
                    }
                }
            }
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

    // Getters and Setters
    public List<Document> getDocuments() {
        loadDocuments();
        if (selectedSubject == null || selectedSubject.trim().isEmpty()) {
            return documents;
        }
        return documents.stream()
                .filter(d -> selectedSubject.equals(d.getSubject()))
                .collect(Collectors.toList());
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

    public String getSetupMessage() {
        return setupMessage;
    }

    public List<String> getSubjects() {
        if (subjects == null) {
            subjects = new ArrayList<>();
        }
        return subjects;
    }

    public String getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(String selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    public boolean isConfigured() {
        return configService != null && configService.isPathConfigured();
    }

    public String getPendingDeleteDocName() {
        return pendingDeleteDocName;
    }

    public void setPendingDeleteDocName(String pendingDeleteDocName) {
        this.pendingDeleteDocName = pendingDeleteDocName;
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
            return "indexed".equals(status) || "uploaded".equals(status);
        }
    }
}
