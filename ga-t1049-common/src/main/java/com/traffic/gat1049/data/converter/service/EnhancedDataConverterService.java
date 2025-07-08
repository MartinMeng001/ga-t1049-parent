// ================================================================
// 4. 增强的数据转换服务（支持视图）
// ================================================================
package com.traffic.gat1049.data.converter.service;

import com.traffic.gat1049.data.converter.impl.EnhancedSysInfoConverter;
import com.traffic.gat1049.data.converter.impl.SystemOverviewConverter;
import com.traffic.gat1049.model.vo.SystemOverviewVo;
import com.traffic.gat1049.protocol.model.system.SysInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 增强的数据转换服务
 * 支持视图数据转换
 */
@Service
public class EnhancedDataConverterService extends DataConverterService {

    @Autowired
    private EnhancedSysInfoConverter enhancedSysInfoConverter;

    @Autowired
    private SystemOverviewConverter systemOverviewConverter;

    /**
     * 基于视图获取完整的系统信息协议对象
     * 包含所有关联的ID列表
     */
    public SysInfo getCompleteSystemInfo(String systemId) {
        return enhancedSysInfoConverter.toProtocolFromView(systemId);
    }

    /**
     * 获取系统概览信息
     */
    public SystemOverviewVo getSystemOverview(String systemId) {
        return systemOverviewConverter.getSystemOverview(systemId);
    }

    /**
     * 获取所有系统概览信息
     */
    public List<SystemOverviewVo> getAllSystemOverview() {
        return systemOverviewConverter.getAllSystemOverview();
    }
}
