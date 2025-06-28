package com.traffic.gat1049.service.interfaces;

import com.traffic.gat1049.exception.BusinessException;
import java.util.List;

/**
 * 重传运行信息服务接口
 * 处理运行信息的重传请求和数据推送
 */
public interface RunInfoRetransService {

    /**
     * 请求重传指定时间段的运行信息
     *
     * @param startTime 开始时间 (格式: yyyyMMddHHmmss)
     * @param endTime   结束时间 (格式: yyyyMMddHHmmss)
     * @param objName   数据对象名称
     * @param crossIds  路口编号列表
     * @throws BusinessException 业务异常
     */
    void requestRetransmission(String startTime, String endTime, String objName, List<String> crossIds)
            throws BusinessException;

    /**
     * 检查重传任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态
     * @throws BusinessException 业务异常
     */
    RetransTaskStatus getTaskStatus(String taskId) throws BusinessException;

    /**
     * 取消重传任务
     *
     * @param taskId 任务ID
     * @throws BusinessException 业务异常
     */
    void cancelTask(String taskId) throws BusinessException;

    /**
     * 获取支持的数据对象类型列表
     *
     * @return 支持的数据对象名称列表
     */
    List<String> getSupportedObjectTypes();

    /**
     * 重传任务状态枚举
     */
    enum RetransTaskStatus {
        PENDING("PENDING", "等待执行"),
        RUNNING("RUNNING", "执行中"),
        COMPLETED("COMPLETED", "完成"),
        FAILED("FAILED", "失败"),
        CANCELLED("CANCELLED", "已取消");

        private final String code;
        private final String description;

        RetransTaskStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    /**
     * 重传任务信息
     */
    class RetransTaskInfo {
        private String taskId;
        private String startTime;
        private String endTime;
        private String objName;
        private List<String> crossIds;
        private RetransTaskStatus status;
        private String createTime;
        private String updateTime;
        private String errorMessage;
        private int totalCount;
        private int completedCount;

        // 构造函数
        public RetransTaskInfo() {}

        public RetransTaskInfo(String taskId, String objName, RetransTaskStatus status) {
            this.taskId = taskId;
            this.objName = objName;
            this.status = status;
        }

        // Getters and Setters
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }

        public String getObjName() { return objName; }
        public void setObjName(String objName) { this.objName = objName; }

        public List<String> getCrossIds() { return crossIds; }
        public void setCrossIds(List<String> crossIds) { this.crossIds = crossIds; }

        public RetransTaskStatus getStatus() { return status; }
        public void setStatus(RetransTaskStatus status) { this.status = status; }

        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }

        public String getUpdateTime() { return updateTime; }
        public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

        public int getCompletedCount() { return completedCount; }
        public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }

        /**
         * 获取进度百分比
         */
        public double getProgress() {
            if (totalCount == 0) return 0.0;
            return (double) completedCount / totalCount * 100.0;
        }

        @Override
        public String toString() {
            return "RetransTaskInfo{" +
                    "taskId='" + taskId + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", objName='" + objName + '\'' +
                    ", status=" + status +
                    ", progress=" + String.format("%.1f%%", getProgress()) +
                    ", crossCount=" + (crossIds != null ? crossIds.size() : 0) +
                    '}';
        }
    }
}