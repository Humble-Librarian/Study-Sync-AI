package com.studysync.beans;

import com.studysync.services.ConfigService;

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

    @ManagedProperty(value = "#{configService}")
    private ConfigService configService;

    private String username;
    private String password;

    public String login() {
        // Mock authentication - accept any non-empty credentials
        if (username != null && !username.trim().isEmpty()
                && password != null && !password.trim().isEmpty()) {

            userSession.login(username, username.hashCode());

            // First-time flow: force setup until shared path is configured.
            if (configService == null || !configService.isPathConfigured()) {
                return "/pages/setup.xhtml?faces-redirect=true";
            }
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

    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
