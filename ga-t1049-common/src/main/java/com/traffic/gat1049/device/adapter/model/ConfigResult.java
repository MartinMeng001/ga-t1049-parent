package com.traffic.gat1049.device.adapter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
 * 配置数据返回结果类
 * 专门用于返回GA/T1049协议的配置参数数据
 * 支持返回单个或多个配置参数
 *
 * @author GA/T1049 Framework
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigResult<T> {

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 设备/控制器ID
     */
    private String controllerId;

    /**
     * 配置类型
     * 如：STAGE_PARAM, PLAN_PARAM, DAY_PLAN_PARAM, SCHEDULE_PARAM
     */
    private String configType;

    /**
     * 操作类型
     * 如：QUERY, SET, DELETE, UPDATE
     */
    private String operation;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 错误详细描述
     */
    private String errorDetails;

    /**
     * 返回的数据总数
     */
    private int totalCount;

    /**
     * 成功处理的数量
     */
    private int successCount;

    /**
     * 失败处理的数量
     */
    private int failureCount;

    /**
     * 单个配置数据
     * 当返回单个配置时使用
     */
    private T data;

    /**
     * 多个配置数据列表
     * 当返回多个配置时使用
     */
    private List<T> dataList;

    /**
     * 配置详情列表
     * 用于详细记录每个配置项的处理结果
     */
    @Builder.Default
    private List<ConfigDetail> details = new ArrayList<>();

    /**
     * 配置统计信息
     */
    private ConfigStats statistics;

    /**
     * 扩展信息
     */
    @Builder.Default
    private Map<String, Object> extendedInfo = new HashMap<>();

    /**
     * 请求ID（用于跟踪）
     */
    private String requestId;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 数据版本
     */
    private String dataVersion;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 处理耗时（毫秒）
     */
    private long processingTimeMs;

    // ============== 静态工厂方法 ==============

    /**
     * 创建成功结果（单个数据）
     */
    public static <T> ConfigResult<T> success(String controllerId, String configType, T data, String message) {
        return ConfigResult.<T>builder()
                .success(true)
                .controllerId(controllerId)
                .configType(configType)
                .data(data)
                .message(message)
                .totalCount(1)
                .successCount(1)
                .createTime(LocalDateTime.now())
                .completeTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建成功结果（多个数据）
     */
    public static <T> ConfigResult<T> success(String controllerId, String configType, List<T> dataList, String message) {
        int count = dataList != null ? dataList.size() : 0;
        return ConfigResult.<T>builder()
                .success(true)
                .controllerId(controllerId)
                .configType(configType)
                .dataList(dataList)
                .message(message)
                .totalCount(count)
                .successCount(count)
                .createTime(LocalDateTime.now())
                .completeTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     */
    public static <T> ConfigResult<T> failure(String controllerId, String configType, String errorCode, String message) {
        return ConfigResult.<T>builder()
                .success(false)
                .controllerId(controllerId)
                .configType(configType)
                .errorCode(errorCode)
                .message(message)
                .totalCount(0)
                .successCount(0)
                .failureCount(1)
                .createTime(LocalDateTime.now())
                .completeTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建部分成功结果
     */
    public static <T> ConfigResult<T> partial(String controllerId, String configType, List<T> dataList,
                                              int successCount, int failureCount, String message) {
        int totalCount = successCount + failureCount;
        return ConfigResult.<T>builder()
                .success(successCount > 0)
                .controllerId(controllerId)
                .configType(configType)
                .dataList(dataList)
                .message(message)
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .createTime(LocalDateTime.now())
                .completeTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建空结果
     */
    public static <T> ConfigResult<T> empty(String controllerId, String configType, String message) {
        return ConfigResult.<T>builder()
                .success(true)
                .controllerId(controllerId)
                .configType(configType)
                .message(message)
                .totalCount(0)
                .successCount(0)
                .createTime(LocalDateTime.now())
                .completeTime(LocalDateTime.now())
                .build();
    }

    // ============== 便捷方法 ==============

    /**
     * 检查是否有数据
     */
    public boolean hasData() {
        return (data != null) || (dataList != null && !dataList.isEmpty());
    }

    /**
     * 检查是否为单个数据
     */
    public boolean isSingleData() {
        return data != null;
    }

    /**
     * 检查是否为多个数据
     */
    public boolean isMultipleData() {
        return dataList != null && !dataList.isEmpty();
    }

    /**
     * 获取数据数量
     */
    public int getDataCount() {
        if (data != null) {
            return 1;
        }
        if (dataList != null) {
            return dataList.size();
        }
        return 0;
    }

    /**
     * 添加配置详情
     */
    public void addDetail(ConfigDetail detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
    }

    /**
     * 添加配置详情
     */
    public void addDetail(String itemType, String itemId, boolean success, String message) {
        ConfigDetail detail = ConfigDetail.builder()
                .itemType(itemType)
                .itemId(itemId)
                .success(success)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        addDetail(detail);
    }

    /**
     * 添加成功详情
     */
    public void addSuccessDetail(String itemType, String itemId, String message) {
        addDetail(itemType, itemId, true, message);
    }

    /**
     * 添加失败详情
     */
    public void addFailureDetail(String itemType, String itemId, String errorCode, String message) {
        ConfigDetail detail = ConfigDetail.builder()
                .itemType(itemType)
                .itemId(itemId)
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        addDetail(detail);
    }

    /**
     * 标记开始处理
     */
    public void markStarted() {
        this.createTime = LocalDateTime.now();
    }

    /**
     * 标记完成处理
     */
    public void markCompleted() {
        this.completeTime = LocalDateTime.now();
        if (this.createTime != null) {
            this.processingTimeMs = java.time.Duration.between(this.createTime, this.completeTime).toMillis();
        }
    }

    /**
     * 设置扩展信息
     */
    public void putExtendedInfo(String key, Object value) {
        if (this.extendedInfo == null) {
            this.extendedInfo = new HashMap<>();
        }
        this.extendedInfo.put(key, value);
    }

    /**
     * 获取扩展信息
     */
    public Object getExtendedInfo(String key) {
        return this.extendedInfo != null ? this.extendedInfo.get(key) : null;
    }

    // ============== 内部类 ==============

    /**
     * 配置详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigDetail {
        /**
         * 项目类型
         */
        private String itemType;

        /**
         * 项目ID
         */
        private String itemId;

        /**
         * 是否成功
         */
        private boolean success;

        /**
         * 消息
         */
        private String message;

        /**
         * 错误代码
         */
        private String errorCode;

        /**
         * 时间戳
         */
        private LocalDateTime timestamp;

        /**
         * 扩展数据
         */
        private Map<String, Object> extendedData;
    }

    /**
     * 配置统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigStats {
        /**
         * 查询统计
         */
        private int queryCount;

        /**
         * 设置统计
         */
        private int setCount;

        /**
         * 删除统计
         */
        private int deleteCount;

        /**
         * 更新统计
         */
        private int updateCount;

        /**
         * 缓存命中次数
         */
        private int cacheHits;

        /**
         * 数据库查询次数
         */
        private int dbQueries;

        /**
         * 平均处理时间（毫秒）
         */
        private double avgProcessingTimeMs;
    }

    @Override
    public String toString() {
        return "ConfigResult{" +
                "success=" + success +
                ", controllerId='" + controllerId + '\'' +
                ", configType='" + configType + '\'' +
                ", operation='" + operation + '\'' +
                ", message='" + message + '\'' +
                ", totalCount=" + totalCount +
                ", successCount=" + successCount +
                ", failureCount=" + failureCount +
                ", hasData=" + hasData() +
                ", dataCount=" + getDataCount() +
                ", processingTimeMs=" + processingTimeMs +
                '}';
    }
}
