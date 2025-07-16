// com/traffic/gat1049/device/adapter/model/SyncResult.java
package com.traffic.gat1049.device.adapter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 同步结果类
 * 用于封装设备配置同步、状态同步等操作的执行结果
 *
 * @author GA/T1049 Framework
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyncResult {

    /**
     * 同步是否成功
     */
    private boolean success;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 同步类型
     * 如：CONFIG_SYNC, STATUS_SYNC, DATA_SYNC等
     */
    private String syncType;

    /**
     * 同步操作类型
     * 如：UPLOAD, DOWNLOAD, UPDATE, DELETE等
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
     * 影响的记录数/处理的项目数
     */
    private int affectedRows;

    /**
     * 成功处理的项目数
     */
    private int successCount;

    /**
     * 失败处理的项目数
     */
    private int failureCount;

    /**
     * 跳过的项目数
     */
    private int skippedCount;

    /**
     * 同步耗时（毫秒）
     */
    private long syncTimeMs;

    /**
     * 同步开始时间
     */
    private LocalDateTime startTime;

    /**
     * 同步完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 同步状态
     */
    @Builder.Default
    private SyncStatus status = SyncStatus.PENDING;

    /**
     * 同步优先级
     * 1-低, 2-中, 3-高, 4-紧急
     */
    @Builder.Default
    private int priority = 2;

    /**
     * 同步进度（百分比 0-100）
     */
    private Integer progress;

    /**
     * 同步详情列表
     */
    @Builder.Default
    private List<SyncDetail> details = new ArrayList<>();

    /**
     * 数据统计信息
     */
    private SyncStats statistics;

    /**
     * 扩展信息
     */
    @Builder.Default
    private Map<String, Object> extendedInfo = new HashMap<>();

    /**
     * 同步请求ID（用于跟踪）
     */
    private String requestId;

    /**
     * 批次ID（批量同步时使用）
     */
    private String batchId;

    /**
     * 重试次数
     */
    @Builder.Default
    private int retryCount = 0;

    /**
     * 最大重试次数
     */
    @Builder.Default
    private int maxRetryCount = 3;

    /**
     * 数据源
     */
    private String dataSource;

    /**
     * 目标源
     */
    private String targetSource;

    /**
     * 同步配置参数
     */
    private Map<String, Object> syncConfig;

    /**
     * 创建成功结果
     */
    public static SyncResult success(String deviceId, String message) {
        return SyncResult.builder()
                .success(true)
                .deviceId(deviceId)
                .message(message)
                .status(SyncStatus.COMPLETED)
                .completeTime(LocalDateTime.now())
                .progress(100)
                .build();
    }

    /**
     * 创建成功结果（带影响行数）
     */
    public static SyncResult success(String deviceId, String message, int affectedRows) {
        return SyncResult.builder()
                .success(true)
                .deviceId(deviceId)
                .message(message)
                .affectedRows(affectedRows)
                .successCount(affectedRows)
                .status(SyncStatus.COMPLETED)
                .completeTime(LocalDateTime.now())
                .progress(100)
                .build();
    }

    /**
     * 创建成功结果（带同步类型）
     */
    public static SyncResult success(String deviceId, String syncType, String message, int affectedRows) {
        return SyncResult.builder()
                .success(true)
                .deviceId(deviceId)
                .syncType(syncType)
                .message(message)
                .affectedRows(affectedRows)
                .successCount(affectedRows)
                .status(SyncStatus.COMPLETED)
                .completeTime(LocalDateTime.now())
                .progress(100)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static SyncResult failure(String deviceId, String errorCode, String message) {
        return SyncResult.builder()
                .success(false)
                .deviceId(deviceId)
                .errorCode(errorCode)
                .message(message)
                .status(SyncStatus.FAILED)
                .completeTime(LocalDateTime.now())
                .failureCount(1)
                .progress(0)
                .build();
    }

    /**
     * 创建失败结果（带同步类型）
     */
    public static SyncResult failure(String deviceId, String syncType, String errorCode, String message) {
        return SyncResult.builder()
                .success(false)
                .deviceId(deviceId)
                .syncType(syncType)
                .errorCode(errorCode)
                .message(message)
                .status(SyncStatus.FAILED)
                .completeTime(LocalDateTime.now())
                .failureCount(1)
                .progress(0)
                .build();
    }

    /**
     * 创建部分成功结果
     */
    public static SyncResult partial(String deviceId, String message, int successCount, int failureCount) {
        int totalCount = successCount + failureCount;
        int progress = totalCount > 0 ? (successCount * 100 / totalCount) : 0;

        return SyncResult.builder()
                .success(successCount > 0) // 有成功的就算部分成功
                .deviceId(deviceId)
                .message(message)
                .affectedRows(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .status(SyncStatus.PARTIAL_SUCCESS)
                .completeTime(LocalDateTime.now())
                .progress(progress)
                .build();
    }

    /**
     * 创建正在进行中的结果
     */
    public static SyncResult inProgress(String deviceId, String syncType, String message) {
        return SyncResult.builder()
                .success(false) // 进行中状态设为false
                .deviceId(deviceId)
                .syncType(syncType)
                .message(message)
                .status(SyncStatus.IN_PROGRESS)
                .startTime(LocalDateTime.now())
                .progress(0)
                .build();
    }

    /**
     * 创建取消的结果
     */
    public static SyncResult cancelled(String deviceId, String message) {
        return SyncResult.builder()
                .success(false)
                .deviceId(deviceId)
                .message(message)
                .status(SyncStatus.CANCELLED)
                .completeTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建超时结果
     */
    public static SyncResult timeout(String deviceId, String message, long timeoutMs) {
        return SyncResult.builder()
                .success(false)
                .deviceId(deviceId)
                .errorCode("TIMEOUT")
                .message(message)
                .syncTimeMs(timeoutMs)
                .status(SyncStatus.TIMEOUT)
                .completeTime(LocalDateTime.now())
                .build();
    }

    /**
     * 添加同步详情
     */
    public void addDetail(SyncDetail detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);

        // 更新统计
        updateCounts();
    }

    /**
     * 添加同步详情
     */
    public void addDetail(String itemType, String itemId, boolean success, String message) {
        SyncDetail detail = SyncDetail.builder()
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
        SyncDetail detail = SyncDetail.builder()
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
     * 更新计数统计
     */
    private void updateCounts() {
        if (details == null || details.isEmpty()) {
            return;
        }

        long successCount = details.stream().mapToLong(d -> d.isSuccess() ? 1 : 0).sum();
        long failureCount = details.stream().mapToLong(d -> !d.isSuccess() ? 1 : 0).sum();

        this.successCount = (int) successCount;
        this.failureCount = (int) failureCount;
        this.affectedRows = this.successCount + this.failureCount;

        // 更新整体成功状态
        if (failureCount == 0) {
            this.success = true;
            this.status = SyncStatus.COMPLETED;
        } else if (successCount > 0) {
            this.success = true; // 部分成功
            this.status = SyncStatus.PARTIAL_SUCCESS;
        } else {
            this.success = false;
            this.status = SyncStatus.FAILED;
        }

        // 更新进度
        if (affectedRows > 0) {
            this.progress =  (int)(successCount * 100) / affectedRows;
        }
    }

    /**
     * 设置开始时间
     */
    public void markStarted() {
        this.startTime = LocalDateTime.now();
        this.status = SyncStatus.IN_PROGRESS;
    }

    /**
     * 设置完成时间并计算耗时
     */
    public void markCompleted() {
        this.completeTime = LocalDateTime.now();
        if (this.startTime != null) {
            this.syncTimeMs = Duration.between(this.startTime, this.completeTime).toMillis();
        }

        // 根据当前状态决定最终状态
        if (this.status == SyncStatus.IN_PROGRESS) {
            if (this.success) {
                this.status = this.failureCount > 0 ? SyncStatus.PARTIAL_SUCCESS : SyncStatus.COMPLETED;
            } else {
                this.status = SyncStatus.FAILED;
            }
        }
        this.progress = 100;
    }

    /**
     * 设置失败状态
     */
    public void markFailed(String errorCode, String errorMessage) {
        this.success = false;
        this.errorCode = errorCode;
        this.message = errorMessage;
        this.status = SyncStatus.FAILED;
        this.completeTime = LocalDateTime.now();

        if (this.startTime != null) {
            this.syncTimeMs = Duration.between(this.startTime, this.completeTime).toMillis();
        }
    }

    /**
     * 更新进度
     */
    public void updateProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        if (this.progress == 100 && this.status == SyncStatus.IN_PROGRESS) {
            markCompleted();
        }
    }

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }

    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return this.retryCount < this.maxRetryCount && !this.success;
    }

    /**
     * 检查是否已完成（成功或失败）
     */
    public boolean isCompleted() {
        return this.status == SyncStatus.COMPLETED ||
                this.status == SyncStatus.FAILED ||
                this.status == SyncStatus.PARTIAL_SUCCESS ||
                this.status == SyncStatus.CANCELLED ||
                this.status == SyncStatus.TIMEOUT;
    }

    /**
     * 检查是否正在进行中
     */
    public boolean isInProgress() {
        return this.status == SyncStatus.IN_PROGRESS;
    }

    /**
     * 检查是否为部分成功
     */
    public boolean isPartialSuccess() {
        return this.status == SyncStatus.PARTIAL_SUCCESS;
    }

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (affectedRows == 0) {
            return success ? 100.0 : 0.0;
        }
        return (double) successCount / affectedRows * 100.0;
    }

    /**
     * 获取失败率
     */
    public double getFailureRate() {
        return 100.0 - getSuccessRate();
    }

    /**
     * 添加扩展信息
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

    /**
     * 合并另一个同步结果
     */
    public void merge(SyncResult other) {
        if (other == null) {
            return;
        }

        // 合并统计信息
        this.affectedRows += other.getAffectedRows();
        this.successCount += other.getSuccessCount();
        this.failureCount += other.getFailureCount();
        this.skippedCount += other.getSkippedCount();

        // 合并详情
        if (other.getDetails() != null) {
            if (this.details == null) {
                this.details = new ArrayList<>();
            }
            this.details.addAll(other.getDetails());
        }

        // 合并扩展信息
        if (other.getExtendedInfo() != null) {
            if (this.extendedInfo == null) {
                this.extendedInfo = new HashMap<>();
            }
            this.extendedInfo.putAll(other.getExtendedInfo());
        }

        // 更新整体状态
        updateOverallStatus();
    }

    /**
     * 更新整体状态
     */
    private void updateOverallStatus() {
        if (this.failureCount == 0) {
            this.success = true;
            this.status = SyncStatus.COMPLETED;
        } else if (this.successCount > 0) {
            this.success = true;
            this.status = SyncStatus.PARTIAL_SUCCESS;
        } else {
            this.success = false;
            this.status = SyncStatus.FAILED;
        }

        // 更新进度
        if (this.affectedRows > 0) {
            this.progress = (int) ((double) this.successCount / this.affectedRows * 100);
        }
    }

    /**
     * 转换为简化的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SyncResult{");
        sb.append("deviceId='").append(deviceId).append('\'');
        sb.append(", success=").append(success);
        sb.append(", status=").append(status);
        sb.append(", syncType='").append(syncType).append('\'');

        if (successCount > 0 || failureCount > 0) {
            sb.append(", stats=(").append(successCount).append("/").append(affectedRows).append(")");
        }

        if (progress != null) {
            sb.append(", progress=").append(progress).append("%");
        }

        if (syncTimeMs > 0) {
            sb.append(", time=").append(syncTimeMs).append("ms");
        }

        if (message != null) {
            sb.append(", message='").append(message).append('\'');
        }

        if (errorCode != null) {
            sb.append(", error='").append(errorCode).append('\'');
        }

        sb.append('}');
        return sb.toString();
    }

    /**
     * 创建结果摘要
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();

        if (success) {
            summary.append("✓ 同步成功");
        } else {
            summary.append("✗ 同步失败");
        }

        if (syncType != null) {
            summary.append(" [").append(syncType).append("]");
        }

        if (affectedRows > 0) {
            summary.append(" - 处理 ").append(affectedRows).append(" 项");
            if (successCount > 0) {
                summary.append(", 成功 ").append(successCount).append(" 项");
            }
            if (failureCount > 0) {
                summary.append(", 失败 ").append(failureCount).append(" 项");
            }
        }

        if (syncTimeMs > 0) {
            summary.append(", 耗时 ").append(syncTimeMs).append("ms");
        }

        return summary.toString();
    }
}
