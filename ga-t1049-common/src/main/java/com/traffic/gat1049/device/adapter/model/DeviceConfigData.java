package com.traffic.gat1049.device.adapter.model;

import com.traffic.gat1049.protocol.model.intersection.CrossParam;
import com.traffic.gat1049.protocol.model.intersection.DetectorParam;
import com.traffic.gat1049.protocol.model.intersection.LampGroupParam;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import com.traffic.gat1049.protocol.model.system.SysInfo;
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
    private SysInfo systemConfig;

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
}
