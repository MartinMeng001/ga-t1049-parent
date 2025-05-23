package com.traffic.gat1049.exception;

/**
 * 数据未找到异常
 */
public class DataNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private String entityType;
    private String entityId;

    public DataNotFoundException(String message) {
        super("DATA_NOT_FOUND", message);
    }

    public DataNotFoundException(String entityType, String entityId) {
        super("ENTITY_NOT_FOUND", String.format("%s with ID '%s' not found", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public DataNotFoundException(String entityType, String entityId, String message) {
        super("ENTITY_NOT_FOUND", message);
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }
}
