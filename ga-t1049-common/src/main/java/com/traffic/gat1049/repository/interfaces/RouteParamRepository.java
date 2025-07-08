package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.RouteParamEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 线路参数Repository
 */
@Repository
public interface RouteParamRepository extends BaseMapper<RouteParamEntity> {

    /**
     * 根据线路ID查询
     */
    @Select("SELECT * FROM route_param WHERE route_id = #{routeId}")
    RouteParamEntity findByRouteId(@Param("routeId") String routeId);

    /**
     * 根据线路类型查询
     */
    @Select("SELECT * FROM route_param WHERE type = #{type} ORDER BY route_id")
    List<RouteParamEntity> findByType(@Param("type") Integer type);

    /**
     * 查询所有线路（带完整信息视图）
     */
    @Select("SELECT * FROM v_route_param_complete ORDER BY route_id")
    @Results(id = "routeCompleteMap", value = {
            @Result(property = "routeId", column = "route_id"),
            @Result(property = "routeName", column = "route_name"),
            @Result(property = "type", column = "type"),
            @Result(property = "subRegionIdList", column = "sub_region_id_list"),
            @Result(property = "crossIdList", column = "cross_id_list"),
            @Result(property = "subRegionCount", column = "sub_region_count"),
            @Result(property = "crossCount", column = "cross_count"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<RouteParamEntity> findAllWithCompleteInfo();

    /**
     * 根据子区ID查询线路
     */
    @Select("SELECT rp.* FROM route_param rp " +
            "JOIN route_sub_region rsr ON rp.route_id = rsr.route_id " +
            "WHERE rsr.sub_region_id = #{subRegionId} ORDER BY rp.route_id")
    List<RouteParamEntity> findBySubRegionId(@Param("subRegionId") String subRegionId);

    /**
     * 根据线路名称模糊查询
     */
    @Select("SELECT * FROM route_param WHERE route_name LIKE CONCAT('%', #{routeName}, '%') ORDER BY route_id")
    List<RouteParamEntity> findByRouteNameLike(@Param("routeName") String routeName);
}
