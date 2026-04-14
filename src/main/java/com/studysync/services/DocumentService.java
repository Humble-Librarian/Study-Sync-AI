package com.studysync.services;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "documentService")
@ApplicationScoped
public class DocumentService implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{configService}")
    private ConfigService configService;

    private String jdbcUrl;
    private String dbUser;
    private String dbPassword;

    @PostConstruct
    public void init() {
        if (configService == null) {
            System.err.println("[DocumentService] ConfigService not injected!");
            return;
        }
        this.jdbcUrl = configService.getDbUrl();
        this.dbUser = configService.getDbUser();
        this.dbPassword = configService.getDbPassword();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ensureTableExists();
        } catch (ClassNotFoundException e) {
            System.err.println("[DocumentService] Driver not found: " + e.getMessage());
        }
    }

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS documents (" +
                     "  id            INT AUTO_INCREMENT PRIMARY KEY," +
                     "  user_id       INT NOT NULL," +
                     "  name          VARCHAR(255) NOT NULL," +
                     "  subject       VARCHAR(100)," +
                     "  status        VARCHAR(50) DEFAULT 'uploaded'," +
                     "  upload_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                     "  UNIQUE KEY user_doc (user_id, name)" +
                     ")";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (Exception e) {
            System.err.println("[DocumentService] Could not ensure documents table: " + e.getMessage());
        }
    }

    public List<DbDocument> getDocumentsForUser(int userId) {
        List<DbDocument> docs = new ArrayList<>();
        String sql = "SELECT * FROM documents WHERE user_id = ? ORDER BY upload_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    docs.add(new DbDocument(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("subject"),
                        rs.getString("status"),
                        rs.getTimestamp("upload_date")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("[DocumentService] Error fetching docs: " + e.getMessage());
        }
        return docs;
    }

    public boolean addDocument(int userId, String name, String subject) {
        String sql = "INSERT INTO documents (user_id, name, subject) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE subject = VALUES(subject), upload_date = CURRENT_TIMESTAMP";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, subject);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[DocumentService] Error adding doc: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDocument(int userId, String name) {
        String sql = "DELETE FROM documents WHERE user_id = ? AND name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[DocumentService] Error deleting doc: " + e.getMessage());
            return false;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }

    // Bean Getters/Setters
    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public static class DbDocument {
        public int id;
        public int userId;
        public String name;
        public String subject;
        public String status;
        public Timestamp uploadDate;

        public DbDocument(int id, int userId, String name, String subject, String status, Timestamp uploadDate) {
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.subject = subject;
            this.status = status;
            this.uploadDate = uploadDate;
        }
    }
}
