package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.SignalGroupParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 信号组参数Repository
 */
@Repository
public interface SignalGroupParamRepository extends BaseMapper<SignalGroupParam> {

    /**
     * 根据路口ID查询所有信号组
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} ORDER BY signal_group_no")
    List<SignalGroupParam> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和信号组编号查询
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} AND signal_group_no = #{signalGroupNo}")
    SignalGroupParam findByCrossIdAndSignalGroupNo(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo);

    /**
     * 根据信号组类型查询
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} AND type = #{type} ORDER BY signal_group_no")
    List<SignalGroupParam> findByCrossIdAndType(@Param("crossId") String crossId, @Param("type") Integer type);

    /**
     * 查询路口的机动车信号组
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} AND type IN (1, 21, 22, 23) ORDER BY signal_group_no")
    List<SignalGroupParam> findVehicleSignalGroups(@Param("crossId") String crossId);

    /**
     * 查询路口的行人信号组
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} AND type = 0 ORDER BY signal_group_no")
    List<SignalGroupParam> findPedestrianSignalGroups(@Param("crossId") String crossId);

    /**
     * 批量查询信号组
     */
    @Select("<script>" +
            "SELECT * FROM signal_group_param WHERE cross_id = #{crossId} " +
            "AND signal_group_no IN " +
            "<foreach collection='signalGroupNos' item='no' open='(' separator=',' close=')'>" +
            "#{no}" +
            "</foreach>" +
            "ORDER BY signal_group_no" +
            "</script>")
    List<SignalGroupParam> findByCrossIdAndSignalGroupNos(@Param("crossId") String crossId, @Param("signalGroupNos") List<Integer> signalGroupNos);
}
