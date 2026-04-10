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

    private String jdbcUrl;
    private String dbUser;
    private String dbPassword;

    @PostConstruct
    public void init() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            javax.faces.context.ExternalContext ext = ctx.getExternalContext();
            String url  = ext.getInitParameter("studysync.db.url");
            String user = ext.getInitParameter("studysync.db.user");
            String pass = ext.getInitParameter("studysync.db.password");
            jdbcUrl     = (url  != null && !url.trim().isEmpty())  ? url.trim()  : "jdbc:mysql://localhost:3306/StudySync?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            dbUser      = (user != null && !user.trim().isEmpty()) ? user.trim() : "root";
            dbPassword  = (pass != null)                           ? pass        : "";
        } else {
            jdbcUrl    = "jdbc:mysql://localhost:3306/StudySync?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            dbUser     = "root";
            dbPassword = "";
        }
        ensureTableExists();
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
        }
        return -1;
    }

    // ── Internal helpers ─────────────────────────────────────────────────────────

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "  id            INT AUTO_INCREMENT PRIMARY KEY," +
                     "  username      VARCHAR(64) NOT NULL UNIQUE," +
                     "  password_hash VARCHAR(64) NOT NULL," +
                     "  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (Exception e) {
            System.err.println("[UserService] Could not ensure users table: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
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
}
