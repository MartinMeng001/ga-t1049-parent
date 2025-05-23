package com.traffic.gat1049.exception;

import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.protocol.exception.GatProtocolException;

/**
 * 异常工具类
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
        // 防止实例化
    }

    /**
     * 将异常转换为错误代码
     */
    public static String getErrorCode(Throwable throwable) {
        if (throwable instanceof ValidationException) {
            return GatConstants.ErrorCode.INVALID_PARAMETER;
        } else if (throwable instanceof DataNotFoundException) {
            return GatConstants.ErrorCode.OBJECT_NOT_FOUND;
        } else if (throwable instanceof OperationNotSupportedException) {
            return GatConstants.ErrorCode.OPERATION_FAILED;
        } else if (throwable instanceof TimeoutException) {
            return GatConstants.ErrorCode.TIMEOUT;
        } else if (throwable instanceof GatProtocolException) {
            GatProtocolException gpe = (GatProtocolException) throwable;
            return gpe.getErrorCode() != null ? gpe.getErrorCode() : GatConstants.ErrorCode.SYSTEM_ERROR;
        } else {
            return GatConstants.ErrorCode.SYSTEM_ERROR;
        }
    }

    /**
     * 将异常转换为错误消息
     */
    public static String getErrorMessage(Throwable throwable) {
        if (throwable instanceof BusinessException) {
            BusinessException be = (BusinessException) throwable;
            return be.getErrorMessage() != null ? be.getErrorMessage() : be.getMessage();
        } else if (throwable instanceof GatProtocolException) {
            GatProtocolException gpe = (GatProtocolException) throwable;
            return gpe.getErrorMessage() != null ? gpe.getErrorMessage() : gpe.getMessage();
        } else {
            return throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getSimpleName();
        }
    }

    /**
     * 创建标准的数据验证异常
     */
    public static ValidationException createValidationException(String fieldName, Object value, String message) {
        return new ValidationException(fieldName, value, message);
    }

    /**
     * 创建标准的数据未找到异常
     */
    public static DataNotFoundException createNotFoundEx(String entityType, String entityId) {
        return new DataNotFoundException(entityType, entityId);
    }

    /**
     * 创建标准的操作不支持异常
     */
    public static OperationNotSupportedException createNotSupportedException(String operation, String objectType) {
        return new OperationNotSupportedException(operation, objectType);
    }
}
