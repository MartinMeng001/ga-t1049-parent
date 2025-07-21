package com.traffic.device.adapter.common.converter.base;

import com.traffic.device.adapter.common.converter.interfaces.DataConverter;
import com.traffic.gat1049.device.adapter.model.*;
import com.traffic.gat1049.device.adapter.exception.DataConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 抽象数据转换器基类
 * 提供通用的转换逻辑和工具方法
 */
public abstract class AbstractDataConverter implements DataConverter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public DeviceStatusData convertToDeviceStatus(Object rawData, String controllerId) throws DataConversionException {
        try {
            if (rawData == null || !StringUtils.hasText(controllerId)) {
                throw new DataConversionException(controllerId, "转换参数无效");
            }

            DeviceStatusData statusData = new DeviceStatusData();
            statusData.setControllerId(controllerId);
            statusData.setTimestamp(LocalDateTime.now());

            // 调用子类实现的具体转换逻辑
            doConvertToDeviceStatus(rawData, statusData);

            // 基础验证
            if (!validateDeviceStatus(statusData)) {
                throw new DataConversionException(controllerId, "转换后的设备状态数据无效");
            }

            return statusData;

        } catch (Exception e) {
            logger.error("设备状态数据转换失败: controllerId={}", controllerId, e);
            throw new DataConversionException(controllerId, "设备状态转换失败: " + e.getMessage(), e);
        }
    }

    @Override
    public DeviceRuntimeData convertToRuntimeData(Object rawData, String controllerId) throws DataConversionException {
        try {
            if (rawData == null || !StringUtils.hasText(controllerId)) {
                throw new DataConversionException(controllerId, "转换参数无效");
            }

            DeviceRuntimeData runtimeData = new DeviceRuntimeData();
            runtimeData.setControllerId(controllerId);
            runtimeData.setTimestamp(LocalDateTime.now());

            // 调用子类实现的具体转换逻辑
            doConvertToRuntimeData(rawData, runtimeData);

            return runtimeData;

        } catch (Exception e) {
            logger.error("实时数据转换失败: controllerId={}", controllerId, e);
            throw new DataConversionException(controllerId, "实时数据转换失败: " + e.getMessage(), e);
        }
    }

    @Override
    public CommandResult convertToCommandResult(Object response, DeviceCommand command) throws DataConversionException {
        try {
            if (command == null) {
                throw new DataConversionException("命令对象为空");
            }

            CommandResult result = new CommandResult();
//            result.setCommandId(command.getCommandId());
//            result.setControllerId(command.getControllerId());
//            result.setTimestamp(LocalDateTime.now());
//
//            // 调用子类实现的具体转换逻辑
//            doConvertToCommandResult(response, command, result);

            return result;

        } catch (Exception e) {
            logger.error("命令结果转换失败: commandId={}",
                    command != null ? command.getCommandId() : "unknown", e);
            throw new DataConversionException("命令结果转换失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateData(Object data) {
        if (data == null) {
            return false;
        }

        // 子类可以重写此方法实现具体验证逻辑
        return doValidateData(data);
    }

    @Override
    public String extractErrorMessage(Object errorData) {
        if (errorData == null) {
            return "未知错误";
        }

        // 子类实现具体的错误信息提取逻辑
        return doExtractErrorMessage(errorData);
    }

    // ============ 抽象方法，子类必须实现 ============

    /**
     * 具体的设备状态转换实现
     */
    protected abstract void doConvertToDeviceStatus(Object rawData, DeviceStatusData statusData);

    /**
     * 具体的实时数据转换实现
     */
    protected abstract void doConvertToRuntimeData(Object rawData, DeviceRuntimeData runtimeData);

    /**
     * 具体的命令结果转换实现
     */
    protected abstract void doConvertToCommandResult(Object response, DeviceCommand command, CommandResult result);

    // ============ 可选重写的方法 ============

    /**
     * 具体的数据验证实现（可选）
     */
    protected boolean doValidateData(Object data) {
        return true; // 默认通过验证
    }

    /**
     * 具体的错误信息提取实现（可选）
     */
    protected String doExtractErrorMessage(Object errorData) {
        return errorData.toString();
    }

    // ============ 通用工具方法 ============

    /**
     * 验证设备状态数据的完整性
     */
    protected boolean validateDeviceStatus(DeviceStatusData statusData) {
        return statusData != null
                && StringUtils.hasText(statusData.getControllerId())
                && statusData.getTimestamp() != null;
    }

    /**
     * 安全地从Map中获取整数值
     */
    protected Integer safeGetInteger(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return null;
        }

        Object value = map.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                logger.warn("无法解析整数值: key={}, value={}", key, value);
                return null;
            }
        }

        return null;
    }

    /**
     * 安全地从Map中获取字符串值
     */
    protected String safeGetString(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return null;
        }

        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 安全地从Map中获取布尔值
     */
    protected Boolean safeGetBoolean(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return null;
        }

        Object value = map.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof String) {
            String str = (String) value;
            return "true".equalsIgnoreCase(str) || "1".equals(str) || "ok".equalsIgnoreCase(str);
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }

        return false;
    }

    /**
     * 创建默认的成功命令结果
     */
    protected CommandResult createSuccessResult(DeviceCommand command, String message) {
        CommandResult result = new CommandResult();
        result.setCommandId(command.getCommandId());
//        result.setControllerId(command.getControllerId());
//        result.setSuccess(true);
//        result.setMessage(message);
//        result.setTimestamp(LocalDateTime.now());
        return result;
    }

    /**
     * 创建默认的失败命令结果
     */
    protected CommandResult createFailureResult(DeviceCommand command, String error) {
        CommandResult result = new CommandResult();
        result.setCommandId(command.getCommandId());
//        result.setControllerId(command.getControllerId());
//        result.setSuccess(false);
//        result.setMessage(error);
//        result.setTimestamp(LocalDateTime.now());
        return result;
    }
}
