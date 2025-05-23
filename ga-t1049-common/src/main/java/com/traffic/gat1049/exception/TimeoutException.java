package com.traffic.gat1049.exception;

/**
 * 超时异常
 */
public class TimeoutException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private long timeoutMillis;
    private String operation;

    public TimeoutException(String message) {
        super("TIMEOUT_ERROR", message);
    }

    public TimeoutException(String operation, long timeoutMillis) {
        super("TIMEOUT_ERROR", String.format("Operation '%s' timed out after %d ms", operation, timeoutMillis));
        this.operation = operation;
        this.timeoutMillis = timeoutMillis;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public String getOperation() {
        return operation;
    }
}
