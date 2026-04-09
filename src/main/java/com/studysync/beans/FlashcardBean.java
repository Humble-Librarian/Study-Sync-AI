package com.studysync.beans;

import com.studysync.services.ConfigService;
import com.studysync.services.RagService;
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

    @ManagedProperty(value = "#{ragService}")
    private RagService ragService;

    private List<Flashcard> flashcards = new ArrayList<>();
    private String documentName = "";
    private int documentId;
    private boolean loading = false;

    private int numEasy = 3;
    private int numMedium = 3;
    private int numHard = 2;

    @PostConstruct
    public void init() {
        refreshDocumentSelectionFromRequest();
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
            if (ragService == null) {
                flashcards.add(new Flashcard(1, "Service unavailable", "RagService not injected.", "hard"));
                return;
            }
            
            JSONArray jsonCards = ragService.generateFlashcards(documentName, "groq", numEasy, numMedium, numHard);
            if (jsonCards != null && jsonCards.length() > 0) {
                int cardId = 1;
                for (int i = 0; i < jsonCards.length(); i++) {
                    JSONObject obj = jsonCards.optJSONObject(i);
                    if (obj != null) {
                        String q = obj.optString("question", "No question");
                        String a = obj.optString("answer", "No answer");
                        String d = obj.optString("difficulty", "medium").toLowerCase();
                        flashcards.add(new Flashcard(cardId++, q, a, d));
                    }
                }
            } else {
                flashcards.add(new Flashcard(1, "No extractable content", "The backend returned an empty response.", "medium"));
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

    public RagService getRagService() {
        return ragService;
    }

    public void setRagService(RagService ragService) {
        this.ragService = ragService;
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

    public int getNumEasy() {
        return numEasy;
    }

    public void setNumEasy(int numEasy) {
        this.numEasy = numEasy;
    }

    public int getNumMedium() {
        return numMedium;
    }

    public void setNumMedium(int numMedium) {
        this.numMedium = numMedium;
    }

    public int getNumHard() {
        return numHard;
    }

    public void setNumHard(int numHard) {
        this.numHard = numHard;
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
