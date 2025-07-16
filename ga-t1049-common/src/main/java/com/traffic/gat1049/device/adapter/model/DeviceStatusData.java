package com.traffic.gat1049.device.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备状态数据
 * 包含信号机的实时状态信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceStatusData {

    /**
     * 设备ID
     */
    private String controllerId;

    /**
     * 状态时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 控制方式
     * 1-手动控制, 2-自动控制, 3-协调控制, 4-感应控制等
     */
    private Integer controlMode;

    /**
     * 当前配时方案号
     */
    private Integer currentPlanNo;

    /**
     * 当前阶段号
     */
    private Integer currentStageNo;

    /**
     * 阶段剩余时间（秒）
     */
    private Integer stageRemainingTime;

    /**
     * 故障状态
     * 0-无故障, 1-有故障
     */
    private Integer faultStatus;

    /**
     * 通信状态
     * 0-通信故障, 1-通信正常
     */
    private Integer communicationStatus;

    /**
     * 设备工作状态
     * 0-离线, 1-在线, 2-维护中
     */
    private Integer deviceStatus;

    /**
     * 信号组状态列表
     */
    private List<SignalGroupStatus> signalGroupStatuses;

    /**
     * 检测器状态列表
     */
    private List<DetectorStatus> detectorStatuses;

    /**
     * 故障代码列表
     */
    private List<String> faultCodes;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 扩展状态信息
     */
    private Map<String, Object> extendedStatus;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 数据版本
     */
    private String dataVersion;

    /**
     * 检查设备是否在线
     */
    public boolean isOnline() {
        return deviceStatus != null && deviceStatus == 1 &&
                communicationStatus != null && communicationStatus == 1;
    }

    /**
     * 检查设备是否有故障
     */
    public boolean hasFault() {
        return faultStatus != null && faultStatus > 0;
    }

    /**
     * 获取故障描述
     */
    public String getFaultDescription() {
        if (!hasFault()) {
            return "无故障";
        }

        if (faultCodes != null && !faultCodes.isEmpty()) {
            return String.join(", ", faultCodes);
        }

        return errorMessage != null ? errorMessage : "未知故障";
    }

    @Data
    @Builder
    public static class SignalGroupStatus {
        private Integer signalGroupNo;
        private Integer lampStatus; // 灯色状态
        private Integer remainingTime; // 剩余时间
        private LocalDateTime statusTime;
    }

    @Data
    @Builder
    public static class DetectorStatus {
        private Integer detectorNo;
        private Integer status; // 检测器状态
        private Integer vehicleCount; // 车辆数
        private LocalDateTime statusTime;
    }
}
