package com.studysync.beans;

import com.studysync.services.RagService;
import com.studysync.services.ConfigService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "chatBean")
@ViewScoped
public class ChatBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{ragService}")
    private RagService ragService;

    @ManagedProperty(value = "#{configService}")
    private ConfigService configService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private String docName;
    private String userQuery = "";
    private String answer = "";
    private String errorMessage = "";
    private boolean showSources = false;
    private String llmChoice = "groq";
    private int topK = 3;

    private List<SourcePage> sourcePages = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (isBlank(docName)) {
            String docFromParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("docName");
            docName = docFromParam == null ? "" : docFromParam.trim();
        }
    }

    public void submitQuery() {
        if (userSession == null || !userSession.isLoggedIn()) {
            errorMessage = "Please log in to use the chat.";
            return;
        }

        if (isBlank(docName)) {
            String docFromParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("docName");
            if (!isBlank(docFromParam)) {
                docName = docFromParam.trim();
            }
        }

        if (isBlank(docName)) {
            errorMessage = "Document name is missing. Open chat from dashboard.";
            answer = "";
            showSources = false;
            sourcePages.clear();
            return;
        }

        if (isBlank(userQuery)) {
            errorMessage = "Please enter a question.";
            answer = "";
            showSources = false;
            sourcePages.clear();
            return;
        }

        try {
            // Apply user isolation prefix for the backend
            String namespacedDoc = userSession.getUserId() + "/" + docName;
            
            JSONObject result = ragService.queryRag(namespacedDoc, userQuery.trim(), topK, llmChoice);
            if (result.optBoolean("success", false)) {
                answer = result.optString("answer", "");
                errorMessage = "";
                sourcePages = parseSourcePages(result.optJSONArray("source_pages"));
                showSources = !sourcePages.isEmpty();
            } else {
                errorMessage = result.optString("error", "Unknown error from RAG backend.");
                answer = "";
                showSources = false;
                sourcePages.clear();
            }
        } catch (Exception e) {
            errorMessage = "Error connecting to RAG backend: " + e.getMessage();
            answer = "";
            showSources = false;
            sourcePages.clear();
        }
    }

    private List<SourcePage> parseSourcePages(JSONArray jsonArray) {
        List<SourcePage> pages = new ArrayList<>();
        if (jsonArray == null) {
            return pages;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.optJSONObject(i);
            if (item == null) {
                continue;
            }

            List<String> images = new ArrayList<>();
            JSONArray imagesArray = item.optJSONArray("images");
            if (imagesArray != null) {
                for (int j = 0; j < imagesArray.length(); j++) {
                    images.add(imagesArray.optString(j));
                }
            }

            pages.add(new SourcePage(
                    item.optInt("page", 0),
                    item.optString("text", ""),
                    item.optDouble("score", 0.0),
                    images
            ));
        }
        return pages;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public RagService getRagService() {
        return ragService;
    }

    public void setRagService(RagService ragService) {
        this.ragService = ragService;
    }

    public com.studysync.services.ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getEncodedDocName() {
        if (isBlank(docName) || userSession == null) {
            return "";
        }
        try {
            // Prepend UserID for namespaced access in the viewer and local-pdf servlet
            String namespacedPath = userSession.getUserId() + "/" + docName;
            return URLEncoder.encode(namespacedPath, StandardCharsets.UTF_8.name())
                    .replace("+", "%20")
                    .replace("%2F", "/"); // Keep the slash so the servlet sees it as a path segment
        } catch (Exception e) {
            return docName;
        }
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isShowSources() {
        return showSources;
    }

    public void setShowSources(boolean showSources) {
        this.showSources = showSources;
    }

    public String getLlmChoice() {
        return llmChoice;
    }

    public void setLlmChoice(String llmChoice) {
        this.llmChoice = llmChoice;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public boolean hasImages() {
        if (sourcePages == null) return false;
        for (SourcePage sp : sourcePages) {
            if (sp.getImages() != null && !sp.getImages().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<SourcePage> getSourcePages() {
        return sourcePages;
    }

    public static class SourcePage implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int page;
        private final String text;
        private final double score;
        private final List<String> images;

        public SourcePage(int page, String text, double score, List<String> images) {
            this.page = page;
            this.text = text;
            this.score = score;
            this.images = images != null ? images : new ArrayList<>();
        }

        public int getPage() {
            return page;
        }

        public String getText() {
            return text;
        }

        public double getScore() {
            return score;
        }

        public List<String> getImages() {
            return images;
        }
    }
}
