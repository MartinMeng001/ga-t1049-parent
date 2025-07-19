package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.service.interfaces.PushNotificationService;
import com.traffic.gat1049.service.interfaces.RunInfoRetransService;
import com.traffic.gat1049.service.interfaces.TrafficDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

/**
 * 重传运行信息服务实现
 */
@Service
public class RunInfoRetransServiceImpl implements RunInfoRetransService {

    private static final Logger logger = LoggerFactory.getLogger(RunInfoRetransServiceImpl.class);

    private TrafficDataService trafficDataService;

    private PushNotificationService pushNotificationService;

    // 任务管理
    private final Map<String, RetransTaskInfo> taskMap = new ConcurrentHashMap<>();

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // 支持的数据对象类型
    private static final List<String> SUPPORTED_OBJECT_TYPES = Arrays.asList(
            "SignalControllerError",  // 信号机故障信息
            "CrossModePlan",          // 路口控制方式方案
            "CrossTrafficData",       // 路口交通流数据
            "CrossCycle",             // 路口周期数据
            "CrossStage",             // 路口阶段数据
            "CrossSignalGroupStatus", // 信号组灯态数据
            "StageTrafficData"        // 阶段交通流数据
    );

    @Override
    public void requestRetransmission(String startTime, String endTime, String objName, List<String> crossIds)
            throws BusinessException {

        // 生成任务ID
        String taskId = generateTaskId();

        logger.info("Creating retransmission task: taskId={}, objName={}, timeRange=[{} - {}], crossCount={}",
                taskId, objName, startTime, endTime, crossIds.size());

        // 创建任务信息
        RetransTaskInfo taskInfo = new RetransTaskInfo();
        taskInfo.setTaskId(taskId);
        taskInfo.setStartTime(startTime);
        taskInfo.setEndTime(endTime);
        taskInfo.setObjName(objName);
        taskInfo.setCrossIds(new ArrayList<>(crossIds));
        taskInfo.setStatus(RetransTaskStatus.PENDING);
        taskInfo.setCreateTime(formatCurrentTime());
        taskInfo.setTotalCount(crossIds.size());
        taskInfo.setCompletedCount(0);

        // 保存任务
        taskMap.put(taskId, taskInfo);

        try {
            // 异步执行重传任务
            executeRetransmissionAsync(taskInfo);

            logger.info("Retransmission task created successfully: taskId={}", taskId);

        } catch (Exception e) {
            // 更新任务状态为失败
            taskInfo.setStatus(RetransTaskStatus.FAILED);
            taskInfo.setErrorMessage(e.getMessage());
            taskInfo.setUpdateTime(formatCurrentTime());

            logger.error("Failed to start retransmission task: taskId={}", taskId, e);
            throw new BusinessException("Failed to start retransmission task: " + e.getMessage());
        }
    }

    /**
     * 异步执行重传任务
     */
//
    public CompletableFuture<Void> executeRetransmissionAsync(RetransTaskInfo taskInfo) {
        return CompletableFuture.runAsync(() -> {
            try {
                executeRetransmission(taskInfo);
            } catch (Exception e) {
                logger.error("Retransmission task failed: taskId={}", taskInfo.getTaskId(), e);
                taskInfo.setStatus(RetransTaskStatus.FAILED);
                taskInfo.setErrorMessage(e.getMessage());
                taskInfo.setUpdateTime(formatCurrentTime());
            }
        });
    }

    /**
     * 执行重传任务
     */
    private void executeRetransmission(RetransTaskInfo taskInfo) {
        String taskId = taskInfo.getTaskId();

        logger.info("Starting retransmission execution: taskId={}", taskId);

        // 更新任务状态为执行中
        taskInfo.setStatus(RetransTaskStatus.RUNNING);
        taskInfo.setUpdateTime(formatCurrentTime());

        try {
            String objName = taskInfo.getObjName();
            String startTime = taskInfo.getStartTime();
            String endTime = taskInfo.getEndTime();
            List<String> crossIds = taskInfo.getCrossIds();

            for (String crossId : crossIds) {
                try {
                    // 检查任务是否被取消
                    if (taskInfo.getStatus() == RetransTaskStatus.CANCELLED) {
                        logger.info("Retransmission task cancelled: taskId={}", taskId);
                        return;
                    }

                    // 根据数据对象类型执行重传
                    retransmitDataForCross(objName, startTime, endTime, crossId);

                    // 更新完成计数
                    taskInfo.setCompletedCount(taskInfo.getCompletedCount() + 1);
                    taskInfo.setUpdateTime(formatCurrentTime());

                    logger.debug("Completed retransmission for cross {}: progress {}/{}",
                            crossId, taskInfo.getCompletedCount(), taskInfo.getTotalCount());

                } catch (Exception e) {
                    logger.error("Failed to retransmit data for cross {}: {}", crossId, e.getMessage());
                    // 继续处理其他路口，不中断整个任务
                }
            }

            // 任务完成
            taskInfo.setStatus(RetransTaskStatus.COMPLETED);
            taskInfo.setUpdateTime(formatCurrentTime());

            logger.info("Retransmission task completed: taskId={}, totalCount={}, completedCount={}",
                    taskId, taskInfo.getTotalCount(), taskInfo.getCompletedCount());

        } catch (Exception e) {
            logger.error("Retransmission task execution failed: taskId={}", taskId, e);
            taskInfo.setStatus(RetransTaskStatus.FAILED);
            taskInfo.setErrorMessage(e.getMessage());
            taskInfo.setUpdateTime(formatCurrentTime());
        }
    }

    /**
     * 为指定路口重传数据
     */
    private void retransmitDataForCross(String objName, String startTime, String endTime, String crossId)
            throws BusinessException {

        logger.debug("Retransmitting {} data for cross {} from {} to {}", objName, crossId, startTime, endTime);

        try {
            switch (objName) {
                case "SignalControllerError":
                    retransmitSignalControllerErrors(crossId, startTime, endTime);
                    break;
                case "CrossModePlan":
                    retransmitCrossModePlans(crossId, startTime, endTime);
                    break;
                case "CrossTrafficData":
                    retransmitCrossTrafficData(crossId, startTime, endTime);
                    break;
                case "CrossCycle":
                    retransmitCrossCycles(crossId, startTime, endTime);
                    break;
                case "CrossStage":
                    retransmitCrossStages(crossId, startTime, endTime);
                    break;
                case "CrossSignalGroupStatus":
                    retransmitSignalGroupStatus(crossId, startTime, endTime);
                    break;
                case "StageTrafficData":
                    retransmitStageTrafficData(crossId, startTime, endTime);
                    break;
                default:
                    throw new BusinessException("Unsupported object type: " + objName);
            }
        } catch (Exception e) {
            throw new BusinessException("Failed to retransmit " + objName + " for cross " + crossId + ": " + e.getMessage());
        }
    }

    /**
     * 重传信号机故障信息
     */
    private void retransmitSignalControllerErrors(String crossId, String startTime, String endTime)
            throws BusinessException {
        // 从数据库查询指定时间段的故障信息
        var errorList = trafficDataService.getSignalControllerErrors(crossId, startTime, endTime);

        // 通过推送服务发送数据
        for (var error : errorList) {
            pushNotificationService.pushSignalControllerError(error);
        }

        logger.debug("Retransmitted {} signal controller errors for cross {}", errorList.size(), crossId);
    }

    /**
     * 重传路口控制方式方案
     */
    private void retransmitCrossModePlans(String crossId, String startTime, String endTime)
            throws BusinessException {
        var modePlans = trafficDataService.getCrossModePlans(crossId, startTime, endTime);

        for (var modePlan : modePlans) {
            pushNotificationService.pushCrossModePlan(modePlan);
        }

        logger.debug("Retransmitted {} mode plans for cross {}", modePlans.size(), crossId);
    }

    /**
     * 重传路口交通流数据
     */
    private void retransmitCrossTrafficData(String crossId, String startTime, String endTime)
            throws BusinessException {
        var trafficDataList = trafficDataService.getCrossTrafficData(crossId, startTime, endTime);

        for (var trafficData : trafficDataList) {
            pushNotificationService.pushCrossTrafficData(trafficData);
        }

        logger.debug("Retransmitted {} traffic data records for cross {}", trafficDataList.size(), crossId);
    }

    /**
     * 重传路口周期数据
     */
    private void retransmitCrossCycles(String crossId, String startTime, String endTime)
            throws BusinessException {
        var cycles = trafficDataService.getCrossCycles(crossId, startTime, endTime);

        for (var cycle : cycles) {
            pushNotificationService.pushCrossCycle(cycle);
        }

        logger.debug("Retransmitted {} cycle records for cross {}", cycles.size(), crossId);
    }

    /**
     * 重传路口阶段数据
     */
    private void retransmitCrossStages(String crossId, String startTime, String endTime)
            throws BusinessException {
        var stages = trafficDataService.getCrossStages(crossId, startTime, endTime);

        for (var stage : stages) {
            pushNotificationService.pushCrossStage(stage);
        }

        logger.debug("Retransmitted {} stage records for cross {}", stages.size(), crossId);
    }

    /**
     * 重传信号组灯态数据
     */
    private void retransmitSignalGroupStatus(String crossId, String startTime, String endTime)
            throws BusinessException {
        var statusList = trafficDataService.getSignalGroupStatus(crossId, startTime, endTime);

        for (var status : statusList) {
            pushNotificationService.pushSignalGroupStatus(status);
        }

        logger.debug("Retransmitted {} signal group status records for cross {}", statusList.size(), crossId);
    }

    /**
     * 重传阶段交通流数据
     */
    private void retransmitStageTrafficData(String crossId, String startTime, String endTime)
            throws BusinessException {
        var stageDataList = trafficDataService.getStageTrafficData(crossId, startTime, endTime);

        for (var stageData : stageDataList) {
            pushNotificationService.pushStageTrafficData(stageData);
        }

        logger.debug("Retransmitted {} stage traffic data records for cross {}", stageDataList.size(), crossId);
    }

    @Override
    public RetransTaskStatus getTaskStatus(String taskId) throws BusinessException {
        RetransTaskInfo taskInfo = taskMap.get(taskId);
        if (taskInfo == null) {
            throw new BusinessException("Task not found: " + taskId);
        }
        return taskInfo.getStatus();
    }

    @Override
    public void cancelTask(String taskId) throws BusinessException {
        RetransTaskInfo taskInfo = taskMap.get(taskId);
        if (taskInfo == null) {
            throw new BusinessException("Task not found: " + taskId);
        }

        if (taskInfo.getStatus() == RetransTaskStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel completed task: " + taskId);
        }

        taskInfo.setStatus(RetransTaskStatus.CANCELLED);
        taskInfo.setUpdateTime(formatCurrentTime());

        logger.info("Retransmission task cancelled: taskId={}", taskId);
    }

    @Override
    public List<String> getSupportedObjectTypes() {
        return new ArrayList<>(SUPPORTED_OBJECT_TYPES);
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "RETRANS_" + System.currentTimeMillis() + "_" +
                String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * 格式化当前时间
     */
    private String formatCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    /**
     * 获取任务信息（用于监控和调试）
     */
    public RetransTaskInfo getTaskInfo(String taskId) {
        return taskMap.get(taskId);
    }

    /**
     * 获取所有任务信息
     */
    public List<RetransTaskInfo> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    /**
     * 清理完成的任务（可定期调用以释放内存）
     */
    public void cleanupCompletedTasks(int maxKeepDays) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(maxKeepDays);
        String cutoffTimeStr = cutoffTime.format(TIME_FORMATTER);

        taskMap.entrySet().removeIf(entry -> {
            RetransTaskInfo task = entry.getValue();
            return (task.getStatus() == RetransTaskStatus.COMPLETED ||
                    task.getStatus() == RetransTaskStatus.FAILED) &&
                    task.getUpdateTime().compareTo(cutoffTimeStr) < 0;
        });
    }
}