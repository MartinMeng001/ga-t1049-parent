package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.intersection.DetectorParam;
import com.traffic.gat1049.repository.entity.DetectorParamEntity;
import com.traffic.gat1049.model.enums.DetectorType;
import com.traffic.gat1049.model.enums.DetectorPosition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检测器参数转换器实现
 * 演示复杂字段转换和枚举处理模式
 */
@Component
public class DetectorParamConverter extends AbstractEntityConverter<DetectorParamEntity, DetectorParam> {

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

            // 检测对象
            protocol.setTarget(entity.getTarget());

            // 距离（数据库已经是厘米，直接使用）
            protocol.setDistance(entity.getDistance());

            // 初始化空列表（需要通过关联表查询填充）
            protocol.setLaneNoList(new ArrayList<>());
            protocol.setPedestrianNoList(new ArrayList<>());

            validateConversion(entity, protocol);

            logger.debug("检测器参数实体转协议成功: crossId={}, detectorNo={}",
                    entity.getCrossId(), entity.getDetectorNo());
            return protocol;

        } catch (Exception e) {
            logger.error("检测器参数转换失败: crossId={}, detectorNo={}",
                    entity.getCrossId(), entity.getDetectorNo(), e);
            throw new DataConversionException("检测器参数转换失败", e);
        }
    }

    @Override
    public DetectorParamEntity toEntity(DetectorParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            DetectorParamEntity entity = new DetectorParamEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setDetectorNo(protocol.getDetectorNo());

            // 枚举类型转换
            if (protocol.getType() != null) {
                entity.setType(Integer.parseInt(protocol.getType().getCode()));
            }

            if (protocol.getPosition() != null) {
                entity.setPosition(Integer.parseInt(protocol.getPosition().getCode()));
            }

            // 检测对象
            entity.setTarget(protocol.getTarget());

            // 距离（协议和数据库都是厘米，直接使用）
            entity.setDistance(protocol.getDistance());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("检测器参数协议转实体成功: crossId={}, detectorNo={}",
                    protocol.getCrossId(), protocol.getDetectorNo());
            return entity;

        } catch (Exception e) {
            logger.error("检测器参数转换失败: crossId={}, detectorNo={}",
                    protocol.getCrossId(), protocol.getDetectorNo(), e);
            throw new DataConversionException("检测器参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(DetectorParam protocol, DetectorParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 检测器编号不可更新（业务主键）

            // 更新检测器类型
            if (protocol.getType() != null) {
                entity.setType(Integer.parseInt(protocol.getType().getCode()));
            }

            // 更新检测位置
            if (protocol.getPosition() != null) {
                entity.setPosition(Integer.parseInt(protocol.getPosition().getCode()));
            }

            // 更新检测对象
            if (protocol.getTarget() != null) {
                entity.setTarget(protocol.getTarget());
            }

            // 更新距离
            if (protocol.getDistance() != null) {
                entity.setDistance(protocol.getDistance());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("检测器参数实体更新成功: crossId={}, detectorNo={}",
                    entity.getCrossId(), entity.getDetectorNo());

        } catch (Exception e) {
            logger.error("检测器参数更新失败: crossId={}, detectorNo={}",
                    entity.getCrossId(), entity.getDetectorNo(), e);
            throw new DataConversionException("检测器参数更新失败", e);
        }
    }
}
