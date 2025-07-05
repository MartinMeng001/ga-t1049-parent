package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.PedestrianParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 行人参数Repository
 */
@Repository
public interface PedestrianParamRepository extends BaseMapper<PedestrianParam> {

    /**
     * 根据路口ID查询所有人行横道
     */
    @Select("SELECT * FROM pedestrian_param WHERE cross_id = #{crossId} ORDER BY pedestrian_no")
    List<PedestrianParam> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和人行横道号查询
     */
    @Select("SELECT * FROM pedestrian_param WHERE cross_id = #{crossId} AND pedestrian_no = #{pedestrianNo}")
    PedestrianParam findByCrossIdAndPedestrianNo(@Param("crossId") String crossId, @Param("pedestrianNo") Integer pedestrianNo);

    /**
     * 根据方向查询人行横道
     */
    @Select("SELECT * FROM pedestrian_param WHERE cross_id = #{crossId} AND direction = #{direction} ORDER BY pedestrian_no")
    List<PedestrianParam> findByDirection(@Param("crossId") String crossId, @Param("direction") String direction);

    /**
     * 查询有按钮的人行横道
     */
    @Select("SELECT * FROM pedestrian_param WHERE cross_id = #{crossId} AND has_button = 1 ORDER BY pedestrian_no")
    List<PedestrianParam> findWithButton(@Param("crossId") String crossId);

    /**
     * 查询有语音提示的人行横道
     */
    @Select("SELECT * FROM pedestrian_param WHERE cross_id = #{crossId} AND has_voice = 1 ORDER BY pedestrian_no")
    List<PedestrianParam> findWithVoice(@Param("crossId") String crossId);

    /**
     * 根据信号组查询关联人行横道
     */
    @Select("SELECT pp.* FROM pedestrian_param pp " +
            "JOIN signal_group_pedestrian sgp ON pp.cross_id = sgp.cross_id AND pp.pedestrian_no = sgp.pedestrian_no " +
            "WHERE pp.cross_id = #{crossId} AND sgp.signal_group_no = #{signalGroupNo} ORDER BY pp.pedestrian_no")
    List<PedestrianParam> findBySignalGroup(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo);
}
