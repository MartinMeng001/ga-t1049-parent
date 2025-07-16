package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.LampGroupParamEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import com.traffic.gat1049.repository.dto.SignalGroupWithLampGroupsDto;

import java.util.List;

/**
 * 信号灯组参数表Repository
 * 基于修正后的实体类和标准数据库表结构
 */
@Repository
public interface LampGroupRepository extends BaseMapper<LampGroupParamEntity> {

    /**
     * 根据路口ID查询所有信号灯组
     */
    @Select("SELECT * FROM lamp_group_param WHERE cross_id = #{crossId} ORDER BY lamp_group_no")
    List<LampGroupParamEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 查询路口所有信号组（包含关联灯组）
     * 返回DTO对象，一次查询获取所有需要的数据
     */
    @Select("SELECT " +
            "sgp.cross_id, sgp.signal_group_no, sgp.name, sgp.type, " +
            "sgp.green_flash_len, sgp.max_green, sgp.min_green, " +
            "sgp.updated_time as last_updated, " +
            "GROUP_CONCAT(sglg.lamp_group_no ORDER BY sglg.lamp_group_no SEPARATOR ',') as lamp_group_nos, " +
            "COUNT(sglg.lamp_group_no) as lamp_group_count " +
            "FROM signal_group_param sgp " +
            "LEFT JOIN signal_group_lamp_group sglg ON sgp.cross_id = sglg.cross_id AND sgp.signal_group_no = sglg.signal_group_no " +
            "WHERE sgp.cross_id = #{crossId} " +
            "GROUP BY sgp.cross_id, sgp.signal_group_no, sgp.name, sgp.type, sgp.green_flash_len, sgp.max_green, sgp.min_green, sgp.updated_time " +
            "ORDER BY sgp.signal_group_no")
    @Results({
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "signalGroupNo", column = "signal_group_no"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
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
            "sgp.cross_id, sgp.signal_group_no, sgp.name, sgp.type, " +
            "sgp.green_flash_len, sgp.max_green, sgp.min_green, " +
            "sgp.updated_time as last_updated, " +
            "GROUP_CONCAT(sglg.lamp_group_no ORDER BY sglg.lamp_group_no SEPARATOR ',') as lamp_group_nos, " +
            "COUNT(sglg.lamp_group_no) as lamp_group_count " +
            "FROM signal_group_param sgp " +
            "LEFT JOIN signal_group_lamp_group sglg ON sgp.cross_id = sglg.cross_id AND sgp.signal_group_no = sglg.signal_group_no " +
            "WHERE sgp.cross_id = #{crossId} AND sgp.signal_group_no = #{signalGroupNo} " +
            "GROUP BY sgp.cross_id, sgp.signal_group_no, sgp.name, sgp.type, sgp.green_flash_len, sgp.max_green, sgp.min_green, sgp.updated_time")
    @Results({
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "signalGroupNo", column = "signal_group_no"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
            @Result(property = "greenFlashLen", column = "green_flash_len"),
            @Result(property = "maxGreen", column = "max_green"),
            @Result(property = "minGreen", column = "min_green"),
            @Result(property = "lampGroupNos", column = "lamp_group_nos"),
            @Result(property = "lampGroupCount", column = "lamp_group_count"),
            @Result(property = "lastUpdated", column = "last_updated")
    })
    SignalGroupWithLampGroupsDto findSignalGroupWithLampGroups(@Param("crossId") String crossId, @Param("signalGroupNo") Integer signalGroupNo);

    /**
     * 根据信号组类型查询（包含关联灯组）
     */
    @Select("SELECT " +
            "sgp.cross_id, sgp.signal_group_no, sgp.name, sgp.type, " +
            "sgp.green_flash_len, sgp.max_green, sgp.min_green, " +
            "sgp.updated_time as last_updated, " +
            "GROUP_CONCAT(sglg.lamp_group_no ORDER BY sglg.lamp_group_no SEPARATOR ',') as lamp_group_nos, " +
            "COUNT(sglg.lamp_group_no) as lamp_group_count " +
            "FROM signal_group_param sgp " +
            "LEFT JOIN signal_group_lamp_group sglg ON sgp.cross_id = sglg.cross_id AND sgp.signal_group_no = sglg.signal_group_no " +
            "WHERE sgp.cross_id = #{crossId} AND sgp.type = #{type} " +
            "GROUP BY sgp.cross_id, sgp.signal_group_no, sgp.name, sgp.type, sgp.green_flash_len, sgp.max_green, sgp.min_green, sgp.updated_time " +
            "ORDER BY sgp.signal_group_no")
    @Results({
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "signalGroupNo", column = "signal_group_no"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
            @Result(property = "greenFlashLen", column = "green_flash_len"),
            @Result(property = "maxGreen", column = "max_green"),
            @Result(property = "minGreen", column = "min_green"),
            @Result(property = "lampGroupNos", column = "lamp_group_nos"),
            @Result(property = "lampGroupCount", column = "lamp_group_count"),
            @Result(property = "lastUpdated", column = "last_updated")
    })
    List<SignalGroupWithLampGroupsDto> findSignalGroupsWithLampGroupsByType(@Param("crossId") String crossId, @Param("type") Integer type);

    /**
     * 根据路口ID查询所有有效信号灯组
     */
    @Select("SELECT * FROM lamp_group_param WHERE cross_id = #{crossId} AND valid = 1 ORDER BY lamp_group_no")
    List<LampGroupParamEntity> findByValidCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和灯组编号查询信号灯组
     */
    @Select("SELECT * FROM lamp_group_param WHERE cross_id = #{crossId} AND lamp_group_no = #{lampGroupNo}")
    LampGroupParamEntity findByCrossIdAndLampGroupNo(@Param("crossId") String crossId, @Param("lampGroupNo") Integer lampGroupNo);

    /**
     * 根据路口ID和方向查询信号灯组
     */
    @Select("SELECT * FROM lamp_group_param WHERE cross_id = #{crossId} AND direction = #{direction} ORDER BY lamp_group_no")
    List<LampGroupParamEntity> findByCrossIdAndDirection(@Param("crossId") String crossId, @Param("direction") String direction);

    /**
     * 根据路口ID和灯组类型查询信号灯组
     */
    @Select("SELECT * FROM lamp_group_param WHERE cross_id = #{crossId} AND type = #{type} ORDER BY lamp_group_no")
    List<LampGroupParamEntity> findByCrossIdAndType(@Param("crossId") String crossId, @Param("type") String type);

    /**
     * 根据方向查询所有信号灯组
     */
    @Select("SELECT * FROM lamp_group_param WHERE direction = #{direction} ORDER BY cross_id, lamp_group_no")
    List<LampGroupParamEntity> findByDirection(@Param("direction") String direction);

    /**
     * 根据类型查询所有信号灯组
     */
    @Select("SELECT * FROM lamp_group_param WHERE type = #{type} ORDER BY cross_id, lamp_group_no")
    List<LampGroupParamEntity> findByType(@Param("type") String type);

    /**
     * 查询指定路口的最大灯组编号
     */
    @Select("SELECT COALESCE(MAX(lamp_group_no), 0) FROM lamp_group_param WHERE cross_id = #{crossId}")
    Integer getMaxLampGroupNo(@Param("crossId") String crossId);

    /**
     * 统计指定路口的灯组数量
     */
    @Select("SELECT COUNT(*) FROM lamp_group_param WHERE cross_id = #{crossId}")
    Integer countByCrossId(@Param("crossId") String crossId);

    /**
     * 检查指定路口和灯组编号是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM lamp_group_param WHERE cross_id = #{crossId} AND lamp_group_no = #{lampGroupNo}")
    Boolean existsByCrossIdAndLampGroupNo(@Param("crossId") String crossId, @Param("lampGroupNo") Integer lampGroupNo);

    /**
     * 删除指定路口的所有信号灯组
     */
    @Delete("DELETE FROM lamp_group_param WHERE cross_id = #{crossId}")
    int deleteByCrossId(@Param("crossId") String crossId);

    /**
     * 批量插入信号灯组
     */
    @Insert("<script>" +
            "INSERT INTO lamp_group_param (cross_id, lamp_group_no, direction, type) VALUES " +
            "<foreach collection='entities' item='entity' separator=','>" +
            "(#{entity.crossId}, #{entity.lampGroupNo}, #{entity.direction}, #{entity.type})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("entities") List<LampGroupParamEntity> entities);

    /**
     * 更新信号灯组信息（不包括主键字段）
     */
    @Update("UPDATE lamp_group_param SET " +
            "direction = #{direction}, " +
            "type = #{type}, " +
            "updated_time = CURRENT_TIMESTAMP " +
            "WHERE cross_id = #{crossId} AND lamp_group_no = #{lampGroupNo}")
    int updateLampGroup(@Param("crossId") String crossId,
                        @Param("lampGroupNo") Integer lampGroupNo,
                        @Param("direction") String direction,
                        @Param("type") String type);

    /**
     * 查询指定路口和方向的信号灯组编号列表
     */
    @Select("SELECT lamp_group_no FROM lamp_group_param WHERE cross_id = #{crossId} AND direction = #{direction} ORDER BY lamp_group_no")
    List<Integer> getLampGroupNosByCrossIdAndDirection(@Param("crossId") String crossId, @Param("direction") String direction);

    /**
     * 根据灯组类型统计数量
     */
    @Select("SELECT type, COUNT(*) as count FROM lamp_group_param WHERE cross_id = #{crossId} GROUP BY type")
    @Results({
            @Result(column = "type", property = "type"),
            @Result(column = "count", property = "count")
    })
    List<LampGroupTypeCount> countByLampGroupType(@Param("crossId") String crossId);

    /**
     * 查询路口的信号灯组完整信息（包含关联的信号组信息）
     */
    @Select("SELECT " +
            "lgp.*, " +
            "GROUP_CONCAT(sglg.signal_group_no ORDER BY sglg.signal_group_no SEPARATOR ',') as signal_group_nos " +
            "FROM lamp_group_param lgp " +
            "LEFT JOIN signal_group_lamp_group sglg ON lgp.cross_id = sglg.cross_id AND lgp.lamp_group_no = sglg.lamp_group_no " +
            "WHERE lgp.cross_id = #{crossId} " +
            "GROUP BY lgp.id " +
            "ORDER BY lgp.lamp_group_no")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "cross_id", property = "crossId"),
            @Result(column = "lamp_group_no", property = "lampGroupNo"),
            @Result(column = "direction", property = "direction"),
            @Result(column = "type", property = "type"),
            @Result(column = "created_time", property = "createdTime"),
            @Result(column = "updated_time", property = "updatedTime"),
            @Result(column = "signal_group_nos", property = "signalGroupNos")
    })
    List<LampGroupWithSignalGroups> findCompleteInfoByCrossId(@Param("crossId") String crossId);

    /**
     * 根据方向和类型组合查询
     */
    @Select("SELECT * FROM lamp_group_param WHERE cross_id = #{crossId} AND direction = #{direction} AND type = #{type} ORDER BY lamp_group_no")
    List<LampGroupParamEntity> findByCrossIdAndDirectionAndType(@Param("crossId") String crossId,
                                                                @Param("direction") String direction,
                                                                @Param("type") String type);

    /**
     * 查询所有不同的方向值
     */
    @Select("SELECT DISTINCT direction FROM lamp_group_param ORDER BY direction")
    List<String> findAllDirections();

    /**
     * 查询所有不同的类型值
     */
    @Select("SELECT DISTINCT type FROM lamp_group_param ORDER BY type")
    List<String> findAllTypes();

    /**
     * 灯组类型统计结果类
     */
    class LampGroupTypeCount {
        private String type;
        private Integer count;

        // getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }

    /**
     * 包含信号组信息的灯组结果类
     */
    class LampGroupWithSignalGroups extends LampGroupParamEntity {
        private String signalGroupNos;

        public String getSignalGroupNos() { return signalGroupNos; }
        public void setSignalGroupNos(String signalGroupNos) { this.signalGroupNos = signalGroupNos; }
    }
}
