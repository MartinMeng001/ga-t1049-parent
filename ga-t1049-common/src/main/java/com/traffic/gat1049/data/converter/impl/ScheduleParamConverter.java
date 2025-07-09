package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.model.enums.ScheduleType;
import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.repository.entity.ScheduleParamEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 调度参数转换器实现
 * 参考SysInfo converter实现模式
 *
 * 字段映射说明：
 * - schedule_no: 调度号(1-999)
 * - schedule_name: 调度名称
 * - type: 调度类型：1-特殊日调度；2-时间段周调度；3-周调度
 * - start_day: 开始月日(MM-DD)
 * - end_day: 结束月日(MM-DD)
 * - week_day: 周几(1-7分别代表周一至周日)
 * - day_plan_no: 日计划号
 */
@Component
public class ScheduleParamConverter extends AbstractEntityConverter<ScheduleParamEntity, ScheduleParam> {

    @Override
    public ScheduleParam toProtocol(ScheduleParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            ScheduleParam protocol = new ScheduleParam();

            // 字段映射（基于SQL表定义）
            protocol.setCrossId(entity.getCrossId());
            protocol.setScheduleNo(entity.getScheduleNo());
            protocol.setScheduleName(entity.getScheduleName());
            protocol.setType(ScheduleType.fromCode(String.valueOf(entity.getType())));
            protocol.setStartDay(entity.getStartDay());
            protocol.setEndDay(entity.getEndDay());
            protocol.setWeekDay(entity.getWeekDay());
            protocol.setDayPlanNo(entity.getDayPlanNo());

            validateConversion(entity, protocol);

            logger.debug("调度参数实体转协议成功: {} - {}", entity.getCrossId(), entity.getScheduleNo());
            return protocol;

        } catch (Exception e) {
            logger.error("调度参数转换失败: {} - {}", entity.getCrossId(), entity.getScheduleNo(), e);
            throw new DataConversionException("调度参数转换失败", e);
        }
    }

    @Override
    public ScheduleParamEntity toEntity(ScheduleParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            ScheduleParamEntity entity = new ScheduleParamEntity();

            // 字段映射（基于SQL表定义）
            entity.setCrossId(protocol.getCrossId());
            entity.setScheduleNo(protocol.getScheduleNo());
            entity.setScheduleName(protocol.getScheduleName());
            entity.setType(Integer.parseInt(protocol.getType().getCode()));
            entity.setStartDay(protocol.getStartDay());
            entity.setEndDay(protocol.getEndDay());
            entity.setWeekDay(protocol.getWeekDay());
            entity.setDayPlanNo(protocol.getDayPlanNo());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("调度参数协议转实体成功: {} - {}", protocol.getCrossId(), protocol.getScheduleNo());
            return entity;

        } catch (Exception e) {
            logger.error("调度参数转换失败: {} - {}", protocol.getCrossId(), protocol.getScheduleNo(), e);
            throw new DataConversionException("调度参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(ScheduleParam protocol, ScheduleParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段（基于SQL表定义）
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }
            if (protocol.getScheduleNo() != null) {
                entity.setScheduleNo(protocol.getScheduleNo());
            }
            if (StringUtils.hasText(protocol.getScheduleName())) {
                entity.setScheduleName(protocol.getScheduleName());
            }
            if (protocol.getType() != null) {
                entity.setType(Integer.parseInt(protocol.getType().getCode()));
            }
            if (StringUtils.hasText(protocol.getStartDay())) {
                entity.setStartDay(protocol.getStartDay());
            }
            if (StringUtils.hasText(protocol.getEndDay())) {
                entity.setEndDay(protocol.getEndDay());
            }
            if (protocol.getWeekDay() != null) {
                entity.setWeekDay(protocol.getWeekDay());
            }
            if (protocol.getDayPlanNo() != null) {
                entity.setDayPlanNo(protocol.getDayPlanNo());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("调度参数实体更新成功: {} - {}", entity.getCrossId(), entity.getScheduleNo());

        } catch (Exception e) {
            logger.error("调度参数更新失败: {} - {}", entity.getCrossId(), entity.getScheduleNo(), e);
            throw new DataConversionException("调度参数更新失败", e);
        }
    }
}
