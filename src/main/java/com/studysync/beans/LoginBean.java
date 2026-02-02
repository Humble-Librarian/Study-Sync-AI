package com.studysync.beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "loginBean")
@RequestScoped
public class LoginBean {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private String username;
    private String password;

    public String login() {
        // Mock authentication - accept any non-empty credentials
        if (username != null && !username.trim().isEmpty()
                && password != null && !password.trim().isEmpty()) {

            userSession.login(username, username.hashCode());
            return "/pages/dashboard.xhtml?faces-redirect=true";
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Invalid credentials", "Please enter username and password"));
        return null;
    }

    public String logout() {
        userSession.logout();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/pages/login.xhtml?faces-redirect=true";
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
