package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.protocol.model.system.SysInfo;
import com.traffic.gat1049.repository.entity.GatSysInfoEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 系统信息转换器实现
 */
@Component
public class SysInfoConverter extends AbstractEntityConverter<GatSysInfoEntity, SysInfo> {

    @Override
    public SysInfo toProtocol(GatSysInfoEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SysInfo protocol = new SysInfo();

            protocol.setSystemID(entity.getSystemId());
            protocol.setSystemType(entity.getSystemType());
            protocol.setSystemVersion(entity.getSystemVersion());
            protocol.setManufacturer(entity.getManufacturer());
            protocol.setModel(entity.getModel());
            protocol.setSerialNumber(entity.getSerialNumber());
            protocol.setDescription(entity.getDescription());

            validateConversion(entity, protocol);

            logger.debug("系统信息实体转协议成功: {}", entity.getSystemId());
            return protocol;

        } catch (Exception e) {
            logger.error("系统信息实体转协议失败: {}", entity.getSystemId(), e);
            throw new DataConversionException("系统信息转换失败", e);
        }
    }

    @Override
    public GatSysInfoEntity toEntity(SysInfo protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            GatSysInfoEntity entity = new GatSysInfoEntity();

            entity.setSystemId(protocol.getSystemID());
            entity.setSystemType(protocol.getSystemType());
            entity.setSystemVersion(protocol.getSystemVersion());
            entity.setManufacturer(protocol.getManufacturer());
            entity.setModel(protocol.getModel());
            entity.setSerialNumber(protocol.getSerialNumber());
            entity.setDescription(protocol.getDescription());

            // 设置时间戳
            LocalDateTime now = LocalDateTime.now();
            if (entity.getId() == null) {
                entity.setCreatedAt(now);
            }
            entity.setUpdatedAt(now);

            validateConversion(entity, protocol);

            logger.debug("系统信息协议转实体成功: {}", protocol.getSystemID());
            return entity;

        } catch (Exception e) {
            logger.error("系统信息协议转实体失败: {}", protocol.getSystemID(), e);
            throw new DataConversionException("系统信息转换失败", e);
        }
    }
}
