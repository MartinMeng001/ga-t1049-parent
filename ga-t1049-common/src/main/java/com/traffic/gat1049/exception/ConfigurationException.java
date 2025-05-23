package com.traffic.gat1049.exception;

/**
 * 配置异常
 */
public class ConfigurationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private String configKey;
    private String configValue;

    public ConfigurationException(String message) {
        super("CONFIGURATION_ERROR", message);
    }

    public ConfigurationException(String configKey, String message) {
        super("CONFIGURATION_ERROR", message);
        this.configKey = configKey;
    }

    public ConfigurationException(String configKey, String configValue, String message) {
        super("CONFIGURATION_ERROR", message);
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getConfigValue() {
        return configValue;
    }
}
