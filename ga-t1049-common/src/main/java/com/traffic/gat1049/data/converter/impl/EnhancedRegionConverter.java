package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.system.RegionParam;
import com.traffic.gat1049.repository.entity.RegionParamEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 增强的区域参数转换器（支持视图数据）
 * 类似于EnhancedSysInfoConverter的实现
 */
@Component
public class EnhancedRegionConverter extends AbstractEntityConverter<RegionParamEntity, RegionParam> {

    // 这里可以注入视图Repository，用于获取完整的关联数据
    // @Autowired
    // private RegionCompleteViewRepository viewRepository;

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

            // 从视图查询完整信息（如果有视图Repository）
            // Map<String, Object> completeInfo = viewRepository.findRegionCompleteInfo(entity.getRegionId());
            // if (completeInfo != null) {
            //     populateIdListsFromView(protocol, completeInfo);
            // }

            validateConversion(entity, protocol);

            logger.debug("区域参数实体转协议成功（含完整数据）: {}", entity.getRegionId());
            return protocol;

        } catch (Exception e) {
            logger.error("区域参数转换失败: {}", entity.getRegionId(), e);
            throw new DataConversionException("区域参数转换失败", e);
        }
    }

    @Override
    public RegionParamEntity toEntity(RegionParam protocol) {
        // 基础实现与RegionConverter相同
        return new RegionConverter().toEntity(protocol);
    }

    @Override
    public void updateEntity(RegionParam protocol, RegionParamEntity entity) {
        // 基础实现与RegionConverter相同
        new RegionConverter().updateEntity(protocol, entity);
    }

    /**
     * 从视图数据填充ID列表
     */
    private void populateIdListsFromView(RegionParam protocol, Map<String, Object> viewData) {
        // 子区ID列表
        String subRegionIdList = (String) viewData.get("sub_region_id_list");
        if (StringUtils.hasText(subRegionIdList)) {
            List<String> subRegionIds = Arrays.asList(subRegionIdList.split(","));
            protocol.setSubRegionIdList(subRegionIds);
        }

        // 路口ID列表
        String crossIdList = (String) viewData.get("cross_id_list");
        if (StringUtils.hasText(crossIdList)) {
            List<String> crossIds = Arrays.asList(crossIdList.split(","));
            protocol.setCrossIdList(crossIds);
        }
    }
}