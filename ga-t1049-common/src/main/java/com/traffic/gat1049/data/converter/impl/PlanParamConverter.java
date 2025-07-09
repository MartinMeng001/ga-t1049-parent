package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.repository.entity.PlanParamEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 配时方案参数转换器实现
 * 参考SysInfo converter实现模式
 */
@Component
public class PlanParamConverter extends AbstractEntityConverter<PlanParamEntity, PlanParam> {

    @Override
    public PlanParam toProtocol(PlanParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            PlanParam protocol = new PlanParam();

            // 直接字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setPlanNo(entity.getPlanNo());
            protocol.setPlanName(entity.getPlanName());
            protocol.setCycleLen(entity.getCycleLen());
            protocol.setCoordStageNo(entity.getCoordStageNo());
            protocol.setOffset(entity.getOffset());

            validateConversion(entity, protocol);

            logger.debug("配时方案实体转协议成功: {} - {}", entity.getCrossId(), entity.getPlanNo());
            return protocol;

        } catch (Exception e) {
            logger.error("配时方案转换失败: {} - {}", entity.getCrossId(), entity.getPlanNo(), e);
            throw new DataConversionException("配时方案转换失败", e);
        }
    }

    @Override
    public PlanParamEntity toEntity(PlanParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            PlanParamEntity entity = new PlanParamEntity();

            // 直接字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setPlanNo(protocol.getPlanNo());
            entity.setPlanName(protocol.getPlanName());
            entity.setCycleLen(protocol.getCycleLen());
            entity.setCoordStageNo(protocol.getCoordStageNo());
            entity.setOffset(protocol.getOffset());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("配时方案协议转实体成功: {} - {}", protocol.getCrossId(), protocol.getPlanNo());
            return entity;

        } catch (Exception e) {
            logger.error("配时方案转换失败: {} - {}", protocol.getCrossId(), protocol.getPlanNo(), e);
            throw new DataConversionException("配时方案转换失败", e);
        }
    }

    @Override
    public void updateEntity(PlanParam protocol, PlanParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }
            if (protocol.getPlanNo() != null) {
                entity.setPlanNo(protocol.getPlanNo());
            }
            if (StringUtils.hasText(protocol.getPlanName())) {
                entity.setPlanName(protocol.getPlanName());
            }
            if (protocol.getCycleLen() != null) {
                entity.setCycleLen(protocol.getCycleLen());
            }
            if (protocol.getCoordStageNo() != null) {
                entity.setCoordStageNo(protocol.getCoordStageNo());
            }
            if (protocol.getOffset() != null) {
                entity.setOffset(protocol.getOffset());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("配时方案实体更新成功: {} - {}", entity.getCrossId(), entity.getPlanNo());

        } catch (Exception e) {
            logger.error("配时方案更新失败: {} - {}", entity.getCrossId(), entity.getPlanNo(), e);
            throw new DataConversionException("配时方案更新失败", e);
        }
    }
}
