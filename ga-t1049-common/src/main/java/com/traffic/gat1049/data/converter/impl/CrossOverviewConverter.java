package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.interfaces.ViewDataConverter;
import com.traffic.gat1049.model.vo.CrossInfoVo;
import com.traffic.gat1049.model.enums.CrossFeature;
import com.traffic.gat1049.model.enums.CrossGrade;
import com.traffic.gat1049.model.enums.SystemState;
import com.traffic.gat1049.repository.interfaces.CompleteInfoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 路口信息概览转换器
 * 专门处理v_cross_overview视图数据
 * 命名规范参考 SystemOverviewConverter
 */
@Component
public class CrossOverviewConverter implements ViewDataConverter<CrossInfoVo> {

    private static final Logger logger = LoggerFactory.getLogger(CrossOverviewConverter.class);

    @Autowired(required = false)
    private CompleteInfoViewRepository viewRepository;

    /**
     * 将视图数据转换为路口信息VO
     */
    @Override
    public CrossInfoVo fromViewData(Map<String, Object> viewData) {
        if (viewData == null) {
            return null;
        }

        CrossInfoVo vo = new CrossInfoVo();

        // 基础信息
        vo.setCrossId((String) viewData.get("cross_id"));
        vo.setCrossName((String) viewData.get("cross_name"));

        // 枚举转换
        String featureStr = (String) viewData.get("feature");
        if (featureStr != null) {
            try {
                vo.setFeature(CrossFeature.fromCode(featureStr));
            } catch (Exception e) {
                logger.debug("路口形状转换失败: {}", featureStr);
            }
        }

        String gradeStr = (String) viewData.get("grade");
        if (gradeStr != null) {
            try {
                vo.setGrade(CrossGrade.fromCode(gradeStr));
            } catch (Exception e) {
                logger.debug("路口等级转换失败: {}", gradeStr);
            }
        }

        // 位置信息
        vo.setLongitude(getDoubleValue(viewData, "longitude"));
        vo.setLatitude(getDoubleValue(viewData, "latitude"));

        // 关联信息
        vo.setSignalControllerId((String) viewData.get("signal_controller_id"));

        // 统计信息
        vo.setLaneCount(getIntValue(viewData, "lane_count"));
        vo.setSignalGroupCount(getIntValue(viewData, "signal_group_count"));

        // 状态信息
        String stateStr = (String) viewData.get("cross_state");
        if (stateStr != null) {
            try {
                vo.setState(SystemState.valueOf(stateStr.toUpperCase()));
            } catch (Exception e) {
                vo.setState(SystemState.OFFLINE);
            }
        } else {
            vo.setState(SystemState.OFFLINE);
        }

        // 时间信息
        vo.setLastUpdateTime(getDateTimeValue(viewData, "last_update_time"));

        return vo;
    }

    /**
     * 获取所有路口概览
     */
    public List<CrossInfoVo> getAllCrossOverview() {
        if (viewRepository == null) {
            throw new UnsupportedOperationException("视图仓库未配置");
        }

        List<Map<String, Object>> viewDataList = viewRepository.findAllCrossOverview();

        return viewDataList.stream()
                .map(this::fromViewData)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定路口概览
     */
    public CrossInfoVo getCrossOverview(String crossId) {
        if (viewRepository == null) {
            throw new UnsupportedOperationException("视图仓库未配置");
        }

        Map<String, Object> viewData = viewRepository.findCrossOverviewById(crossId);
        return fromViewData(viewData);
    }

    /**
     * 根据条件查询路口概览
     */
    public List<CrossInfoVo> findCrossOverview(Map<String, Object> conditions) {
        if (viewRepository == null) {
            throw new UnsupportedOperationException("视图仓库未配置");
        }

        List<Map<String, Object>> viewDataList = viewRepository.findCrossOverviewByCondition(conditions);

        return viewDataList.stream()
                .map(this::fromViewData)
                .collect(Collectors.toList());
    }

    private Integer getIntValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private Double getDoubleValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private LocalDateTime getDateTimeValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        return null;
    }
}
