package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.SignalControllerParamEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 信号机Repository
 */
@Repository
public interface SignalControllerRepository extends BaseMapper<SignalControllerParamEntity> {

    /**
     * 根据信号机ID查询
     */
    @Select("SELECT * FROM signal_controller WHERE signal_controller_id = #{signalControllerId}")
    SignalControllerParamEntity findBySignalControllerId(@Param("signalControllerId") String signalControllerId);

    /**
     * 查询所有信号机（带完整信息视图）
     */
    @Select("SELECT * FROM v_signal_controller_complete ORDER BY signal_controller_id")
    @Results(id = "signalControllerCompleteMap", value = {
            @Result(property = "signalControllerId", column = "signal_controller_id"),
            @Result(property = "supplier", column = "supplier"),
            @Result(property = "type", column = "type"),
            @Result(property = "id", column = "id"),
            @Result(property = "ip", column = "ip"),
            @Result(property = "port", column = "port"),
            @Result(property = "crossIdList", column = "cross_id_list"),
            @Result(property = "systemIdList", column = "system_id_list"),
            @Result(property = "crossCount", column = "cross_count"),
            @Result(property = "systemCount", column = "system_count"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<SignalControllerParamEntity> findAllWithCompleteInfo();

    /**
     * 根据供应商查询
     */
    @Select("SELECT * FROM signal_controller WHERE supplier = #{supplier} ORDER BY signal_controller_id")
    List<SignalControllerParamEntity> findBySupplier(@Param("supplier") String supplier);

    /**
     * 根据类型查询
     */
    @Select("SELECT * FROM signal_controller WHERE type = #{type} ORDER BY signal_controller_id")
    List<SignalControllerParamEntity> findByType(@Param("type") String type);

    /**
     * 根据路口ID查询信号机
     */
    @Select("SELECT sc.* FROM signal_controller sc " +
            "WHERE JSON_CONTAINS(sc.cross_id_list, JSON_QUOTE(#{crossId}))")
    List<SignalControllerParamEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据系统ID查询信号机
     */
    @Select("SELECT sc.* FROM signal_controller sc " +
            "JOIN sys_signal_controller_relation sscr ON sc.signal_controller_id = sscr.signal_controller_id " +
            "WHERE sscr.system_id = #{systemId} AND sscr.is_active = 1 ORDER BY sc.signal_controller_id")
    List<SignalControllerParamEntity> findBySystemId(@Param("systemId") String systemId);

    /**
     * 根据IP地址查询
     */
    @Select("SELECT * FROM signal_controller WHERE ip = #{ip}")
    List<SignalControllerParamEntity> findByIp(@Param("ip") String ip);
}
