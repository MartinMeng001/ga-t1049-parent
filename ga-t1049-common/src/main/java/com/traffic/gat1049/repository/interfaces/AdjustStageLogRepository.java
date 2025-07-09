package com.traffic.gat1049.repository.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.AdjustStageLogEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 阶段干预记录Repository
 * 提供阶段干预记录相关的数据访问功能
 */
@Repository
public interface AdjustStageLogRepository extends BaseMapper<AdjustStageLogEntity> {

    /**
     * 根据路口ID查询干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findByCrossId(@Param("crossId") String crossId);

    /**
     * 根据路口ID和阶段号查询干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} AND stage_no = #{stageNo} " +
            "ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findByCrossIdAndStageNo(@Param("crossId") String crossId,
                                                       @Param("stageNo") Integer stageNo);

    /**
     * 根据干预类型查询记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} AND type = #{type} " +
            "ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findByCrossIdAndType(@Param("crossId") String crossId,
                                                    @Param("type") Integer type);

    /**
     * 根据执行结果查询记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} AND result = #{result} " +
            "ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findByCrossIdAndResult(@Param("crossId") String crossId,
                                                      @Param("result") String result);

    /**
     * 根据操作员ID查询记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE operator_id = #{operatorId} ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findByOperatorId(@Param("operatorId") String operatorId);

    /**
     * 查询指定时间范围内的记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "AND execute_time >= #{startTime} AND execute_time <= #{endTime} " +
            "ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findByCrossIdAndTimeRange(@Param("crossId") String crossId,
                                                         @Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最近的干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "ORDER BY execute_time DESC LIMIT #{limit}")
    List<AdjustStageLogEntity> findRecentByCrossId(@Param("crossId") String crossId,
                                                   @Param("limit") Integer limit);

    /**
     * 查询最近的成功干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} AND result = 'SUCCESS' " +
            "ORDER BY execute_time DESC LIMIT 1")
    AdjustStageLogEntity findLastSuccessByCrossId(@Param("crossId") String crossId);

    /**
     * 查询最近的失败干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} AND result = 'FAILED' " +
            "ORDER BY execute_time DESC LIMIT 1")
    AdjustStageLogEntity findLastFailedByCrossId(@Param("crossId") String crossId);

    /**
     * 查询指定阶段的最近干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} AND stage_no = #{stageNo} " +
            "ORDER BY execute_time DESC LIMIT 1")
    AdjustStageLogEntity findLastByCrossIdAndStageNo(@Param("crossId") String crossId,
                                                     @Param("stageNo") Integer stageNo);

    /**
     * 查询今天的干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "AND DATE(execute_time) = CURDATE() ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findTodayByCrossId(@Param("crossId") String crossId);

    /**
     * 查询本周的干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "AND execute_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findThisWeekByCrossId(@Param("crossId") String crossId);

    /**
     * 查询本月的干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "AND YEAR(execute_time) = YEAR(NOW()) AND MONTH(execute_time) = MONTH(NOW()) " +
            "ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findThisMonthByCrossId(@Param("crossId") String crossId);

    /**
     * 统计干预记录数量
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN result = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
            "SUM(CASE WHEN result = 'FAILED' THEN 1 ELSE 0 END) as failed_count, " +
            "SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) as extend_count, " +
            "SUM(CASE WHEN type = 2 THEN 1 ELSE 0 END) as shorten_count, " +
            "SUM(CASE WHEN type = 3 THEN 1 ELSE 0 END) as skip_count " +
            "FROM adjust_stage_log WHERE cross_id = #{crossId}")
    @MapKey("cross_id")
    java.util.Map<String, Object> getStatisticsByCrossId(@Param("crossId") String crossId);

    /**
     * 统计指定时间范围内的干预记录
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN result = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
            "SUM(CASE WHEN result = 'FAILED' THEN 1 ELSE 0 END) as failed_count, " +
            "SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) as extend_count, " +
            "SUM(CASE WHEN type = 2 THEN 1 ELSE 0 END) as shorten_count, " +
            "SUM(CASE WHEN type = 3 THEN 1 ELSE 0 END) as skip_count " +
            "FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "AND execute_time >= #{startTime} AND execute_time <= #{endTime}")
    @MapKey("cross_id")
    java.util.Map<String, Object> getStatisticsByTimeRange(@Param("crossId") String crossId,
                                                           @Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 按阶段号统计干预记录
     */
    @Select("SELECT " +
            "stage_no, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN result = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
            "SUM(CASE WHEN result = 'FAILED' THEN 1 ELSE 0 END) as failed_count " +
            "FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "GROUP BY stage_no ORDER BY stage_no")
    List<java.util.Map<String, Object>> getStatisticsByStage(@Param("crossId") String crossId);

    /**
     * 按操作员统计干预记录
     */
    @Select("SELECT " +
            "operator_id, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN result = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
            "SUM(CASE WHEN result = 'FAILED' THEN 1 ELSE 0 END) as failed_count " +
            "FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "GROUP BY operator_id ORDER BY total_count DESC")
    List<java.util.Map<String, Object>> getStatisticsByOperator(@Param("crossId") String crossId);

    /**
     * 按日期统计干预记录（最近30天）
     */
    @Select("SELECT " +
            "DATE(execute_time) as execute_date, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN result = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
            "SUM(CASE WHEN result = 'FAILED' THEN 1 ELSE 0 END) as failed_count " +
            "FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "AND execute_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY DATE(execute_time) ORDER BY execute_date DESC")
    List<java.util.Map<String, Object>> getDailyStatistics(@Param("crossId") String crossId);

    /**
     * 按小时统计干预记录（今天）
     */
    @Select("SELECT " +
            "HOUR(execute_time) as execute_hour, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN result = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
            "SUM(CASE WHEN result = 'FAILED' THEN 1 ELSE 0 END) as failed_count " +
            "FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "AND DATE(execute_time) = CURDATE() " +
            "GROUP BY HOUR(execute_time) ORDER BY execute_hour")
    List<java.util.Map<String, Object>> getHourlyStatistics(@Param("crossId") String crossId);

    /**
     * 查询干预频率最高的阶段
     */
    @Select("SELECT stage_no, COUNT(*) as count FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "GROUP BY stage_no ORDER BY count DESC LIMIT 1")
    @MapKey("stage_no")
    java.util.Map<String, Object> getMostAdjustedStage(@Param("crossId") String crossId);

    /**
     * 查询成功率最低的阶段
     */
    @Select("SELECT " +
            "stage_no, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN result = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
            "ROUND(SUM(CASE WHEN result = 'SUCCESS' THEN 1 ELSE 0 END) / COUNT(*) * 100, 2) as success_rate " +
            "FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "GROUP BY stage_no HAVING total_count >= 5 ORDER BY success_rate ASC LIMIT 1")
    @MapKey("stage_no")
    java.util.Map<String, Object> getLowestSuccessRateStage(@Param("crossId") String crossId);

    /**
     * 删除指定时间之前的历史记录
     */
    @Delete("DELETE FROM adjust_stage_log WHERE execute_time < #{beforeTime}")
    int deleteHistoryRecords(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 分页查询干预记录
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "ORDER BY execute_time DESC LIMIT #{offset}, #{limit}")
    List<AdjustStageLogEntity> findByCrossIdWithPagination(@Param("crossId") String crossId,
                                                           @Param("offset") Integer offset,
                                                           @Param("limit") Integer limit);

    /**
     * 查询干预记录总数
     */
    @Select("SELECT COUNT(*) FROM adjust_stage_log WHERE cross_id = #{crossId}")
    int countByCrossId(@Param("crossId") String crossId);

    /**
     * 查询干预记录详情及路口信息
     */
    @Select("SELECT asl.*, c.cross_name " +
            "FROM adjust_stage_log asl " +
            "LEFT JOIN cross_param c ON asl.cross_id = c.cross_id " +
            "WHERE asl.id = #{id}")
    @Results(id = "adjustStageLogWithCrossMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "crossId", column = "cross_id"),
            @Result(property = "stageNo", column = "stage_no"),
            @Result(property = "type", column = "type"),
            @Result(property = "len", column = "len"),
            @Result(property = "executeTime", column = "execute_time"),
            @Result(property = "result", column = "result"),
            @Result(property = "operatorId", column = "operator_id"),
            @Result(property = "createdTime", column = "created_time")
    })
    AdjustStageLogEntity findWithCrossInfo(@Param("id") Integer id);

    /**
     * 查询正在进行的干预操作（最近5分钟内的延长或缩短操作）
     */
    @Select("SELECT * FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "AND type IN (1, 2) AND result = 'SUCCESS' " +
            "AND execute_time >= DATE_SUB(NOW(), INTERVAL 5 MINUTE) " +
            "ORDER BY execute_time DESC")
    List<AdjustStageLogEntity> findActiveAdjustments(@Param("crossId") String crossId);

    /**
     * 检查是否存在冲突的干预操作
     */
    @Select("SELECT COUNT(*) FROM adjust_stage_log WHERE cross_id = #{crossId} " +
            "AND stage_no = #{stageNo} AND result = 'SUCCESS' " +
            "AND execute_time >= DATE_SUB(NOW(), INTERVAL 1 MINUTE)")
    int countRecentAdjustments(@Param("crossId") String crossId, @Param("stageNo") Integer stageNo);
}
