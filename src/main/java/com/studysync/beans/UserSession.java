package com.studysync.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "userSession")
@SessionScoped
public class UserSession implements Serializable {

    private Integer userId;
    private String username;
    private String selectedSubject;

    public boolean isLoggedIn() {
        return userId != null;
    }

    public void login(String username, int userId) {
        this.username = username;
        this.userId = userId;
    }

    public void logout() {
        this.userId = null;
        this.username = null;
        this.selectedSubject = null;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(String selectedSubject) {
        this.selectedSubject = selectedSubject;
    }
}
