package com.studysync.beans;

import com.studysync.services.RagService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "chatBean")
@ViewScoped
public class ChatBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{ragService}")
    private RagService ragService;

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
            JSONObject result = ragService.queryRag(docName, userQuery.trim(), topK, llmChoice);
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

            pages.add(new SourcePage(
                    item.optInt("page", 0),
                    item.optString("text", ""),
                    item.optDouble("score", 0.0)
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

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
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

    public List<SourcePage> getSourcePages() {
        return sourcePages;
    }

    public static class SourcePage implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int page;
        private final String text;
        private final double score;

        public SourcePage(int page, String text, double score) {
            this.page = page;
            this.text = text;
            this.score = score;
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
    }
}
