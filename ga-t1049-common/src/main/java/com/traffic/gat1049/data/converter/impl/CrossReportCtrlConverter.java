package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.model.enums.ReportCommand;
import com.traffic.gat1049.model.enums.ReportDataType;
import com.traffic.gat1049.protocol.model.command.CrossReportCtrl;
import com.traffic.gat1049.repository.entity.CrossReportCtrlEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 路口数据上传控制转换器
 * 处理路口数据上传控制命令与实体的转换
 */
@Component
public class CrossReportCtrlConverter extends AbstractEntityConverter<CrossReportCtrlEntity, CrossReportCtrl> {

    @Override
    public CrossReportCtrl toProtocol(CrossReportCtrlEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CrossReportCtrl protocol = new CrossReportCtrl();

            // 基础字段映射
            protocol.setCrossIdList(entity.getCrossList());
            protocol.setType(ReportDataType.fromCode(entity.getDataType()));

            // 枚举转换
            if (entity.getCmd() != null) {
                protocol.setCmd(ReportCommand.fromCode(entity.getCmd().name()));
            }

            validateConversion(entity, protocol);

            logger.debug("数据上传控制实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("数据上传控制转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("数据上传控制转换失败", e);
        }
    }

    @Override
    public CrossReportCtrlEntity toEntity(CrossReportCtrl protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            CrossReportCtrlEntity entity = new CrossReportCtrlEntity();

            // 基础字段映射
            entity.setCrossList(protocol.getCrossIdList());
            entity.setDataType(protocol.getType().getCode());

            // 命令转换
            if (StringUtils.hasText(protocol.getCmd().getCode())) {
                try {
                    entity.setCmd(CrossReportCtrlEntity.ControlCommand.valueOf(
                            protocol.getCmd().getCode().substring(0, 1).toUpperCase() +
                                    protocol.getCmd().getCode().substring(1).toLowerCase()));
                } catch (IllegalArgumentException e) {
                    logger.warn("无效的控制命令: {}", protocol.getCmd());
                    entity.setCmd(CrossReportCtrlEntity.ControlCommand.Start);
                }
            }

            // 设置默认值
            entity.setStatus(CrossReportCtrlEntity.ControlStatus.ACTIVE);

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("数据上传控制协议转实体成功: {}", protocol.getCrossIdList());
            return entity;

        } catch (Exception e) {
            logger.error("数据上传控制转换失败: {}", protocol.getCrossIdList(), e);
            throw new DataConversionException("数据上传控制转换失败", e);
        }
    }

    @Override
    public void updateEntity(CrossReportCtrl protocol, CrossReportCtrlEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (protocol.getCrossIdList()!=null) {
                entity.setCrossList(protocol.getCrossIdList());
            }
            if (protocol.getType()!=null) {
                entity.setDataType(protocol.getType().getCode());
            }
            if (protocol.getCmd()!=null) {
                try {
                    entity.setCmd(CrossReportCtrlEntity.ControlCommand.valueOf(
                            protocol.getCmd().getCode().substring(0, 1).toUpperCase() +
                                    protocol.getCmd().getCode().substring(1).toLowerCase()));
                } catch (IllegalArgumentException e) {
                    logger.warn("无效的控制命令: {}", protocol.getCmd());
                }
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("数据上传控制实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("数据上传控制更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("数据上传控制更新失败", e);
        }
    }
}
