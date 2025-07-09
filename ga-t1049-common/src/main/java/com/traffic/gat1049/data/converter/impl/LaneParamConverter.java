package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.data.converter.util.JsonConverterUtil;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.intersection.LaneParam;
import com.traffic.gat1049.repository.entity.LaneParamEntity;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LaneAttribute;
import com.traffic.gat1049.model.enums.LaneMovement;
import com.traffic.gat1049.model.enums.LaneFeature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * 车道参数转换器实现
 * 演示枚举转换和可变转向列表处理
 */
@Component
public class LaneParamConverter extends AbstractEntityConverter<LaneParamEntity, LaneParam> {

    @Override
    public LaneParam toProtocol(LaneParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            LaneParam protocol = new LaneParam();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setLaneNo(entity.getLaneNo());

            // 枚举转换
            if (StringUtils.hasText(entity.getDirection())) {
                protocol.setDirection(Direction.fromCode(entity.getDirection()));
            }

            if (entity.getAttribute() != null) {
                protocol.setAttribute(LaneAttribute.fromCode(entity.getAttribute().toString()));
            }

            if (StringUtils.hasText(entity.getMovement())) {
                protocol.setMovement(LaneMovement.fromCode(entity.getMovement()));
            }

            if (entity.getFeature() != null) {
                protocol.setFeature(LaneFeature.fromCode(entity.getFeature().toString()));
            }

            // 设置方位角
            if (entity.getAzimuth() != null) {
                protocol.setAzimuth(entity.getAzimuth());
            }

            // 设置待行区
            if (entity.getWaitingArea() != null) {
                protocol.setWaitingArea(entity.getWaitingArea());
            }

            // 解析可变转向列表（使用工具类）
            List<LaneMovement> varMovements = JsonConverterUtil.deserializeEnumCodes(
                    entity.getVarMovementList(), LaneMovement::fromCode);
            protocol.setVarMovementList(varMovements);

            validateConversion(entity, protocol);

            logger.debug("车道参数实体转协议成功: crossId={}, laneNo={}",
                    entity.getCrossId(), entity.getLaneNo());
            return protocol;

        } catch (Exception e) {
            logger.error("车道参数转换失败: crossId={}, laneNo={}",
                    entity.getCrossId(), entity.getLaneNo(), e);
            throw new DataConversionException("车道参数转换失败", e);
        }
    }

    @Override
    public LaneParamEntity toEntity(LaneParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            LaneParamEntity entity = new LaneParamEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setLaneNo(protocol.getLaneNo());

            // 枚举转换
            if (protocol.getDirection() != null) {
                entity.setDirection(protocol.getDirection().getCode());
            }

            if (protocol.getAttribute() != null) {
                entity.setAttribute(Integer.valueOf(protocol.getAttribute().getCode()));
            }

            if (protocol.getMovement() != null) {
                entity.setMovement(protocol.getMovement().getCode());
            }

            if (protocol.getFeature() != null) {
                entity.setFeature(Integer.valueOf(protocol.getFeature().getCode()));
            }

            // 设置方位角
            if (protocol.getAzimuth() != null) {
                entity.setAzimuth(protocol.getAzimuth());
            }

            // 设置待行区
            if (protocol.getWaitingArea() != null) {
                entity.setWaitingArea(protocol.getWaitingArea());
            }

            // 序列化可变转向列表（使用工具类）
            String varMovementJson = JsonConverterUtil.serializeEnumCodes(
                    protocol.getVarMovementList(), LaneMovement::getCode);
            entity.setVarMovementList(varMovementJson);

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("车道参数协议转实体成功: crossId={}, laneNo={}",
                    protocol.getCrossId(), protocol.getLaneNo());
            return entity;

        } catch (Exception e) {
            logger.error("车道参数转换失败: crossId={}, laneNo={}",
                    protocol.getCrossId(), protocol.getLaneNo(), e);
            throw new DataConversionException("车道参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(LaneParam protocol, LaneParamEntity entity) {
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

            if (protocol.getMovement() != null) {
                entity.setMovement(protocol.getMovement().getCode());
            }

            if (protocol.getFeature() != null) {
                entity.setFeature(Integer.valueOf(protocol.getFeature().getCode()));
            }

            if (protocol.getAzimuth() != null) {
                entity.setAzimuth(protocol.getAzimuth());
            }

            if (protocol.getWaitingArea() != null) {
                entity.setWaitingArea(protocol.getWaitingArea());
            }

            // 更新可变转向列表（使用工具类）
            if (protocol.getVarMovementList() != null) {
                String varMovementJson = JsonConverterUtil.serializeEnumCodes(
                        protocol.getVarMovementList(), LaneMovement::getCode);
                entity.setVarMovementList(varMovementJson);
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("车道参数实体更新成功: crossId={}, laneNo={}",
                    entity.getCrossId(), entity.getLaneNo());

        } catch (Exception e) {
            logger.error("车道参数更新失败: crossId={}, laneNo={}",
                    entity.getCrossId(), entity.getLaneNo(), e);
            throw new DataConversionException("车道参数更新失败", e);
        }
    }
}
