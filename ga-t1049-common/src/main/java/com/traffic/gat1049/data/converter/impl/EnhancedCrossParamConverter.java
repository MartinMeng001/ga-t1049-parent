package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.intersection.CrossParam;
import com.traffic.gat1049.repository.entity.CrossParamEntity;
import com.traffic.gat1049.repository.interfaces.CompleteInfoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支持视图的路口参数转换器
 * 集成数据库视图查询能力
 * 命名规范参考 EnhancedSysInfoConverter
 */
@Component
public class EnhancedCrossParamConverter extends AbstractEntityConverter<CrossParamEntity, CrossParam> {

    @Autowired(required = false)
    private CompleteInfoViewRepository viewRepository;

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
            protocol.setLongitude(entity.getLongitude());
            protocol.setLatitude(entity.getLatitude());
            //protocol.setDescription(entity.getDescription());

            // 查询完整视图信息（如果视图仓库可用）
            if (viewRepository != null) {
                Map<String, Object> completeInfo = viewRepository.findCrossCompleteInfo(entity.getCrossId());
                if (completeInfo != null) {
                    // 从视图获取关联的ID列表
                    populateIdListsFromView(protocol, completeInfo);
                }
            }

            validateConversion(entity, protocol);

            logger.debug("路口参数实体转协议成功（含视图数据）: {}", entity.getCrossId());
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
            entity.setLongitude(protocol.getLongitude());
            entity.setLatitude(protocol.getLatitude());
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

    /**
     * 基于视图获取完整的路口参数协议对象
     * 包含所有关联的ID列表
     */
    public CrossParam toProtocolFromView(String crossId) {
        if (viewRepository == null) {
            throw new UnsupportedOperationException("视图仓库未配置");
        }

        Map<String, Object> completeInfo = viewRepository.findCrossCompleteInfo(crossId);
        if (completeInfo == null) {
            return null;
        }

        try {
            CrossParam protocol = new CrossParam();

            // 基础字段映射
            protocol.setCrossId((String) completeInfo.get("cross_id"));
            protocol.setCrossName((String) completeInfo.get("cross_name"));
            protocol.setLongitude(getDoubleValue(completeInfo, "longitude"));
            protocol.setLatitude(getDoubleValue(completeInfo, "latitude"));
            //protocol.setDescription((String) completeInfo.get("description"));

            // 从视图获取关联的ID列表
            populateIdListsFromView(protocol, completeInfo);

            logger.debug("从视图转换路口参数成功: {}", crossId);
            return protocol;

        } catch (Exception e) {
            logger.error("从视图转换路口参数失败: {}", crossId, e);
            throw new DataConversionException("从视图转换路口参数失败", e);
        }
    }

    /**
     * 从视图数据填充ID列表
     */
    private void populateIdListsFromView(CrossParam protocol, Map<String, Object> viewData) {
        // 车道ID列表
        String laneIdList = (String) viewData.get("lane_id_list");
        if (StringUtils.hasText(laneIdList)) {
            List<Integer> laneIds = List.of(laneIdList.split(","))
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            protocol.setLaneNoList(laneIds);
        }

        // 信号组ID列表
        String signalGroupIdList = (String) viewData.get("signal_group_id_list");
        if (StringUtils.hasText(signalGroupIdList)) {
            List<String> signalGroupIds = List.of(signalGroupIdList.split(","));
            protocol.setSignalGroupNoList(signalGroupIds);
        }

        // 配时方案ID列表
        String planIdList = (String) viewData.get("plan_id_list");
        if (StringUtils.hasText(planIdList)) {
            List<Integer> planIds = List.of(planIdList.split(","))
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            protocol.setPlanNoList(planIds);
        }

        // 检测器ID列表
        String detectorIdList = (String) viewData.get("detector_id_list");
        if (StringUtils.hasText(detectorIdList)) {
            List<Integer> detectorIds = List.of(detectorIdList.split(","))
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            protocol.setDetNoList(detectorIds);
        }
    }

    private Double getDoubleValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }
}
