package com.studysync.services;

import org.json.JSONObject;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ManagedBean(name = "ragService", eager = true)
@ApplicationScoped
public class RagService {

    private static final String DEFAULT_BACKEND = "http://localhost:5000";

    @ManagedProperty(value = "#{configService}")
    private ConfigService configService;

    public JSONObject queryRag(String docName, String userQuery, int topK, String llmChoice) throws Exception {
        if (isBlank(docName) || isBlank(userQuery)) {
            return new JSONObject()
                    .put("success", false)
                    .put("error", "docName and userQuery are required.");
        }

        String backendUrl = resolveBackendUrl();
        String selectedLlm = isBlank(llmChoice) ? "groq" : llmChoice;
        String dataDir = resolveDataDir();

        String url = backendUrl + "/chat"
                + "?docName=" + URLEncoder.encode(docName, StandardCharsets.UTF_8.name())
                + "&query=" + URLEncoder.encode(userQuery, StandardCharsets.UTF_8.name())
                + "&top_k=" + topK
                + "&llm_choice=" + URLEncoder.encode(selectedLlm, StandardCharsets.UTF_8.name())
                + "&data_dir=" + URLEncoder.encode(dataDir, StandardCharsets.UTF_8.name());

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(30_000);
        connection.setReadTimeout(30_000);

        int responseCode = connection.getResponseCode();
        InputStream responseStream = responseCode >= 200 && responseCode < 300
                ? connection.getInputStream()
                : connection.getErrorStream();

        String body = readBody(responseStream);
        JSONObject json;
        try {
            json = new JSONObject(body);
        } catch (Exception parseError) {
            json = new JSONObject()
                    .put("success", false)
                    .put("error", "Invalid JSON response from Python backend.")
                    .put("raw", body);
        }

        if (!json.has("success")) {
            json.put("success", responseCode >= 200 && responseCode < 300);
        }

        if (responseCode >= 400 && !json.optBoolean("success", false)) {
            if (!json.has("error")) {
                json.put("error", "Python backend returned HTTP " + responseCode);
            }
        }

        return json;
    }

    private String resolveBackendUrl() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            String fromWebXml = context.getExternalContext().getInitParameter("studysync.python.backend.url");
            if (!isBlank(fromWebXml)) {
                return fromWebXml.trim();
            }
        }
        return DEFAULT_BACKEND;
    }

    private String resolveDataDir() {
        if (configService != null) {
            String configured = configService.resolveSharedDataDir();
            if (!isBlank(configured)) {
                return configured;
            }
        }
        return "";
    }

    private String readBody(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
