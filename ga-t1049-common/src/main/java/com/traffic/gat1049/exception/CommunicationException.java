package com.traffic.gat1049.exception;

import com.traffic.gat1049.protocol.exception.GatProtocolException;

/**
 * 通信异常
 */
public class CommunicationException extends GatProtocolException {

    private static final long serialVersionUID = 1L;

    private String remoteAddress;
    private int errorCode;

    public CommunicationException(String message) {
        super("COMMUNICATION_ERROR", message);
    }

    public CommunicationException(String message, Throwable cause) {
        super("COMMUNICATION_ERROR", message, cause);
    }

    public CommunicationException(String remoteAddress, String message) {
        super("COMMUNICATION_ERROR", message);
        this.remoteAddress = remoteAddress;
    }

    public CommunicationException(String remoteAddress, int errorCode, String message) {
        super("COMMUNICATION_ERROR", message);
        this.remoteAddress = remoteAddress;
        this.errorCode = errorCode;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
