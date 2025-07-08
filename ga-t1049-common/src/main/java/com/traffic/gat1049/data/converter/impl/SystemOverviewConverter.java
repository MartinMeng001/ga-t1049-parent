// ================================================================
// 2. 系统概览转换器（支持视图）
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.interfaces.ViewDataConverter;
import com.traffic.gat1049.model.vo.SystemOverviewVo;
import com.traffic.gat1049.model.enums.SystemState;
import com.traffic.gat1049.repository.interfaces.CompleteInfoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统概览转换器
 * 专门处理v_system_overview视图数据
 */
@Component
public class SystemOverviewConverter implements ViewDataConverter<SystemOverviewVo> {

    @Autowired
    private CompleteInfoViewRepository viewRepository;

    /**
     * 将视图数据转换为系统概览VO
     */
    public SystemOverviewVo fromViewData(Map<String, Object> viewData) {
        if (viewData == null) {
            return null;
        }

        SystemOverviewVo vo = new SystemOverviewVo();

        // 基础信息
        //vo.setSystemId((String) viewData.get("system_id"));
        vo.setSystemName((String) viewData.get("sys_name"));
        vo.setSystemVersion((String) viewData.get("sys_version"));
        //vo.set.setSupplier((String) viewData.get("supplier"));
        //vo.setDescription((String) viewData.get("description"));

        // 系统状态
        String statusStr = (String) viewData.get("sys_status");
        if (statusStr != null) {
            vo.setSystemState(SystemState.valueOf(statusStr.toUpperCase()));
        }

        // 统计信息
        vo.setTotalCrossCount(getIntValue(viewData, "total_crosses"));
        //vo.setTotalSubRegions(getIntValue(viewData, "total_sub_regions"));
        //vo.setTotalRoutes(getIntValue(viewData, "total_routes"));
        //vo.setTotalRegions(getIntValue(viewData, "total_regions"));
        //vo.setTotalControllers(getIntValue(viewData, "total_controllers"));

        vo.setOnlineCrossCount(getIntValue(viewData, "active_crosses"));
        vo.setOnlineSignalControllerCount(getIntValue(viewData, "active_controllers"));

        // 时间信息
        vo.setLastUpdateTime(getDateTimeValue(viewData, "last_status_time"));
        //vo.setCreatedTime(getDateTimeValue(viewData, "created_time"));
        //vo.setUpdatedTime(getDateTimeValue(viewData, "updated_time"));

        return vo;
    }

    /**
     * 获取所有系统概览
     */
    public List<SystemOverviewVo> getAllSystemOverview() {
        List<Map<String, Object>> viewDataList = viewRepository.findSystemOverview();

        return viewDataList.stream()
                .map(this::fromViewData)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定系统概览
     */
    public SystemOverviewVo getSystemOverview(String systemId) {
        Map<String, Object> viewData = viewRepository.findSystemOverviewById(systemId);
        return fromViewData(viewData);
    }

    private Integer getIntValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    private LocalDateTime getDateTimeValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        return null;
    }
}