// ================================================================
// CrossCtrlInfo 转换器实现
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.protocol.model.command.CrossCtrlInfo;
import com.traffic.gat1049.repository.entity.CrossCtrlInfoEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 路口控制信息转换器实现
 * 参考 SysInfo converter 的实现模式
 */
@Component
public class CrossCtrlInfoConverter extends AbstractEntityConverter<CrossCtrlInfoEntity, CrossCtrlInfo> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public CrossCtrlInfo toProtocol(CrossCtrlInfoEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CrossCtrlInfo protocol = new CrossCtrlInfo();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());

            // 控制模式转换（从字符串转为枚举）
            if (StringUtils.hasText(entity.getControlMode())) {
                ControlMode controlMode = ControlMode.fromCode(entity.getControlMode());
                protocol.setControlMode(controlMode);
            }

            protocol.setPlanNo(entity.getPlanNo());

            // 时间字段转换（从 LocalDateTime 转为字符串）
            if (entity.getTime() != null) {
                protocol.setTime(entity.getTime().format(DATE_TIME_FORMATTER));
            }

            validateConversion(entity, protocol);

            logger.debug("路口控制信息实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("路口控制信息转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口控制信息转换失败", e);
        }
    }

    @Override
    public CrossCtrlInfoEntity toEntity(CrossCtrlInfo protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            CrossCtrlInfoEntity entity = new CrossCtrlInfoEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());

            // 控制模式转换（从枚举转为字符串）
            if (protocol.getControlMode() != null) {
                entity.setControlMode(protocol.getControlMode().getCode());
            }

            entity.setPlanNo(protocol.getPlanNo());

            // 时间字段转换（从字符串转为 LocalDateTime）
            if (StringUtils.hasText(protocol.getTime())) {
                try {
                    entity.setTime(LocalDateTime.parse(protocol.getTime(), DATE_TIME_FORMATTER));
                } catch (Exception e) {
                    logger.warn("时间格式转换失败，使用当前时间: {}", protocol.getTime());
                    entity.setTime(LocalDateTime.now());
                }
            } else {
                entity.setTime(LocalDateTime.now());
            }

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("路口控制信息协议转实体成功: {}", protocol.getCrossId());
            return entity;

        } catch (Exception e) {
            logger.error("路口控制信息转换失败: {}", protocol.getCrossId(), e);
            throw new DataConversionException("路口控制信息转换失败", e);
        }
    }

    @Override
    public void updateEntity(CrossCtrlInfo protocol, CrossCtrlInfoEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getCrossId())) {
                entity.setCrossId(protocol.getCrossId());
            }

            if (protocol.getControlMode() != null) {
                entity.setControlMode(protocol.getControlMode().getCode());
            }

            if (protocol.getPlanNo() != null) {
                entity.setPlanNo(protocol.getPlanNo());
            }

            if (StringUtils.hasText(protocol.getTime())) {
                try {
                    entity.setTime(LocalDateTime.parse(protocol.getTime(), DATE_TIME_FORMATTER));
                } catch (Exception e) {
                    logger.warn("时间格式转换失败，保持原值: {}", protocol.getTime());
                }
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("路口控制信息实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("路口控制信息更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口控制信息更新失败", e);
        }
    }

    /**
     * 重写父类的验证方法
     */
    @Override
    protected void validateConversion(CrossCtrlInfoEntity entity, CrossCtrlInfo protocol) {
        super.validateConversion(entity, protocol);

        // 添加特定的业务验证逻辑
        if (protocol != null && !StringUtils.hasText(protocol.getCrossId())) {
            throw new DataConversionException("路口编号不能为空");
        }

        if (entity != null && !StringUtils.hasText(entity.getCrossId())) {
            throw new DataConversionException("路口编号不能为空");
        }

        // 验证控制模式和方案号的匹配性
        if (protocol != null && protocol.getControlMode() != null && protocol.getPlanNo() != null) {
            if (!protocol.isValid()) {
                throw new DataConversionException("控制模式与方案号不匹配");
            }
        }
    }

    /**
     * 创建用于状态推送的 CrossCtrlInfo
     */
    public CrossCtrlInfo createForStatusPush(String crossId, ControlMode controlMode, Integer planNo) {
        return CrossCtrlInfo.forStatusPush(crossId, controlMode, planNo);
    }

    /**
     * 创建用于命令下发的 CrossCtrlInfo
     */
    public CrossCtrlInfo createForCommand(String crossId, ControlMode controlMode, Integer planNo) {
        return CrossCtrlInfo.forCommand(crossId, controlMode, planNo);
    }
}