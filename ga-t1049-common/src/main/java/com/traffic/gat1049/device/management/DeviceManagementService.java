package com.traffic.gat1049.device.management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.traffic.gat1049.device.adapter.interfaces.SignalControllerAdapter;
import com.traffic.gat1049.device.adapter.registry.AdapterRegistry;
import com.traffic.gat1049.device.adapter.model.*;
import com.traffic.gat1049.device.adapter.event.DeviceConnectionEvent;
import com.traffic.gat1049.device.adapter.event.DeviceStatusChangeEvent;

import com.traffic.gat1049.device.sync.interfaces.SyncService;
import com.traffic.gat1049.device.sync.interfaces.SyncTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 设备管理服务
 * 负责设备适配器的统一管理、设备连接管理、数据同步协调等
 */
@Service
public class DeviceManagementService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceManagementService.class);

    @Autowired
    private AdapterRegistry adapterRegistry;

    @Autowired
    private SyncService syncService;

    @Autowired
    private SyncTaskManager syncTaskManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired(required = false)
    private DeviceDiscoveryService deviceDiscoveryService;

    @Autowired(required = false)
    private DeviceHealthChecker deviceHealthChecker;

    /**
     * 设备连接信息缓存
     */
    private final ConcurrentMap<String, DeviceConnectionInfo> deviceConnections = new ConcurrentHashMap<>();

    /**
     * 设备状态缓存
     */
    private final ConcurrentMap<String, DeviceStatusData> deviceStatusCache = new ConcurrentHashMap<>();

    /**
     * 同步任务执行器
     */
    private final ExecutorService syncExecutor = Executors.newFixedThreadPool(10,
            r -> new Thread(r, "device-sync-" + System.currentTimeMillis()));

    /**
     * 状态监控执行器
     */
    private final ScheduledExecutorService monitorExecutor = Executors.newScheduledThreadPool(5,
            r -> new Thread(r, "device-monitor-" + System.currentTimeMillis()));

    /**
     * 设备品牌映射缓存 (controllerId -> brand)
     */
    private final ConcurrentMap<String, String> deviceBrandMapping = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialize() {
        logger.info("正在初始化设备管理服务...");

        // 启动设备发现服务
        if (deviceDiscoveryService != null) {
            deviceDiscoveryService.startDiscovery();
        }

        // 启动设备健康检查
        startDeviceHealthMonitoring();

        logger.info("设备管理服务初始化完成");
    }

    @PreDestroy
    public void shutdown() {
        logger.info("正在关闭设备管理服务...");

        // 关闭执行器
        syncExecutor.shutdown();
        monitorExecutor.shutdown();

        try {
            if (!syncExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                syncExecutor.shutdownNow();
            }
            if (!monitorExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                monitorExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 断开所有设备连接
        disconnectAllDevices();

        logger.info("设备管理服务已关闭");
    }

    // =================================================================
    // 设备连接管理
    // =================================================================

    /**
     * 连接设备
     */
    public ConnectionResult connectDevice(String controllerId, String brand, String ipAddress, Integer port) {
        try {
            logger.info("正在连接设备: controllerId={}, brand={}, ip={}, port={}",
                    controllerId, brand, ipAddress, port);

            // 获取适配器
            SignalControllerAdapter adapter = getAdapter(brand);
            if (adapter == null) {
                return ConnectionResult.failure(controllerId, "ADAPTER_NOT_FOUND",
                        "未找到品牌 " + brand + " 的适配器");
            }

            // 创建连接信息
            DeviceConnectionInfo connectionInfo = DeviceConnectionInfo.builder()
                    .deviceId(controllerId)
                    .ipAddress(ipAddress)
                    .port(port)
                    .connectionType("TCP")
                    .timeoutSeconds(30)
                    .maxRetryCount(3)
                    .build();

            // 执行连接
            ConnectionResult result = adapter.connect(connectionInfo);

            if (result.isSuccess()) {
                // 缓存连接信息和品牌映射
                deviceConnections.put(controllerId, connectionInfo);
                deviceBrandMapping.put(controllerId, brand);

                // 发布连接事件
                publishConnectionEvent(controllerId, brand, true, connectionInfo, "连接成功");

                logger.info("设备连接成功: {}", controllerId);
                //return ConnectionResult.success(controllerId, )
            } else {
                logger.warn("设备连接失败: controllerId={}, error={}", controllerId, result.getMessage());
            }

            return result;

        } catch (Exception e) {
            logger.error("连接设备异常: controllerId={}", controllerId, e);
            return ConnectionResult.failure(controllerId, "CONNECTION_EXCEPTION",
                    "连接异常: " + e.getMessage());
        }
    }
    public ConnectionResult connectDevice(String controllerId, String brand, String ipServer, Integer port, String ipDevice) {
        try {
            logger.info("正在连接设备: controllerId={}, brand={}, ip={}, port={}, ipDevice={}",
                    controllerId, brand, ipServer, port, ipDevice);

            // 获取适配器
            SignalControllerAdapter adapter = getAdapter(brand);
            if (adapter == null) {
                return ConnectionResult.failure(controllerId, "ADAPTER_NOT_FOUND",
                        "未找到品牌 " + brand + " 的适配器");
            }

            // 创建连接信息
            DeviceConnectionInfo connectionInfo = DeviceConnectionInfo.builder()
                    .deviceId(controllerId)
                    .ipAddress(ipDevice)
                    .ipServer(ipServer)
                    .port(port)
                    .connectionType("TCP")
                    .timeoutSeconds(30)
                    .maxRetryCount(3)
                    .build();

            // 执行连接
            ConnectionResult result = adapter.connect(connectionInfo);

            if (result.isSuccess()) {
                // 缓存连接信息和品牌映射
                deviceConnections.put(controllerId, connectionInfo);
                deviceBrandMapping.put(controllerId, brand);

                // 发布连接事件
                publishConnectionEvent(controllerId, brand, true, connectionInfo, "连接成功");

                logger.info("设备连接成功: {}", controllerId);
                //return ConnectionResult.success(controllerId, )
            } else {
                logger.warn("设备连接失败: controllerId={}, error={}", controllerId, result.getMessage());
            }

            return result;

        } catch (Exception e) {
            logger.error("连接设备异常: controllerId={}", controllerId, e);
            return ConnectionResult.failure(controllerId, "CONNECTION_EXCEPTION",
                    "连接异常: " + e.getMessage());
        }
    }

    /**
     * 断开设备连接
     */
    public DisconnectionResult disconnectDevice(String controllerId) {
        try {
            logger.info("正在断开设备连接: {}", controllerId);

            String brand = deviceBrandMapping.get(controllerId);
            if (brand == null) {
                return DisconnectionResult.failure(controllerId, "DEVICE_NOT_FOUND", "设备未连接");
            }

            SignalControllerAdapter adapter = getAdapter(brand);
            if (adapter == null) {
                return DisconnectionResult.failure(controllerId, "ADAPTER_NOT_FOUND", "适配器未找到");
            }

            // 执行断开连接
            DisconnectionResult result = adapter.disconnect(controllerId);

            // 清除缓存
            deviceConnections.remove(controllerId);
            deviceBrandMapping.remove(controllerId);
            deviceStatusCache.remove(controllerId);

            // 发布断开连接事件
            publishConnectionEvent(controllerId, brand, false, null, "断开连接");

            logger.info("设备断开连接: {}", controllerId);
            return result;

        } catch (Exception e) {
            logger.error("断开设备连接异常: controllerId={}", controllerId, e);
            return DisconnectionResult.failure(controllerId, "DISCONNECTION_EXCEPTION",
                    "断开连接异常: " + e.getMessage());
        }
    }

    /**
     * 检查设备连接状态
     */
    public boolean isDeviceConnected(String controllerId) {
        String brand = deviceBrandMapping.get(controllerId);
        if (brand == null) {
            return false;
        }

        SignalControllerAdapter adapter = getAdapter(brand);
        return adapter != null && adapter.isConnected(controllerId);
    }

    /**
     * 获取所有连接的设备
     */
    public List<String> getConnectedDevices() {
        return deviceConnections.keySet().stream()
                .filter(this::isDeviceConnected)
                .collect(Collectors.toList());
    }

    /**
     * 断开所有设备连接
     */
    private void disconnectAllDevices() {
        List<String> connectedDevices = new ArrayList<>(deviceConnections.keySet());
        for (String controllerId : connectedDevices) {
            try {
                disconnectDevice(controllerId);
            } catch (Exception e) {
                logger.error("断开设备连接失败: {}", controllerId, e);
            }
        }
    }

    // =================================================================
    // 配置同步管理
    // =================================================================
    public SyncResult readConfigFromDevice(String brand, JsonNode param){
        try{
            logger.info("读取设备: controllerId={}, brand={}, ip={}, sigid={}",
                    param.get("controllerId").asText(), brand,
                    param.get("ip").asText(), param.get("sigid").asText());

            // 获取适配器
            SignalControllerAdapter adapter = getAdapter(brand);
            if (adapter == null) {
                return SyncResult.failure(param.get("controllerId").asText(), "ADAPTER_NOT_FOUND",
                        "未找到品牌 " + brand + " 的适配器");
            }

            SyncResult result = adapter.readConfigData(param);
            return result;
        }catch (Exception e){
            logger.error("读取设备配置异常: controllerId={}", param.get("controllerId").asText(), e);
            return SyncResult.failure(param.get("controllerId").asText(), "READ_EXCEPTION",
                    "读取异常: " + e.getMessage());
        }
    }
    /**
     * 同步配置到设备
     */
    public CompletableFuture<SyncResult> syncConfigToDevice(String controllerId, DeviceConfigData configData) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("开始同步配置到设备: {}", controllerId);

                String brand = deviceBrandMapping.get(controllerId);
                if (brand == null) {
                    return SyncResult.failure(controllerId, "DEVICE_NOT_CONNECTED", "设备未连接");
                }

                SignalControllerAdapter adapter = getAdapter(brand);
                if (adapter == null) {
                    return SyncResult.failure(controllerId, "ADAPTER_NOT_FOUND", "适配器未找到");
                }

                // 检查设备连接状态
                if (!adapter.isConnected(controllerId)) {
                    return SyncResult.failure(controllerId, "DEVICE_OFFLINE", "设备离线");
                }

                // 执行同步
                SyncResult result = adapter.syncConfigToDevice(controllerId, configData);

                if (result.isSuccess()) {
                    logger.info("配置同步成功: controllerId={}, affectedRows={}",
                            controllerId, result.getAffectedRows());
                } else {
                    logger.warn("配置同步失败: controllerId={}, error={}",
                            controllerId, result.getMessage());
                }

                return result;

            } catch (Exception e) {
                logger.error("配置同步异常: controllerId={}", controllerId, e);
                return SyncResult.failure(controllerId, "SYNC_EXCEPTION",
                        "同步异常: " + e.getMessage());
            }
        }, syncExecutor);
    }

    /**
     * 批量同步配置
     */
    public CompletableFuture<List<SyncResult>> batchSyncConfig(List<BatchSyncRequest> syncRequests) {
        List<CompletableFuture<SyncResult>> futures = syncRequests.stream()
                .map(request -> syncConfigToDevice(request.getControllerId(), request.getConfigData()))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    // =================================================================
    // 状态读取管理
    // =================================================================

    /**
     * 读取设备状态
     */
    public DeviceStatusData readDeviceStatus(String controllerId) {
        try {
            String brand = deviceBrandMapping.get(controllerId);
            if (brand == null) {
                return createErrorStatus(controllerId, "设备未连接");
            }

            SignalControllerAdapter adapter = getAdapter(brand);
            if (adapter == null) {
                return createErrorStatus(controllerId, "适配器未找到");
            }

            // 检查设备连接状态
            if (!adapter.isConnected(controllerId)) {
                return createErrorStatus(controllerId, "设备离线");
            }

            // 读取状态
            DeviceStatusData statusData = adapter.readDeviceStatus(controllerId);

            if (statusData != null) {
                // 缓存状态
                DeviceStatusData oldStatus = deviceStatusCache.put(controllerId, statusData);

                // 检查状态是否发生变化，发布事件
                if (isStatusChanged(oldStatus, statusData)) {
                    publishStatusChangeEvent(controllerId, brand, oldStatus, statusData);
                }
            }

            return statusData;

        } catch (Exception e) {
            logger.error("读取设备状态异常: controllerId={}", controllerId, e);
            return createErrorStatus(controllerId, "状态读取异常: " + e.getMessage());
        }
    }

    /**
     * 批量读取设备状态
     */
    public Map<String, DeviceStatusData> batchReadDeviceStatus(List<String> controllerIds) {
        Map<String, DeviceStatusData> results = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = controllerIds.stream()
                .map(controllerId -> CompletableFuture.runAsync(() -> {
                    DeviceStatusData status = readDeviceStatus(controllerId);
                    if (status != null) {
                        results.put(controllerId, status);
                    }
                }, syncExecutor))
                .collect(Collectors.toList());

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return results;
    }

    /**
     * 定时同步所有在线设备状态
     */
    @Scheduled(fixedRate = 30000) // 每30秒同步一次
    public void syncAllOnlineDevicesStatus() {
        List<String> connectedDevices = getConnectedDevices();

        if (connectedDevices.isEmpty()) {
            return;
        }

        logger.debug("开始定时同步设备状态，设备数量: {}", connectedDevices.size());

        // 并行读取所有设备状态
        connectedDevices.parallelStream().forEach(controllerId -> {
            try {
                readDeviceStatus(controllerId);
            } catch (Exception e) {
                logger.error("定时状态同步失败: controllerId={}", controllerId, e);
            }
        });

        logger.debug("定时状态同步完成");
    }

    // =================================================================
    // 命令发送管理
    // =================================================================

    /**
     * 发送控制命令
     */
    public CompletableFuture<CommandResult> sendCommand(String controllerId, DeviceCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("发送控制命令: controllerId={}, commandType={}",
                        controllerId, command.getCommandType());

                String brand = deviceBrandMapping.get(controllerId);
                if (brand == null) {
                    return CommandResult.failure(command.getCommandId(), controllerId,
                            "DEVICE_NOT_CONNECTED", "设备未连接");
                }

                SignalControllerAdapter adapter = getAdapter(brand);
                if (adapter == null) {
                    return CommandResult.failure(command.getCommandId(), controllerId,
                            "ADAPTER_NOT_FOUND", "适配器未找到");
                }

                // 检查设备连接状态
                if (!adapter.isConnected(controllerId)) {
                    return CommandResult.failure(command.getCommandId(), controllerId,
                            "DEVICE_OFFLINE", "设备离线");
                }

                // 执行命令
                CommandResult result = adapter.sendCommand(controllerId, command);

                if (result.isSuccess()) {
                    logger.info("命令执行成功: controllerId={}, commandId={}",
                            controllerId, command.getCommandId());
                } else {
                    logger.warn("命令执行失败: controllerId={}, commandId={}, error={}",
                            controllerId, command.getCommandId(), result.getMessage());
                }

                return result;

            } catch (Exception e) {
                logger.error("命令执行异常: controllerId={}, commandId={}",
                        controllerId, command.getCommandId(), e);
                return CommandResult.failure(command.getCommandId(), controllerId,
                        "COMMAND_EXCEPTION", "命令执行异常: " + e.getMessage());
            }
        }, syncExecutor);
    }

    // =================================================================
    // 设备发现和管理
    // =================================================================

    /**
     * 发现设备
     */
    public List<DeviceInfo> discoverDevices() {
        if (deviceDiscoveryService != null) {
            return deviceDiscoveryService.discoverDevices();
        }
        return Collections.emptyList();
    }

    /**
     * 自动连接发现的设备
     */
    public void autoConnectDevices() {
        List<DeviceInfo> discoveredDevices = discoverDevices();

        for (DeviceInfo deviceInfo : discoveredDevices) {
            try {
                if (!isDeviceConnected(deviceInfo.getDeviceId())) {
                    connectDevice(deviceInfo.getDeviceId(), deviceInfo.getBrand(),
                            deviceInfo.getIpAddress(), deviceInfo.getPort());
                }
            } catch (Exception e) {
                logger.error("自动连接设备失败: deviceId={}", deviceInfo.getDeviceId(), e);
            }
        }
    }

    // =================================================================
    // 健康检查和监控
    // =================================================================

    /**
     * 启动设备健康监控
     */
    private void startDeviceHealthMonitoring() {
        monitorExecutor.scheduleWithFixedDelay(this::performHealthCheck, 60, 60, TimeUnit.SECONDS);
        logger.info("设备健康监控已启动，检查间隔: 60秒");
    }

    /**
     * 执行健康检查
     */
    private void performHealthCheck() {
        try {
            List<String> connectedDevices = getConnectedDevices();

            for (String controllerId : connectedDevices) {
                try {
                    String brand = deviceBrandMapping.get(controllerId);
                    SignalControllerAdapter adapter = getAdapter(brand);

                    if (adapter != null) {
                        boolean healthy = adapter.healthCheck();
                        if (!healthy) {
                            logger.warn("设备健康检查失败: controllerId={}", controllerId);
                            // 可以在这里实现重连逻辑
                        }
                    }
                } catch (Exception e) {
                    logger.error("设备健康检查异常: controllerId={}", controllerId, e);
                }
            }
        } catch (Exception e) {
            logger.error("健康检查任务异常", e);
        }
    }

    /**
     * 获取设备健康状态报告
     */
    public Map<String, Boolean> getDeviceHealthReport() {
        Map<String, Boolean> healthReport = new HashMap<>();

        List<String> connectedDevices = getConnectedDevices();
        for (String controllerId : connectedDevices) {
            try {
                String brand = deviceBrandMapping.get(controllerId);
                SignalControllerAdapter adapter = getAdapter(brand);

                boolean healthy = adapter != null && adapter.healthCheck();
                healthReport.put(controllerId, healthy);
            } catch (Exception e) {
                logger.error("获取设备健康状态失败: controllerId={}", controllerId, e);
                healthReport.put(controllerId, false);
            }
        }

        return healthReport;
    }

    // =================================================================
    // 工具方法
    // =================================================================

    /**
     * 获取适配器
     */
    private SignalControllerAdapter getAdapter(String brand) {
        return adapterRegistry.getSignalControllerAdapter(brand);
    }

    /**
     * 根据控制器ID获取适配器
     */
    public SignalControllerAdapter getAdapterByControllerId(String controllerId) {
        String brand = deviceBrandMapping.get(controllerId);
        return brand != null ? getAdapter(brand) : null;
    }

    /**
     * 创建错误状态
     */
    private DeviceStatusData createErrorStatus(String controllerId, String errorMessage) {
        return DeviceStatusData.builder()
                .controllerId(controllerId)
                .timestamp(LocalDateTime.now())
                .deviceStatus(0) // 离线
                .communicationStatus(0) // 通信故障
                .faultStatus(1) // 有故障
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * 检查状态是否发生变化
     */
    private boolean isStatusChanged(DeviceStatusData oldStatus, DeviceStatusData newStatus) {
        if (oldStatus == null) {
            return true;
        }

        // 检查关键状态字段是否发生变化
        return !Objects.equals(oldStatus.getDeviceStatus(), newStatus.getDeviceStatus()) ||
                !Objects.equals(oldStatus.getCurrentPlanNo(), newStatus.getCurrentPlanNo()) ||
                !Objects.equals(oldStatus.getCurrentStageNo(), newStatus.getCurrentStageNo()) ||
                !Objects.equals(oldStatus.getFaultStatus(), newStatus.getFaultStatus()) ||
                !Objects.equals(oldStatus.getCommunicationStatus(), newStatus.getCommunicationStatus());
    }

    /**
     * 发布连接事件
     */
    private void publishConnectionEvent(String controllerId, String brand, boolean connected,
                                        DeviceConnectionInfo connectionInfo, String reason) {
        try {
            DeviceConnectionEvent event = connected ?
                    DeviceConnectionEvent.connected(this, controllerId, brand, connectionInfo) :
                    DeviceConnectionEvent.disconnected(this, controllerId, brand, reason);

            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            logger.error("发布连接事件失败", e);
        }
    }

    /**
     * 发布状态变化事件
     */
    private void publishStatusChangeEvent(String controllerId, String brand,
                                          DeviceStatusData oldStatus, DeviceStatusData newStatus) {
        try {
            DeviceStatusChangeEvent event = new DeviceStatusChangeEvent(
                    this, controllerId, brand, oldStatus, newStatus, "STATUS_UPDATE");

            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            logger.error("发布状态变化事件失败", e);
        }
    }

    // =================================================================
    // 统计和监控信息
    // =================================================================

    /**
     * 获取设备管理统计信息
     */
    public DeviceManagementStats getManagementStats() {
        List<String> allDevices = new ArrayList<>(deviceBrandMapping.keySet());
        List<String> connectedDevices = getConnectedDevices();
        Map<String, Boolean> healthReport = getDeviceHealthReport();

        long healthyDevices = healthReport.values().stream()
                .mapToLong(healthy -> healthy ? 1 : 0)
                .sum();

        return DeviceManagementStats.builder()
                .totalDevices(allDevices.size())
                .connectedDevices(connectedDevices.size())
                .disconnectedDevices(allDevices.size() - connectedDevices.size())
                .healthyDevices((int) healthyDevices)
                .unhealthyDevices(connectedDevices.size() - (int) healthyDevices)
                .adapterCount(adapterRegistry.getAdapterCount())
                .lastUpdateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 获取设备连接信息
     */
    public Map<String, DeviceConnectionInfo> getDeviceConnections() {
        return new HashMap<>(deviceConnections);
    }

    /**
     * 获取设备品牌映射
     */
    public Map<String, String> getDeviceBrandMapping() {
        return new HashMap<>(deviceBrandMapping);
    }

    /**
     * 完整的配置同步流程
     */
//    public SyncResult syncPlanConfigComplete(String crossId, List<PlanParam> planParams) {
//        try {
//            // 1. 同步到数据库
//            SyncResult dbResult = configSyncService.syncPlanConfigToDatabase(crossId, planParams);
//            if (!dbResult.isSuccess()) {
//                return dbResult;
//            }
//
//            // 2. 获取设备适配器
//            SignalControllerAdapter adapter = adapterRegistry.getAdapterByCrossId(crossId);
//            if (adapter == null) {
//                logger.warn("未找到路口{}的适配器", crossId);
//                return SyncResult.success("数据已保存到数据库，但未找到设备适配器");
//            }
//
//            // 3. 同步到设备
//            DeviceConfigData configData = buildDeviceConfigData(crossId, planParams);
//            SyncResult deviceResult = adapter.syncConfigToDevice(crossId, configData);
//
//            // 4. 记录同步结果
//            recordSyncResult(crossId, "PlanParam", dbResult, deviceResult);
//
//            if (deviceResult.isSuccess()) {
//                return SyncResult.success("配置同步到数据库和设备均成功");
//            } else {
//                return SyncResult.partial("配置已保存到数据库，但同步到设备失败: " + deviceResult.getMessage());
//            }
//
//        } catch (Exception e) {
//            logger.error("配置同步失败: crossId={}", crossId, e);
//            return SyncResult.failure("配置同步失败: " + e.getMessage());
//        }
//    }

    /**
     * 构建设备配置数据
     */
//    private DeviceConfigData buildDeviceConfigData(String crossId, List<PlanParam> planParams) {
//        DeviceConfigData configData = new DeviceConfigData();
//        configData.setPlans(planParams);
//
//        // 可能需要加载其他相关配置
//        configData.setCrossParam(loadCrossParam(crossId));
//        configData.setSignalGroups(loadSignalGroups(crossId));
//        configData.setStages(loadStages(crossId));
//
//        return configData;
//    }
}
