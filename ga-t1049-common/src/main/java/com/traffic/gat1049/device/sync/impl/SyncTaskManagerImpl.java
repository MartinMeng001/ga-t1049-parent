package com.traffic.gat1049.device.sync.impl;

import com.traffic.gat1049.device.sync.interfaces.*;
import com.traffic.gat1049.device.sync.model.*;
import com.traffic.gat1049.device.adapter.model.SyncResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 同步任务管理器实现
 */
@Service
public class SyncTaskManagerImpl implements SyncTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(SyncTaskManagerImpl.class);

    @Autowired
    private SyncService syncService;

//    @Autowired
    private SyncResultHandler syncResultHandler;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 任务存储
     */
    private final ConcurrentMap<String, SyncTask> tasks = new ConcurrentHashMap<>();

    /**
     * 任务执行器
     */
    private ExecutorService taskExecutor;

    /**
     * 调度执行器
     */
    private ScheduledExecutorService scheduledExecutor;

    /**
     * 任务队列
     */
    private final BlockingQueue<SyncTask> taskQueue = new PriorityBlockingQueue<>(1000,
            Comparator.comparing(SyncTask::getPriority).reversed()
                    .thenComparing(SyncTask::getCreateTime));

    /**
     * 管理器状态
     */
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean paused = new AtomicBoolean(false);

    /**
     * 统计信息
     */
    private final AtomicLong totalTasks = new AtomicLong(0);
    private final AtomicLong completedTasks = new AtomicLong(0);
    private final AtomicLong failedTasks = new AtomicLong(0);

    @PostConstruct
    public void initialize() {
        logger.info("正在初始化同步任务管理器...");

        // 创建线程池
        taskExecutor = Executors.newFixedThreadPool(10,
                r -> new Thread(r, "sync-task-" + System.currentTimeMillis()));

        scheduledExecutor = Executors.newScheduledThreadPool(2,
                r -> new Thread(r, "sync-scheduler-" + System.currentTimeMillis()));

        // 启动任务处理器
        startTaskProcessor();

        // 启动任务监控
        startTaskMonitor();

        logger.info("同步任务管理器初始化完成");
    }

    @PreDestroy
    public void shutdown() {
        logger.info("正在关闭同步任务管理器...");

        running.set(false);

        // 关闭执行器
        taskExecutor.shutdown();
        scheduledExecutor.shutdown();

        try {
            if (!taskExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                taskExecutor.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("同步任务管理器已关闭");
    }

    @Override
    public String createSyncTask(SyncTaskRequest taskRequest) {
        try {
            String taskId = generateTaskId();

            SyncTask task = SyncTask.builder()
                    .taskId(taskId)
                    .controllerId(taskRequest.getControllerId())
                    .syncType(taskRequest.getSyncType())
                    .configData(taskRequest.getConfigData())
                    .priority(taskRequest.getPriority())
                    .timeoutSeconds(taskRequest.getTimeoutSeconds())
                    .maxRetryCount(taskRequest.getMaxRetryCount())
                    .status(SyncTaskStatus.CREATED)
                    .createTime(LocalDateTime.now())
                    .requestSource(taskRequest.getRequestSource())
                    .description(taskRequest.getDescription())
                    .build();

            tasks.put(taskId, task);
            totalTasks.incrementAndGet();

            logger.info("同步任务创建成功: taskId={}, controllerId={}, syncType={}",
                    taskId, taskRequest.getControllerId(), taskRequest.getSyncType());

            return taskId;

        } catch (Exception e) {
            logger.error("创建同步任务失败", e);
            throw new RuntimeException("创建同步任务失败: " + e.getMessage(), e);
        }
    }

    @Override
    public CompletableFuture<SyncResult> submitTask(String taskId) {
        SyncTask task = tasks.get(taskId);
        if (task == null) {
            return CompletableFuture.completedFuture(
                    SyncResult.failure(null, "TASK_NOT_FOUND", "任务不存在: " + taskId));
        }

        try {
            // 更新任务状态
            task.setStatus(SyncTaskStatus.PENDING);
            task.setSubmitTime(LocalDateTime.now());

            // 添加到队列
            taskQueue.offer(task);

            logger.info("同步任务已提交: taskId={}, controllerId={}", taskId, task.getControllerId());

            // 返回异步结果
            return CompletableFuture.supplyAsync(() -> {
                try {
                    // 等待任务完成
                    return waitForTaskCompletion(task);
                } catch (Exception e) {
                    logger.error("等待任务完成异常: taskId={}", taskId, e);
                    return SyncResult.failure(task.getControllerId(), "TASK_WAIT_ERROR",
                            "等待任务完成异常: " + e.getMessage());
                }
            }, taskExecutor);

        } catch (Exception e) {
            logger.error("提交同步任务失败: taskId={}", taskId, e);
            return CompletableFuture.completedFuture(
                    SyncResult.failure(task.getControllerId(), "TASK_SUBMIT_ERROR",
                            "提交任务失败: " + e.getMessage()));
        }
    }

    @Override
    public boolean cancelTask(String taskId) {
        SyncTask task = tasks.get(taskId);
        if (task == null) {
            return false;
        }

        try {
            if (task.getStatus() == SyncTaskStatus.RUNNING) {
                // 正在运行的任务无法取消
                logger.warn("任务正在运行，无法取消: taskId={}", taskId);
                return false;
            }

            task.setStatus(SyncTaskStatus.CANCELLED);
            task.setCompleteTime(LocalDateTime.now());

            // 从队列中移除
            taskQueue.remove(task);

            logger.info("任务已取消: taskId={}", taskId);
            return true;

        } catch (Exception e) {
            logger.error("取消任务失败: taskId={}", taskId, e);
            return false;
        }
    }

    @Override
    public SyncTaskStatus getTaskStatus(String taskId) {
        SyncTask task = tasks.get(taskId);
        return task != null ? task.getStatus() : null;
    }

    @Override
    public SyncTaskProgress getTaskProgress(String taskId) {
        SyncTask task = tasks.get(taskId);
        if (task == null) {
            return null;
        }

        return SyncTaskProgress.builder()
                .taskId(taskId)
                .status(task.getStatus())
                .progress(task.getProgress())
                .currentStep(task.getCurrentStep())
                .totalSteps(task.getTotalSteps())
                .message(task.getMessage())
                .startTime(task.getStartTime())
                .estimatedCompleteTime(calculateEstimatedCompleteTime(task))
                .build();
    }

    @Override
    public List<SyncTask> getActiveTasks() {
        return tasks.values().stream()
                .filter(task -> !task.getStatus().isCompleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<SyncTask> getTaskHistory(String controllerId, int limit) {
        Stream<SyncTask> stream = tasks.values().stream()
                .filter(task -> task.getStatus().isCompleted());

        if (controllerId != null) {
            stream = stream.filter(task -> controllerId.equals(task.getControllerId()));
        }

        return stream
                .sorted(Comparator.comparing(SyncTask::getCompleteTime).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void pause() {
        paused.set(true);
        logger.info("同步任务管理器已暂停");
    }

    @Override
    public void resume() {
        paused.set(false);
        logger.info("同步任务管理器已恢复");
    }

    @Override
    public SyncManagerStats getStats() {
        List<SyncTask> activeTasks = getActiveTasks();

        long pendingTasks = activeTasks.stream()
                .mapToLong(task -> task.getStatus() == SyncTaskStatus.PENDING ? 1 : 0)
                .sum();

        long runningTasks = activeTasks.stream()
                .mapToLong(task -> task.getStatus() == SyncTaskStatus.RUNNING ? 1 : 0)
                .sum();

        return SyncManagerStats.builder()
                .totalTasks(totalTasks.get())
                .completedTasks(completedTasks.get())
                .failedTasks(failedTasks.get())
                .pendingTasks((int) pendingTasks)
                .runningTasks((int) runningTasks)
                .queueSize(taskQueue.size())
                .running(!paused.get())
                .lastUpdateTime(LocalDateTime.now())
                .build();
    }

    // =================================================================
    // 私有方法
    // =================================================================

    /**
     * 启动任务处理器
     */
    private void startTaskProcessor() {
        taskExecutor.submit(() -> {
            logger.info("同步任务处理器已启动");

            while (running.get()) {
                try {
                    if (paused.get()) {
                        Thread.sleep(1000);
                        continue;
                    }

                    SyncTask task = taskQueue.poll(1, TimeUnit.SECONDS);
                    if (task != null) {
                        processTask(task);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("任务处理器异常", e);
                }
            }

            logger.info("同步任务处理器已停止");
        });
    }

    /**
     * 启动任务监控
     */
    private void startTaskMonitor() {
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            try {
                monitorTaskTimeouts();
                cleanupCompletedTasks();
            } catch (Exception e) {
                logger.error("任务监控异常", e);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 处理单个任务
     */
//    @Async
    private void processTask(SyncTask task) {
        try {
            logger.info("开始处理同步任务: taskId={}, controllerId={}",
                    task.getTaskId(), task.getControllerId());

            // 更新任务状态
            task.setStatus(SyncTaskStatus.RUNNING);
            task.setStartTime(LocalDateTime.now());
            task.setProgress(0);

            // 执行同步
            SyncResult result = executeSync(task);

            // 更新任务结果
            task.setSyncResult(result);
            task.setProgress(100);
            task.setCompleteTime(LocalDateTime.now());

            if (result.isSuccess()) {
                task.setStatus(SyncTaskStatus.COMPLETED);
                completedTasks.incrementAndGet();
                syncResultHandler.handleSuccess(task, result);
            } else {
                task.setStatus(SyncTaskStatus.FAILED);
                failedTasks.incrementAndGet();
                syncResultHandler.handleFailure(task, result);
            }

            logger.info("同步任务处理完成: taskId={}, success={}",
                    task.getTaskId(), result.isSuccess());

        } catch (Exception e) {
            logger.error("处理同步任务异常: taskId={}", task.getTaskId(), e);

            task.setStatus(SyncTaskStatus.FAILED);
            task.setCompleteTime(LocalDateTime.now());
            task.setMessage("任务执行异常: " + e.getMessage());

            failedTasks.incrementAndGet();
            syncResultHandler.handleException(task, e);
        }
    }

    /**
     * 执行同步操作
     */
    private SyncResult executeSync(SyncTask task) {
        switch (task.getSyncType()) {
            case CONFIG_TO_DEVICE:
                return syncService.syncConfigToDevice(task.getControllerId(), task.getConfigData());
            case STATUS_FROM_DEVICE:
                // 这里需要从设备读取状态
                return SyncResult.success(task.getControllerId(), "状态读取成功");
            default:
                return SyncResult.failure(task.getControllerId(), "UNSUPPORTED_SYNC_TYPE",
                        "不支持的同步类型: " + task.getSyncType());
        }
    }

    /**
     * 等待任务完成
     */
    private SyncResult waitForTaskCompletion(SyncTask task) {
        try {
            int timeoutSeconds = task.getTimeoutSeconds() != null ? task.getTimeoutSeconds() : 300;
            LocalDateTime deadline = LocalDateTime.now().plusSeconds(timeoutSeconds);

            while (LocalDateTime.now().isBefore(deadline)) {
                if (task.getStatus().isCompleted()) {
                    return task.getSyncResult() != null ? task.getSyncResult() :
                            SyncResult.success(task.getControllerId(), "任务完成");
                }

                Thread.sleep(1000);
            }

            // 超时
            task.setStatus(SyncTaskStatus.TIMEOUT);
            task.setCompleteTime(LocalDateTime.now());
            syncResultHandler.handleTimeout(task);

            return SyncResult.timeout(task.getControllerId(), "任务执行超时", timeoutSeconds * 1000L);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return SyncResult.failure(task.getControllerId(), "TASK_INTERRUPTED", "任务被中断");
        }
    }

    /**
     * 监控任务超时
     */
    private void monitorTaskTimeouts() {
        LocalDateTime now = LocalDateTime.now();

        tasks.values().stream()
                .filter(task -> task.getStatus() == SyncTaskStatus.RUNNING)
                .filter(task -> {
                    int timeoutSeconds = task.getTimeoutSeconds() != null ? task.getTimeoutSeconds() : 300;
                    return task.getStartTime() != null &&
                            task.getStartTime().plusSeconds(timeoutSeconds).isBefore(now);
                })
                .forEach(task -> {
                    logger.warn("任务执行超时: taskId={}, controllerId={}",
                            task.getTaskId(), task.getControllerId());

                    task.setStatus(SyncTaskStatus.TIMEOUT);
                    task.setCompleteTime(now);
                    task.setMessage("任务执行超时");

                    syncResultHandler.handleTimeout(task);
                });
    }

    /**
     * 清理已完成的任务
     */
    private void cleanupCompletedTasks() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

        List<String> toRemove = tasks.entrySet().stream()
                .filter(entry -> {
                    SyncTask task = entry.getValue();
                    return task.getStatus().isCompleted() &&
                            task.getCompleteTime() != null &&
                            task.getCompleteTime().isBefore(cutoffTime);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        toRemove.forEach(tasks::remove);

        if (!toRemove.isEmpty()) {
            logger.info("清理已完成任务: 数量={}", toRemove.size());
        }
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "SYNC_" + System.currentTimeMillis() + "_" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 计算预计完成时间
     */
    private LocalDateTime calculateEstimatedCompleteTime(SyncTask task) {
        if (task.getStartTime() == null || task.getProgress() <= 0) {
            return null;
        }

        long elapsedMs = java.time.Duration.between(task.getStartTime(), LocalDateTime.now()).toMillis();
        long totalEstimatedMs = (elapsedMs * 100) / task.getProgress();
        long remainingMs = totalEstimatedMs - elapsedMs;

        return LocalDateTime.now().plusNanos(remainingMs * 1_000_000);
    }
}
