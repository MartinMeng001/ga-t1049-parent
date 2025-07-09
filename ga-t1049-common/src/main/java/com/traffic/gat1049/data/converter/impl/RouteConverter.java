package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.system.RouteParam;
import com.traffic.gat1049.protocol.model.system.RouteCross;
import com.traffic.gat1049.repository.entity.RouteParamEntity;
import com.traffic.gat1049.model.enums.RouteType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

/**
 * 线路参数转换器实现
 * 参考SysInfo converter的实现模式
 */
@Component
public class RouteConverter extends AbstractEntityConverter<RouteParamEntity, RouteParam> {

    @Override
    public RouteParam toProtocol(RouteParamEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            RouteParam protocol = new RouteParam();

            // 基础字段映射
            protocol.setRouteId(entity.getRouteId());
            protocol.setRouteName(entity.getRouteName());

            // 枚举转换
            if (entity.getType() != null) {
                protocol.setType(RouteType.fromCode(String.valueOf(entity.getType())));
            }

            // 注意：线路路口列表和子区ID列表需要从关联表查询
            // 这里暂时设置空列表，实际使用时需要增强版本或在服务层填充
            // protocol.setRouteCrossList(new ArrayList<>());
            // protocol.setSubRegionIdList(new ArrayList<>());

            validateConversion(entity, protocol);

            logger.debug("线路参数实体转协议成功: {}", entity.getRouteId());
            return protocol;

        } catch (Exception e) {
            logger.error("线路参数转换失败: {}", entity.getRouteId(), e);
            throw new DataConversionException("线路参数转换失败", e);
        }
    }

    @Override
    public RouteParamEntity toEntity(RouteParam protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            RouteParamEntity entity = new RouteParamEntity();

            // 基础字段映射
            entity.setRouteId(protocol.getRouteId());
            entity.setRouteName(protocol.getRouteName());

            // 枚举转换
            if (protocol.getType() != null) {
                entity.setType(Integer.valueOf(protocol.getType().getCode()));
            }

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("线路参数协议转实体成功: {}", protocol.getRouteId());
            return entity;

        } catch (Exception e) {
            logger.error("线路参数转换失败: {}", protocol.getRouteId(), e);
            throw new DataConversionException("线路参数转换失败", e);
        }
    }

    @Override
    public void updateEntity(RouteParam protocol, RouteParamEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getRouteName())) {
                entity.setRouteName(protocol.getRouteName());
            }
            if (protocol.getType() != null) {
                entity.setType(Integer.valueOf(protocol.getType().getCode()));
            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("线路参数实体更新成功: {}", entity.getRouteId());

        } catch (Exception e) {
            logger.error("线路参数更新失败: {}", entity.getRouteId(), e);
            throw new DataConversionException("线路参数更新失败", e);
        }
    }
}
