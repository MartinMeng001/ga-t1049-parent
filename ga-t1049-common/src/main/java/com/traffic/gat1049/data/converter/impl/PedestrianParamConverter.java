package com.traffic.gat1049.data.converter.impl;
import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.intersection.PedestrianParam;
import com.traffic.gat1049.repository.entity.PedestrianParamEntity;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.PedestrianAttribute;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PedestrianParamConverter extends AbstractEntityConverter<PedestrianParamEntity, PedestrianParam> {

    @Override
    public PedestrianParam toProtocol(PedestrianParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            PedestrianParam protocol = new PedestrianParam();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setPedestrianNo(entity.getPedestrianNo());

            // 枚举转换
            if (StringUtils.hasText(entity.getDirection())) {
                protocol.setDirection(Direction.fromCode(entity.getDirection()));
            }

            if (entity.getAttribute() != null) {
                protocol.setAttribute(PedestrianAttribute.fromCode(entity.getAttribute().toString()));
            }

            validateConversion(entity, protocol);

            logger.debug("人行横道参数实体转协议成功: crossId={}, pedestrianNo={}",
                    entity.getCrossId(), entity.getPedestrianNo());
            return protocol;

        } catch (Exception e) {
            logger.error("人行横道参数转换失败: crossId={}, pedestrianNo={}",
                    entity.getCrossId(), entity.getPedestrianNo(), e);
            throw new DataConversionException("人行横道参数转换失败", e);
        }
    }

    @Override
    public PedestrianParamEntity toEntity(PedestrianParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            PedestrianParamEntity entity = new PedestrianParamEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setPedestrianNo(protocol.getPedestrianNo());

            // 枚举转换
            if (protocol.getDirection() != null) {
                entity.setDirection(protocol.getDirection().getCode());
            }

            if (protocol.getAttribute() != null) {
                entity.setAttribute(Integer.valueOf(protocol.getAttribute().getCode()));
            }

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("人行横道参数协议转实体成功: crossId={}, pedestrianNo={}",
                    protocol.getCrossId(), protocol.getPedestrianNo());
            return entity;

        } catch (Exception e) {
            logger.error("人行横道参数转换失败: crossId={}, pedestrianNo={}",
                    protocol.getCrossId(), protocol.getPedestrianNo(), e);
            throw new DataConversionException("人行横道参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(PedestrianParam protocol, PedestrianParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 更新可修改的字段
            if (protocol.getDirection() != null) {
                entity.setDirection(protocol.getDirection().getCode());
            }

            if (protocol.getAttribute() != null) {
                entity.setAttribute(Integer.valueOf(protocol.getAttribute().getCode()));
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("人行横道参数实体更新成功: crossId={}, pedestrianNo={}",
                    entity.getCrossId(), entity.getPedestrianNo());

        } catch (Exception e) {
            logger.error("人行横道参数更新失败: crossId={}, pedestrianNo={}",
                    entity.getCrossId(), entity.getPedestrianNo(), e);
            throw new DataConversionException("人行横道参数更新失败", e);
        }
    }
}
