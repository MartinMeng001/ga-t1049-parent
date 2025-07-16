package com.traffic.gat1049.device.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备配置数据
 * 包含信号机的完整配置信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceConfigData {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 路口参数
     */
    private CrossParam crossParam;

    /**
     * 信号组配置列表
     */
    private List<SignalGroupParam> signalGroups;

    /**
     * 阶段配置列表
     */
    private List<StageParam> stages;

    /**
     * 配时方案列表
     */
    private List<PlanParam> plans;

    /**
     * 灯组配置列表
     */
    private List<LampGroupParam> lampGroups;

    /**
     * 检测器配置列表
     */
    private List<DetectorParam> detectors;

    /**
     * 系统配置
     */
    private SystemConfig systemConfig;

    /**
     * 配置版本号
     */
    private String configVersion;

    /**
     * 配置创建时间
     */
    private LocalDateTime createTime;

    /**
     * 配置更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 扩展配置参数
     */
    private Map<String, Object> extendedConfig;

    /**
     * 验证配置数据是否完整
     */
    public boolean isValid() {
        return deviceId != null && !deviceId.trim().isEmpty() &&
                crossParam != null &&
                signalGroups != null && !signalGroups.isEmpty();
    }

    /**
     * 获取配置项数量统计
     */
    public ConfigStats getConfigStats() {
        return ConfigStats.builder()
                .signalGroupCount(signalGroups != null ? signalGroups.size() : 0)
                .stageCount(stages != null ? stages.size() : 0)
                .planCount(plans != null ? plans.size() : 0)
                .lampGroupCount(lampGroups != null ? lampGroups.size() : 0)
                .detectorCount(detectors != null ? detectors.size() : 0)
                .build();
    }

    @Data
    @Builder
    public static class ConfigStats {
        private int signalGroupCount;
        private int stageCount;
        private int planCount;
        private int lampGroupCount;
        private int detectorCount;
    }

    // 内部类定义配置参数（这些应该引用实际的协议模型类）
    @Data
    public static class CrossParam {
        private String crossId;
        private String crossName;
        private Integer signalGroupCount;
        private Integer stageCount;
        // 其他路口参数...
    }

    @Data
    public static class SignalGroupParam {
        private Integer signalGroupNo;
        private String signalGroupName;
        private Integer minGreen;
        private Integer maxGreen;
        // 其他信号组参数...
    }

    @Data
    public static class StageParam {
        private Integer stageNo;
        private String stageName;
        private Integer stageTime;
        // 其他阶段参数...
    }

    @Data
    public static class PlanParam {
        private Integer planNo;
        private String planName;
        private Integer cycleTime;
        // 其他方案参数...
    }

    @Data
    public static class LampGroupParam {
        private Integer lampGroupNo;
        private String lampGroupName;
        // 其他灯组参数...
    }

    @Data
    public static class DetectorParam {
        private Integer detectorNo;
        private String detectorName;
        // 其他检测器参数...
    }

    @Data
    public static class SystemConfig {
        private String systemVersion;
        private Map<String, Object> parameters;
        // 其他系统配置...
    }
}
