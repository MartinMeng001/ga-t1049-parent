package com.traffic.gat1049.exception;

/**
 * 通信异常
 */
public class CommunicationException extends GatProtocolException {

    private static final long serialVersionUID = 1L;

    private String remoteAddress;
    private int iErrorCode;

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
        this.iErrorCode = errorCode;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public int getIErrorCode() {
        return iErrorCode;
    }
}
