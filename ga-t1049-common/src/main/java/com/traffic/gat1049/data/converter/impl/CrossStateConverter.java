package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.runtime.CrossState;
import com.traffic.gat1049.repository.entity.CrossStateEntity;
import com.traffic.gat1049.model.enums.SystemState;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 路口状态转换器实现
 * 参考SysInfo转换器的实现模式
 */
@Component
public class CrossStateConverter extends AbstractEntityConverter<CrossStateEntity, CrossState> {

    @Override
    public CrossState toProtocol(CrossStateEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CrossState protocol = new CrossState();

            // 路口ID映射
            protocol.setCrossId(entity.getCrossId());

            // 状态值转换：Entity枚举到Protocol枚举
            if (entity.getValue() != null) {
                SystemState systemState = convertEntityStateToProtocol(entity.getValue());
                protocol.setValue(systemState);
            }

            validateConversion(entity, protocol);

            logger.debug("路口状态实体转协议成功: crossId={}, state={}",
                    entity.getCrossId(), entity.getValue());
            return protocol;

        } catch (Exception e) {
            logger.error("路口状态转换失败: crossId={}", entity.getCrossId(), e);
            throw new DataConversionException("路口状态转换失败", e);
        }
    }

    @Override
    public CrossStateEntity toEntity(CrossState protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            CrossStateEntity entity = new CrossStateEntity();

            // 路口ID映射
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }

            // 状态值转换：Protocol枚举到Entity枚举
            if (protocol.getValue() != null) {
                CrossStateEntity.StateValue entityState = convertProtocolStateToEntity(protocol.getValue());
                entity.setValue(entityState);
            }

            // 设置时间
            entity.setTime(LocalDateTime.now());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("路口状态协议转实体成功: crossId={}, state={}",
                    protocol.getCrossId(), protocol.getValue());
            return entity;

        } catch (Exception e) {
            logger.error("路口状态转换失败: crossId={}", protocol.getCrossId(), e);
            throw new DataConversionException("路口状态转换失败", e);
        }
    }

    @Override
    public void updateEntity(CrossState protocol, CrossStateEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 更新路口ID
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }

            // 更新状态值
            if (protocol.getValue() != null) {
                CrossStateEntity.StateValue entityState = convertProtocolStateToEntity(protocol.getValue());
                entity.setValue(entityState);
            }

            // 更新时间
            entity.setTime(LocalDateTime.now());

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("路口状态实体更新成功: crossId={}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("路口状态更新失败: crossId={}", entity.getCrossId(), e);
            throw new DataConversionException("路口状态更新失败", e);
        }
    }

    /**
     * Entity状态枚举转换为Protocol状态枚举
     */
    private SystemState convertEntityStateToProtocol(CrossStateEntity.StateValue entityState) {
        switch (entityState) {
            case Online:
                return SystemState.ONLINE;
            case Offline:
                return SystemState.OFFLINE;
            case Error:
                return SystemState.ERROR;
            default:
                throw new DataConversionException("未知的实体状态值: " + entityState);
        }
    }

    /**
     * Protocol状态枚举转换为Entity状态枚举
     */
    private CrossStateEntity.StateValue convertProtocolStateToEntity(SystemState protocolState) {
        switch (protocolState) {
            case ONLINE:
                return CrossStateEntity.StateValue.Online;
            case OFFLINE:
                return CrossStateEntity.StateValue.Offline;
            case ERROR:
                return CrossStateEntity.StateValue.Error;
            default:
                throw new DataConversionException("未知的协议状态值: " + protocolState);
        }
    }
}
