package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.DetectorParamEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 检测器参数Repository
 */
@Repository
public interface DetectorParamRepository extends BaseMapper<DetectorParamEntity> {

    /**
     * 根据路口ID查询所有检测器
     */
    @Select("SELECT * FROM detector_param WHERE cross_id = #{crossId} ORDER BY detector_no")
    List<DetectorParamEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和检测器号查询
     */
    @Select("SELECT * FROM detector_param WHERE cross_id = #{crossId} AND detector_no = #{detectorNo}")
    DetectorParamEntity findByCrossIdAndDetectorNo(@Param("crossId") String crossId, @Param("detectorNo") Integer detectorNo);

    /**
     * 根据检测器类型查询
     */
    @Select("SELECT * FROM detector_param WHERE cross_id = #{crossId} AND type = #{type} ORDER BY detector_no")
    List<DetectorParamEntity> findByType(@Param("crossId") String crossId, @Param("type") Integer type);

    /**
     * 根据检测功能查询
     */
    @Select("SELECT * FROM detector_param WHERE cross_id = #{crossId} AND function = #{function} ORDER BY detector_no")
    List<DetectorParamEntity> findByFunction(@Param("crossId") String crossId, @Param("function") Integer function);

    /**
     * 查询车道关联的检测器
     */
    @Select("SELECT dp.* FROM detector_param dp " +
            "JOIN detector_lane dl ON dp.cross_id = dl.cross_id AND dp.detector_no = dl.detector_no " +
            "WHERE dp.cross_id = #{crossId} AND dl.lane_no = #{laneNo} ORDER BY dp.detector_no")
    List<DetectorParamEntity> findByLane(@Param("crossId") String crossId, @Param("laneNo") Integer laneNo);

    /**
     * 查询人行横道关联的检测器
     */
    @Select("SELECT dp.* FROM detector_param dp " +
            "JOIN detector_pedestrian dpd ON dp.cross_id = dpd.cross_id AND dp.detector_no = dpd.detector_no " +
            "WHERE dp.cross_id = #{crossId} AND dpd.pedestrian_no = #{pedestrianNo} ORDER BY dp.detector_no")
    List<DetectorParamEntity> findByPedestrian(@Param("crossId") String crossId, @Param("pedestrianNo") Integer pedestrianNo);

    /**
     * 查询感应检测器
     */
    @Select("SELECT * FROM detector_param WHERE cross_id = #{crossId} AND function IN (1, 2, 3) ORDER BY detector_no")
    List<DetectorParamEntity> findInductiveDetectors(@Param("crossId") String crossId);
}
