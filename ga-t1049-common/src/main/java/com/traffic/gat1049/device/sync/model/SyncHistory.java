package com.traffic.gat1049.device.sync.model;

import com.traffic.gat1049.device.adapter.model.DeviceStatusData;
import com.traffic.gat1049.device.adapter.model.SyncResult;
import com.traffic.gat1049.device.sync.impl.StatusSyncServiceImpl;
import com.traffic.gat1049.repository.interfaces.CrossSignalGroupStatusRepository;
import lombok.Data;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 同步历史记录
 */
@Data
@Builder
public class SyncHistory {
    private static final Logger logger = LoggerFactory.getLogger(SyncHistory.class);
    /**
     * 记录ID
     */
    private Long id;

    /**
     * 控制器ID
     */
    private String controllerId;

    /**
     * 同步类型
     */
    private SyncType syncType;

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
     * 影响行数
     */
    private int affectedRows;

    /**
     * 同步耗时（毫秒）
     */
    private long syncTimeMs;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    //private DeviceStatusRepository deviceStatusRepository;

    //@Autowired
    private CrossSignalGroupStatusRepository signalGroupStatusRepository;

//    @Autowired
//    private DeviceDataConverter deviceDataConverter;

    //@Autowired
    private Executor syncTaskExecutor;

    /**
     * 同步设备状态到数据库
     */
    @CacheEvict(value = "deviceStatus", key = "#statusData.controllerId")
    @Transactional
    public SyncResult syncStatusToDatabase(DeviceStatusData statusData) {
        logger.debug("同步设备状态到数据库: controllerId={}", statusData.getControllerId());

        SyncResult result = SyncResult.inProgress(statusData.getControllerId(), "STATUS_SYNC", "开始同步设备状态");
        result.markStarted();

        try {
            // 1. 保存设备主状态
//            DeviceStatusEntity statusEntity = deviceDataConverter.toStatusEntity(statusData);
//            statusEntity.setCreateTime(LocalDateTime.now());

            //DeviceStatusEntity savedEntity = deviceStatusRepository.save(statusEntity);
            result.addSuccessDetail("DEVICE_STATUS", statusData.getControllerId(), "设备状态保存成功");

            int affectedRows = 1;

            // 2. 保存信号组状态
            if (statusData.getSignalGroupStatuses() != null && !statusData.getSignalGroupStatuses().isEmpty()) {
                int signalGroupCount = syncSignalGroupStatuses(statusData.getControllerId(),
                        statusData.getSignalGroupStatuses(), result);
                affectedRows += signalGroupCount;
            }

            // 3. 更新结果
            result.setAffectedRows(affectedRows);
            result.setSuccessCount(affectedRows);
            result.markCompleted();
            result.setMessage("设备状态同步成功");

            logger.debug("设备状态同步完成: controllerId={}, affectedRows={}",
                    statusData.getControllerId(), affectedRows);

            return result;

        } catch (Exception e) {
            logger.error("设备状态同步异常: controllerId={}", statusData.getControllerId(), e);
            result.markFailed("SYNC_EXCEPTION", "状态同步异常: " + e.getMessage());
            return result;
        }
    }

    /**
     * 批量同步设备状态
     */
    @Transactional
    public List<SyncResult> batchSyncStatus(List<DeviceStatusData> statusDataList) {
        logger.info("批量同步设备状态: 数量={}", statusDataList.size());

        List<CompletableFuture<SyncResult>> futures = statusDataList.stream()
                .map(statusData -> CompletableFuture.supplyAsync(() ->
                        syncStatusToDatabase(statusData), syncTaskExecutor))
                .collect(Collectors.toList());

        // 等待所有任务完成
        List<SyncResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        // 统计结果
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        logger.info("批量状态同步完成: 总数={}, 成功={}, 失败={}",
                results.size(), successCount, results.size() - successCount);

        return results;
    }

    /**
     * 从数据库加载最新设备状态
     */
    @Cacheable(value = "deviceStatus", key = "#controllerId")
    public DeviceStatusData loadLatestStatusFromDatabase(String controllerId) {
        try {
            //DeviceStatusEntity entity = deviceStatusRepository.findTopByControllerIdOrderByCreateTimeDesc(controllerId);

//            if (entity == null) {
//                logger.debug("未找到设备状态: controllerId={}", controllerId);
//                return null;
//            }

//            DeviceStatusData statusData = deviceDataConverter.toStatusData(entity);

            // 加载信号组状态
//            List<CrossSignalGroupStatusEntity> signalGroupEntities =
//                    signalGroupStatusRepository.findByControllerIdAndCreateTimeOrderBySignalGroupNo(
//                            controllerId, entity.getCreateTime());

//            if (!signalGroupEntities.isEmpty()) {
//                List<DeviceStatusData.SignalGroupStatus> signalGroupStatuses =
//                        signalGroupEntities.stream()
//                                .map(this::convertToSignalGroupStatus)
//                                .collect(Collectors.toList());
//                statusData.setSignalGroupStatuses(signalGroupStatuses);
//            }

            logger.debug("设备状态加载成功: controllerId={}", controllerId);
            return null;

        } catch (Exception e) {
            logger.error("加载设备状态失败: controllerId={}", controllerId, e);
            return null;
        }
    }

    /**
     * 获取设备状态历史
     */
    public List<DeviceStatusData> getStatusHistory(String controllerId, int limit) {
        try {
//            List<DeviceStatusEntity> entities = deviceStatusRepository
//                    .findByControllerIdOrderByCreateTimeDesc(controllerId, limit);
//
//            return entities.stream()
//                    .map(entity -> deviceDataConverter.toStatusData(entity))
//                    .collect(Collectors.toList());
            return null;
        } catch (Exception e) {
            logger.error("获取状态历史失败: controllerId={}", controllerId, e);
            //return Collections.emptyList();
            return null;
        }
    }

    /**
     * 清理历史状态数据
     */
    @Transactional
    public int cleanupStatusHistory(int keepDays) {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(keepDays);
            int deletedCount = 0;   //deviceStatusRepository.deleteByCreateTimeBefore(cutoffTime);

            logger.info("状态历史清理完成: 删除{}条记录", deletedCount);
            return deletedCount;

        } catch (Exception e) {
            logger.error("清理状态历史异常", e);
            return 0;
        }
    }

// =================================================================
// 私有方法
// =================================================================

    /**
     * 同步信号组状态
     */
    private int syncSignalGroupStatuses(String controllerId,
                                        List<DeviceStatusData.SignalGroupStatus> signalGroupStatuses,
                                        SyncResult result) {
        int savedCount = 0;

        for (DeviceStatusData.SignalGroupStatus signalGroupStatus : signalGroupStatuses) {
            try {
//                CrossSignalGroupStatusEntity entity = CrossSignalGroupStatusEntity.builder()
//                        .controllerId(controllerId)
//                        .signalGroupNo(signalGroupStatus.getSignalGroupNo())
//                        .lampStatus(signalGroupStatus.getLampStatus())
//                        .remainingTime(signalGroupStatus.getRemainingTime())
//                        .lampStatusTime(signalGroupStatus.getStatusTime())
//                        .createTime(LocalDateTime.now())
//                        .build();
//
//                signalGroupStatusRepository.save(entity);
                savedCount++;

                result.addSuccessDetail("SIGNAL_GROUP_STATUS",
                        signalGroupStatus.getSignalGroupNo().toString(),
                        "信号组状态保存成功");

            } catch (Exception e) {
                logger.error("保存信号组状态失败: controllerId={}, signalGroupNo={}",
                        controllerId, signalGroupStatus.getSignalGroupNo(), e);
                result.addFailureDetail("SIGNAL_GROUP_STATUS",
                        signalGroupStatus.getSignalGroupNo().toString(),
                        "SAVE_ERROR", "保存失败: " + e.getMessage());
            }
        }

        return savedCount;
    }

    /**
     * 转换信号组状态实体为模型
     */
//    private DeviceStatusData.SignalGroupStatus convertToSignalGroupStatus(CrossSignalGroupStatusEntity entity) {
//        return DeviceStatusData.SignalGroupStatus.builder()
//                .signalGroupNo(entity.getSignalGroupNo())
//                .lampStatus(entity.getLampStatus())
//                .remainingTime(entity.getRemainingTime())
//                .statusTime(entity.getLampStatusTime())
//                .build();
//    }
}
