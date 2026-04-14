package com.studysync.services;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

@ManagedBean(name = "userService", eager = true)
@ApplicationScoped
public class UserService {

    @ManagedProperty(value = "#{configService}")
    private ConfigService configService;

    @PostConstruct
    public void init() {
        System.out.println("[UserService] SQL Auth is ENABLED with Self-Healing Bootstrap.");
    }

    // ── Public API ──────────────────────────────────────────────────────────────

    /**
     * Registers a new user. Returns null on success, or an error message string.
     */
    public String register(String username, String rawPassword) {
        if (isBlank(username) || isBlank(rawPassword)) {
            return "Username and password are required.";
        }
        String hash = sha256(rawPassword);
        String sql  = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, hash);
            ps.executeUpdate();
            return null; // success
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Username \"" + username.trim() + "\" is already taken.";
        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }
    }

    /**
     * Authenticates a user. Returns the user's DB id on success, or -1 on failure.
     */
    public int authenticate(String username, String rawPassword) {
        if (isBlank(username) || isBlank(rawPassword)) return -1;
        String hash = sha256(rawPassword);
        String sql  = "SELECT id FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, hash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (Exception e) {
            // log but don't expose internals
            System.err.println("[UserService] Authentication error: " + e.getMessage());
        }
        return -1;
    }

    // ── Internal helpers ─────────────────────────────────────────────────────────

    private Connection getConnection() throws Exception {
        return configService.openConnection();
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 unavailable", e);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // JSF Getters/Setters
    public ConfigService getConfigService() { return configService; }
    public void setConfigService(ConfigService configService) { this.configService = configService; }
}
