package com.traffic.gat1049.exception;

/**
 * 操作不支持异常
 */
public class OperationNotSupportedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private String operationType;
    private String objectType;

    public OperationNotSupportedException(String message) {
        super("OPERATION_NOT_SUPPORTED", message);
    }

    public OperationNotSupportedException(String operationType, String objectType) {
        super("OPERATION_NOT_SUPPORTED",
                String.format("Operation '%s' is not supported for object type '%s'", operationType, objectType));
        this.operationType = operationType;
        this.objectType = objectType;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getObjectType() {
        return objectType;
    }
}
