package com.traffic.gat1049.device.adapter.registry;

import com.traffic.gat1049.device.adapter.interfaces.DeviceAdapter;
import com.traffic.gat1049.device.adapter.interfaces.SignalControllerAdapter;
import com.traffic.gat1049.device.adapter.model.AdapterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 适配器注册表
 * 管理所有适配器的注册、查找和生命周期
 */
@Component
public class AdapterRegistry {

    private static final Logger logger = LoggerFactory.getLogger(AdapterRegistry.class);

    /**
     * 适配器存储 - key: brand, value: adapter
     */
    private final ConcurrentMap<String, DeviceAdapter> adapters = new ConcurrentHashMap<>();

    /**
     * 适配器元数据存储
     */
    private final ConcurrentMap<String, AdapterMetadata> metadata = new ConcurrentHashMap<>();

    /**
     * 类型映射 - key: brand, value: AdapterType
     */
    private final ConcurrentMap<String, AdapterType> typeMapping = new ConcurrentHashMap<>();

    /**
     * 注册适配器
     * @param adapter 适配器实例
     * @return 注册是否成功
     */
    public boolean register(DeviceAdapter adapter) {
        if (adapter == null) {
            logger.warn("尝试注册空的适配器");
            return false;
        }

        AdapterInfo adapterInfo = adapter.getAdapterInfo();
        if (adapterInfo == null || !adapterInfo.isValid()) {
            logger.warn("适配器信息无效，注册失败: {}", adapter.getClass().getSimpleName());
            return false;
        }

        String brand = adapterInfo.getBrand();

        try {
            // 检查是否已注册
            if (adapters.containsKey(brand)) {
                logger.warn("品牌 {} 的适配器已存在，将替换原有适配器", brand);
            }

            // 注册适配器
            adapters.put(brand, adapter);

            // 创建并存储元数据
            AdapterMetadata meta = AdapterMetadata.builder()
                    .brand(brand)
                    .adapterType(adapterInfo.getAdapterType())
                    .version(adapterInfo.getVersion())
                    .description(adapterInfo.getDescription())
                    .supportedProtocols(adapterInfo.getSupportedProtocols())
                    .registrationTime(java.time.LocalDateTime.now())
                    .adapterClass(adapter.getClass().getName())
                    .build();

            metadata.put(brand, meta);
            typeMapping.put(brand, adapterInfo.getAdapterType());

            logger.info("适配器注册成功: brand={}, type={}, class={}",
                    brand, adapterInfo.getAdapterType(), adapter.getClass().getSimpleName());

            return true;

        } catch (Exception e) {
            logger.error("注册适配器失败: brand={}", brand, e);
            return false;
        }
    }

    /**
     * 注销适配器
     * @param brand 设备品牌
     * @return 注销是否成功
     */
    public boolean unregister(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return false;
        }

        try {
            DeviceAdapter adapter = adapters.remove(brand);
            if (adapter != null) {
                // 停止并销毁适配器
                adapter.stop();
                adapter.destroy();

                metadata.remove(brand);
                typeMapping.remove(brand);

                logger.info("适配器注销成功: brand={}", brand);
                return true;
            } else {
                logger.warn("未找到要注销的适配器: brand={}", brand);
                return false;
            }

        } catch (Exception e) {
            logger.error("注销适配器失败: brand={}", brand, e);
            return false;
        }
    }

    /**
     * 获取适配器
     * @param brand 设备品牌
     * @return 适配器实例
     */
    public DeviceAdapter getAdapter(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return null;
        }
        return adapters.get(brand.toUpperCase());
    }

    /**
     * 获取信号机适配器
     * @param brand 设备品牌
     * @return 信号机适配器实例
     */
    public SignalControllerAdapter getSignalControllerAdapter(String brand) {
        DeviceAdapter adapter = getAdapter(brand);
        if (adapter instanceof SignalControllerAdapter) {
            return (SignalControllerAdapter) adapter;
        }
        return null;
    }

    /**
     * 根据控制器ID获取适配器
     * @param controllerId 控制器ID
     * @return 适配器实例
     */
    public SignalControllerAdapter getAdapterByControllerId(String controllerId) {
        // 这里需要根据控制器ID查找对应的品牌
        // 实际实现中可能需要查询数据库或配置
        String brand = extractBrandFromControllerId(controllerId);
        return getSignalControllerAdapter(brand);
    }

    /**
     * 获取所有已注册的适配器
     * @return 适配器映射
     */
    public Map<String, DeviceAdapter> getAllAdapters() {
        return new HashMap<>(adapters);
    }

    /**
     * 获取指定类型的适配器
     * @param adapterType 适配器类型
     * @return 适配器列表
     */
    public List<DeviceAdapter> getAdaptersByType(AdapterType adapterType) {
        List<DeviceAdapter> result = new ArrayList<>();

        for (Map.Entry<String, AdapterType> entry : typeMapping.entrySet()) {
            if (entry.getValue() == adapterType) {
                DeviceAdapter adapter = adapters.get(entry.getKey());
                if (adapter != null) {
                    result.add(adapter);
                }
            }
        }

        return result;
    }

    /**
     * 获取适配器元数据
     * @param brand 设备品牌
     * @return 适配器元数据
     */
    public AdapterMetadata getAdapterMetadata(String brand) {
        return metadata.get(brand);
    }

    /**
     * 获取所有适配器元数据
     * @return 元数据列表
     */
    public List<AdapterMetadata> getAllAdapterMetadata() {
        return new ArrayList<>(metadata.values());
    }

    /**
     * 检查是否支持指定品牌
     * @param brand 设备品牌
     * @return 是否支持
     */
    public boolean supportsAdapter(String brand) {
        return adapters.containsKey(brand);
    }

    /**
     * 获取支持的品牌列表
     * @return 品牌列表
     */
    public List<String> getSupportedBrands() {
        return new ArrayList<>(adapters.keySet());
    }

    /**
     * 获取注册的适配器数量
     * @return 适配器数量
     */
    public int getAdapterCount() {
        return adapters.size();
    }

    /**
     * 检查所有适配器的健康状态
     * @return 健康状态报告
     */
    public Map<String, Boolean> checkAllAdaptersHealth() {
        Map<String, Boolean> healthReport = new HashMap<>();

        for (Map.Entry<String, DeviceAdapter> entry : adapters.entrySet()) {
            String brand = entry.getKey();
            DeviceAdapter adapter = entry.getValue();

            try {
                boolean healthy = adapter.healthCheck();
                healthReport.put(brand, healthy);
            } catch (Exception e) {
                logger.error("适配器健康检查异常: brand={}", brand, e);
                healthReport.put(brand, false);
            }
        }

        return healthReport;
    }

    /**
     * 从控制器ID提取品牌信息
     * 这是一个示例实现，实际中需要根据具体的ID规则来实现
     */
    private String extractBrandFromControllerId(String controllerId) {
        if (controllerId == null || controllerId.length() < 2) {
            return null;
        }

        // 示例：假设控制器ID前两位表示品牌
        // HS = 海信, EH = 易华录, 等等
        String prefix = controllerId.substring(0, 2).toUpperCase();

        switch (prefix) {
            case "HS":
                return "HISENSE";
            case "EH":
                return "EHUALU";
            default:
                return "GENERIC";
        }
    }
}
