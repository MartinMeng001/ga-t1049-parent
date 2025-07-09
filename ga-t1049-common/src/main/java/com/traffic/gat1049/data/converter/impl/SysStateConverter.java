package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.system.SysState;
import com.traffic.gat1049.repository.entity.SysStateEntity;
import com.traffic.gat1049.model.enums.SystemState;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 系统状态转换器实现
 * 参考SysInfo转换器的实现模式
 */
@Component
public class SysStateConverter extends AbstractEntityConverter<SysStateEntity, SysState> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public SysState toProtocol(SysStateEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SysState protocol = new SysState();

            // 状态值转换：Entity枚举到Protocol枚举
            if (entity.getValue() != null) {
                SystemState systemState = convertEntityStateToProtocol(entity.getValue());
                protocol.setValue(systemState);
            }

            // 时间转换：LocalDateTime到String
            if (entity.getTime() != null) {
                protocol.setTime(entity.getTime().format(DATE_TIME_FORMATTER));
            }

            validateConversion(entity, protocol);

            logger.debug("系统状态实体转协议成功: systemId={}, state={}",
                    entity.getSystemId(), entity.getValue());
            return protocol;

        } catch (Exception e) {
            logger.error("系统状态转换失败: systemId={}", entity.getSystemId(), e);
            throw new DataConversionException("系统状态转换失败", e);
        }
    }

    @Override
    public SysStateEntity toEntity(SysState protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            SysStateEntity entity = new SysStateEntity();

            // 状态值转换：Protocol枚举到Entity枚举
            if (protocol.getValue() != null) {
                SysStateEntity.StateValue entityState = convertProtocolStateToEntity(protocol.getValue());
                entity.setValue(entityState);
            }

            // 时间转换：String到LocalDateTime
            if (StringUtils.hasText(protocol.getTime())) {
                try {
                    LocalDateTime time = LocalDateTime.parse(protocol.getTime(), DATE_TIME_FORMATTER);
                    entity.setTime(time);
                } catch (Exception e) {
                    logger.warn("时间格式解析失败，使用当前时间: {}", protocol.getTime());
                    entity.setTime(LocalDateTime.now());
                }
            } else {
                entity.setTime(LocalDateTime.now());
            }

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("系统状态协议转实体成功: state={}", protocol.getValue());
            return entity;

        } catch (Exception e) {
            logger.error("系统状态转换失败: state={}", protocol.getValue(), e);
            throw new DataConversionException("系统状态转换失败", e);
        }
    }

    @Override
    public void updateEntity(SysState protocol, SysStateEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 更新状态值
            if (protocol.getValue() != null) {
                SysStateEntity.StateValue entityState = convertProtocolStateToEntity(protocol.getValue());
                entity.setValue(entityState);
            }

            // 更新时间
            if (StringUtils.hasText(protocol.getTime())) {
                try {
                    LocalDateTime time = LocalDateTime.parse(protocol.getTime(), DATE_TIME_FORMATTER);
                    entity.setTime(time);
                } catch (Exception e) {
                    logger.warn("时间格式解析失败，保持原时间: {}", protocol.getTime());
                }
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("系统状态实体更新成功: systemId={}", entity.getSystemId());

        } catch (Exception e) {
            logger.error("系统状态更新失败: systemId={}", entity.getSystemId(), e);
            throw new DataConversionException("系统状态更新失败", e);
        }
    }

    /**
     * Entity状态枚举转换为Protocol状态枚举
     */
    private SystemState convertEntityStateToProtocol(SysStateEntity.StateValue entityState) {
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
    private SysStateEntity.StateValue convertProtocolStateToEntity(SystemState protocolState) {
        switch (protocolState) {
            case ONLINE:
                return SysStateEntity.StateValue.Online;
            case OFFLINE:
                return SysStateEntity.StateValue.Offline;
            case ERROR:
                return SysStateEntity.StateValue.Error;
            default:
                throw new DataConversionException("未知的协议状态值: " + protocolState);
        }
    }
}
