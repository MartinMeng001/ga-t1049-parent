package com.traffic.gat1049.device.adapter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 同步详情类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class SyncDetail {

    /**
     * 项目类型（如：PLAN, STAGE, SIGNAL_GROUP等）
     */
    private String itemType;

    /**
     * 项目ID
     */
    private String itemId;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 错误详情
     */
    private String errorDetails;

    /**
     * 处理时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 处理耗时（毫秒）
     */
    private Long processingTimeMs;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 扩展属性
     */
    private Map<String, Object> properties;

    /**
     * 创建成功详情
     */
    public static SyncDetail success(String itemType, String itemId, String message) {
        return SyncDetail.builder()
                .itemType(itemType)
                .itemId(itemId)
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败详情
     */
    public static SyncDetail failure(String itemType, String itemId, String errorCode, String message) {
        return SyncDetail.builder()
                .itemType(itemType)
                .itemId(itemId)
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
