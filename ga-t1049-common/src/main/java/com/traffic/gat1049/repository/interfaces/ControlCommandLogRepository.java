package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.ControlCommandLogEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 控制命令日志Repository
 */
@Repository
public interface ControlCommandLogRepository extends BaseMapper<ControlCommandLogEntity> {

    /**
     * 查询路口命令历史
     */
    @Select("SELECT * FROM control_command_log WHERE cross_id = #{crossId} " +
            "AND execute_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY execute_time DESC")
    List<ControlCommandLogEntity> findByCrossIdAndTimeRange(@Param("crossId") String crossId,
                                                            @Param("startTime") LocalDateTime startTime,
                                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 查询指定命令类型的日志
     */
    @Select("SELECT * FROM control_command_log WHERE command_type = #{commandType} " +
            "AND execute_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY execute_time DESC")
    List<ControlCommandLogEntity> findByCommandTypeAndTimeRange(@Param("commandType") String commandType,
                                                                @Param("startTime") LocalDateTime startTime,
                                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 查询执行失败的命令
     */
    @Select("SELECT * FROM control_command_log WHERE result = 'FAILED' " +
            "AND execute_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY execute_time DESC")
    List<ControlCommandLogEntity> findFailedCommands(@Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 查询待执行命令
     */
    @Select("SELECT * FROM control_command_log WHERE result = 'PENDING' ORDER BY execute_time")
    List<ControlCommandLogEntity> findPendingCommands();

    /**
     * 更新命令执行结果
     */
    @Update("UPDATE control_command_log SET result = #{result}, error_message = #{errorMessage} WHERE id = #{id}")
    int updateCommandResult(@Param("id") Integer id, @Param("result") String result, @Param("errorMessage") String errorMessage);

    /**
     * 统计命令执行情况
     */
    @Select("SELECT command_type, result, COUNT(*) as count " +
            "FROM control_command_log WHERE execute_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY command_type, result")
    List<Object> getCommandStatistics(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
}
