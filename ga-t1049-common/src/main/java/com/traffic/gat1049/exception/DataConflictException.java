package com.traffic.gat1049.exception;

/**
 * 数据冲突异常
 */
public class DataConflictException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private String conflictReason;

    public DataConflictException(String message) {
        super("DATA_CONFLICT", message);
    }

    public DataConflictException(String message, String conflictReason) {
        super("DATA_CONFLICT", message);
        this.conflictReason = conflictReason;
    }

    public String getConflictReason() {
        return conflictReason;
    }
}
