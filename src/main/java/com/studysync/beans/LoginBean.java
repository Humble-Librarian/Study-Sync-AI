package com.studysync.beans;

import com.studysync.services.ConfigService;
import com.studysync.services.UserService;

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

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private String username;
    private String password;
    private String confirmPassword;

    public String login() {
        if (isBlank(username) || isBlank(password)) {
            addError("Please enter your username and password.");
            return null;
        }

        int userId = userService.authenticate(username, password);
        if (userId > 0) {
            userSession.login(username.trim(), userId);
            if (configService == null || !configService.isPathConfigured()) {
                return "/pages/setup.xhtml?faces-redirect=true";
            }
            return "/pages/dashboard.xhtml?faces-redirect=true";
        }

        addError("Invalid username or password.");
        return null;
    }

    public String register() {
        if (isBlank(username) || isBlank(password)) {
            addError("Username and password are required.");
            return null;
        }
        if (!password.equals(confirmPassword)) {
            addError("Passwords do not match.");
            return null;
        }
        if (password.length() < 6) {
            addError("Password must be at least 6 characters.");
            return null;
        }

        String error = userService.register(username, password);
        if (error != null) {
            addError(error);
            return null;
        }

        // Auto-login after successful registration
        int userId = userService.authenticate(username, password);
        userSession.login(username.trim(), userId);
        if (configService == null || !configService.isPathConfigured()) {
            return "/pages/setup.xhtml?faces-redirect=true";
        }
        return "/pages/dashboard.xhtml?faces-redirect=true";
    }

    public String logout() {
        userSession.logout();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/pages/login.xhtml?faces-redirect=true";
    }

    private void addError(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public UserSession getUserSession() { return userSession; }
    public void setUserSession(UserSession userSession) { this.userSession = userSession; }

    public ConfigService getConfigService() { return configService; }
    public void setConfigService(ConfigService configService) { this.configService = configService; }

    public UserService getUserService() { return userService; }
    public void setUserService(UserService userService) { this.userService = userService; }
}

