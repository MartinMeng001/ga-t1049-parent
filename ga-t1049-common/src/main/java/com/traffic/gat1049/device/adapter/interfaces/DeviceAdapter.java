package com.traffic.gat1049.device.adapter.interfaces;

import com.traffic.gat1049.device.adapter.registry.AdapterInfo;
import com.traffic.gat1049.device.adapter.model.AdapterStatus;

/**
 * 通用设备适配器接口
 * 所有设备适配器的基础接口
 */
public interface DeviceAdapter {

    /**
     * 获取适配器信息
     * @return 适配器信息
     */
    AdapterInfo getAdapterInfo();

    /**
     * 初始化适配器
     * @return 初始化是否成功
     */
    boolean initialize();

    /**
     * 启动适配器
     * @return 启动是否成功
     */
    boolean start();

    /**
     * 停止适配器
     * @return 停止是否成功
     */
    boolean stop();

    /**
     * 销毁适配器资源
     */
    void destroy();

    /**
     * 获取适配器状态
     * @return 适配器状态
     */
    AdapterStatus getStatus();

    /**
     * 检查适配器健康状态
     * @return 健康检查结果
     */
    boolean healthCheck();

    /**
     * 获取支持的设备类型列表
     * @return 设备类型列表
     */
    String[] getSupportedDeviceTypes();

    /**
     * 获取支持的协议版本
     * @return 协议版本
     */
    String getSupportedProtocolVersion();
}
