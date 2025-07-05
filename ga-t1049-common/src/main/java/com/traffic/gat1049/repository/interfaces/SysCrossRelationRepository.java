package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.SysCrossRelation;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统路口关联Repository
 */
@Repository
public interface SysCrossRelationRepository extends BaseMapper<SysCrossRelation> {

    /**
     * 查询系统的所有路口关联
     */
    @Select("SELECT * FROM sys_cross_relation WHERE system_id = #{systemId} AND is_active = 1 ORDER BY priority, cross_id")
    List<SysCrossRelation> findBySystemId(@Param("systemId") String systemId);

    /**
     * 查询路口的所有系统关联
     */
    @Select("SELECT * FROM sys_cross_relation WHERE cross_id = #{crossId} AND is_active = 1 ORDER BY priority")
    List<SysCrossRelation> findByCrossId(@Param("crossId") String crossId);

    /**
     * 查询主控系统关联
     */
    @Select("SELECT * FROM sys_cross_relation WHERE cross_id = #{crossId} AND is_primary = 1 AND is_active = 1")
    SysCrossRelation findPrimarySystemByCrossId(@Param("crossId") String crossId);

    /**
     * 查询系统的主控路口
     */
    @Select("SELECT * FROM sys_cross_relation WHERE system_id = #{systemId} AND is_primary = 1 AND is_active = 1 ORDER BY cross_id")
    List<SysCrossRelation> findPrimaryCrossesBySystemId(@Param("systemId") String systemId);

    /**
     * 更新关联状态
     */
    @Update("UPDATE sys_cross_relation SET is_active = #{isActive}, updated_time = #{updateTime} " +
            "WHERE system_id = #{systemId} AND cross_id = #{crossId}")
    int updateRelationStatus(@Param("systemId") String systemId,
                             @Param("crossId") String crossId,
                             @Param("isActive") Boolean isActive,
                             @Param("updateTime") LocalDateTime updateTime);

    /**
     * 设置主控系统
     */
    @Update("UPDATE sys_cross_relation SET is_primary = #{isPrimary}, updated_time = #{updateTime} " +
            "WHERE system_id = #{systemId} AND cross_id = #{crossId}")
    int updatePrimaryStatus(@Param("systemId") String systemId,
                            @Param("crossId") String crossId,
                            @Param("isPrimary") Boolean isPrimary,
                            @Param("updateTime") LocalDateTime updateTime);

    /**
     * 批量取消其他系统的主控状态
     */
    @Update("UPDATE sys_cross_relation SET is_primary = 0, updated_time = #{updateTime} " +
            "WHERE cross_id = #{crossId} AND system_id != #{excludeSystemId}")
    int clearOtherPrimaryStatus(@Param("crossId") String crossId,
                                @Param("excludeSystemId") String excludeSystemId,
                                @Param("updateTime") LocalDateTime updateTime);

    /**
     * 查询系统概览信息（使用视图）
     */
    @Select("SELECT * FROM v_system_overview WHERE system_id = #{systemId}")
    @Results(id = "systemOverviewMap", value = {
            @Result(property = "systemId", column = "system_id"),
            @Result(property = "sysName", column = "sys_name"),
            @Result(property = "sysVersion", column = "sys_version"),
            @Result(property = "supplier", column = "supplier"),
            @Result(property = "isActive", column = "is_active"),
            @Result(property = "sysStatus", column = "sys_status"),
            @Result(property = "lastStatusTime", column = "last_status_time"),
            @Result(property = "crossCount", column = "cross_count"),
            @Result(property = "primaryCrossCount", column = "primary_cross_count"),
            @Result(property = "regionCount", column = "region_count"),
            @Result(property = "subRegionCount", column = "sub_region_count"),
            @Result(property = "routeCount", column = "route_count"),
            @Result(property = "controllerCount", column = "controller_count")
    })
    Object findSystemOverview(@Param("systemId") String systemId);

    /**
     * 查询路口系统映射（使用视图）
     */
    @Select("SELECT * FROM v_cross_system_mapping WHERE cross_id = #{crossId}")
    @Results(id = "crossSystemMappingMap", value = {
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "crossName", column = "cross_name"),
            @Result(property = "primarySystemId", column = "primary_system_id"),
            @Result(property = "primarySystemName", column = "primary_system_name"),
            @Result(property = "allSystemIds", column = "all_system_ids"),
            @Result(property = "allSystemNames", column = "all_system_names"),
            @Result(property = "systemCount", column = "system_count")
    })
    Object findCrossSystemMapping(@Param("crossId") String crossId);
}
