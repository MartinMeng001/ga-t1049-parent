package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.command.CenterPlan;
import com.traffic.gat1049.repository.entity.CenterPlanEntity;
import com.traffic.gat1049.model.enums.ControlMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 中心预案转换器
 * 处理中心预案命令与实体的转换
 * 注意：根据数据库表结构，这里假设有CenterPlanEntity实体类
 */
@Component
public class CenterPlanConverter extends AbstractEntityConverter<CenterPlanEntity, CenterPlan> {

    @Override
    public CenterPlan toProtocol(CenterPlanEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CenterPlan protocol = new CenterPlan();

            // 基础字段映射
            //protocol.setCrossId(entity.getCrossId());

            // 枚举转换
            if (entity.getControlMode() != null) {
                protocol.setControlMode(ControlMode.fromCode(entity.getControlMode()));
            }

            protocol.setMaxRunTime(entity.getMaxRunTime());

            // JSON字段处理 - 配时方案参数
            if (StringUtils.hasText(entity.getPlanData())) {
                // 这里需要根据实际的PlanParam结构进行反序列化
                // protocol.setPlanParam(deserializeFromJson(entity.getPlanData(), PlanParam.class));
            }

            validateConversion(entity, protocol);

            logger.debug("中心预案实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("中心预案转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("中心预案转换失败", e);
        }
    }

    @Override
    public CenterPlanEntity toEntity(CenterPlan protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            CenterPlanEntity entity = new CenterPlanEntity();

            // 基础字段映射
            //entity.setCrossId(protocol.getCrossId());

            // 枚举转换
            if (protocol.getControlMode() != null) {
                entity.setControlMode(protocol.getControlMode().getCode());
            }

            entity.setMaxRunTime(protocol.getMaxRunTime());

            // JSON字段处理
            if (protocol.getPlanParam() != null) {
                entity.setPlanData(serializeToJson(protocol.getPlanParam()));
            }

            // 设置默认值
            entity.setStatus(CenterPlanEntity.PlanStatus.ACTIVE);
            entity.setStartTime(LocalDateTime.now());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("中心预案协议转实体成功: {}", protocol.getControlMode());
            return entity;

        } catch (Exception e) {
            logger.error("中心预案转换失败: {}", protocol.getControlMode(), e);
            throw new DataConversionException("中心预案转换失败", e);
        }
    }

    @Override
    public void updateEntity(CenterPlan protocol, CenterPlanEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
//            if (StringUtils.hasText(protocol.getCrossId())) {
//                entity.setCrossId(protocol.getCrossId());
//            }
            if (protocol.getControlMode() != null) {
                entity.setControlMode(protocol.getControlMode().getCode());
            }
            if (protocol.getMaxRunTime() != null) {
                entity.setMaxRunTime(protocol.getMaxRunTime());
            }
            if (protocol.getPlanParam() != null) {
                entity.setPlanData(serializeToJson(protocol.getPlanParam()));
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("中心预案实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("中心预案更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("中心预案更新失败", e);
        }
    }
}
