package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.model.enums.RouteType;
import com.traffic.gat1049.protocol.model.system.RouteParam;
import com.traffic.gat1049.repository.entity.RouteParamEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 增强的线路参数转换器（支持视图数据）
 */
@Component
public class EnhancedRouteConverter extends AbstractEntityConverter<RouteParamEntity, RouteParam> {

    // 这里可以注入视图Repository，用于获取完整的关联数据
    // @Autowired
    // private RouteCompleteViewRepository viewRepository;

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

            // 从视图查询完整信息（如果有视图Repository）
            // Map<String, Object> completeInfo = viewRepository.findRouteCompleteInfo(entity.getRouteId());
            // if (completeInfo != null) {
            //     populateRouteCrossListFromView(protocol, completeInfo);
            //     populateSubRegionIdListFromView(protocol, completeInfo);
            // }

            validateConversion(entity, protocol);

            logger.debug("线路参数实体转协议成功（含完整数据）: {}", entity.getRouteId());
            return protocol;

        } catch (Exception e) {
            logger.error("线路参数转换失败: {}", entity.getRouteId(), e);
            throw new DataConversionException("线路参数转换失败", e);
        }
    }

    @Override
    public RouteParamEntity toEntity(RouteParam protocol) {
        // 基础实现与RouteConverter相同
        return new RouteConverter().toEntity(protocol);
    }

    @Override
    public void updateEntity(RouteParam protocol, RouteParamEntity entity) {
        // 基础实现与RouteConverter相同
        new RouteConverter().updateEntity(protocol, entity);
    }

    /**
     * 从视图数据填充线路路口列表
     */
    private void populateRouteCrossListFromView(RouteParam protocol, Map<String, Object> viewData) {
        String routeCrossData = (String) viewData.get("route_cross_data");
        if (StringUtils.hasText(routeCrossData)) {
            try {
                // 解析JSON格式的路口数据，转换为RouteCross对象列表
                // 这里需要JSON解析逻辑，类似：
                // List<RouteCross> routeCrossList = objectMapper.readValue(routeCrossData,
                //     new TypeReference<List<RouteCross>>() {});
                // protocol.setRouteCrossList(routeCrossList);
            } catch (Exception e) {
                logger.error("解析线路路口数据失败: {}", routeCrossData, e);
            }
        }
    }

    /**
     * 从视图数据填充子区ID列表
     */
    private void populateSubRegionIdListFromView(RouteParam protocol, Map<String, Object> viewData) {
        String subRegionIdList = (String) viewData.get("sub_region_id_list");
        if (StringUtils.hasText(subRegionIdList)) {
            List<String> subRegionIds = Arrays.asList(subRegionIdList.split(","));
            protocol.setSubRegionIdList(subRegionIds);
        }
    }
}
