package com.traffic.gat1049.device.sync.model;

import com.traffic.gat1049.device.adapter.model.DeviceConfigData;
import com.traffic.gat1049.device.adapter.model.SyncResult;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 同步任务
 */
@Data
@Builder
public class SyncTask {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 控制器ID
     */
    private String controllerId;

    /**
     * 同步类型
     */
    private SyncType syncType;

    /**
     * 配置数据
     */
    private DeviceConfigData configData;

    /**
     * 任务状态
     */
    private SyncTaskStatus status;

    /**
     * 优先级 (1-低, 2-中, 3-高, 4-紧急)
     */
    @Builder.Default
    private Integer priority = 2;

    /**
     * 超时时间（秒）
     */
    @Builder.Default
    private Integer timeoutSeconds = 300;

    /**
     * 最大重试次数
     */
    @Builder.Default
    private Integer maxRetryCount = 3;

    /**
     * 当前重试次数
     */
    @Builder.Default
    private Integer currentRetryCount = 0;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 进度百分比 (0-100)
     */
    @Builder.Default
    private Integer progress = 0;

    /**
     * 当前步骤
     */
    private String currentStep;

    /**
     * 总步骤数
     */
    private Integer totalSteps;

    /**
     * 任务消息
     */
    private String message;

    /**
     * 请求来源
     */
    private String requestSource;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 同步结果
     */
    private SyncResult syncResult;

    /**
     * 获取任务执行时长（毫秒）
     */
    public Long getDurationMs() {
        if (startTime == null) {
            return null;
        }

        LocalDateTime endTime = completeTime != null ? completeTime : LocalDateTime.now();
        return java.time.Duration.between(startTime, endTime).toMillis();
    }

    /**
     * 检查任务是否可以取消
     */
    public boolean isCancellable() {
        return status == SyncTaskStatus.CREATED || status == SyncTaskStatus.PENDING;
    }

    /**
     * 检查任务是否正在运行
     */
    public boolean isRunning() {
        return status == SyncTaskStatus.RUNNING;
    }
}
