package com.traffic.gat1049.device.adapter.base;

import com.traffic.gat1049.device.adapter.interfaces.SignalControllerAdapter;
import com.traffic.gat1049.device.adapter.model.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 信号机适配器基类
 * 提供信号机适配器的通用实现
 */
public abstract class BaseSignalControllerAdapter<P> extends AbstractDeviceAdapter
        implements SignalControllerAdapter<P> {

    /**
     * 连接管理 - 维护设备连接状态
     */
    protected final ConcurrentMap<String, DeviceConnectionInfo> connections = new ConcurrentHashMap<>();

    /**
     * 设备状态缓存
     */
    protected final ConcurrentMap<String, DeviceStatusData> statusCache = new ConcurrentHashMap<>();

    @Override
    public boolean isConnected(String controllerId) {
        if (!StringUtils.hasText(controllerId)) {
            return false;
        }

        DeviceConnectionInfo connectionInfo = connections.get(controllerId);
        return connectionInfo != null && connectionInfo.isConnected();
    }

    @Override
    public List<DeviceStatusData> batchReadDeviceStatus(List<String> controllerIds) {
        List<DeviceStatusData> results = new ArrayList<>();

        if (controllerIds == null || controllerIds.isEmpty()) {
            return results;
        }

        for (String controllerId : controllerIds) {
            try {
                DeviceStatusData statusData = readDeviceStatus(controllerId);
                if (statusData != null) {
                    results.add(statusData);
                }
            } catch (Exception e) {
                logger.error("批量读取设备状态失败: controllerId={}", controllerId, e);
                // 创建错误状态数据
                DeviceStatusData errorStatus = createErrorStatusData(controllerId, e.getMessage());
                results.add(errorStatus);
            }
        }

        return results;
    }

    @Override
    public List<SyncResult> batchSyncConfig(List<BatchSyncRequest> syncRequests) {
        List<SyncResult> results = new ArrayList<>();

        if (syncRequests == null || syncRequests.isEmpty()) {
            return results;
        }

        for (BatchSyncRequest request : syncRequests) {
            try {
                SyncResult result = syncConfigToDevice(request.getControllerId(), request.getConfigData());
                results.add(result);
            } catch (Exception e) {
                logger.error("批量同步配置失败: controllerId={}", request.getControllerId(), e);
                SyncResult errorResult = SyncResult.failure(request.getControllerId(),
                        "同步失败: " + e.getMessage(), "");
                results.add(errorResult);
            }
        }

        return results;
    }

    @Override
    protected boolean doHealthCheck() {
        try {
            // 检查适配器基础状态
            if (connections.isEmpty()) {
                logger.debug("没有活动的设备连接");
                return true; // 没有连接也算健康
            }

            // 检查连接状态
            long connectedCount = connections.values().stream()
                    .mapToLong(conn -> conn.isConnected() ? 1 : 0)
                    .sum();

            long totalCount = connections.size();
            double connectionRate = (double) connectedCount / totalCount;

            // 如果连接率低于50%，认为不健康
            boolean healthy = connectionRate >= 0.5;

            if (!healthy) {
                logger.warn("适配器连接率过低: {}/{} ({}%)",
                        connectedCount, totalCount, connectionRate * 100);
            }

            return healthy;

        } catch (Exception e) {
            logger.error("信号机适配器健康检查异常", e);
            return false;
        }
    }

    /**
     * 创建错误状态数据
     */
    protected DeviceStatusData createErrorStatusData(String controllerId, String errorMessage) {
        DeviceStatusData errorStatus = new DeviceStatusData();
        errorStatus.setControllerId(controllerId);
        errorStatus.setTimestamp(java.time.LocalDateTime.now());
        errorStatus.setCommunicationStatus(0); // 通信故障
        errorStatus.setFaultStatus(1); // 有故障
        errorStatus.setErrorMessage(errorMessage);
        return errorStatus;
    }

    /**
     * 更新连接状态
     */
    protected void updateConnectionStatus(String controllerId, boolean connected) {
        DeviceConnectionInfo connectionInfo = connections.get(controllerId);
        if (connectionInfo != null) {
            connectionInfo.setConnected(connected);
            connectionInfo.setLastUpdateTime(java.time.LocalDateTime.now());
        }
    }

    /**
     * 缓存设备状态
     */
    protected void cacheDeviceStatus(String controllerId, DeviceStatusData statusData) {
        if (statusData != null) {
            statusCache.put(controllerId, statusData);
        }
    }

    /**
     * 获取缓存的设备状态
     */
    protected DeviceStatusData getCachedDeviceStatus(String controllerId) {
        return statusCache.get(controllerId);
    }
}
