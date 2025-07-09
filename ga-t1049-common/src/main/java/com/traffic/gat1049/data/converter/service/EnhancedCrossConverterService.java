package com.traffic.gat1049.data.converter.service;

import com.traffic.gat1049.data.converter.impl.CrossOverviewConverter;
import com.traffic.gat1049.data.converter.impl.EnhancedCrossParamConverter;
import com.traffic.gat1049.model.vo.CrossInfoVo;
import com.traffic.gat1049.protocol.model.intersection.CrossParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 增强的数据转换服务扩展
 * 支持路口视图数据转换
 * 命名规范参考 EnhancedDataConverterService
 */
@Service
public class EnhancedCrossConverterService {

    @Autowired
    private EnhancedCrossParamConverter enhancedCrossParamConverter;

    @Autowired
    private CrossOverviewConverter crossOverviewConverter;

    /**
     * 基于视图获取完整的路口参数协议对象
     * 包含所有关联的ID列表
     */
    public CrossParam getCompleteCrossInfo(String crossId) {
        return enhancedCrossParamConverter.toProtocolFromView(crossId);
    }

    /**
     * 获取路口概览信息
     */
    public CrossInfoVo getCrossOverview(String crossId) {
        return crossOverviewConverter.getCrossOverview(crossId);
    }

    /**
     * 获取所有路口概览信息
     */
    public List<CrossInfoVo> getAllCrossOverview() {
        return crossOverviewConverter.getAllCrossOverview();
    }

    /**
     * 根据条件查询路口概览信息
     */
    public List<CrossInfoVo> findCrossOverview(Map<String, Object> conditions) {
        return crossOverviewConverter.findCrossOverview(conditions);
    }
}
