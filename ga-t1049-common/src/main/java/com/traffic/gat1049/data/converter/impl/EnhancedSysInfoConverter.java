
// ================================================================
// 数据库视图支持情况分析
// ================================================================

/**
 * 现有视图结构：
 *
 * 1. v_protocol_sys_info_complete - 系统完整信息视图
 *    - 包含基础系统信息
 *    - 关联的路口、子区、线路、区域、信号机列表和数量
 *    - 支持多系统架构
 *
 * 2. v_system_overview - 系统概览视图
 *    - 系统基础信息 + 状态信息
 *    - 统计信息（路口总数、在线数等）
 *    - 支持实时状态展示
 *
 * 3. v_cross_system_mapping - 路口系统映射视图
 *    - 路口与系统的归属关系
 *    - 主控系统和优先级信息
 */

// ================================================================
// 1. 增强的系统信息转换器（支持视图数据）
// ================================================================
package com.traffic.gat1049.data.converter.impl;

import com.traffic.gat1049.data.converter.base.AbstractEntityConverter;
import com.traffic.gat1049.exception.DataConversionException;
import com.traffic.gat1049.protocol.model.system.SysInfo;
import com.traffic.gat1049.repository.entity.SysInfoEntity;
import com.traffic.gat1049.repository.interfaces.CompleteInfoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 支持视图的系统信息转换器
 * 集成数据库视图查询能力
 */
@Component
public class EnhancedSysInfoConverter extends AbstractEntityConverter<SysInfoEntity, SysInfo> {

    @Autowired
    private CompleteInfoViewRepository viewRepository;

    @Override
    public SysInfo toProtocol(SysInfoEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            SysInfo protocol = new SysInfo();

            // 基础字段映射
            //protocol.setSystemID(entity.getSystemId());
            protocol.setSysName(entity.getSysName());
            protocol.setSysVersion(entity.getSysVersion());
            protocol.setSupplier(entity.getSupplier());
            //protocol.setDescription(entity.getDescription());

            // 查询完整视图信息
            Map<String, Object> completeInfo = viewRepository.findSystemCompleteInfo(entity.getSystemId());
            if (completeInfo != null) {
                // 从视图获取关联的ID列表
                populateIdListsFromView(protocol, completeInfo);
            }

            validateConversion(entity, protocol);

            logger.debug("系统信息实体转协议成功（含视图数据）: {}", entity.getSystemId());
            return protocol;

        } catch (Exception e) {
            logger.error("系统信息转换失败: {}", entity.getSystemId(), e);
            throw new DataConversionException("系统信息转换失败", e);
        }
    }

    @Override
    public SysInfoEntity toEntity(SysInfo protocol) {
        if (protocol == null) {
            return null;
        }

        try {
            SysInfoEntity entity = new SysInfoEntity();

            // 基础字段映射
            //entity.setSystemId(protocol.getSystemID());
            entity.setSysName(protocol.getSysName());
            entity.setSysVersion(protocol.getSysVersion());
            entity.setSupplier(protocol.getSupplier());
            //entity.setDescription(protocol.getDescription());

            // 设置默认值
            entity.setIsActive(true);

            // 设置审计字段
            setEntityAuditFields(entity, true);

            validateConversion(entity, protocol);

            logger.debug("系统信息协议转实体成功: {}", protocol.getSysName());
            return entity;

        } catch (Exception e) {
            logger.error("系统信息转换失败: {}", protocol.getSysName(), e);
            throw new DataConversionException("系统信息转换失败", e);
        }
    }

    @Override
    public void updateEntity(SysInfo protocol, SysInfoEntity entity) {
        if (protocol == null || entity == null) {
            throw new DataConversionException("更新参数不能为null");
        }

        try {
            // 只更新非空字段
            if (StringUtils.hasText(protocol.getSysName())) {
                entity.setSysName(protocol.getSysName());
            }
            if (StringUtils.hasText(protocol.getSysVersion())) {
                entity.setSysVersion(protocol.getSysVersion());
            }
            if (StringUtils.hasText(protocol.getSupplier())) {
                entity.setSupplier(protocol.getSupplier());
            }
//            if (StringUtils.hasText(protocol.getDescription())) {
//                entity.setDescription(protocol.getDescription());
//            }

            // 更新修改时间
            setEntityAuditFields(entity, false);

            logger.debug("系统信息实体更新成功: {}", entity.getSystemId());

        } catch (Exception e) {
            logger.error("系统信息更新失败: {}", entity.getSystemId(), e);
            throw new DataConversionException("系统信息更新失败", e);
        }
    }

    /**
     * 从视图数据填充ID列表
     */
    private void populateIdListsFromView(SysInfo protocol, Map<String, Object> viewData) {
        // 路口ID列表
        String crossIdList = (String) viewData.get("cross_id_list");
        if (StringUtils.hasText(crossIdList)) {
            List<String> crossIds = List.of(crossIdList.split(","));
            protocol.setCrossIdList(crossIds);
        }

        // 子区ID列表
        String subRegionIdList = (String) viewData.get("sub_region_id_list");
        if (StringUtils.hasText(subRegionIdList)) {
            List<String> subRegionIds = List.of(subRegionIdList.split(","));
            protocol.setSubRegionIdList(subRegionIds);
        }

        // 线路ID列表
        String routeIdList = (String) viewData.get("route_id_list");
        if (StringUtils.hasText(routeIdList)) {
            List<String> routeIds = List.of(routeIdList.split(","));
            protocol.setRouteIdList(routeIds);
        }

        // 区域ID列表
        String regionIdList = (String) viewData.get("region_id_list");
        if (StringUtils.hasText(regionIdList)) {
            List<String> regionIds = List.of(regionIdList.split(","));
            protocol.setRegionIdList(regionIds);
        }

        // 信号机ID列表
        String controllerIdList = (String) viewData.get("signal_controller_id_list");
        if (StringUtils.hasText(controllerIdList)) {
            List<String> controllerIds = List.of(controllerIdList.split(","));
            protocol.setSignalControllerIdList(controllerIds);
        }
    }

    /**
     * 专门从视图获取完整协议对象
     * 这是一个增强方法，直接基于视图构建协议对象
     */
    public SysInfo toProtocolFromView(String systemId) {
        if (!StringUtils.hasText(systemId)) {
            return null;
        }

        try {
            Map<String, Object> completeInfo = viewRepository.findSystemCompleteInfo(systemId);
            if (completeInfo == null) {
                return null;
            }

            SysInfo protocol = new SysInfo();

            // 从视图直接映射字段
            //protocol.setSystemID((String) completeInfo.get("system_id"));
            protocol.setSysName((String) completeInfo.get("sys_name"));
            protocol.setSysVersion((String) completeInfo.get("sys_version"));
            protocol.setSupplier((String) completeInfo.get("supplier"));
            //protocol.setDescription((String) completeInfo.get("system_description"));

            // 从视图获取关联的ID列表
            populateIdListsFromView(protocol, completeInfo);

            logger.debug("从视图构建协议对象成功: {}", systemId);
            return protocol;

        } catch (Exception e) {
            logger.error("从视图构建协议对象失败: {}", systemId, e);
            throw new DataConversionException("从视图构建协议对象失败", e);
        }
    }
}