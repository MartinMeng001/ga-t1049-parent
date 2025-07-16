package com.traffic.gat1049.device.sync.model;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 同步任务进度
 */
@Data
@Builder
public class SyncTaskProgress {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务状态
     */
    private SyncTaskStatus status;

    /**
     * 进度百分比
     */
    private Integer progress;

    /**
     * 当前步骤
     */
    private String currentStep;

    /**
     * 总步骤数
     */
    private Integer totalSteps;

    /**
     * 进度消息
     */
    private String message;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 预计完成时间
     */
    private LocalDateTime estimatedCompleteTime;
}
