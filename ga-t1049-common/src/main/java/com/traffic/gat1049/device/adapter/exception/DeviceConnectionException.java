package com.traffic.gat1049.device.adapter.exception;

/**
 * 设备连接异常
 */
public class DeviceConnectionException extends AdapterException {

    private static final long serialVersionUID = 1L;

    public DeviceConnectionException(String message) {
        super("CONNECTION_ERROR", message);
    }

    public DeviceConnectionException(String message, Throwable cause) {
        super("CONNECTION_ERROR", message, cause);
    }

    public DeviceConnectionException(String deviceId, String message) {
        super(deviceId, "CONNECTION_ERROR", message);
    }

    public DeviceConnectionException(String deviceId, String message, Throwable cause) {
        super(deviceId, "CONNECTION_ERROR", message, cause);
    }
}
