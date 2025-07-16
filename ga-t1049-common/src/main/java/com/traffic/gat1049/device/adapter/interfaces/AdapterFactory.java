package com.traffic.gat1049.device.adapter.interfaces;

import com.traffic.gat1049.device.adapter.model.AdapterType;

public interface AdapterFactory {

    /**
     * 创建适配器实例
     * @param adapterType 适配器类型
     * @param brand 设备品牌
     * @return 适配器实例
     */
    DeviceAdapter createAdapter(AdapterType adapterType, String brand);

    /**
     * 创建信号机适配器
     * @param brand 设备品牌
     * @return 信号机适配器实例
     */
    SignalControllerAdapter createSignalControllerAdapter(String brand);

    /**
     * 检查是否支持指定品牌
     * @param brand 设备品牌
     * @return 是否支持
     */
    boolean supportsAdapter(String brand);

    /**
     * 获取支持的品牌列表
     * @return 品牌列表
     */
    String[] getSupportedBrands();
}
