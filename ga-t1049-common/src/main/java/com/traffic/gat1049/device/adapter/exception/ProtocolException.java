package com.traffic.gat1049.device.adapter.exception;

/**
 * 协议异常
 */
public class ProtocolException extends AdapterException {

    private static final long serialVersionUID = 1L;

    public ProtocolException(String message) {
        super("PROTOCOL_ERROR", message);
    }

    public ProtocolException(String message, Throwable cause) {
        super("PROTOCOL_ERROR", message, cause);
    }

    public ProtocolException(String deviceId, String message) {
        super(deviceId, "PROTOCOL_ERROR", message);
    }

    public ProtocolException(String deviceId, String message, Throwable cause) {
        super(deviceId, "PROTOCOL_ERROR", message, cause);
    }
}
