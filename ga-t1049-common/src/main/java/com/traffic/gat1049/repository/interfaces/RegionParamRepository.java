package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.RegionParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 区域参数Repository
 */
@Repository
public interface RegionParamRepository extends BaseMapper<RegionParam> {

    /**
     * 根据区域ID查询
     */
    @Select("SELECT * FROM region_param WHERE region_id = #{regionId}")
    RegionParam findByRegionId(@Param("regionId") String regionId);

    /**
     * 根据区域名称模糊查询
     */
    @Select("SELECT * FROM region_param WHERE region_name LIKE CONCAT('%', #{regionName}, '%') ORDER BY region_id")
    List<RegionParam> findByRegionNameLike(@Param("regionName") String regionName);

    /**
     * 查询所有区域（带完整信息视图）
     */
    @Select("SELECT * FROM v_region_param_complete ORDER BY region_id")
    @Results(id = "regionCompleteMap", value = {
            @Result(property = "regionId", column = "region_id"),
            @Result(property = "regionName", column = "region_name"),
            @Result(property = "subRegionIdList", column = "sub_region_id_list"),
            @Result(property = "crossIdList", column = "cross_id_list"),
            @Result(property = "subRegionCount", column = "sub_region_count"),
            @Result(property = "directCrossCount", column = "direct_cross_count"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<RegionParam> findAllWithCompleteInfo();

    /**
     * 根据行政区划代码前缀查询
     */
    @Select("SELECT * FROM region_param WHERE region_id LIKE CONCAT(#{prefix}, '%') ORDER BY region_id")
    List<RegionParam> findByRegionIdPrefix(@Param("prefix") String prefix);
}
