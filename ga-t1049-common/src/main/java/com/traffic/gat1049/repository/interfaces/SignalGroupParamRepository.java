package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.dto.SignalGroupWithLampGroupsDto;
import com.traffic.gat1049.repository.entity.SignalGroupParamEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 信号组参数Repository
 */
@Repository
public interface SignalGroupParamRepository extends BaseMapper<SignalGroupParamEntity> {

    /**
     * 查询路口所有信号组（包含关联灯组）
     * 返回DTO对象，一次查询获取所有需要的数据
     */
    @Select("SELECT " +
            "sgp.cross_id, sgp.signal_group_no, sgp.name,  " +
            "sgp.green_flash_len, sgp.max_green, sgp.min_green, " +
            "sgp.updated_time as last_updated, " +
            "GROUP_CONCAT(sglg.lamp_group_no ORDER BY sglg.lamp_group_no SEPARATOR ',') as lamp_group_nos, " +
            "COUNT(sglg.lamp_group_no) as lamp_group_count " +
            "FROM signal_group_param sgp " +
            "LEFT JOIN signal_group_lamp_group sglg ON sgp.cross_id = sglg.cross_id AND sgp.signal_group_no = sglg.signal_group_no " +
            "WHERE sgp.cross_id = #{crossId} " +
            "GROUP BY sgp.cross_id, sgp.signal_group_no, sgp.name, sgp.green_flash_len, sgp.max_green, sgp.min_green, sgp.updated_time " +
            "ORDER BY sgp.signal_group_no")
    @Results({
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "signalGroupNo", column = "signal_group_no"),
            @Result(property = "name", column = "name"),
            @Result(property = "greenFlashLen", column = "green_flash_len"),
            @Result(property = "maxGreen", column = "max_green"),
            @Result(property = "minGreen", column = "min_green"),
            @Result(property = "lampGroupNos", column = "lamp_group_nos"),
            @Result(property = "lampGroupCount", column = "lamp_group_count"),
            @Result(property = "lastUpdated", column = "last_updated")
    })
    List<SignalGroupWithLampGroupsDto> findSignalGroupsWithLampGroups(@Param("crossId") String crossId);

    /**
     * 查询单个信号组（包含关联灯组）
     */
    @Select("SELECT " +
            "sgp.cross_id, sgp.signal_group_no, sgp.name, " +
            "sgp.green_flash_len, sgp.max_green, sgp.min_green, " +
            "sgp.updated_time as last_updated, " +
            "GROUP_CONCAT(sglg.lamp_group_no ORDER BY sglg.lamp_group_no SEPARATOR ',') as lamp_group_nos, " +
            "COUNT(sglg.lamp_group_no) as lamp_group_count " +
            "FROM signal_group_param sgp " +
            "LEFT JOIN signal_group_lamp_group sglg ON sgp.cross_id = sglg.cross_id AND sgp.signal_group_no = sglg.signal_group_no " +
            "WHERE sgp.cross_id = #{crossId} AND sgp.signal_group_no = #{signalGroupNo} " +
            "GROUP BY sgp.cross_id, sgp.signal_group_no, sgp.name, sgp.green_flash_len, sgp.max_green, sgp.min_green, sgp.updated_time")
    @Results({
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "signalGroupNo", column = "signal_group_no"),
            @Result(property = "name", column = "name"),
            @Result(property = "greenFlashLen", column = "green_flash_len"),
            @Result(property = "maxGreen", column = "max_green"),
            @Result(property = "minGreen", column = "min_green"),
            @Result(property = "lampGroupNos", column = "lamp_group_nos"),
            @Result(property = "lampGroupCount", column = "lamp_group_count"),
            @Result(property = "lastUpdated", column = "last_updated")
    })
    SignalGroupWithLampGroupsDto findSignalGroupWithLampGroups(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo);

    /**
     * 根据路口ID查询所有信号组
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} ORDER BY signal_group_no")
    List<SignalGroupParamEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID查询所有主信号组
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} AND signal_group_no >= 80 AND signal_group_no < 90  ORDER BY signal_group_no")
    List<SignalGroupParamEntity> findMainByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和信号组编号查询
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} AND signal_group_no = #{signalGroupNo}")
    SignalGroupParamEntity findByCrossIdAndSignalGroupNo(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo);

    /**
     * 根据信号组类型查询
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} AND type = #{type} ORDER BY signal_group_no")
    List<SignalGroupParamEntity> findByCrossIdAndType(@Param("crossId") String crossId, @Param("type") Integer type);

    /**
     * 查询路口的机动车信号组
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} AND type IN (1, 21, 22, 23) ORDER BY signal_group_no")
    List<SignalGroupParamEntity> findVehicleSignalGroups(@Param("crossId") String crossId);

    /**
     * 查询路口的行人信号组
     */
    @Select("SELECT * FROM signal_group_param WHERE cross_id = #{crossId} AND type = 0 ORDER BY signal_group_no")
    List<SignalGroupParamEntity> findPedestrianSignalGroups(@Param("crossId") String crossId);

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
    List<SignalGroupParamEntity> findByCrossIdAndSignalGroupNos(@Param("crossId") String crossId, @Param("signalGroupNos") List<Integer> signalGroupNos);
}
