package com.traffic.gat1049.device.adapter.registry;

import com.traffic.gat1049.device.adapter.model.AdapterType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 适配器元数据
 * 用于存储适配器的详细信息和统计数据
 */
@Data
@Builder
public class AdapterMetadata {

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
     * 支持的协议列表
     */
    private List<String> supportedProtocols;

    /**
     * 注册时间
     */
    private LocalDateTime registrationTime;

    /**
     * 最后活动时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 适配器类名
     */
    private String adapterClass;

    /**
     * 连接的设备数量
     */
    private int connectedDeviceCount;

    /**
     * 总处理请求数
     */
    private long totalRequestCount;

    /**
     * 成功处理请求数
     */
    private long successRequestCount;

    /**
     * 失败处理请求数
     */
    private long failedRequestCount;

    /**
     * 平均响应时间（毫秒）
     */
    private double averageResponseTime;

    /**
     * 扩展属性
     */
    private Map<String, Object> properties;

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (totalRequestCount == 0) {
            return 0.0;
        }
        return (double) successRequestCount / totalRequestCount * 100;
    }

    /**
     * 更新请求统计
     */
    public void updateRequestStats(boolean success, long responseTime) {
        totalRequestCount++;
        if (success) {
            successRequestCount++;
        } else {
            failedRequestCount++;
        }

        // 更新平均响应时间（简单移动平均）
        averageResponseTime = (averageResponseTime * (totalRequestCount - 1) + responseTime) / totalRequestCount;
        lastActiveTime = LocalDateTime.now();
    }
}
