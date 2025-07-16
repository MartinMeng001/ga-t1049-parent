package com.traffic.gat1049.device.adapter.exception;

/**
 * 适配器异常基类
 */
public class AdapterException extends Exception {

    private static final long serialVersionUID = 1L;

    private String errorCode;
    private String deviceId;

    public AdapterException(String message) {
        super(message);
    }

    public AdapterException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdapterException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AdapterException(String deviceId, String errorCode, String message) {
        super(message);
        this.deviceId = deviceId;
        this.errorCode = errorCode;
    }

    public AdapterException(String deviceId, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.deviceId = deviceId;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
