// ================================================================
// 3. SysInfo转换器实现示例
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.system.SysInfo;
import com.traffic.gat1049.repository.entity.SysInfoEntity;
import org.springframework.stereotype.Component;

/**
 * 系统信息转换器实现
 * 演示基础数据转换模式
 */
@Component
public class SysInfoConverter extends AbstractEntityConverter<SysInfoEntity, SysInfo> {

    @Override
    public SysInfo toProtocol(SysInfoEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SysInfo protocol = new SysInfo();

            // 直接字段映射
            //protocol.setSystemID(entity.getSystemId());
            protocol.setSysName(entity.getSysName());
            //protocol.setSystemType(entity.getSysType());
            protocol.setSysVersion(entity.getSysVersion());
            protocol.setSupplier(entity.getSupplier());


            validateConversion(entity, protocol);

            logger.debug("系统信息实体转协议成功: {}", entity.getSystemId());
            return protocol;

        } catch (Exception e) {
            logger.error("系统信息转换失败: {}", entity.getSystemId(), e);
            throw new DataConversionException("系统信息转换失败", e);
        }
    }

    @Override
    public SysInfoEntity toEntity(SysInfo protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            SysInfoEntity entity = new SysInfoEntity();

            // 直接字段映射
            //entity.setSystemId(protocol.getSystemID());
            entity.setSysName(protocol.getSysName());
            //entity.setSysType(protocol.getSystemType());
            entity.setSysVersion(protocol.getSysVersion());
            entity.setSupplier(protocol.getSupplier());
            //entity.setModel(protocol.getModel());
            //entity.setSerialNumber(protocol.getSerialNumber());
            //entity.setDescription(protocol.getDescription());

            // 设置默认值
            //entity.setIsActive(true);

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("系统信息协议转实体成功: {}", protocol.getSysName());
            return entity;

        } catch (Exception e) {
            logger.error("系统信息转换失败: {}", protocol.getSysName(), e);
            throw new DataConversionException("系统信息转换失败", e);
        }
    }

    @Override
    public void updateEntity(SysInfo protocol, SysInfoEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (protocol.getSysName() != null) {
                entity.setSysName(protocol.getSysName());
            }
//            if (protocol.getSystemType() != null) {
//                entity.setSysType(protocol.getSystemType());
//            }
            if (protocol.getSysVersion() != null) {
                entity.setSysVersion(protocol.getSysVersion());
            }
            if (protocol.getSupplier() != null) {
                entity.setSupplier(protocol.getSupplier());
            }
//            if (protocol.getModel() != null) {
//                entity.setModel(protocol.getModel());
//            }
//            if (protocol.getSerialNumber() != null) {
//                entity.setSerialNumber(protocol.getSerialNumber());
//            }
//            if (protocol.getDescription() != null) {
//                entity.setDescription(protocol.getDescription());
//            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("系统信息实体更新成功: {}", entity.getSysName());

        } catch (Exception e) {
            logger.error("系统信息更新失败: {}", entity.getSysName(), e);
            throw new DataConversionException("系统信息更新失败", e);
        }
    }
}