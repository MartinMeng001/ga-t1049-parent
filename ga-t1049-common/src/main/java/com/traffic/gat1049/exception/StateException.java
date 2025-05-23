package com.traffic.gat1049.exception;

/**
 * 状态异常
 */
public class StateException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private String currentState;
    private String expectedState;

    public StateException(String message) {
        super("STATE_ERROR", message);
    }

    public StateException(String currentState, String expectedState, String message) {
        super("STATE_ERROR", message);
        this.currentState = currentState;
        this.expectedState = expectedState;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getExpectedState() {
        return expectedState;
    }
}
