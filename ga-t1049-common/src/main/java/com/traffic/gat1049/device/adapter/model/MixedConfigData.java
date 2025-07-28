package com.traffic.gat1049.device.adapter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.traffic.gat1049.protocol.model.signal.StageParam;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 混合配置数据容器
 * 用于承载多种类型的GA/T1049协议配置参数
 *
 * @author GA/T1049 Framework
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MixedConfigData {

    /**
     * 控制器ID
     */
    private String controllerId;

    /**
     * 数据版本
     */
    private String dataVersion;

    /**
     * 数据创建时间
     */
    private LocalDateTime createTime;

    /**
     * 阶段参数列表
     */
    @Builder.Default
    private List<StageParam> stageParams = new ArrayList<>();

    /**
     * 配时方案参数列表
     */
    @Builder.Default
    private List<PlanParam> planParams = new ArrayList<>();

    /**
     * 日计划参数列表
     */
    @Builder.Default
    private List<DayPlanParam> dayPlanParams = new ArrayList<>();

    /**
     * 调度参数列表
     */
    @Builder.Default
    private List<ScheduleParam> scheduleParams = new ArrayList<>();

    /**
     * 信号组参数列表
     */
    @Builder.Default
    private List<SignalGroupParam> signalGroupParams = new ArrayList<>();

    /**
     * 元数据信息
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * 扩展配置数据
     * 用于存储其他类型的配置参数
     */
    @Builder.Default
    private Map<String, List<Object>> extendedConfigs = new HashMap<>();

    // ============== 便捷方法 ==============

    /**
     * 获取总配置数量
     */
    public int getTotalCount() {
        return stageParams.size() + planParams.size() + dayPlanParams.size() +
                scheduleParams.size() + signalGroupParams.size() +
                extendedConfigs.values().stream().mapToInt(List::size).sum();
    }

    /**
     * 检查是否有阶段参数
     */
    public boolean hasStageParams() {
        return !stageParams.isEmpty();
    }

    /**
     * 检查是否有配时方案参数
     */
    public boolean hasPlanParams() {
        return !planParams.isEmpty();
    }

    /**
     * 检查是否有日计划参数
     */
    public boolean hasDayPlanParams() {
        return !dayPlanParams.isEmpty();
    }

    /**
     * 检查是否有调度参数
     */
    public boolean hasScheduleParams() {
        return !scheduleParams.isEmpty();
    }

    /**
     * 检查是否有信号组参数
     */
    public boolean hasSignalGroupParams() {
        return !signalGroupParams.isEmpty();
    }

    /**
     * 检查是否有任何配置数据
     */
    public boolean hasAnyConfig() {
        return getTotalCount() > 0;
    }

    /**
     * 添加元数据
     */
    public void putMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    /**
     * 获取元数据
     */
    public Object getMetadata(String key) {
        return this.metadata.get(key);
    }

    /**
     * 添加扩展配置
     */
    public void putExtendedConfig(String configType, List<Object> configs) {
        this.extendedConfigs.put(configType, configs);
    }

    /**
     * 获取扩展配置
     */
    public List<Object> getExtendedConfig(String configType) {
        return this.extendedConfigs.get(configType);
    }

    /**
     * 获取所有包含的配置类型
     */
    public List<String> getIncludedConfigTypes() {
        List<String> types = new ArrayList<>();
        if (hasStageParams()) types.add("STAGE_PARAM");
        if (hasPlanParams()) types.add("PLAN_PARAM");
        if (hasDayPlanParams()) types.add("DAY_PLAN_PARAM");
        if (hasScheduleParams()) types.add("SCHEDULE_PARAM");
        if (hasSignalGroupParams()) types.add("SIGNAL_GROUP_PARAM");
        types.addAll(extendedConfigs.keySet());
        return types;
    }

    /**
     * 清空所有配置数据
     */
    public void clearAll() {
        stageParams.clear();
        planParams.clear();
        dayPlanParams.clear();
        scheduleParams.clear();
        signalGroupParams.clear();
        extendedConfigs.clear();
        metadata.clear();
    }

    /**
     * 创建统计信息
     */
    public ConfigStats createStats() {
        return ConfigStats.builder()
                .stageParamCount(stageParams.size())
                .planParamCount(planParams.size())
                .dayPlanParamCount(dayPlanParams.size())
                .scheduleParamCount(scheduleParams.size())
                .signalGroupParamCount(signalGroupParams.size())
                .totalCount(getTotalCount())
                .includedTypes(getIncludedConfigTypes())
                .build();
    }

    /**
     * 配置统计信息内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigStats {
        private int stageParamCount;
        private int planParamCount;
        private int dayPlanParamCount;
        private int scheduleParamCount;
        private int signalGroupParamCount;
        private int totalCount;
        private List<String> includedTypes;
    }

    @Override
    public String toString() {
        return String.format("MixedConfigData{controllerId='%s', totalCount=%d, types=%s}",
                controllerId, getTotalCount(), getIncludedConfigTypes());
    }
}
