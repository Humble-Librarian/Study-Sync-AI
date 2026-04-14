package com.studysync.services;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@ManagedBean(name = "configService", eager = true)
@ApplicationScoped
public class ConfigService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SHARED_DIR_KEY = "shared_data_dir";
    private static final String GROQ_API_KEY_KEY = "groq_api_key";
    private static final String NIM_API_KEY_KEY = "nim_api_key";
    private static final String DEFAULT_SHARED_DIR = "C:/StudySync_Data";
    private static final String CONTEXT_SHARED_DIR = "studysync.data.dir";
    private static final String CONTEXT_DB_URL = "studysync.db.url";
    private static final String CONTEXT_DB_USER = "studysync.db.user";
    private static final String CONTEXT_DB_PASSWORD = "studysync.db.password";

    private transient volatile String cachedSharedDir;
    private volatile String lastWarningMessage = "";

    public synchronized String getSharedDataDir() {
        if (cachedSharedDir != null) {
            return cachedSharedDir;
        }

        String fromDb = readFromDatabase();
        if (!fromDb.isEmpty()) {
            cachedSharedDir = normalizePath(fromDb);
            return cachedSharedDir;
        }

        String fromFile = readFromLocalConfig();
        if (!fromFile.isEmpty()) {
            cachedSharedDir = normalizePath(fromFile);
            return cachedSharedDir;
        }

        cachedSharedDir = "";
        return "";
    }

    public String getGroqApiKey() {
        return readPropertyFromLocalConfig(GROQ_API_KEY_KEY);
    }

    public String getNimApiKey() {
        return readPropertyFromLocalConfig(NIM_API_KEY_KEY);
    }

    public synchronized void saveSharedDataDir(String dataDir) throws Exception {
        String normalizedInput = safeTrim(dataDir);
        if (normalizedInput.isEmpty()) {
            throw new IllegalArgumentException("Please enter a valid folder path.");
        }
        String normalized = normalizePath(normalizedInput);

        Files.createDirectories(Paths.get(normalized, "pdfs"));
        Files.createDirectories(Paths.get(normalized, "indices"));

        Exception dbError = null;
        if (!isBlank(getDbUrl())) {
            try {
                saveToDatabase(normalized);
            } catch (Exception e) {
                dbError = e;
            }
        }

        saveToLocalConfig(normalized);
        cachedSharedDir = normalized;

        if (dbError != null) {
            lastWarningMessage = "Path saved locally, but DB persistence failed: " + dbError.getMessage();
        } else {
            lastWarningMessage = "";
        }
    }

    public void saveApiKeys(String groqKey, String nimKey) throws Exception {
        savePropertyToLocalConfig(GROQ_API_KEY_KEY, safeTrim(groqKey));
        savePropertyToLocalConfig(NIM_API_KEY_KEY, safeTrim(nimKey));
    }

    public boolean isPathConfigured() {
        return !getSharedDataDir().isEmpty();
    }

    public boolean isSetupComplete() {
        boolean pathReady = isPathConfigured();
        boolean keysReady = !getGroqApiKey().isEmpty() || !getNimApiKey().isEmpty();
        return pathReady && keysReady;
    }

    public String resolveSharedDataDir() {
        String configured = getSharedDataDir();
        if (!configured.isEmpty()) {
            return configured;
        }
        return getDefaultDataDir();
    }

    public String getDefaultDataDir() {
        String contextValue = safeTrim(getContextParam(CONTEXT_SHARED_DIR));
        String candidate = contextValue.isEmpty() ? DEFAULT_SHARED_DIR : contextValue;
        return normalizePath(candidate);
    }

    public String getPdfsDir() {
        return Paths.get(resolveSharedDataDir(), "pdfs").toString();
    }

    public String getIndicesDir() {
        return Paths.get(resolveSharedDataDir(), "indices").toString();
    }

    public String getImagesDir() {
        return Paths.get(resolveSharedDataDir(), "images").toString();
    }

    public String getLastWarningMessage() {
        return lastWarningMessage;
    }

    public void clearLastWarningMessage() {
        lastWarningMessage = "";
    }

    private String readFromDatabase() {
        String dbUrl = getDbUrl();
        if (dbUrl.isEmpty()) {
            return "";
        }

        try (Connection connection = openConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT config_value FROM app_config WHERE config_key = ? LIMIT 1")) {
                ps.setString(1, SHARED_DIR_KEY);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return safeTrim(rs.getString(1));
                    }
                }
            }
        } catch (Exception ignored) {
            // Fallback to local config when DB is unavailable.
        }

        return "";
    }

    private void saveToDatabase(String value) throws Exception {
        try (Connection connection = openConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO app_config(config_key, config_value) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE config_value = VALUES(config_value), " +
                            "updated_at = CURRENT_TIMESTAMP")) {
                ps.setString(1, SHARED_DIR_KEY);
                ps.setString(2, value);
                ps.executeUpdate();
            }
        }
    }

    private Connection openConnection() throws Exception {
        String dbUrl = getDbUrl();
        String dbUser = getDbUser();
        String dbPassword = getDbPassword();

        if (dbUrl.isEmpty()) {
            throw new IllegalStateException("studysync.db.url is empty");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {}

        try {
            Connection conn = (dbUser.isEmpty()) ? 
                DriverManager.getConnection(dbUrl) : 
                DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            
            // If connection success, ensure all tables exist
            ensureAllTablesExist(conn);
            return conn;
        } catch (SQLException e) {
            // Error code 1049 is "Unknown database"
            if (e.getErrorCode() == 1049) {
                System.out.println("[ConfigService] Database not found. Bootstrapping...");
                bootstrapDatabase();
                // Retry connection once after bootstrap
                Connection conn = (dbUser.isEmpty()) ? 
                    DriverManager.getConnection(dbUrl) : 
                    DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                ensureAllTablesExist(conn);
                return conn;
            }
            throw e;
        }
    }

    private void bootstrapDatabase() throws Exception {
        String fullUrl = getDbUrl();
        String dbUser = getDbUser();
        String dbPassword = getDbPassword();
        
        // Extract server root URL (e.g., jdbc:mysql://localhost:3306/) and db name
        int lastSlash = fullUrl.lastIndexOf("/");
        int queryParam = fullUrl.indexOf("?");
        String baseUrl = fullUrl.substring(0, lastSlash + 1);
        if (queryParam != -1) {
            baseUrl += fullUrl.substring(queryParam);
        }
        
        String dbName = fullUrl.substring(lastSlash + 1);
        if (queryParam != -1) {
            dbName = dbName.substring(0, dbName.indexOf("?"));
        }

        System.out.println("[ConfigService] Connecting to " + baseUrl + " to create " + dbName);

        try (Connection conn = dbUser.isEmpty() ? 
                DriverManager.getConnection(baseUrl) : 
                DriverManager.getConnection(baseUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
            System.out.println("[ConfigService] Database '" + dbName + "' created successfully.");
        }
    }

    public void ensureAllTablesExist(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // 1. app_config
            stmt.execute("CREATE TABLE IF NOT EXISTS app_config (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "config_key VARCHAR(100) UNIQUE NOT NULL," +
                "config_value VARCHAR(500) NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")");

            // 2. users
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "  id            INT AUTO_INCREMENT PRIMARY KEY," +
                "  username      VARCHAR(64) NOT NULL UNIQUE," +
                "  password_hash VARCHAR(64) NOT NULL," +
                "  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            // 3. documents
            stmt.execute("CREATE TABLE IF NOT EXISTS documents (" +
                "  id            INT AUTO_INCREMENT PRIMARY KEY," +
                "  user_id       INT NOT NULL," +
                "  name          VARCHAR(255) NOT NULL," +
                "  subject       VARCHAR(100)," +
                "  status        VARCHAR(50) DEFAULT 'uploaded'," +
                "  upload_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  UNIQUE KEY user_doc (user_id, name)" +
                ")");
        }
    }

    private String readFromLocalConfig() {
        return readPropertyFromLocalConfig(SHARED_DIR_KEY);
    }

    private String readPropertyFromLocalConfig(String propKey) {
        Path cfg = localConfigPath();
        if (!Files.exists(cfg)) {
            return "";
        }

        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(cfg)) {
            properties.load(in);
            return safeTrim(properties.getProperty(propKey, ""));
        } catch (IOException ignored) {
            return "";
        }
    }

    private void saveToLocalConfig(String value) throws IOException {
        savePropertyToLocalConfig(SHARED_DIR_KEY, value);
    }

    private synchronized void savePropertyToLocalConfig(String key, String value) throws IOException {
        Path cfg = localConfigPath();
        Files.createDirectories(cfg.getParent());

        Properties properties = new Properties();
        if (Files.exists(cfg)) {
            try (InputStream in = Files.newInputStream(cfg)) {
                properties.load(in);
            }
        }

        if (value.isEmpty()) {
            properties.remove(key);
        } else {
            properties.setProperty(key, value);
        }
        
        try (OutputStream out = Files.newOutputStream(cfg)) {
            properties.store(out, "Study Sync AI configuration");
        }
    }

    private Path localConfigPath() {
        return Paths.get(System.getProperty("user.home"), ".studysync", "app-config.properties");
    }

    public String getDbUrl() {
        return safeTrim(getContextParam(CONTEXT_DB_URL));
    }

    public String getDbUser() {
        return safeTrim(getContextParam(CONTEXT_DB_USER));
    }

    public String getDbPassword() {
        return safeTrim(getContextParam(CONTEXT_DB_PASSWORD));
    }

    private String getContextParam(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            String value = context.getExternalContext().getInitParameter(key);
            if (value != null) {
                return value;
            }
        }

        // Environment-variable fallback for non-Faces execution contexts.
        return System.getenv(key.toUpperCase().replace('.', '_'));
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizePath(String value) {
        String trimmed = safeTrim(value);
        if (trimmed.isEmpty()) {
            return "";
        }

        try {
            return Paths.get(trimmed).toAbsolutePath().normalize().toString();
        } catch (Exception e) {
            return trimmed;
        }
    }
}
