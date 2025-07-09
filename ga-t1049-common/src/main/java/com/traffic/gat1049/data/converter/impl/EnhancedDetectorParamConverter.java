package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.intersection.DetectorParam;
import com.traffic.gat1049.repository.entity.DetectorParamEntity;
import com.traffic.gat1049.repository.interfaces.DetectorParamRepository;
import com.traffic.gat1049.model.enums.DetectorType;
import com.traffic.gat1049.model.enums.DetectorPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 增强的检测器参数转换器
 * 支持关联表数据查询
 */
@Component
public class EnhancedDetectorParamConverter extends AbstractEntityConverter<DetectorParamEntity, DetectorParam> {

    @Autowired(required = false)
    private DetectorParamRepository detectorParamRepository;

    @Override
    public DetectorParam toProtocol(DetectorParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            DetectorParam protocol = new DetectorParam();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setDetectorNo(entity.getDetectorNo());

            // 枚举类型转换
            if (entity.getType() != null) {
                protocol.setType(DetectorType.fromCode(entity.getType().toString()));
            }

            if (entity.getPosition() != null) {
                protocol.setPosition(DetectorPosition.fromCode(entity.getPosition().toString()));
            }

            // 检测对象和距离
            protocol.setTarget(entity.getTarget());
            protocol.setDistance(entity.getDistance());

            // 查询关联的车道和人行横道信息
            if (detectorParamRepository != null) {
                populateRelatedData(protocol, entity);
            }

            validateConversion(entity, protocol);

            logger.debug("增强检测器参数实体转协议成功: crossId={}, detectorNo={}",
                    entity.getCrossId(), entity.getDetectorNo());
            return protocol;

        } catch (Exception e) {
            logger.error("增强检测器参数转换失败: crossId={}, detectorNo={}",
                    entity.getCrossId(), entity.getDetectorNo(), e);
            throw new DataConversionException("增强检测器参数转换失败", e);
        }
    }

    @Override
    public DetectorParamEntity toEntity(DetectorParam protocol) {
        // 基础转换逻辑与DetectorParamConverter相同
        DetectorParamEntity entity = new DetectorParamEntity();

        entity.setCrossId(protocol.getCrossId());
        entity.setDetectorNo(protocol.getDetectorNo());

        if (protocol.getType() != null) {
            entity.setType(Integer.parseInt(protocol.getType().getCode()));
        }

        if (protocol.getPosition() != null) {
            entity.setPosition(Integer.parseInt(protocol.getPosition().getCode()));
        }

        entity.setTarget(protocol.getTarget());
        entity.setDistance(protocol.getDistance());

        setEntityAuditFields(entity, true);

        return entity;
    }

    @Override
    public void updateEntity(DetectorParam protocol, DetectorParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        if (protocol.getType() != null) {
            entity.setType(Integer.parseInt(protocol.getType().getCode()));
        }

        if (protocol.getPosition() != null) {
            entity.setPosition(Integer.parseInt(protocol.getPosition().getCode()));
        }

        if (protocol.getTarget() != null) {
            entity.setTarget(protocol.getTarget());
        }

        if (protocol.getDistance() != null) {
            entity.setDistance(protocol.getDistance());
        }

        setEntityAuditFields(entity, false);
    }

    /**
     * 填充关联数据（车道和人行横道信息）
     */
    private void populateRelatedData(DetectorParam protocol, DetectorParamEntity entity) {
        try {
            // 查询关联的车道信息
            List<DetectorParamEntity> laneDetectors = detectorParamRepository.findByLane(
                    entity.getCrossId(), null); // 需要实现具体的查询逻辑

            // 查询关联的人行横道信息
            List<DetectorParamEntity> pedestrianDetectors = detectorParamRepository.findByPedestrian(
                    entity.getCrossId(), null); // 需要实现具体的查询逻辑

            // 这里需要根据实际的关联表结构来实现
            // 暂时使用空列表
            protocol.setLaneNoList(List.of());
            protocol.setPedestrianNoList(List.of());

        } catch (Exception e) {
            logger.warn("查询检测器关联数据失败: crossId={}, detectorNo={}",
                    entity.getCrossId(), entity.getDetectorNo(), e);
            // 设置空列表作为默认值
            protocol.setLaneNoList(List.of());
            protocol.setPedestrianNoList(List.of());
        }
    }
}
