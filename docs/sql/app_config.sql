CREATE TABLE IF NOT EXISTS app_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO app_config (config_key, config_value)
VALUES ('shared_data_dir', '')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);
