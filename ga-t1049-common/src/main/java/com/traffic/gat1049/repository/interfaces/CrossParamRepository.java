package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.traffic.gat1049.repository.entity.CrossParamEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 路口参数Repository
 */
@Repository
public interface CrossParamRepository extends BaseMapper<CrossParamEntity> {

    /**
     * 根据路口ID查询
     */
    @Select("SELECT * FROM cross_param WHERE cross_id = #{crossId}")
    CrossParamEntity findByCrossId(@Param("crossId") String crossId);

    /**
     * 查询所有路口（带完整信息视图）
     */
    @Select("SELECT * FROM v_cross_param_complete ORDER BY cross_id")
    @Results(id = "crossCompleteMap", value = {
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "crossName", column = "cross_name"),
            @Result(property = "feature", column = "feature"),
            @Result(property = "grade", column = "grade"),
            @Result(property = "greenConflictMatrix", column = "green_conflict_matrix"),
            @Result(property = "longitude", column = "longitude"),
            @Result(property = "latitude", column = "latitude"),
            @Result(property = "altitude", column = "altitude"),
            @Result(property = "regionId", column = "region_id"),
            @Result(property = "subRegionId", column = "sub_region_id"),
            @Result(property = "routeIdList", column = "route_id_list"),
            @Result(property = "signalControllerId", column = "signal_controller_id"),
            @Result(property = "systemIdList", column = "system_id_list"),
            @Result(property = "isPrimaryList", column = "is_primary_list"),
            @Result(property = "systemCount", column = "system_count"),
            @Result(property = "routeCount", column = "route_count"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<CrossParamEntity> findAllWithCompleteInfo();

    /**
     * 根据区域ID查询路口
     */
    @Select("SELECT cp.* FROM cross_param cp " +
            "JOIN region_cross rc ON cp.cross_id = rc.cross_id " +
            "WHERE rc.region_id = #{regionId} ORDER BY cp.cross_id")
    List<CrossParamEntity> findByRegionId(@Param("regionId") String regionId);

    /**
     * 根据子区ID查询路口
     */
    @Select("SELECT cp.* FROM cross_param cp " +
            "JOIN sub_region_cross src ON cp.cross_id = src.cross_id " +
            "WHERE src.sub_region_id = #{subRegionId} ORDER BY cp.cross_id")
    List<CrossParamEntity> findBySubRegionId(@Param("subRegionId") String subRegionId);

    /**
     * 根据线路ID查询路口（按顺序）
     */
    @Select("SELECT cp.* FROM cross_param cp " +
            "JOIN route_cross rc ON cp.cross_id = rc.cross_id " +
            "WHERE rc.route_id = #{routeId} ORDER BY rc.order_seq")
    List<CrossParamEntity> findByRouteIdOrderBySeq(@Param("routeId") String routeId);

    /**
     * 根据路口等级查询
     */
    @Select("SELECT * FROM cross_param WHERE grade = #{grade} ORDER BY cross_id")
    List<CrossParamEntity> findByGrade(@Param("grade") String grade);

    /**
     * 根据路口形状特征查询
     */
    @Select("SELECT * FROM cross_param WHERE feature = #{feature} ORDER BY cross_id")
    List<CrossParamEntity> findByFeature(@Param("feature") Integer feature);

    /**
     * 根据经纬度范围查询路口
     */
    @Select("SELECT * FROM cross_param WHERE longitude BETWEEN #{minLng} AND #{maxLng} " +
            "AND latitude BETWEEN #{minLat} AND #{maxLat} ORDER BY cross_id")
    List<CrossParamEntity> findByLocation(@Param("minLng") Double minLng, @Param("maxLng") Double maxLng,
                                          @Param("minLat") Double minLat, @Param("maxLat") Double maxLat);

    /**
     * 分页查询路口
     */
    @Select("SELECT * FROM cross_param WHERE cross_name LIKE CONCAT('%', #{keyword}, '%') ORDER BY cross_id")
    IPage<CrossParamEntity> findByKeywordPaged(Page<CrossParamEntity> page, @Param("keyword") String keyword);

    /**
     * 查询系统关联的路口
     */
    @Select("SELECT cp.* FROM cross_param cp " +
            "JOIN sys_cross_relation scr ON cp.cross_id = scr.cross_id " +
            "WHERE scr.system_id = #{systemId} AND scr.is_active = 1 ORDER BY cp.cross_id")
    List<CrossParamEntity> findBySystemId(@Param("systemId") String systemId);

    /**
     * 查询主控路口
     */
    @Select("SELECT cp.* FROM cross_param cp " +
            "JOIN sys_cross_relation scr ON cp.cross_id = scr.cross_id " +
            "WHERE scr.system_id = #{systemId} AND scr.is_primary = 1 AND scr.is_active = 1 ORDER BY cp.cross_id")
    List<CrossParamEntity> findPrimaryCrossesBySystemId(@Param("systemId") String systemId);
}
