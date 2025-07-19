package com.traffic.gat1049.device.adapter.interfaces;

import com.traffic.gat1049.device.adapter.model.*;
import java.util.List;

/**
 * 信号机适配器接口
 * 定义了与信号机设备通信的标准接口
 */
public interface SignalControllerAdapter<P> extends DeviceAdapter {

    /**
     * 连接信号机设备
     * @param deviceInfo 设备连接信息
     * @return 连接结果
     */
    ConnectionResult connect(DeviceConnectionInfo deviceInfo);

    /**
     * 断开与信号机设备的连接
     * @param controllerId 信号机ID
     * @return 断开结果
     */
    DisconnectionResult disconnect(String controllerId);

    /**
     * 检查设备连接状态
     * @param controllerId 信号机ID
     * @return 连接状态
     */
    boolean isConnected(String controllerId);

    /**
     * 同步配置到设备（数据库 → 设备）
     * @param controllerId 信号机ID
     * @param configData 配置数据
     * @return 同步结果
     */
    SyncResult syncConfigToDevice(String controllerId, DeviceConfigData configData);

    /**
     * 从设备读取状态（设备 → 数据库）
     * @param controllerId 信号机ID
     * @return 设备状态数据
     */
    DeviceStatusData readDeviceStatus(String controllerId);

    /**
     * 从设备读取配置数据数据
     * @param inputParam 协议需求参数
     * @return 实时数据
     */
    SyncResult readConfigData(P inputParam);

    /**
     * 从设备读取实时数据
     * @param controllerId 信号机ID
     * @return 实时数据
     */
    DeviceRuntimeData readRuntimeData(String controllerId);

    /**
     * 发送控制命令到设备
     * @param controllerId 信号机ID
     * @param command 控制命令
     * @return 命令执行结果
     */
    CommandResult sendCommand(String controllerId, DeviceCommand command);

    /**
     * 获取设备能力信息
     * @param controllerId 信号机ID
     * @return 设备能力
     */
    DeviceCapabilities getDeviceCapabilities(String controllerId);

    /**
     * 批量读取多个设备状态
     * @param controllerIds 信号机ID列表
     * @return 状态数据列表
     */
    List<DeviceStatusData> batchReadDeviceStatus(List<String> controllerIds);

    /**
     * 批量同步配置到多个设备
     * @param syncRequests 同步请求列表
     * @return 同步结果列表
     */
    List<SyncResult> batchSyncConfig(List<BatchSyncRequest> syncRequests);

    // 需要提供一些方案转换方式，每个适配器都必须提供该方式的具体实现，将信号机的方案转化为标准的1049方案，这种转化应该是双向的
    /**
     * 转换信号机方案为1049方案
     * @param controllerPlan 信号机方案参数
     * @return 1049方案参数
     */
    DevicePlanData toStdPlan(Object controllerPlan);
    /**
     * 下发方案到信号机
     * @param plan 1049信号机方案参数
     * @return 下发结果
     */
    SyncResult execToControllerPlan(DevicePlanData plan);
}
