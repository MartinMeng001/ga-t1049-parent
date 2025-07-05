package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.SubRegionParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 子区参数Repository
 */
@Repository
public interface SubRegionParamRepository extends BaseMapper<SubRegionParam> {

    /**
     * 根据子区ID查询
     */
    @Select("SELECT * FROM sub_region_param WHERE sub_region_id = #{subRegionId}")
    SubRegionParam findBySubRegionId(@Param("subRegionId") String subRegionId);

    /**
     * 根据区域ID查询所有子区
     */
    @Select("SELECT sp.* FROM sub_region_param sp " +
            "JOIN region_sub_region rsr ON sp.sub_region_id = rsr.sub_region_id " +
            "WHERE rsr.region_id = #{regionId} ORDER BY sp.sub_region_id")
    List<SubRegionParam> findByRegionId(@Param("regionId") String regionId);

    /**
     * 查询所有子区（带完整信息视图）
     */
    @Select("SELECT * FROM v_sub_region_param_complete ORDER BY sub_region_id")
    @Results(id = "subRegionCompleteMap", value = {
            @Result(property = "subRegionId", column = "sub_region_id"),
            @Result(property = "subRegionName", column = "sub_region_name"),
            @Result(property = "regionId", column = "region_id"),
            @Result(property = "crossIdList", column = "cross_id_list"),
            @Result(property = "routeIdList", column = "route_id_list"),
            @Result(property = "crossCount", column = "cross_count"),
            @Result(property = "routeCount", column = "route_count"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<SubRegionParam> findAllWithCompleteInfo();

    /**
     * 根据子区名称模糊查询
     */
    @Select("SELECT * FROM sub_region_param WHERE sub_region_name LIKE CONCAT('%', #{subRegionName}, '%') ORDER BY sub_region_id")
    List<SubRegionParam> findBySubRegionNameLike(@Param("subRegionName") String subRegionName);
}
