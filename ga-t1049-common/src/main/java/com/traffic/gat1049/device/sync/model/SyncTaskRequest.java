package com.traffic.gat1049.device.sync.model;

import com.traffic.gat1049.device.adapter.model.DeviceConfigData;
import lombok.Data;
import lombok.Builder;

/**
 * 同步任务请求
 */
@Data
@Builder
public class SyncTaskRequest {

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
     * 优先级
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
     * 请求来源
     */
    private String requestSource;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 是否异步执行
     */
    @Builder.Default
    private Boolean async = true;
}
