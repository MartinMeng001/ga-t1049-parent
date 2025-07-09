package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.runtime.VarLaneStatus;
import com.traffic.gat1049.repository.entity.LaneParamEntity;
import com.traffic.gat1049.model.enums.LaneMovement;
import com.traffic.gat1049.model.enums.VarLaneMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 可变车道状态转换器实现
 * 基于LaneParamEntity实现VarLaneStatus的转换
 */
@Component
public class VarLaneStatusConverter extends AbstractEntityConverter<LaneParamEntity, VarLaneStatus> {

    @Override
    public VarLaneStatus toProtocol(LaneParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            VarLaneStatus protocol = new VarLaneStatus();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setLaneNo(entity.getLaneNo());

            // 车道转向映射 - 从movement字段转换为LaneMovement枚举
            if (entity.getMovement() != null) {
                protocol.setCurMovement(LaneMovement.fromCode(entity.getMovement()));
            }

            // 默认控制方式为自动（可变车道一般为自动控制）
            protocol.setCurMode(VarLaneMode.CONTROLLER_FIXED);

            validateConversion(entity, protocol);

            logger.debug("可变车道状态实体转协议成功: crossId={}, laneNo={}",
                    entity.getCrossId(), entity.getLaneNo());
            return protocol;

        } catch (Exception e) {
            logger.error("可变车道状态转换失败: crossId={}, laneNo={}",
                    entity.getCrossId(), entity.getLaneNo(), e);
            throw new DataConversionException("可变车道状态转换失败", e);
        }
    }

    @Override
    public LaneParamEntity toEntity(VarLaneStatus protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            LaneParamEntity entity = new LaneParamEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setLaneNo(protocol.getLaneNo());

            // 转向映射
            if (protocol.getCurMovement() != null) {
                entity.setMovement(protocol.getCurMovement().getCode());
            }

            // 设置默认值
//            entity.setIsImport(true);

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("可变车道状态协议转实体成功: crossId={}, laneNo={}",
                    protocol.getCrossId(), protocol.getLaneNo());
            return entity;

        } catch (Exception e) {
            logger.error("可变车道状态转换失败: crossId={}, laneNo={}",
                    protocol.getCrossId(), protocol.getLaneNo(), e);
            throw new DataConversionException("可变车道状态转换失败", e);
        }
    }

    @Override
    public void updateEntity(VarLaneStatus protocol, LaneParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新可变字段
            if (protocol.getCurMovement() != null) {
                entity.setMovement(protocol.getCurMovement().getCode());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("可变车道状态实体更新成功: crossId={}, laneNo={}",
                    entity.getCrossId(), entity.getLaneNo());

        } catch (Exception e) {
            logger.error("可变车道状态更新失败: crossId={}, laneNo={}",
                    entity.getCrossId(), entity.getLaneNo(), e);
            throw new DataConversionException("可变车道状态更新失败", e);
        }
    }
}
