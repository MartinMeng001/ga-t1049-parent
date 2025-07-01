package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.dto.TrafficDataQueryDto;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficData;
import com.traffic.gat1049.protocol.model.runtime.*;
import com.traffic.gat1049.model.vo.TrafficStatisticsVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交通数据服务接口
 */
public interface TrafficDataService {

    /**
     * 获取所有交通流数据
     *
     * @return 路口交通流数据
     * @throws BusinessException 业务异常
     */
    List<CrossTrafficData> findAll() throws BusinessException;

    /**
     * 根据Crossid获取交通流数据
     *
     * @param id 路口编号
     * @return 路口交通流数据
     * @throws BusinessException 业务异常
     */
    List<CrossTrafficData> findById(String id) throws BusinessException;

    /**
     * 获取路口交通流数据
     *
     * @param crossId 路口编号
     * @param endTime 统计截止时间
     * @param interval 时间间隔
     * @return 路口交通流数据
     * @throws BusinessException 业务异常
     */
    CrossTrafficData getCrossTrafficData(String crossId, LocalDateTime endTime, Integer interval) throws BusinessException;

    /**
     * 根据查询条件获取交通流数据
     *
     * @param queryDto 查询条件
     * @return 交通统计数据列表
     * @throws BusinessException 业务异常
     */
    List<TrafficStatisticsVo> getTrafficStatistics(TrafficDataQueryDto queryDto) throws BusinessException;

    /**
     * 获取阶段交通流数据
     *
     * @param crossId 路口编号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 阶段交通流数据
     * @throws BusinessException 业务异常
     */
    StageTrafficData getStageTrafficData(String crossId, LocalDateTime startTime, LocalDateTime endTime) throws BusinessException;

    /**
     * 获取阶段交通流数据
     *
     * @param crossId 路口编号
     * @return 阶段交通流数据
     * @throws BusinessException 业务异常
     */
    List<StageTrafficData> getStageTrafficDataByCrossId(String crossId) throws BusinessException;

    /**
     * 获取阶段交通流数据列表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 阶段交通流数据
     * @throws BusinessException 业务异常
     */
    List<StageTrafficData> getAllStageTrafficData(LocalDateTime startTime, LocalDateTime endTime) throws BusinessException;

    /**
     * 保存路口交通流数据
     *
     * @param crossTrafficData 路口交通流数据
     * @throws BusinessException 业务异常
     */
    void saveCrossTrafficData(CrossTrafficData crossTrafficData) throws BusinessException;

    /**
     * 保存阶段交通流数据
     *
     * @param stageTrafficData 阶段交通流数据
     * @throws BusinessException 业务异常
     */
    void saveStageTrafficData(StageTrafficData stageTrafficData) throws BusinessException;

    /**
     * 获取路口周期信息
     *
     * @param crossId 路口编号
     * @return 路口周期
     * @throws BusinessException 业务异常
     */
    CrossCycle getCrossCycle(String crossId) throws BusinessException;

    /**
     * 获取路口周期信息
     *
     * @return 路口周期列表
     * @throws BusinessException 业务异常
     */
    List<CrossCycle> getAllCrossCycle() throws BusinessException;

    /**
     * 更新路口周期信息
     *
     * @param crossCycle 路口周期
     * @throws BusinessException 业务异常
     */
    void updateCrossCycle(CrossCycle crossCycle) throws BusinessException;

    /**
     * 获取路口阶段信息
     *
     * @param crossId 路口编号
     * @return 路口阶段
     * @throws BusinessException 业务异常
     */
    CrossStage getCrossStage(String crossId) throws BusinessException;

    /**
     * 获取路口阶段信息列表
     *
     * @return 路口阶段
     * @throws BusinessException 业务异常
     */
    List<CrossStage> getAllCrossStage() throws BusinessException;

    /**
     * 更新路口阶段信息
     *
     * @param crossStage 路口阶段
     * @throws BusinessException 业务异常
     */
    void updateCrossStage(CrossStage crossStage) throws BusinessException;

    /**
     * 启动或停止数据上传
     *
     * @param command Start或Stop
     * @param dataType 数据类型
     * @param crossIds 路口编号列表
     * @throws BusinessException 业务异常
     */
    void controlDataReport(String command, String dataType, List<String> crossIds) throws BusinessException;

    /**
     * 清理历史数据
     *
     * @param beforeDate 清理此日期之前的数据
     * @throws BusinessException 业务异常
     */
    void cleanHistoryData(LocalDateTime beforeDate) throws BusinessException;

    // ==================== 为重传功能新增的历史数据查询方法 ====================

    /**
     * 查询指定时间段内的信号机故障信息
     *
     * @param crossId   路口编号
     * @param startTime 开始时间 (格式: yyyyMMddHHmmss)
     * @param endTime   结束时间 (格式: yyyyMMddHHmmss)
     * @return 故障信息列表
     * @throws BusinessException 业务异常
     */
    List<SignalControllerError> getSignalControllerErrors(String crossId, String startTime, String endTime)
            throws BusinessException;

    /**
     * 查询指定时间段内的路口控制方式方案
     *
     * @param crossId   路口编号
     * @param startTime 开始时间 (格式: yyyyMMddHHmmss)
     * @param endTime   结束时间 (格式: yyyyMMddHHmmss)
     * @return 控制方式方案列表
     * @throws BusinessException 业务异常
     */
    List<CrossModePlan> getCrossModePlans(String crossId, String startTime, String endTime)
            throws BusinessException;

    /**
     * 查询指定时间段内的路口交通流数据
     *
     * @param crossId   路口编号
     * @param startTime 开始时间 (格式: yyyyMMddHHmmss)
     * @param endTime   结束时间 (格式: yyyyMMddHHmmss)
     * @return 交通流数据列表
     * @throws BusinessException 业务异常
     */
    List<CrossTrafficData> getCrossTrafficData(String crossId, String startTime, String endTime)
            throws BusinessException;

    /**
     * 查询指定时间段内的路口周期数据
     *
     * @param crossId   路口编号
     * @param startTime 开始时间 (格式: yyyyMMddHHmmss)
     * @param endTime   结束时间 (格式: yyyyMMddHHmmss)
     * @return 周期数据列表
     * @throws BusinessException 业务异常
     */
    List<CrossCycle> getCrossCycles(String crossId, String startTime, String endTime)
            throws BusinessException;

    /**
     * 查询指定时间段内的路口阶段数据
     *
     * @param crossId   路口编号
     * @param startTime 开始时间 (格式: yyyyMMddHHmmss)
     * @param endTime   结束时间 (格式: yyyyMMddHHmmss)
     * @return 阶段数据列表
     * @throws BusinessException 业务异常
     */
    List<CrossStage> getCrossStages(String crossId, String startTime, String endTime)
            throws BusinessException;

    /**
     * 查询指定时间段内的信号组灯态数据
     *
     * @param crossId   路口编号
     * @param startTime 开始时间 (格式: yyyyMMddHHmmss)
     * @param endTime   结束时间 (格式: yyyyMMddHHmmss)
     * @return 信号组灯态数据列表
     * @throws BusinessException 业务异常
     */
    List<CrossSignalGroupStatus> getSignalGroupStatus(String crossId, String startTime, String endTime)
            throws BusinessException;

    /**
     * 查询指定时间段内的阶段交通流数据
     *
     * @param crossId   路口编号
     * @param startTime 开始时间 (格式: yyyyMMddHHmmss)
     * @param endTime   结束时间 (格式: yyyyMMddHHmmss)
     * @return 阶段交通流数据列表
     * @throws BusinessException 业务异常
     */
    List<StageTrafficData> getStageTrafficData(String crossId, String startTime, String endTime)
            throws BusinessException;

    // ==================== 批量查询方法（可选实现） ====================

    /**
     * 批量查询多个路口的历史数据
     *
     * @param crossIds  路口编号列表
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param dataType  数据类型
     * @return 数据列表
     * @throws BusinessException 业务异常
     */
    List<Object> getBatchHistoryData(List<String> crossIds, String startTime, String endTime, String dataType)
            throws BusinessException;

    /**
     * 统计指定时间段内的数据记录数
     *
     * @param crossId   路口编号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param dataType  数据类型
     * @return 记录数
     * @throws BusinessException 业务异常
     */
    long countHistoryData(String crossId, String startTime, String endTime, String dataType)
            throws BusinessException;

    /**
     * 分页查询历史数据（用于大数据量场景）
     *
     * @param crossId   路口编号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param dataType  数据类型
     * @param pageNum   页码（从1开始）
     * @param pageSize  每页大小
     * @return 分页结果
     * @throws BusinessException 业务异常
     */
    PageResult<Object> getHistoryDataByPage(String crossId, String startTime, String endTime,
                                            String dataType, int pageNum, int pageSize)
            throws BusinessException;

    // ==================== 数据验证和清理方法 ====================

    /**
     * 验证历史数据完整性
     *
     * @param crossId   路口编号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param dataType  数据类型
     * @return 验证结果
     * @throws BusinessException 业务异常
     */
    DataIntegrityResult validateDataIntegrity(String crossId, String startTime, String endTime, String dataType)
            throws BusinessException;

    /**
     * 清理过期的历史数据
     *
     * @param beforeTime 清理此时间之前的数据
     * @param dataType   数据类型
     * @return 清理的记录数
     * @throws BusinessException 业务异常
     */
    long cleanupExpiredData(String beforeTime, String dataType) throws BusinessException;

    // ==================== 辅助类定义 ====================

    /**
     * 分页结果类
     */
    class PageResult<T> {
        private List<T> data;
        private long totalCount;
        private int pageNum;
        private int pageSize;
        private int totalPages;

        public PageResult() {}

        public PageResult(List<T> data, long totalCount, int pageNum, int pageSize) {
            this.data = data;
            this.totalCount = totalCount;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
        }

        // Getters and Setters
        public List<T> getData() { return data; }
        public void setData(List<T> data) { this.data = data; }

        public long getTotalCount() { return totalCount; }
        public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

        public int getPageNum() { return pageNum; }
        public void setPageNum(int pageNum) { this.pageNum = pageNum; }

        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

        public boolean hasNext() {
            return pageNum < totalPages;
        }

        public boolean hasPrevious() {
            return pageNum > 1;
        }

        @Override
        public String toString() {
            return "PageResult{" +
                    "dataSize=" + (data != null ? data.size() : 0) +
                    ", totalCount=" + totalCount +
                    ", pageNum=" + pageNum +
                    ", pageSize=" + pageSize +
                    ", totalPages=" + totalPages +
                    '}';
        }
    }

    /**
     * 数据完整性验证结果
     */
    class DataIntegrityResult {
        private boolean isValid;
        private long expectedCount;
        private long actualCount;
        private List<String> missingTimeRanges;
        private String message;

        public DataIntegrityResult() {}

        public DataIntegrityResult(boolean isValid, long expectedCount, long actualCount, String message) {
            this.isValid = isValid;
            this.expectedCount = expectedCount;
            this.actualCount = actualCount;
            this.message = message;
        }

        // Getters and Setters
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }

        public long getExpectedCount() { return expectedCount; }
        public void setExpectedCount(long expectedCount) { this.expectedCount = expectedCount; }

        public long getActualCount() { return actualCount; }
        public void setActualCount(long actualCount) { this.actualCount = actualCount; }

        public List<String> getMissingTimeRanges() { return missingTimeRanges; }
        public void setMissingTimeRanges(List<String> missingTimeRanges) { this.missingTimeRanges = missingTimeRanges; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public double getCompleteness() {
            if (expectedCount == 0) return 100.0;
            return (double) actualCount / expectedCount * 100.0;
        }

        @Override
        public String toString() {
            return "DataIntegrityResult{" +
                    "isValid=" + isValid +
                    ", expectedCount=" + expectedCount +
                    ", actualCount=" + actualCount +
                    ", completeness=" + String.format("%.2f%%", getCompleteness()) +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
