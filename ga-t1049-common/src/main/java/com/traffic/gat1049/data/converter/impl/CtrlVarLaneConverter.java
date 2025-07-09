package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.command.CtrlVarLane;
import com.traffic.gat1049.repository.entity.CtrlVarLaneLogEntity;
import com.traffic.gat1049.model.enums.LaneMovement;
import com.traffic.gat1049.model.enums.VarLaneMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 可变导向车道控制转换器
 * 处理可变导向车道控制命令与日志实体的转换
 * 注意：根据数据库表结构，对应ctrl_var_lane_log表
 */
@Component
public class CtrlVarLaneConverter extends AbstractEntityConverter<CtrlVarLaneLogEntity, CtrlVarLane> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public CtrlVarLane toProtocol(CtrlVarLaneLogEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CtrlVarLane protocol = new CtrlVarLane();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setLaneNo(entity.getLaneNo());

            // 枚举转换
            if (entity.getMovement() != null) {
                protocol.setMovement(LaneMovement.fromCode(entity.getMovement()));
            }
            if (entity.getCtrlMode() != null) {
                protocol.setCtrlMode(VarLaneMode.fromCode(entity.getCtrlMode()));
            }

            // 时间字段转换
            if (entity.getStartTime() != null) {
                protocol.setStartTime(entity.getStartTime().format(TIME_FORMATTER));
            }
            if (entity.getEndTime() != null) {
                protocol.setEndTime(entity.getEndTime().format(TIME_FORMATTER));
            }

            validateConversion(entity, protocol);

            logger.debug("可变导向车道控制实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("可变导向车道控制转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("可变导向车道控制转换失败", e);
        }
    }

    @Override
    public CtrlVarLaneLogEntity toEntity(CtrlVarLane protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            CtrlVarLaneLogEntity entity = new CtrlVarLaneLogEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setLaneNo(protocol.getLaneNo());

            // 枚举转换
            if (protocol.getMovement() != null) {
                entity.setMovement(protocol.getMovement().getCode());
            }
            if (protocol.getCtrlMode() != null) {
                entity.setCtrlMode(protocol.getCtrlMode().getCode());
            }

            // 时间字段转换
            if (StringUtils.hasText(protocol.getStartTime())) {
                entity.setStartTime(LocalDateTime.parse(protocol.getStartTime(), TIME_FORMATTER));
            } else {
                entity.setStartTime(LocalDateTime.now());
            }

            if (StringUtils.hasText(protocol.getEndTime())) {
                entity.setEndTime(LocalDateTime.parse(protocol.getEndTime(), TIME_FORMATTER));
            }

            // 设置默认值
            entity.setStatus(CtrlVarLaneLogEntity.ControlStatus.ACTIVE);

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("可变导向车道控制协议转实体成功: {}", protocol.getCrossId());
            return entity;

        } catch (Exception e) {
            logger.error("可变导向车道控制转换失败: {}", protocol.getCrossId(), e);
            throw new DataConversionException("可变导向车道控制转换失败", e);
        }
    }

    @Override
    public void updateEntity(CtrlVarLane protocol, CtrlVarLaneLogEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }
            if (protocol.getLaneNo() != null) {
                entity.setLaneNo(protocol.getLaneNo());
            }
            if (protocol.getMovement() != null) {
                entity.setMovement(protocol.getMovement().getCode());
            }
            if (protocol.getCtrlMode() != null) {
                entity.setCtrlMode(protocol.getCtrlMode().getCode());
            }
            if (StringUtils.hasText(protocol.getStartTime())) {
                entity.setStartTime(LocalDateTime.parse(protocol.getStartTime(), TIME_FORMATTER));
            }
            if (StringUtils.hasText(protocol.getEndTime())) {
                entity.setEndTime(LocalDateTime.parse(protocol.getEndTime(), TIME_FORMATTER));
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("可变导向车道控制实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("可变导向车道控制更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("可变导向车道控制更新失败", e);
        }
    }
}
