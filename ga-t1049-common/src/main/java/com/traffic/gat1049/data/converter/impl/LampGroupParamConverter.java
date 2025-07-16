package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.intersection.LampGroupParam;
import com.traffic.gat1049.repository.entity.LampGroupParamEntity;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LampGroupType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 信号灯组参数转换器实现
 * 演示枚举类型转换模式
 */
@Component
public class LampGroupParamConverter extends AbstractEntityConverter<LampGroupParamEntity, LampGroupParam> {

    @Override
    public LampGroupParam toProtocol(LampGroupParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            LampGroupParam protocol = new LampGroupParam();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setLampGroupNo(entity.getLampGroupNo());

            // 枚举类型转换 - 从数据库的字符串字段转换为枚举
            if (StringUtils.hasText(entity.getDirection())) {
                protocol.setDirection(Direction.fromCode(entity.getDirection()));
            }

            if (StringUtils.hasText(entity.getType())) {
                protocol.setType(LampGroupType.fromCode(entity.getType()));
            }

            validateConversion(entity, protocol);

            logger.debug("信号灯组参数实体转协议成功: crossId={}, lampGroupNo={}",
                    entity.getCrossId(), entity.getLampGroupNo());
            return protocol;

        } catch (Exception e) {
            logger.error("信号灯组参数转换失败: crossId={}, lampGroupNo={}",
                    entity.getCrossId(), entity.getLampGroupNo(), e);
            throw new DataConversionException("信号灯组参数转换失败", e);
        }
    }

    @Override
    public LampGroupParamEntity toEntity(LampGroupParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            LampGroupParamEntity entity = new LampGroupParamEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setLampGroupNo(protocol.getLampGroupNo());

            // 枚举类型转换 - 从枚举转换为数据库的字符串字段
            if (protocol.getDirection() != null) {
                entity.setDirection(protocol.getDirection().getCode());
            }

            if (protocol.getType() != null) {
                entity.setType(protocol.getType().getCode());
            }

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("信号灯组参数协议转实体成功: crossId={}, lampGroupNo={}",
                    protocol.getCrossId(), protocol.getLampGroupNo());
            return entity;

        } catch (Exception e) {
            logger.error("信号灯组参数转换失败: crossId={}, lampGroupNo={}",
                    protocol.getCrossId(), protocol.getLampGroupNo(), e);
            throw new DataConversionException("信号灯组参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(LampGroupParam protocol, LampGroupParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 灯组编号不可更新（业务主键）

            // 更新方向 - 直接使用字符串代码
            if (protocol.getDirection() != null) {
                entity.setDirection(protocol.getDirection().getCode());
            }

            // 更新类型 - 直接使用字符串代码
            if (protocol.getType() != null) {
                entity.setType(protocol.getType().getCode());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("信号灯组参数实体更新成功: crossId={}, lampGroupNo={}",
                    entity.getCrossId(), entity.getLampGroupNo());

        } catch (Exception e) {
            logger.error("信号灯组参数更新失败: crossId={}, lampGroupNo={}",
                    entity.getCrossId(), entity.getLampGroupNo(), e);
            throw new DataConversionException("信号灯组参数更新失败", e);
        }
    }

    /**
     * 验证转换结果
     */
    protected void validateConversion(LampGroupParamEntity entity, LampGroupParam protocol) {
        if (entity == null || protocol == null) {
            throw new DataConversionException("转换结果不能为null");
        }

        // 验证关键字段是否正确转换
        if (!entity.getCrossId().equals(protocol.getCrossId())) {
            throw new DataConversionException("路口编号转换失败");
        }

        if (!entity.getLampGroupNo().equals(protocol.getLampGroupNo())) {
            throw new DataConversionException("灯组编号转换失败");
        }

        // 验证枚举转换
        if (protocol.getDirection() != null && entity.getDirection() != null) {
            if (!entity.getDirection().equals(protocol.getDirection().getCode())) {
                throw new DataConversionException("方向枚举转换失败");
            }
        }

        if (protocol.getType() != null && entity.getType() != null) {
            if (!entity.getType().equals(protocol.getType().getCode())) {
                throw new DataConversionException("类型枚举转换失败");
            }
        }
    }
}
