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
            ensureTableExists(connection);
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
            ensureTableExists(connection);
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

    private void ensureTableExists(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS app_config (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "config_key VARCHAR(100) UNIQUE NOT NULL," +
                "config_value VARCHAR(500) NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private Connection openConnection() throws Exception {
        String dbUrl = getDbUrl();
        String dbUser = getDbUser();
        String dbPassword = getDbPassword();

        if (dbUrl.isEmpty()) {
            throw new IllegalStateException("studysync.db.url is empty");
        }

        // Load MySQL driver when available on classpath.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
            // Driver may still auto-register depending on runtime.
        }

        if (dbUser.isEmpty()) {
            return DriverManager.getConnection(dbUrl);
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
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

    private String getDbUrl() {
        return safeTrim(getContextParam(CONTEXT_DB_URL));
    }

    private String getDbUser() {
        return safeTrim(getContextParam(CONTEXT_DB_USER));
    }

    private String getDbPassword() {
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
