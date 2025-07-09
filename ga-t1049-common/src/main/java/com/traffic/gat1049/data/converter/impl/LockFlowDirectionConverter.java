package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.command.LockFlowDirection;
import com.traffic.gat1049.repository.entity.LockFlowDirectionEntity;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.FlowType;
import com.traffic.gat1049.model.enums.LockType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 锁定交通流向转换器
 * 处理锁定交通流向命令与实体的转换
 */
@Component
public class LockFlowDirectionConverter extends AbstractEntityConverter<LockFlowDirectionEntity, LockFlowDirection> {

    @Override
    public LockFlowDirection toProtocol(LockFlowDirectionEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            LockFlowDirection protocol = new LockFlowDirection();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());

            // 枚举转换
            if (entity.getType() != null) {
                protocol.setType(FlowType.fromCode(String.valueOf(entity.getType())));
            }
            if (entity.getEntrance() != null) {
                protocol.setEntrance(Direction.fromCode(entity.getEntrance()));
            }
            if (entity.getExit() != null) {
                protocol.setExit(Direction.fromCode(entity.getExit()));
            }
            if (entity.getLockType() != null) {
                protocol.setLockType(LockType.fromCode(entity.getLockType()));
            }

            // 其他字段
            protocol.setLockStageNo(entity.getLockStageNo());
            protocol.setDuration(entity.getDuration());

            validateConversion(entity, protocol);

            logger.debug("锁定流向实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("锁定流向转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("锁定流向转换失败", e);
        }
    }

    @Override
    public LockFlowDirectionEntity toEntity(LockFlowDirection protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            LockFlowDirectionEntity entity = new LockFlowDirectionEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());

            // 枚举转换
            if (protocol.getType() != null) {
                entity.setType(Integer.parseInt(protocol.getType().getCode()));
            }
            if (protocol.getEntrance() != null) {
                entity.setEntrance(protocol.getEntrance().getCode());
            }
            if (protocol.getExit() != null) {
                entity.setExit(protocol.getExit().getCode());
            }
            if (protocol.getLockType() != null) {
                entity.setLockType(Integer.parseInt(protocol.getLockType().getCode()));
            }

            entity.setLockStageNo(protocol.getLockStageNo());
            entity.setDuration(protocol.getDuration());

            // 设置默认值
            entity.setStatus(LockFlowDirectionEntity.LockStatus.ACTIVE);
            entity.setStartTime(LocalDateTime.now());
            entity.setEndTime(LocalDateTime.now().plusSeconds(protocol.getDuration()));

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("锁定流向协议转实体成功: {}", protocol.getCrossId());
            return entity;

        } catch (Exception e) {
            logger.error("锁定流向转换失败: {}", protocol.getCrossId(), e);
            throw new DataConversionException("锁定流向转换失败", e);
        }
    }

    @Override
    public void updateEntity(LockFlowDirection protocol, LockFlowDirectionEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }
            if (protocol.getType() != null) {
                entity.setType(Integer.parseInt(protocol.getType().getCode()));
            }
            if (protocol.getEntrance() != null) {
                entity.setEntrance(protocol.getEntrance().getCode());
            }
            if (protocol.getExit() != null) {
                entity.setExit(protocol.getExit().getCode());
            }
            if (protocol.getLockType() != null) {
                entity.setLockType(Integer.parseInt(protocol.getLockType().getCode()));
            }
            if (protocol.getLockStageNo() != null) {
                entity.setLockStageNo(protocol.getLockStageNo());
            }
            if (protocol.getDuration() != null) {
                entity.setDuration(protocol.getDuration());
                entity.setEndTime(entity.getStartTime().plusSeconds(protocol.getDuration()));
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("锁定流向实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("锁定流向更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("锁定流向更新失败", e);
        }
    }
}
