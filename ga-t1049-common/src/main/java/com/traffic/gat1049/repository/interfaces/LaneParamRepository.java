package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.LaneParamEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 车道参数Repository
 */
@Repository
public interface LaneParamRepository extends BaseMapper<LaneParamEntity> {

    /**
     * 根据路口ID查询所有车道
     */
    @Select("SELECT * FROM lane_param WHERE cross_id = #{crossId} ORDER BY lane_no")
    List<LaneParamEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和车道号查询
     */
    @Select("SELECT * FROM lane_param WHERE cross_id = #{crossId} AND lane_no = #{laneNo}")
    LaneParamEntity findByCrossIdAndLaneNo(@Param("crossId") String crossId, @Param("laneNo") Integer laneNo);

    /**
     * 根据进口方向查询车道
     */
    @Select("SELECT * FROM lane_param WHERE cross_id = #{crossId} AND entrance = #{entrance} ORDER BY lane_no")
    List<LaneParamEntity> findByEntrance(@Param("crossId") String crossId, @Param("entrance") String entrance);

    /**
     * 根据车道转向属性查询
     */
    @Select("SELECT * FROM lane_param WHERE cross_id = #{crossId} AND movement = #{movement} ORDER BY lane_no")
    List<LaneParamEntity> findByMovement(@Param("crossId") String crossId, @Param("movement") Integer movement);

    /**
     * 查询机动车道
     */
    @Select("SELECT * FROM lane_param WHERE cross_id = #{crossId} AND type = 1 ORDER BY lane_no")
    List<LaneParamEntity> findVehicleLanes(@Param("crossId") String crossId);

    /**
     * 查询非机动车道
     */
    @Select("SELECT * FROM lane_param WHERE cross_id = #{crossId} AND type = 2 ORDER BY lane_no")
    List<LaneParamEntity> findNonVehicleLanes(@Param("crossId") String crossId);

    /**
     * 根据信号组查询关联车道
     */
    @Select("SELECT lp.* FROM lane_param lp " +
            "JOIN signal_group_lane sgl ON lp.cross_id = sgl.cross_id AND lp.lane_no = sgl.lane_no " +
            "WHERE lp.cross_id = #{crossId} AND sgl.signal_group_no = #{signalGroupNo} ORDER BY lp.lane_no")
    List<LaneParamEntity> findBySignalGroup(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo);
}
