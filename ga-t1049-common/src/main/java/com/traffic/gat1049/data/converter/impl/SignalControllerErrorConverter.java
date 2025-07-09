package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.runtime.SignalControllerError;
import com.traffic.gat1049.repository.entity.SignalControllerErrorEntity;
import com.traffic.gat1049.model.enums.ControllerErrorType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 信号机故障转换器实现
 * 参考SysInfo转换器的实现模式
 */
@Component
public class SignalControllerErrorConverter extends AbstractEntityConverter<SignalControllerErrorEntity, SignalControllerError> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public SignalControllerError toProtocol(SignalControllerErrorEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SignalControllerError protocol = new SignalControllerError();

            // 信号机ID映射
            protocol.setSignalControllerId(entity.getSignalControllerId());

            // 故障类型转换：错误代码到枚举
            if (StringUtils.hasText(entity.getErrorCode())) {
                ControllerErrorType errorType = convertErrorCodeToType(entity.getErrorCode());
                protocol.setErrorType(errorType);
            }

            // 故障描述映射
            protocol.setErrorDesc(entity.getErrorDescription());

            // 时间转换：LocalDateTime到String
            if (entity.getTime() != null) {
                protocol.setOccurTime(entity.getTime().format(DATE_TIME_FORMATTER));
            }

            validateConversion(entity, protocol);

            logger.debug("信号机故障实体转协议成功: controllerId={}, errorCode={}",
                    entity.getSignalControllerId(), entity.getErrorCode());
            return protocol;

        } catch (Exception e) {
            logger.error("信号机故障转换失败: controllerId={}", entity.getSignalControllerId(), e);
            throw new DataConversionException("信号机故障转换失败", e);
        }
    }

    @Override
    public SignalControllerErrorEntity toEntity(SignalControllerError protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            SignalControllerErrorEntity entity = new SignalControllerErrorEntity();

            // 信号机ID映射
            if (StringUtils.hasText(protocol.getSignalControllerId())) {
                entity.setSignalControllerId(protocol.getSignalControllerId());
            }

            // 故障类型转换：枚举到错误代码
            if (protocol.getErrorType() != null) {
                String errorCode = convertErrorTypeToCode(protocol.getErrorType());
                entity.setErrorCode(errorCode);
            }

            // 故障描述映射
            entity.setErrorDescription(protocol.getErrorDesc());

            // 时间转换：String到LocalDateTime
            if (StringUtils.hasText(protocol.getOccurTime())) {
                try {
                    LocalDateTime time = LocalDateTime.parse(protocol.getOccurTime(), DATE_TIME_FORMATTER);
                    entity.setTime(time);
                } catch (Exception e) {
                    logger.warn("时间格式解析失败，使用当前时间: {}", protocol.getOccurTime());
                    entity.setTime(LocalDateTime.now());
                }
            } else {
                entity.setTime(LocalDateTime.now());
            }

            // 设置默认严重级别
            entity.setSeverity(determineSeverityLevel(protocol.getErrorType()));

            // 设置默认状态
            entity.setIsResolved(false);

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("信号机故障协议转实体成功: controllerId={}, errorType={}",
                    protocol.getSignalControllerId(), protocol.getErrorType());
            return entity;

        } catch (Exception e) {
            logger.error("信号机故障转换失败: controllerId={}", protocol.getSignalControllerId(), e);
            throw new DataConversionException("信号机故障转换失败", e);
        }
    }

    @Override
    public void updateEntity(SignalControllerError protocol, SignalControllerErrorEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 更新信号机ID
            if (StringUtils.hasText(protocol.getSignalControllerId())) {
                entity.setSignalControllerId(protocol.getSignalControllerId());
            }

            // 更新故障类型
            if (protocol.getErrorType() != null) {
                String errorCode = convertErrorTypeToCode(protocol.getErrorType());
                entity.setErrorCode(errorCode);
                // 同时更新严重级别
                entity.setSeverity(determineSeverityLevel(protocol.getErrorType()));
            }

            // 更新故障描述
            if (StringUtils.hasText(protocol.getErrorDesc())) {
                entity.setErrorDescription(protocol.getErrorDesc());
            }

            // 更新时间
            if (StringUtils.hasText(protocol.getOccurTime())) {
                try {
                    LocalDateTime time = LocalDateTime.parse(protocol.getOccurTime(), DATE_TIME_FORMATTER);
                    entity.setTime(time);
                } catch (Exception e) {
                    logger.warn("时间格式解析失败，保持原时间: {}", protocol.getOccurTime());
                }
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("信号机故障实体更新成功: controllerId={}", entity.getSignalControllerId());

        } catch (Exception e) {
            logger.error("信号机故障更新失败: controllerId={}", entity.getSignalControllerId(), e);
            throw new DataConversionException("信号机故障更新失败", e);
        }
    }

    /**
     * 错误代码转换为故障类型枚举
     */
    private ControllerErrorType convertErrorCodeToType(String errorCode) {
        try {
            // 假设错误代码直接对应枚举值，如果不是，需要建立映射关系
            return ControllerErrorType.fromCode(errorCode);
        } catch (Exception e) {
            logger.warn("未知的错误代码: {}, 使用默认类型", errorCode);
            return ControllerErrorType.OTHER; // 需要确认是否有此枚举值
        }
    }

    /**
     * 故障类型枚举转换为错误代码
     */
    private String convertErrorTypeToCode(ControllerErrorType errorType) {
        // 假设枚举有getCode()方法，如果没有，需要建立映射关系
        return errorType.getCode();
    }

    /**
     * 根据故障类型确定严重级别
     */
    private SignalControllerErrorEntity.SeverityLevel determineSeverityLevel(ControllerErrorType errorType) {
        if (errorType == null) {
            return SignalControllerErrorEntity.SeverityLevel.INFO;
        }

        // 根据故障类型映射严重级别，这里需要根据具体的业务规则调整
        switch (errorType.getCode()) {
            case "001": // 假设001是通信故障
            case "002": // 假设002是电源故障
                return SignalControllerErrorEntity.SeverityLevel.CRITICAL;
            case "003": // 假设003是信号灯故障
                return SignalControllerErrorEntity.SeverityLevel.ERROR;
            case "004": // 假设004是检测器故障
                return SignalControllerErrorEntity.SeverityLevel.WARNING;
            default:
                return SignalControllerErrorEntity.SeverityLevel.INFO;
        }
    }
}
