package com.traffic.gat1049.device.adapter.util;

import com.traffic.gat1049.device.adapter.model.ConnectionResult;
import com.traffic.gat1049.device.adapter.model.DeviceConnectionInfo;
import com.traffic.gat1049.device.adapter.model.DeviceStatusData;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 适配器工具类
 */
public class AdapterUtils {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 生成唯一ID
     */
    public static String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带前缀的ID
     */
    public static String generateId(String prefix) {
        return prefix + "_" + generateId();
    }

    /**
     * 格式化时间
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * 解析时间
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (!StringUtils.hasText(dateTimeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查设备连接是否超时
     */
    public static boolean isConnectionTimeout(DeviceConnectionInfo connectionInfo, int timeoutSeconds) {
        if (connectionInfo == null || connectionInfo.getLastCommunicationTime() == null) {
            return true;
        }

        LocalDateTime expireTime = connectionInfo.getLastCommunicationTime().plusSeconds(timeoutSeconds);
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 创建默认连接信息
     */
    public static DeviceConnectionInfo createDefaultConnectionInfo(String deviceId, String ipAddress, Integer port) {
        return DeviceConnectionInfo.builder()
                .deviceId(deviceId)
                .ipAddress(ipAddress)
                .port(port)
                .connectionType("TCP")
                .connected(false)
                .timeoutSeconds(30)
                .retryCount(0)
                .maxRetryCount(3)
                .build();
    }

    /**
     * 创建错误状态数据
     */
    public static DeviceStatusData createErrorStatus(String controllerId, String errorMessage) {
        return DeviceStatusData.builder()
                .controllerId(controllerId)
                .timestamp(LocalDateTime.now())
                .deviceStatus(0) // 离线
                .communicationStatus(0) // 通信故障
                .faultStatus(1) // 有故障
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * 验证设备ID格式
     */
    public static boolean isValidDeviceId(String deviceId) {
        return StringUtils.hasText(deviceId) &&
                deviceId.length() >= 3 &&
                deviceId.length() <= 50 &&
                deviceId.matches("^[A-Za-z0-9_-]+$");
    }

    /**
     * 验证IP地址格式
     */
    public static boolean isValidIpAddress(String ipAddress) {
        if (!StringUtils.hasText(ipAddress)) {
            return false;
        }

        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证端口号
     */
    public static boolean isValidPort(Integer port) {
        return port != null && port > 0 && port <= 65535;
    }

    /**
     * 计算两个时间的差值（秒）
     */
    public static long calculateTimeDifferenceSeconds(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return java.time.Duration.between(start, end).getSeconds();
    }

    /**
     * 安全地获取字符串
     */
    public static String safeString(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    /**
     * 安全地获取整数
     */
    public static Integer safeInteger(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Integer) {
            return (Integer) obj;
        }

        try {
            return Integer.valueOf(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 合并两个设备状态数据（新状态覆盖旧状态的非空字段）
     */
    public static DeviceStatusData mergeDeviceStatus(DeviceStatusData oldStatus, DeviceStatusData newStatus) {
        if (oldStatus == null) {
            return newStatus;
        }

        if (newStatus == null) {
            return oldStatus;
        }

        DeviceStatusData.DeviceStatusDataBuilder builder = DeviceStatusData.builder()
                .controllerId(newStatus.getControllerId() != null ? newStatus.getControllerId() : oldStatus.getControllerId())
                .timestamp(newStatus.getTimestamp() != null ? newStatus.getTimestamp() : oldStatus.getTimestamp())
                .controlMode(newStatus.getControlMode() != null ? newStatus.getControlMode() : oldStatus.getControlMode())
                .currentPlanNo(newStatus.getCurrentPlanNo() != null ? newStatus.getCurrentPlanNo() : oldStatus.getCurrentPlanNo())
                .currentStageNo(newStatus.getCurrentStageNo() != null ? newStatus.getCurrentStageNo() : oldStatus.getCurrentStageNo())
                .stageRemainingTime(newStatus.getStageRemainingTime() != null ? newStatus.getStageRemainingTime() : oldStatus.getStageRemainingTime())
                .faultStatus(newStatus.getFaultStatus() != null ? newStatus.getFaultStatus() : oldStatus.getFaultStatus())
                .communicationStatus(newStatus.getCommunicationStatus() != null ? newStatus.getCommunicationStatus() : oldStatus.getCommunicationStatus())
                .deviceStatus(newStatus.getDeviceStatus() != null ? newStatus.getDeviceStatus() : oldStatus.getDeviceStatus())
                .signalGroupStatuses(newStatus.getSignalGroupStatuses() != null ? newStatus.getSignalGroupStatuses() : oldStatus.getSignalGroupStatuses())
                .detectorStatuses(newStatus.getDetectorStatuses() != null ? newStatus.getDetectorStatuses() : oldStatus.getDetectorStatuses())
                .faultCodes(newStatus.getFaultCodes() != null ? newStatus.getFaultCodes() : oldStatus.getFaultCodes())
                .errorMessage(newStatus.getErrorMessage() != null ? newStatus.getErrorMessage() : oldStatus.getErrorMessage())
                .extendedStatus(newStatus.getExtendedStatus() != null ? newStatus.getExtendedStatus() : oldStatus.getExtendedStatus())
                .dataSource(newStatus.getDataSource() != null ? newStatus.getDataSource() : oldStatus.getDataSource())
                .dataVersion(newStatus.getDataVersion() != null ? newStatus.getDataVersion() : oldStatus.getDataVersion());

        return builder.build();
    }
}
