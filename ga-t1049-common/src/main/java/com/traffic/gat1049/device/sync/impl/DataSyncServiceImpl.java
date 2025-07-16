package com.traffic.gat1049.device.sync.impl;

import com.traffic.gat1049.device.sync.interfaces.SyncService;
import com.traffic.gat1049.device.adapter.model.*;
import com.traffic.gat1049.device.sync.model.*;
import com.traffic.gat1049.device.adapter.registry.AdapterRegistry;
import com.traffic.gat1049.device.adapter.interfaces.SignalControllerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 数据同步服务实现
 */
@Service
@Transactional
public class DataSyncServiceImpl implements SyncService {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncServiceImpl.class);

    @Autowired
    private AdapterRegistry adapterRegistry;
//
//    @Autowired
//    private DeviceConfigRepository deviceConfigRepository;
//
//    @Autowired
//    private DeviceStatusRepository deviceStatusRepository;
//
//    @Autowired
//    private SyncHistoryRepository syncHistoryRepository;
//
//    @Autowired
//    private DeviceDataConverter deviceDataConverter;

    @Autowired
    private Executor syncTaskExecutor;

    @Override
    public SyncResult syncConfigToDevice(String controllerId, DeviceConfigData configData) {
        logger.info("开始同步配置到设备: controllerId={}", controllerId);

        SyncResult result = SyncResult.inProgress(controllerId, "CONFIG_SYNC", "开始同步配置");
        result.markStarted();

        try {
            // 1. 验证参数
            if (!validateSyncParameters(controllerId, configData)) {
                result.markFailed("INVALID_PARAMS", "同步参数无效");
                return result;
            }

            // 2. 获取适配器
            SignalControllerAdapter adapter = adapterRegistry.getAdapterByControllerId(controllerId);
            if (adapter == null) {
                result.markFailed("ADAPTER_NOT_FOUND", "未找到设备适配器");
                return result;
            }

            // 3. 检查设备连接状态
            if (!adapter.isConnected(controllerId)) {
                result.markFailed("DEVICE_OFFLINE", "设备未连接");
                return result;
            }

            // 4. 执行同步
            SyncResult deviceResult = adapter.syncConfigToDevice(controllerId, configData);

            // 5. 更新结果
            if (deviceResult.isSuccess()) {
                result = deviceResult;
                result.setSyncType("CONFIG_SYNC");
                result.markCompleted();

                // 6. 保存到数据库
                saveConfigToDatabase(controllerId, configData);

                logger.info("配置同步成功: controllerId={}, affectedRows={}",
                        controllerId, result.getAffectedRows());
            } else {
                result = deviceResult;
                result.setSyncType("CONFIG_SYNC");
                logger.warn("配置同步失败: controllerId={}, error={}",
                        controllerId, result.getMessage());
            }

            // 7. 记录同步历史
            recordSyncHistory(controllerId, SyncType.CONFIG_TO_DEVICE, result);

            return result;

        } catch (Exception e) {
            logger.error("配置同步异常: controllerId={}", controllerId, e);
            result.markFailed("SYNC_EXCEPTION", "同步异常: " + e.getMessage());
            recordSyncHistory(controllerId, SyncType.CONFIG_TO_DEVICE, result);
            return result;
        }
    }

    @Override
    @Transactional
    public SyncResult syncStatusFromDevice(String controllerId, DeviceStatusData statusData) {
        logger.debug("开始同步状态从设备: controllerId={}", controllerId);

        try {
            // 1. 验证参数
            if (controllerId == null || statusData == null) {
                return SyncResult.failure(controllerId, "INVALID_PARAMS", "同步参数无效");
            }

            // 2. 保存状态到数据库
            SyncResult result = saveDeviceStatus(statusData);

            // 3. 记录同步历史
            recordSyncHistory(controllerId, SyncType.STATUS_FROM_DEVICE, result);

            logger.debug("状态同步完成: controllerId={}, success={}", controllerId, result.isSuccess());
            return result;

        } catch (Exception e) {
            logger.error("状态同步异常: controllerId={}", controllerId, e);
            SyncResult errorResult = SyncResult.failure(controllerId, "SYNC_EXCEPTION",
                    "状态同步异常: " + e.getMessage());
            recordSyncHistory(controllerId, SyncType.STATUS_FROM_DEVICE, errorResult);
            return errorResult;
        }
    }

    @Override
    @Cacheable(value = "deviceConfig", key = "#controllerId")
    public DeviceConfigData loadDeviceConfig(String controllerId) {
        logger.debug("加载设备配置: controllerId={}", controllerId);

        try {
            // 从数据库加载配置
//            DeviceConfigEntity configEntity = deviceConfigRepository.findByControllerId(controllerId);
//
//            if (configEntity == null) {
//                logger.warn("未找到设备配置: controllerId={}", controllerId);
//                return null;
//            }
//
//            // 转换为配置数据对象
            DeviceConfigData configData = new DeviceConfigData();//deviceDataConverter.toConfigData(configEntity);

            logger.debug("设备配置加载成功: controllerId={}", controllerId);
            return configData;

        } catch (Exception e) {
            logger.error("加载设备配置异常: controllerId={}", controllerId, e);
            return null;
        }
    }

    @Override
    @CacheEvict(value = "deviceStatus", key = "#statusData.controllerId")
    public SyncResult saveDeviceStatus(DeviceStatusData statusData) {
        logger.debug("保存设备状态: controllerId={}", statusData.getControllerId());

        try {
//            // 1. 转换为实体对象
//            DeviceStatusEntity statusEntity = deviceDataConverter.toStatusEntity(statusData);
//            statusEntity.setCreateTime(LocalDateTime.now());
//
//            // 2. 保存到数据库
//            DeviceStatusEntity savedEntity = deviceStatusRepository.save(statusEntity);

//            logger.debug("设备状态保存成功: controllerId={}, id={}",
//                    statusData.getControllerId(), savedEntity.getId());

            return SyncResult.success(statusData.getControllerId(), "状态保存成功", 1);

        } catch (Exception e) {
            logger.error("保存设备状态异常: controllerId={}", statusData.getControllerId(), e);
            return SyncResult.failure(statusData.getControllerId(), "SAVE_ERROR",
                    "状态保存失败: " + e.getMessage());
        }
    }

    @Override
    public List<SyncResult> batchSyncConfig(List<BatchSyncRequest> syncRequests) {
        logger.info("开始批量同步配置，任务数量: {}", syncRequests.size());

        List<SyncResult> results = new ArrayList<>();

        for (BatchSyncRequest request : syncRequests) {
            try {
                SyncResult result = syncConfigToDevice(request.getControllerId(), request.getConfigData());
                results.add(result);
            } catch (Exception e) {
                logger.error("批量同步配置失败: controllerId={}", request.getControllerId(), e);
                SyncResult errorResult = SyncResult.failure(request.getControllerId(),
                        "BATCH_SYNC_ERROR", "批量同步失败: " + e.getMessage());
                results.add(errorResult);
            }
        }

        // 统计结果
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        logger.info("批量同步完成: 总数={}, 成功={}, 失败={}",
                results.size(), successCount, results.size() - successCount);

        return results;
    }

    @Override
    public CompletableFuture<SyncResult> asyncSyncConfig(String controllerId, DeviceConfigData configData) {
        logger.info("开始异步同步配置: controllerId={}", controllerId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return syncConfigToDevice(controllerId, configData);
            } catch (Exception e) {
                logger.error("异步同步配置异常: controllerId={}", controllerId, e);
                return SyncResult.failure(controllerId, "ASYNC_SYNC_ERROR",
                        "异步同步失败: " + e.getMessage());
            }
        }, syncTaskExecutor);
    }

    @Override
    public List<SyncHistory> getSyncHistory(String controllerId, int limit) {
        try {
//            List<SyncHistoryEntity> entities = syncHistoryRepository
//                    .findByControllerIdOrderByCreateTimeDesc(controllerId, limit);
//
//            return entities.stream()
//                    .map(this::convertToSyncHistory)
//                    .collect(Collectors.toList());
            return null;
        } catch (Exception e) {
            logger.error("获取同步历史异常: controllerId={}", controllerId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public int cleanupSyncHistory(int beforeDays) {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(beforeDays);
            int deletedCount = 0;//syncHistoryRepository.deleteByCreateTimeBefore(cutoffTime);

            logger.info("同步历史清理完成: 删除{}条记录", deletedCount);
            return deletedCount;

        } catch (Exception e) {
            logger.error("清理同步历史异常", e);
            return 0;
        }
    }

    // =================================================================
    // 私有方法
    // =================================================================

    /**
     * 验证同步参数
     */
    private boolean validateSyncParameters(String controllerId, DeviceConfigData configData) {
        if (controllerId == null || controllerId.trim().isEmpty()) {
            return false;
        }

        if (configData == null || !configData.isValid()) {
            return false;
        }

        return true;
    }

    /**
     * 保存配置到数据库
     */
    @CacheEvict(value = "deviceConfig", key = "#controllerId")
    public void saveConfigToDatabase(String controllerId, DeviceConfigData configData) {
        try {
//            DeviceConfigEntity configEntity = deviceDataConverter.toConfigEntity(configData);
//            configEntity.setControllerId(controllerId);
//            configEntity.setUpdateTime(LocalDateTime.now());
//
//            deviceConfigRepository.save(configEntity);

            logger.debug("配置保存到数据库成功: controllerId={}", controllerId);

        } catch (Exception e) {
            logger.error("保存配置到数据库失败: controllerId={}", controllerId, e);
        }
    }

    /**
     * 记录同步历史
     */
    private void recordSyncHistory(String controllerId, SyncType syncType, SyncResult result) {
        try {
//            SyncHistoryEntity historyEntity = SyncHistoryEntity.builder()
//                    .controllerId(controllerId)
//                    .syncType(syncType)
//                    .success(result.isSuccess())
//                    .message(result.getMessage())
//                    .errorCode(result.getErrorCode())
//                    .affectedRows(result.getAffectedRows())
//                    .syncTimeMs(result.getSyncTimeMs())
//                    .createTime(LocalDateTime.now())
//                    .build();
//
//            syncHistoryRepository.save(historyEntity);

        } catch (Exception e) {
            logger.error("记录同步历史失败: controllerId={}", controllerId, e);
        }
    }

    /**
     * 转换同步历史实体为模型
     */
//    private SyncHistory convertToSyncHistory(SyncHistoryEntity entity) {
//        return SyncHistory.builder()
//                .id(entity.getId())
//                .controllerId(entity.getControllerId())
//                .syncType(entity.getSyncType())
//                .success(entity.isSuccess())
//                .message(entity.getMessage())
//                .errorCode(entity.getErrorCode())
//                .affectedRows(entity.getAffectedRows())
//                .syncTimeMs(entity.getSyncTimeMs())
//                .createTime(entity.getCreateTime())
//                .build();
//    }
}
