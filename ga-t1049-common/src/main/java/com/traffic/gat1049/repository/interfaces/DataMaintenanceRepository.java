package com.traffic.gat1049.repository.interfaces;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 数据维护Repository
 * 用于数据清理、分区管理等维护操作
 */
@Repository
public interface DataMaintenanceRepository {

    /**
     * 执行历史数据清理存储过程
     */
    @Select("CALL CleanHistoryData()")
    String cleanHistoryData();

    /**
     * 添加月度分区
     */
    @Select("CALL AddMonthlyPartition(#{year}, #{month})")
    String addMonthlyPartition(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 清理指定日期之前的交通数据
     */
    @Delete("DELETE FROM cross_traffic_data WHERE end_time < #{cutoffDate}")
    int cleanTrafficDataBefore(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 清理指定日期之前的信号状态数据
     */
    @Delete("DELETE FROM cross_signal_group_status WHERE lamp_status_time < #{cutoffDate}")
    int cleanSignalStatusBefore(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 清理指定日期之前的命令日志
     */
    @Delete("DELETE FROM control_command_log WHERE created_time < #{cutoffDate}")
    int cleanCommandLogBefore(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 清理指定日期之前的系统状态
     */
    @Delete("DELETE FROM sys_state WHERE time < #{cutoffDate}")
    int cleanSysStateBefore(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 清理指定日期之前的路口状态（保留最新状态）
     */
    @Delete("DELETE FROM cross_state WHERE created_time < #{cutoffDate} " +
            "AND id NOT IN (SELECT MAX(id) FROM cross_state GROUP BY cross_id)")
    int cleanCrossStateBefore(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 获取数据库统计信息
     */
    @Select("SELECT " +
            "  table_name, " +
            "  table_rows, " +
            "  ROUND((data_length + index_length) / 1024 / 1024, 2) AS size_mb " +
            "FROM information_schema.tables " +
            "WHERE table_schema = DATABASE() " +
            "ORDER BY (data_length + index_length) DESC")
    @Results({
            @Result(property = "tableName", column = "table_name"),
            @Result(property = "tableRows", column = "table_rows"),
            @Result(property = "sizeMb", column = "size_mb")
    })
    List<Object> getDatabaseStatistics();

    /**
     * 检查分区状态
     */
    @Select("SELECT " +
            "  partition_name, " +
            "  partition_description, " +
            "  table_rows " +
            "FROM information_schema.partitions " +
            "WHERE table_schema = DATABASE() " +
            "AND table_name = 'cross_traffic_data' " +
            "AND partition_name IS NOT NULL " +
            "ORDER BY partition_ordinal_position")
    @Results({
            @Result(property = "partitionName", column = "partition_name"),
            @Result(property = "partitionDescription", column = "partition_description"),
            @Result(property = "tableRows", column = "table_rows")
    })
    List<Object> getPartitionStatus();

    /**
     * 重建索引
     */
    @Update("OPTIMIZE TABLE ${tableName}")
    void optimizeTable(@Param("tableName") String tableName);

    /**
     * 分析表统计信息
     */
    @Update("ANALYZE TABLE ${tableName}")
    void analyzeTable(@Param("tableName") String tableName);
}
