package com.studysync.beans;

import com.studysync.services.ConfigService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "setupBean")
@RequestScoped
public class SetupBean {

    @ManagedProperty(value = "#{configService}")
    private ConfigService configService;

    private String userPath;
    private String groqApiKey;
    private String nimApiKey;
    private String errorMessage;
    private String successMessage;

    @PostConstruct
    public void init() {
        if (configService == null) {
            return;
        }

        if (configService.isSetupComplete()) {
            redirectToDashboard();
            return;
        }

        userPath = configService.getDefaultDataDir();
        groqApiKey = configService.getGroqApiKey();
        nimApiKey = configService.getNimApiKey();
    }

    public String saveSharedPath() {
        String normalized = userPath == null ? "" : userPath.trim();
        if (normalized.isEmpty()) {
            errorMessage = "Please enter a valid folder path.";
            successMessage = "";
            return null;
        }

        try {
            configService.saveSharedDataDir(normalized);
            configService.saveApiKeys(groqApiKey, nimApiKey);
            successMessage = "Path and API keys saved successfully.";
            if (!configService.getLastWarningMessage().isEmpty()) {
                errorMessage = configService.getLastWarningMessage();
            } else {
                errorMessage = "";
            }
            return "/pages/dashboard.xhtml?faces-redirect=true";
        } catch (Exception e) {
            errorMessage = "Could not save settings: " + e.getMessage();
            successMessage = "";
            return null;
        }
    }

    private void redirectToDashboard() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return;
        }

        try {
            String contextPath = context.getExternalContext().getRequestContextPath();
            context.getExternalContext().redirect(contextPath + "/pages/dashboard.xhtml");
            context.responseComplete();
        } catch (Exception e) {
            errorMessage = "Could not redirect to dashboard: " + e.getMessage();
        }
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getGroqApiKey() {
        return groqApiKey;
    }

    public void setGroqApiKey(String groqApiKey) {
        this.groqApiKey = groqApiKey;
    }

    public String getNimApiKey() {
        return nimApiKey;
    }

    public void setNimApiKey(String nimApiKey) {
        this.nimApiKey = nimApiKey;
    }
}
