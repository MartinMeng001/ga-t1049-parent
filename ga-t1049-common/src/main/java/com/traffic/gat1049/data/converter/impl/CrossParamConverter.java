package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.intersection.CrossParam;
import com.traffic.gat1049.repository.entity.CrossParamEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 路口参数转换器实现
 * 演示基础数据转换模式
 */
@Component
public class CrossParamConverter extends AbstractEntityConverter<CrossParamEntity, CrossParam> {

    @Override
    public CrossParam toProtocol(CrossParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            CrossParam protocol = new CrossParam();

            // 基础字段映射
            protocol.setCrossId(entity.getCrossId());
            protocol.setCrossName(entity.getCrossName());

            // 位置信息映射
            protocol.setLongitude(entity.getLongitude());
            protocol.setLatitude(entity.getLatitude());

            // 描述信息
            //protocol.setDescription(entity.getDescription());

            validateConversion(entity, protocol);

            logger.debug("路口参数实体转协议成功: {}", entity.getCrossId());
            return protocol;

        } catch (Exception e) {
            logger.error("路口参数转换失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口参数转换失败", e);
        }
    }

    @Override
    public CrossParamEntity toEntity(CrossParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            CrossParamEntity entity = new CrossParamEntity();

            // 基础字段映射
            entity.setCrossId(protocol.getCrossId());
            entity.setCrossName(protocol.getCrossName());

            // 位置信息映射
            entity.setLongitude(protocol.getLongitude());
            entity.setLatitude(protocol.getLatitude());

            // 描述信息
            //entity.setDescription(protocol.getDescription());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("路口参数协议转实体成功: {}", protocol.getCrossId());
            return entity;

        } catch (Exception e) {
            logger.error("路口参数转换失败: {}", protocol.getCrossId(), e);
            throw new DataConversionException("路口参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(CrossParam protocol, CrossParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getCrossName())) {
                entity.setCrossName(protocol.getCrossName());
            }

            if (protocol.getLongitude() != null) {
                entity.setLongitude(protocol.getLongitude());
            }

            if (protocol.getLatitude() != null) {
                entity.setLatitude(protocol.getLatitude());
            }

//            if (StringUtils.hasText(protocol.getDescription())) {
//                entity.setDescription(protocol.getDescription());
//            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("路口参数实体更新成功: {}", entity.getCrossId());

        } catch (Exception e) {
            logger.error("路口参数更新失败: {}", entity.getCrossId(), e);
            throw new DataConversionException("路口参数更新失败", e);
        }
    }
}
