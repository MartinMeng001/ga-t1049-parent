package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.repository.entity.DayPlanParamEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 日计划参数转换器实现
 * 参考SysInfo converter实现模式
 */
@Component
public class DayPlanParamConverter extends AbstractEntityConverter<DayPlanParamEntity, DayPlanParam> {

    @Override
    public DayPlanParam toProtocol(DayPlanParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            DayPlanParam protocol = new DayPlanParam();

            // 直接字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setDayPlanNo(entity.getDayPlanNo());
            protocol.setDayPlanName(entity.getDayPlanName());

            // 注意：Period列表需要从相关表中查询，这里暂时设置为null
            // 在实际使用时，可能需要增强版本的转换器来处理关联数据
            protocol.setPeriodList(null);

            validateConversion(entity, protocol);

            logger.debug("日计划实体转协议成功: {} - {}", entity.getCrossId(), entity.getDayPlanNo());
            return protocol;

        } catch (Exception e) {
            logger.error("日计划转换失败: {} - {}", entity.getCrossId(), entity.getDayPlanNo(), e);
            throw new DataConversionException("日计划转换失败", e);
        }
    }

    @Override
    public DayPlanParamEntity toEntity(DayPlanParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            DayPlanParamEntity entity = new DayPlanParamEntity();

            // 直接字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setDayPlanNo(protocol.getDayPlanNo());
            entity.setDayPlanName(protocol.getDayPlanName());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("日计划协议转实体成功: {} - {}", protocol.getCrossId(), protocol.getDayPlanNo());
            return entity;

        } catch (Exception e) {
            logger.error("日计划转换失败: {} - {}", protocol.getCrossId(), protocol.getDayPlanNo(), e);
            throw new DataConversionException("日计划转换失败", e);
        }
    }

    @Override
    public void updateEntity(DayPlanParam protocol, DayPlanParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }
            if (protocol.getDayPlanNo() != null) {
                entity.setDayPlanNo(protocol.getDayPlanNo());
            }
            if (StringUtils.hasText(protocol.getDayPlanName())) {
                entity.setDayPlanName(protocol.getDayPlanName());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("日计划实体更新成功: {} - {}", entity.getCrossId(), entity.getDayPlanNo());

        } catch (Exception e) {
            logger.error("日计划更新失败: {} - {}", entity.getCrossId(), entity.getDayPlanNo(), e);
            throw new DataConversionException("日计划更新失败", e);
        }
    }
}
