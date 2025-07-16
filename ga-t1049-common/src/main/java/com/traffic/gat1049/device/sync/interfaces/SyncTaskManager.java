package com.traffic.gat1049.device.sync.interfaces;

import com.traffic.gat1049.device.adapter.model.SyncResult;
import com.traffic.gat1049.device.sync.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 同步任务管理器接口
 * 负责同步任务的调度、执行和监控
 */
public interface SyncTaskManager {

    /**
     * 创建同步任务
     * @param taskRequest 任务请求
     * @return 任务ID
     */
    String createSyncTask(SyncTaskRequest taskRequest);

    /**
     * 提交同步任务
     * @param taskId 任务ID
     * @return 异步执行结果
     */
    CompletableFuture<SyncResult> submitTask(String taskId);

    /**
     * 取消同步任务
     * @param taskId 任务ID
     * @return 取消是否成功
     */
    boolean cancelTask(String taskId);

    /**
     * 获取任务状态
     * @param taskId 任务ID
     * @return 任务状态
     */
    SyncTaskStatus getTaskStatus(String taskId);

    /**
     * 获取任务进度
     * @param taskId 任务ID
     * @return 任务进度
     */
    SyncTaskProgress getTaskProgress(String taskId);

    /**
     * 获取活动任务列表
     * @return 活动任务列表
     */
    List<SyncTask> getActiveTasks();

    /**
     * 获取任务历史
     * @param controllerId 控制器ID（可选）
     * @param limit 限制数量
     * @return 任务历史
     */
    List<SyncTask> getTaskHistory(String controllerId, int limit);

    /**
     * 暂停任务管理器
     */
    void pause();

    /**
     * 恢复任务管理器
     */
    void resume();

    /**
     * 关闭任务管理器
     */
    void shutdown();

    /**
     * 获取管理器统计信息
     * @return 统计信息
     */
    SyncManagerStats getStats();
}
