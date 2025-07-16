package com.traffic.gat1049.device.adapter.registry;

import com.traffic.gat1049.device.adapter.model.AdapterType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 适配器信息
 */
@Data
@Builder
public class AdapterInfo {

    /**
     * 适配器名称
     */
    private String adapterName;

    /**
     * 设备品牌
     */
    private String brand;

    /**
     * 适配器类型
     */
    private AdapterType adapterType;

    /**
     * 版本号
     */
    private String version;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 供应商信息
     */
    private String vendor;

    /**
     * 支持的协议列表
     */
    private List<String> supportedProtocols;

    /**
     * 支持的设备型号
     */
    private List<String> supportedDeviceModels;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 配置参数
     */
    private java.util.Map<String, Object> configParameters;

    /**
     * 验证适配器信息是否有效
     */
    public boolean isValid() {
        return brand != null && !brand.trim().isEmpty() &&
                adapterType != null &&
                version != null && !version.trim().isEmpty();
    }
}
