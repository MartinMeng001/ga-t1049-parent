package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.model.enums.InterventionType;
import com.traffic.gat1049.protocol.model.command.AdjustStage;
import com.traffic.gat1049.repository.entity.AdjustStageLogEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 阶段干预转换器
 * 处理阶段干预命令与日志实体的转换
 * 注意：根据数据库表结构，对应adjust_stage_log表
 */
@Component
public class AdjustStageConverter extends AbstractEntityConverter<AdjustStageLogEntity, AdjustStage> {

    @Override
    public AdjustStage toProtocol(AdjustStageLogEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            AdjustStage protocol = new AdjustStage();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setStageNo(entity.getStageNo());

            // 枚举转换
            if (entity.getType() != null) {
                protocol.setType(InterventionType.fromCode(String.valueOf(entity.getType())));
            }

            protocol.setLen(entity.getLen());

            validateConversion(entity, protocol);

            logger.debug("阶段干预实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("阶段干预转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("阶段干预转换失败", e);
        }
    }

    @Override
    public AdjustStageLogEntity toEntity(AdjustStage protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            AdjustStageLogEntity entity = new AdjustStageLogEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setStageNo(protocol.getStageNo());

            // 枚举转换
            if (protocol.getType() != null) {
                entity.setType(Integer.parseInt(protocol.getType().getCode()));
            }

            entity.setLen(protocol.getLen());

            // 设置默认值
            entity.setExecuteTime(LocalDateTime.now());
            entity.setResult(AdjustStageLogEntity.ExecuteResult.SUCCESS);

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("阶段干预协议转实体成功: {}", protocol.getCrossId());
            return entity;

        } catch (Exception e) {
            logger.error("阶段干预转换失败: {}", protocol.getCrossId(), e);
            throw new DataConversionException("阶段干预转换失败", e);
        }
    }

    @Override
    public void updateEntity(AdjustStage protocol, AdjustStageLogEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }
            if (protocol.getStageNo() != null) {
                entity.setStageNo(protocol.getStageNo());
            }
            if (protocol.getType() != null) {
                entity.setType(Integer.parseInt(protocol.getType().getCode()));
            }
            if (protocol.getLen() != null) {
                entity.setLen(protocol.getLen());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("阶段干预实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("阶段干预更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("阶段干预更新失败", e);
        }
    }
}
