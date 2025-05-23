package com.traffic.gat1049.exception;

/**
 * GA/T 1049协议异常基类
 */
public class GatProtocolException extends Exception {

    private static final long serialVersionUID = 1L;

    private String errorCode;
    private String errorMessage;

    public GatProtocolException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public GatProtocolException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public GatProtocolException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

    public GatProtocolException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
