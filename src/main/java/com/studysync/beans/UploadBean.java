package com.studysync.beans;

import com.studysync.services.ConfigService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@ManagedBean(name = "uploadBean")
@RequestScoped
public class UploadBean {

    private static final long MAX_FILE_SIZE_BYTES = 20L * 1024L * 1024L;

    @ManagedProperty(value = "#{configService}")
    private ConfigService configService;

    @ManagedProperty(value = "#{dashboardBean}")
    private DashboardBean dashboardBean;

    private Part file;

    public String upload() {
        if (file == null || file.getSize() <= 0) {
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Upload failed", "Please choose a PDF file.");
            return null;
        }

        String fileName = safeFileName(file.getSubmittedFileName());
        if (fileName.isEmpty()) {
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Upload failed", "Invalid file name.");
            return null;
        }

        if (!fileName.toLowerCase().endsWith(".pdf")) {
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Upload failed", "Only PDF files are supported.");
            return null;
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Upload failed", "File exceeds 20MB limit.");
            return null;
        }

        if (configService == null || !configService.isPathConfigured()) {
            return "/pages/setup.xhtml?faces-redirect=true";
        }

        try {
            Path pdfsPath = Paths.get(configService.resolveSharedDataDir(), "pdfs");
            Files.createDirectories(pdfsPath);

            Path destination = pdfsPath.resolve(fileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            if (dashboardBean != null) {
                dashboardBean.loadDocuments();
            }

            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Upload complete", fileName + " uploaded successfully.");
        } catch (Exception e) {
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Upload failed", e.getMessage());
        }

        return null; // stay on page
    }

    private String safeFileName(String submittedFileName) {
        if (submittedFileName == null) {
            return "";
        }
        try {
            return Paths.get(submittedFileName).getFileName().toString().trim();
        } catch (Exception e) {
            return submittedFileName.trim();
        }
    }

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
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

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }
}
