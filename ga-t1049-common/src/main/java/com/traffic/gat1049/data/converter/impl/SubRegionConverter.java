package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.system.SubRegionParam;
import com.traffic.gat1049.repository.entity.SubRegionParamEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

/**
 * 子区参数转换器实现
 * 参考SysInfo converter的实现模式
 */
@Component
public class SubRegionConverter extends AbstractEntityConverter<SubRegionParamEntity, SubRegionParam> {

    @Override
    public SubRegionParam toProtocol(SubRegionParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SubRegionParam protocol = new SubRegionParam();

            // 基础字段映射
            protocol.setSubRegionId(entity.getSubRegionId());
            protocol.setSubRegionName(entity.getSubRegionName());

            // 注意：关联的路口ID列表和关键路口ID列表需要从关联表查询
            // 这里暂时设置空列表，实际使用时需要增强版本或在服务层填充
            // protocol.setCrossIdList(new ArrayList<>());
            // protocol.setKeyCrossIdList(new ArrayList<>());

            validateConversion(entity, protocol);

            logger.debug("子区参数实体转协议成功: {}", entity.getSubRegionId());
            return protocol;

        } catch (Exception e) {
            logger.error("子区参数转换失败: {}", entity.getSubRegionId(), e);
            throw new DataConversionException("子区参数转换失败", e);
        }
    }

    @Override
    public SubRegionParamEntity toEntity(SubRegionParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            SubRegionParamEntity entity = new SubRegionParamEntity();

            // 基础字段映射
            entity.setSubRegionId(protocol.getSubRegionId());
            entity.setSubRegionName(protocol.getSubRegionName());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("子区参数协议转实体成功: {}", protocol.getSubRegionId());
            return entity;

        } catch (Exception e) {
            logger.error("子区参数转换失败: {}", protocol.getSubRegionId(), e);
            throw new DataConversionException("子区参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(SubRegionParam protocol, SubRegionParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getSubRegionName())) {
                entity.setSubRegionName(protocol.getSubRegionName());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("子区参数实体更新成功: {}", entity.getSubRegionId());

        } catch (Exception e) {
            logger.error("子区参数更新失败: {}", entity.getSubRegionId(), e);
            throw new DataConversionException("子区参数更新失败", e);
        }
    }
}
