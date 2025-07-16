package com.traffic.gat1049.device.sync.interfaces;

import com.traffic.gat1049.device.adapter.model.*;
import com.traffic.gat1049.device.sync.model.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 同步服务接口
 * 定义设备数据同步的核心功能
 */
public interface SyncService {

    /**
     * 同步配置到设备（数据库 → 设备）
     * @param controllerId 控制器ID
     * @param configData 配置数据
     * @return 同步结果
     */
    SyncResult syncConfigToDevice(String controllerId, DeviceConfigData configData);

    /**
     * 从设备同步状态到数据库（设备 → 数据库）
     * @param controllerId 控制器ID
     * @param statusData 状态数据
     * @return 同步结果
     */
    SyncResult syncStatusFromDevice(String controllerId, DeviceStatusData statusData);

    /**
     * 从数据库加载设备配置
     * @param controllerId 控制器ID
     * @return 配置数据
     */
    DeviceConfigData loadDeviceConfig(String controllerId);

    /**
     * 保存设备状态到数据库
     * @param statusData 状态数据
     * @return 保存结果
     */
    SyncResult saveDeviceStatus(DeviceStatusData statusData);

    /**
     * 批量同步配置
     * @param syncRequests 同步请求列表
     * @return 同步结果列表
     */
    List<SyncResult> batchSyncConfig(List<BatchSyncRequest> syncRequests);

    /**
     * 异步同步配置
     * @param controllerId 控制器ID
     * @param configData 配置数据
     * @return 异步同步结果
     */
    CompletableFuture<SyncResult> asyncSyncConfig(String controllerId, DeviceConfigData configData);

    /**
     * 获取同步历史记录
     * @param controllerId 控制器ID
     * @param limit 限制数量
     * @return 同步历史
     */
    List<SyncHistory> getSyncHistory(String controllerId, int limit);

    /**
     * 清理同步历史
     * @param beforeDays 保留天数
     * @return 清理数量
     */
    int cleanupSyncHistory(int beforeDays);
}
