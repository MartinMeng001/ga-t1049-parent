package com.traffic.gat1049.device.adapter.model;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 批量同步请求
 */
@Data
@Builder
public class BatchSyncRequest {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 设备ID
     */
    private String controllerId;

    /**
     * 配置数据
     */
    private DeviceConfigData configData;

    /**
     * 同步类型
     */
    private String syncType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 超时时间（秒）
     */
    private Integer timeoutSeconds;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 请求来源
     */
    private String source;

    /**
     * 是否强制同步
     */
    private boolean forceSync;

    /**
     * 备注信息
     */
    private String remarks;
}
