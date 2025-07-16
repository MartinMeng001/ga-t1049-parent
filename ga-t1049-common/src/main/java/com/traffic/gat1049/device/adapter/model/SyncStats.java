package com.traffic.gat1049.device.adapter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 同步统计信息类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class SyncStats {

    /**
     * 总项目数
     */
    private int totalItems;

    /**
     * 成功项目数
     */
    private int successItems;

    /**
     * 失败项目数
     */
    private int failedItems;

    /**
     * 跳过项目数
     */
    private int skippedItems;

    /**
     * 平均处理时间（毫秒）
     */
    private double averageProcessingTime;

    /**
     * 最大处理时间（毫秒）
     */
    private long maxProcessingTime;

    /**
     * 最小处理时间（毫秒）
     */
    private long minProcessingTime;

    /**
     * 数据传输量（字节）
     */
    private long bytesTransferred;

    /**
     * 错误统计
     */
    private Map<String, Integer> errorCounts;

    /**
     * 按类型统计
     */
    private Map<String, Integer> itemTypeCounts;

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (totalItems == 0) {
            return 0.0;
        }
        return (double) successItems / totalItems * 100.0;
    }

    /**
     * 获取失败率
     */
    public double getFailureRate() {
        if (totalItems == 0) {
            return 0.0;
        }
        return (double) failedItems / totalItems * 100.0;
    }
}
