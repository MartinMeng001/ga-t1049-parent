package com.traffic.gat1049.exception;

/**
 * 数据验证异常
 */
public class ValidationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private String fieldName;
    private Object fieldValue;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }

    public ValidationException(String fieldName, String message) {
        super("FIELD_VALIDATION_ERROR", message);
        this.fieldName = fieldName;
    }

    public ValidationException(String fieldName, Object fieldValue, String message) {
        super("FIELD_VALUE_ERROR", message);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
