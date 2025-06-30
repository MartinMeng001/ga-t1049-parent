package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.protocol.model.runtime.*;
import com.traffic.gat1049.protocol.model.runtime.SignalControllerError;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficData;

/**
 * 推送通知服务接口
 * 用于向订阅的客户端推送各类运行数据
 */
public interface PushNotificationService {

    /**
     * 推送信号机故障信息
     *
     * @param error 故障信息
     * @throws BusinessException 业务异常
     */
    void pushSignalControllerError(SignalControllerError error) throws BusinessException;

    /**
     * 推送路口控制方式方案
     *
     * @param modePlan 控制方式方案
     * @throws BusinessException 业务异常
     */
    void pushCrossModePlan(CrossModePlan modePlan) throws BusinessException;

    /**
     * 推送路口交通流数据
     *
     * @param trafficData 交通流数据
     * @throws BusinessException 业务异常
     */
    void pushCrossTrafficData(CrossTrafficData trafficData) throws BusinessException;

    /**
     * 推送路口周期数据
     *
     * @param cycle 周期数据
     * @throws BusinessException 业务异常
     */
    void pushCrossCycle(CrossCycle cycle) throws BusinessException;

    /**
     * 推送路口阶段数据
     *
     * @param stage 阶段数据
     * @throws BusinessException 业务异常
     */
    void pushCrossStage(CrossStage stage) throws BusinessException;

    /**
     * 推送信号组灯态数据
     *
     * @param signalGroupStatus 信号组灯态数据
     * @throws BusinessException 业务异常
     */
    void pushSignalGroupStatus(CrossSignalGroupStatus signalGroupStatus) throws BusinessException;

    /**
     * 推送阶段交通流数据
     *
     * @param stageTrafficData 阶段交通流数据
     * @throws BusinessException 业务异常
     */
    void pushStageTrafficData(StageTrafficData stageTrafficData) throws BusinessException;

    /**
     * 批量推送数据
     *
     * @param dataList 数据列表
     * @param dataType 数据类型
     * @throws BusinessException 业务异常
     */
    void pushBatch(java.util.List<? extends Object> dataList, String dataType) throws BusinessException;

    /**
     * 检查推送服务状态
     *
     * @return 推送服务是否正常
     */
    boolean isServiceAvailable();

    /**
     * 获取推送统计信息
     *
     * @return 推送统计信息
     */
    PushStatistics getPushStatistics();

    /**
     * 推送统计信息类
     */
    class PushStatistics {
        private long totalPushCount;
        private long successPushCount;
        private long failedPushCount;
        private long lastPushTime;
        private int activeConnections;

        public PushStatistics() {}

        public PushStatistics(long totalPushCount, long successPushCount, long failedPushCount,
                              long lastPushTime, int activeConnections) {
            this.totalPushCount = totalPushCount;
            this.successPushCount = successPushCount;
            this.failedPushCount = failedPushCount;
            this.lastPushTime = lastPushTime;
            this.activeConnections = activeConnections;
        }

        // Getters and Setters
        public long getTotalPushCount() { return totalPushCount; }
        public void setTotalPushCount(long totalPushCount) { this.totalPushCount = totalPushCount; }

        public long getSuccessPushCount() { return successPushCount; }
        public void setSuccessPushCount(long successPushCount) { this.successPushCount = successPushCount; }

        public long getFailedPushCount() { return failedPushCount; }
        public void setFailedPushCount(long failedPushCount) { this.failedPushCount = failedPushCount; }

        public long getLastPushTime() { return lastPushTime; }
        public void setLastPushTime(long lastPushTime) { this.lastPushTime = lastPushTime; }

        public int getActiveConnections() { return activeConnections; }
        public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }

        /**
         * 获取成功率
         */
        public double getSuccessRate() {
            if (totalPushCount == 0) return 0.0;
            return (double) successPushCount / totalPushCount * 100.0;
        }

        @Override
        public String toString() {
            return "PushStatistics{" +
                    "totalPushCount=" + totalPushCount +
                    ", successPushCount=" + successPushCount +
                    ", failedPushCount=" + failedPushCount +
                    ", successRate=" + String.format("%.2f%%", getSuccessRate()) +
                    ", lastPushTime=" + lastPushTime +
                    ", activeConnections=" + activeConnections +
                    '}';
        }
    }
}