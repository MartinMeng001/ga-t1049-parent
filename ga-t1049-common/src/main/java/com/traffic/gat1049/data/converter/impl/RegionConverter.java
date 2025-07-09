// ================================================================
// Region Converter Implementation
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.system.RegionParam;
import com.traffic.gat1049.repository.entity.RegionParamEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 区域参数转换器实现
 * 参考SysInfo converter的实现模式
 */
@Component
public class RegionConverter extends AbstractEntityConverter<RegionParamEntity, RegionParam> {

    @Override
    public RegionParam toProtocol(RegionParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            RegionParam protocol = new RegionParam();

            // 基础字段映射
            protocol.setRegionId(entity.getRegionId());
            protocol.setRegionName(entity.getRegionName());

            // 注意：关联的子区ID列表和路口ID列表需要从关联表查询
            // 这里暂时设置空列表，实际使用时需要增强版本或在服务层填充
            // protocol.setSubRegionIdList(new ArrayList<>());
            // protocol.setCrossIdList(new ArrayList<>());

            validateConversion(entity, protocol);

            logger.debug("区域参数实体转协议成功: {}", entity.getRegionId());
            return protocol;

        } catch (Exception e) {
            logger.error("区域参数转换失败: {}", entity.getRegionId(), e);
            throw new DataConversionException("区域参数转换失败", e);
        }
    }

    @Override
    public RegionParamEntity toEntity(RegionParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            RegionParamEntity entity = new RegionParamEntity();

            // 基础字段映射
            entity.setRegionId(protocol.getRegionId());
            entity.setRegionName(protocol.getRegionName());

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("区域参数协议转实体成功: {}", protocol.getRegionId());
            return entity;

        } catch (Exception e) {
            logger.error("区域参数转换失败: {}", protocol.getRegionId(), e);
            throw new DataConversionException("区域参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(RegionParam protocol, RegionParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getRegionName())) {
                entity.setRegionName(protocol.getRegionName());
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("区域参数实体更新成功: {}", entity.getRegionId());

        } catch (Exception e) {
            logger.error("区域参数更新失败: {}", entity.getRegionId(), e);
            throw new DataConversionException("区域参数更新失败", e);
        }
    }
}
