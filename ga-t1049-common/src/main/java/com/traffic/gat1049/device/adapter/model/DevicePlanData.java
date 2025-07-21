package com.traffic.gat1049.device.adapter.model;

import com.traffic.gat1049.protocol.model.intersection.LampGroupParam;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备方案数据
 * 包含信号机的完整方案信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevicePlanData {
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 灯组配置列表
     */
    private List<LampGroupParam> lampGroups;
    /**
     * 阶段配置列表
     */
    private List<StageParam> stages;

    /**
     * 配时方案
     */
    private PlanParam plan;

    /**
     * 配置创建时间
     */
    private LocalDateTime createTime;

    /**
     * 配置更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 验证配置数据是否完整
     */
    public boolean isValid() {
        return deviceId != null && !deviceId.trim().isEmpty();// &&
                //crossParam != null &&
                //signalGroups != null && !signalGroups.isEmpty();
    }
}
