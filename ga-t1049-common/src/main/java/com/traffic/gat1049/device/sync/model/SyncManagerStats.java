package com.traffic.gat1049.device.sync.model;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 同步管理器统计信息
 */
@Data
@Builder
public class SyncManagerStats {

    /**
     * 总任务数
     */
    private long totalTasks;

    /**
     * 已完成任务数
     */
    private long completedTasks;

    /**
     * 失败任务数
     */
    private long failedTasks;

    /**
     * 等待任务数
     */
    private int pendingTasks;

    /**
     * 运行中任务数
     */
    private int runningTasks;

    /**
     * 队列大小
     */
    private int queueSize;

    /**
     * 是否运行中
     */
    private boolean running;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) completedTasks / totalTasks * 100.0;
    }

    /**
     * 获取失败率
     */
    public double getFailureRate() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) failedTasks / totalTasks * 100.0;
    }
}
