package com.studysync.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.Part;

@ManagedBean(name = "uploadBean")
@RequestScoped
public class UploadBean {

    private Part file;

    public String upload() {
        if (file != null) {
            // Mock upload - just log the filename
            String fileName = file.getSubmittedFileName();
            System.out.println("Uploaded file: " + fileName);
            return null; // Stay on page
        }
        return null;
    }

    // Getters and Setters
    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }
}
