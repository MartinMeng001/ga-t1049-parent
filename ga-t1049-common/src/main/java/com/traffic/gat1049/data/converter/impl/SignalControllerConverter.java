// ================================================================
// 4. SignalController转换器实现示例（复杂对象转换）
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.repository.entity.SignalControllerParamEntity;
import com.traffic.gat1049.protocol.model.intersection.SignalController;
import com.traffic.gat1049.model.enums.CommMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 信号机转换器实现
 * 演示复杂对象和JSON字段转换模式
 */
@Component
public class SignalControllerConverter extends AbstractEntityConverter<SignalControllerParamEntity, SignalController> {

    @Override
    public SignalController toProtocol(SignalControllerParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SignalController protocol = new SignalController();

            // 基础字段映射
            protocol.setSignalControllerID(entity.getSignalControllerId());
            protocol.setSupplier(entity.getSupplier());
            protocol.setType(entity.getType());
            protocol.setId(entity.getSignalControllerId());

            // 枚举转换
            if (entity.getCommMode() != null) {
                protocol.setCommMode(CommMode.fromCode(entity.getCommMode()));
            }

            // 网络配置
            protocol.setIp(entity.getIp());
            protocol.setPort(entity.getPort());
            protocol.setSubMask(entity.getSubMask());
            protocol.setGateway(entity.getGateway());

            // 位置信息
            protocol.setLongitude(entity.getLongitude());
            protocol.setLatitude(entity.getLatitude());
            protocol.setHasDoorStatus(entity.getHasDoorStatus());

            validateConversion(entity, protocol);

            logger.debug("信号机实体转协议成功: {}", entity.getSignalControllerId());
            return protocol;

        } catch (Exception e) {
            logger.error("信号机转换失败: {}", entity.getSignalControllerId(), e);
            throw new DataConversionException("信号机转换失败", e);
        }
    }

    @Override
    public SignalControllerParamEntity toEntity(SignalController protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            SignalControllerParamEntity entity = new SignalControllerParamEntity();

            // 基础字段映射
            entity.setSignalControllerId(protocol.getSignalControllerID());
            entity.setSupplier(protocol.getSupplier());
            entity.setType(protocol.getType());
            entity.setIdCode(protocol.getId());

            // 枚举转换
            if (protocol.getCommMode() != null) {
                entity.setCommMode(protocol.getCommMode().getCode());
            }

            // 网络配置
            entity.setIp(protocol.getIp());
            entity.setPort(protocol.getPort());
            entity.setSubMask(protocol.getSubMask());
            entity.setGateway(protocol.getGateway());

            // 位置信息
            entity.setLongitude(protocol.getLongitude());
            entity.setLatitude(protocol.getLatitude());
            entity.setHasDoorStatus(protocol.getHasDoorStatus());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("信号机协议转实体成功: {}", protocol.getSignalControllerID());
            return entity;

        } catch (Exception e) {
            logger.error("信号机转换失败: {}", protocol.getSignalControllerID(), e);
            throw new DataConversionException("信号机转换失败", e);
        }
    }

    @Override
    public void updateEntity(SignalController protocol, SignalControllerParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 基础字段更新
            if (StringUtils.hasText(protocol.getSupplier())) {
                entity.setSupplier(protocol.getSupplier());
            }
            if (StringUtils.hasText(protocol.getType())) {
                entity.setType(protocol.getType());
            }
            if (StringUtils.hasText(protocol.getId())) {
                entity.setIdCode(protocol.getId());
            }

            // 枚举字段更新
            if (protocol.getCommMode() != null) {
                entity.setCommMode(protocol.getCommMode().getCode());
            }

            // 网络配置更新
            if (StringUtils.hasText(protocol.getIp())) {
                entity.setIp(protocol.getIp());
            }
            if (protocol.getPort() != null) {
                entity.setPort(protocol.getPort());
            }
            if (StringUtils.hasText(protocol.getSubMask())) {
                entity.setSubMask(protocol.getSubMask());
            }
            if (StringUtils.hasText(protocol.getGateway())) {
                entity.setGateway(protocol.getGateway());
            }

            // 位置信息更新
            if (protocol.getLongitude() != null) {
                entity.setLongitude(protocol.getLongitude());
            }
            if (protocol.getLatitude() != null) {
                entity.setLatitude(protocol.getLatitude());
            }
            if (protocol.getHasDoorStatus() != null) {
                entity.setHasDoorStatus(protocol.getHasDoorStatus());
            }


            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("信号机实体更新成功: {}", entity.getSignalControllerId());

        } catch (Exception e) {
            logger.error("信号机更新失败: {}", entity.getSignalControllerId(), e);
            throw new DataConversionException("信号机更新失败", e);
        }
    }
}