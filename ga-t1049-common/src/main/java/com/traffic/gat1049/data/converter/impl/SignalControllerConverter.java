package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.model.enums.CommMode;
import com.traffic.gat1049.protocol.model.intersection.SignalController;
import com.traffic.gat1049.repository.entity.GatSignalControllerEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 信号控制器转换器实现
 * 处理GatSignalControllerEntity与SignalController之间的转换
 */
@Component
public class SignalControllerConverter extends AbstractEntityConverter<GatSignalControllerEntity, SignalController> {

    @Override
    public SignalController toProtocol(GatSignalControllerEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SignalController protocol = new SignalController();

            // 基础字段转换
            protocol.setSignalControllerID(entity.getControllerId());
            protocol.setSupplier(entity.getVendor());
            protocol.setType(entity.getDeviceModel());
            protocol.setId(entity.getControllerId());

            // 枚举字段转换
            if (entity.getCommMode() != null) {
                protocol.setCommMode(CommMode.fromValue(entity.getCommMode()));
            }

            // 复杂字段转换
            protocol.setCommAddress(parseCommAddress(entity.getCommAddress()));
            protocol.setCapabilitySet(deserializeFromJson(entity.getCapabilities(), CapabilitySet.class));

            // 自定义参数转换
            if (StringUtils.hasText(entity.getCustomParams())) {
                Map<String, Object> customParams = deserializeFromJson(entity.getCustomParams(), Map.class);
                protocol.setCustomParams(customParams);
            }

            // 状态字段
            protocol.setStatus(entity.getStatus());

            validateConversion(entity, protocol);

            logger.debug("实体转协议成功: {}", entity.getControllerId());
            return protocol;

        } catch (Exception e) {
            logger.error("信号控制器实体转协议失败: {}", entity.getControllerId(), e);
            throw new DataConversionException("信号控制器转换失败", e);
        }
    }

    @Override
    public GatSignalControllerEntity toEntity(SignalController protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            GatSignalControllerEntity entity = new GatSignalControllerEntity();

            // 基础字段转换
            entity.setControllerId(protocol.getSignalControllerID());
            entity.setSupplier(protocol.getSupplier());
            entity.setType(protocol.getType());
            entity.setIdentifier(protocol.getIdentifier());
            entity.setVersion(protocol.getVersion());
            entity.setDescription(protocol.getDescription());

            // 枚举字段转换
            if (protocol.getCommMode() != null) {
                entity.setCommMode(protocol.getCommMode().getValue());
            }

            // 复杂字段转换
            entity.setCommAddress(serializeCommAddress(protocol.getCommAddress()));
            entity.setCapabilities(serializeToJson(protocol.getCapabilitySet()));
            entity.setCustomParams(serializeToJson(protocol.getCustomParams()));

            // 状态字段
            entity.setStatus(protocol.getStatus());

            // 设置时间戳
            LocalDateTime now = LocalDateTime.now();
            if (entity.getId() == null) {
                entity.setCreatedAt(now);
            }
            entity.setUpdatedAt(now);

            validateConversion(entity, protocol);

            logger.debug("协议转实体成功: {}", protocol.getSignalControllerID());
            return entity;

        } catch (Exception e) {
            logger.error("信号控制器协议转实体失败: {}", protocol.getSignalControllerID(), e);
            throw new DataConversionException("信号控制器转换失败", e);
        }
    }

    @Override
    public void updateEntity(SignalController protocol, GatSignalControllerEntity entity) {
        if (protocol == null || entity == null) {
            return;
        }

        // 只更新非空字段
        if (StringUtils.hasText(protocol.getSupplier())) {
            entity.setSupplier(protocol.getSupplier());
        }
        if (StringUtils.hasText(protocol.getType())) {
            entity.setType(protocol.getType());
        }
        if (StringUtils.hasText(protocol.getIdentifier())) {
            entity.setIdentifier(protocol.getIdentifier());
        }
        if (StringUtils.hasText(protocol.getVersion())) {
            entity.setVersion(protocol.getVersion());
        }
        if (StringUtils.hasText(protocol.getDescription())) {
            entity.setDescription(protocol.getDescription());
        }

        if (protocol.getCommMode() != null) {
            entity.setCommMode(protocol.getCommMode().getValue());
        }

        if (protocol.getCommAddress() != null) {
            entity.setCommAddress(serializeCommAddress(protocol.getCommAddress()));
        }

        if (protocol.getCapabilitySet() != null) {
            entity.setCapabilities(serializeToJson(protocol.getCapabilitySet()));
        }

        if (protocol.getCustomParams() != null) {
            entity.setCustomParams(serializeToJson(protocol.getCustomParams()));
        }

        if (protocol.getStatus() != null) {
            entity.setStatus(protocol.getStatus());
        }

        // 更新时间戳
        entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 解析通信地址
     */
    private CommAddress parseCommAddress(String addressStr) {
        if (!StringUtils.hasText(addressStr)) {
            return null;
        }

        try {
            String[] parts = addressStr.split(":");
            if (parts.length >= 2) {
                CommAddress address = new CommAddress();
                address.setIp(parts[0]);
                address.setPort(Integer.parseInt(parts[1]));
                return address;
            }
        } catch (Exception e) {
            logger.warn("解析通信地址失败: {}", addressStr, e);
        }

        return null;
    }

    /**
     * 序列化通信地址
     */
    private String serializeCommAddress(CommAddress address) {
        if (address == null) {
            return null;
        }

        return String.format("%s:%d",
                address.getIp() != null ? address.getIp() : "",
                address.getPort() != null ? address.getPort() : 0);
    }
}
