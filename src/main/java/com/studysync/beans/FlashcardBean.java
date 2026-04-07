package com.studysync.beans;

import com.studysync.services.ConfigService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

@ManagedBean(name = "flashcardBean")
@ViewScoped
public class FlashcardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{configService}")
    private ConfigService configService;

    @ManagedProperty(value = "#{dashboardBean}")
    private DashboardBean dashboardBean;

    private List<Flashcard> flashcards = new ArrayList<>();
    private String documentName = "";
    private int documentId;
    private boolean loading = false;

    @PostConstruct
    public void init() {
        refreshDocumentSelectionFromRequest();
        generateFlashcards();
    }

    public void generateFlashcards() {
        loading = true;
        flashcards.clear();

        if (documentName == null || documentName.trim().isEmpty()) {
            flashcards.add(new Flashcard(
                    1,
                    "No document selected",
                    "Please open flashcards from a specific document in Dashboard.",
                    "easy"
            ));
            loading = false;
            return;
        }

        try {
            List<PageChunk> pages = readSourcePages(documentName);
            if (pages.isEmpty()) {
                flashcards.add(new Flashcard(
                        1,
                        "How to generate cards for " + documentName + "?",
                        "No extractable text was found for this document yet. Try re-uploading a text-based PDF (not image-only scan).",
                        "easy"
                ));
                loading = false;
                return;
            }

            int cardId = 1;
            int limit = Math.min(8, pages.size());
            for (int i = 0; i < limit; i++) {
                PageChunk chunk = pages.get(i);
                String answer = summarize(chunk.text, 280);
                if (answer.isEmpty()) {
                    continue;
                }

                String question = buildQuestion(documentName, chunk.page, chunk.text);
                String difficulty = difficultyFor(answer.length(), chunk.page);
                flashcards.add(new Flashcard(cardId++, question, answer, difficulty));
            }

            if (flashcards.isEmpty()) {
                flashcards.add(new Flashcard(
                        1,
                        "No extractable content",
                        "We found the index file, but it has no usable text for flashcard generation.",
                        "medium"
                ));
            }
        } catch (Exception e) {
            flashcards.add(new Flashcard(
                    1,
                    "Flashcard generation failed",
                    "Could not generate flashcards for " + documentName + ": " + e.getMessage(),
                    "hard"
            ));
        } finally {
            loading = false;
        }
    }

    public void regenerate() {
        refreshDocumentSelectionFromRequest();
        generateFlashcards();
    }

    private void refreshDocumentSelectionFromRequest() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();

        String docFromParam = params.get("docName");
        if (docFromParam != null && !docFromParam.trim().isEmpty()) {
            documentName = docFromParam.trim();
            return;
        }

        String docIdParam = params.get("docId");
        if (docIdParam != null && !docIdParam.trim().isEmpty()) {
            try {
                documentId = Integer.parseInt(docIdParam.trim());
                if (dashboardBean != null) {
                    for (DashboardBean.Document doc : dashboardBean.getDocuments()) {
                        if (doc.getId() == documentId) {
                            documentName = doc.getName();
                            break;
                        }
                    }
                }
            } catch (NumberFormatException ignored) {
                // keep existing selection
            }
        }
    }

    private List<PageChunk> readSourcePages(String docName) throws Exception {
        List<PageChunk> fromIndex = readIndexedPages(docName);
        if (!fromIndex.isEmpty()) {
            return fromIndex;
        }
        return readPdfPages(docName);
    }

    private List<PageChunk> readIndexedPages(String docName) throws Exception {
        List<PageChunk> pages = new ArrayList<>();
        if (configService == null || !configService.isPathConfigured()) {
            return pages;
        }

        String base = removeExtension(docName);
        Path indexPath = Paths.get(configService.getIndicesDir(), base + ".index.json");
        if (!Files.exists(indexPath)) {
            return pages;
        }

        String json = Files.readString(indexPath, StandardCharsets.UTF_8);
        JSONArray arr = new JSONArray(json);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.optJSONObject(i);
            if (obj == null) {
                continue;
            }
            int page = obj.optInt("page", i + 1);
            String text = obj.optString("text", "").trim();
            if (!text.isEmpty()) {
                pages.add(new PageChunk(page, text));
            }
        }
        return pages;
    }

    private List<PageChunk> readPdfPages(String docName) throws Exception {
        List<PageChunk> pages = new ArrayList<>();
        if (configService == null || !configService.isPathConfigured()) {
            return pages;
        }

        Path pdfPath = Paths.get(configService.getPdfsDir(), docName);
        if (!Files.exists(pdfPath)) {
            return pages;
        }

        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            int pageCount = Math.min(document.getNumberOfPages(), 20);
            PDFTextStripper stripper = new PDFTextStripper();

            for (int page = 1; page <= pageCount; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String text = stripper.getText(document);
                if (text != null) {
                    text = text.replaceAll("\\s+", " ").trim();
                    if (!text.isEmpty()) {
                        pages.add(new PageChunk(page, text));
                    }
                }
            }
        }
        return pages;
    }

    private String buildQuestion(String docName, int page, String text) {
        String firstSentence = firstSentence(text);
        if (firstSentence.isEmpty()) {
            return "What is discussed on page " + page + " of " + docName + "?";
        }

        String seed = summarize(firstSentence, 80);
        return "Explain this idea from page " + page + " of " + docName + ": \"" + seed + "\"";
    }

    private String firstSentence(String text) {
        String normalized = text.replace('\n', ' ').trim();
        if (normalized.isEmpty()) {
            return "";
        }
        int idx = normalized.indexOf('.');
        if (idx > 0) {
            return normalized.substring(0, idx + 1).trim();
        }
        return normalized;
    }

    private String summarize(String text, int maxLen) {
        String normalized = text.replaceAll("\\s+", " ").trim();
        if (normalized.isEmpty()) {
            return "";
        }
        if (normalized.length() <= maxLen) {
            return normalized;
        }
        return normalized.substring(0, maxLen).trim() + "...";
    }

    private String difficultyFor(int textLength, int page) {
        if (textLength < 130 || page % 3 == 1) {
            return "easy";
        }
        if (textLength < 220 || page % 3 == 2) {
            return "medium";
        }
        return "hard";
    }

    private String removeExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex <= 0) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }

    // Getters and Setters
    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public DashboardBean getDashboardBean() {
        return dashboardBean;
    }

    public void setDashboardBean(DashboardBean dashboardBean) {
        this.dashboardBean = dashboardBean;
    }

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

    private static class PageChunk {
        private final int page;
        private final String text;

        private PageChunk(int page, String text) {
            this.page = page;
            this.text = text;
        }
    }

    // Inner Flashcard class
    public static class Flashcard implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int id;
        private final String question;
        private final String answer;
        private final String difficulty;

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
