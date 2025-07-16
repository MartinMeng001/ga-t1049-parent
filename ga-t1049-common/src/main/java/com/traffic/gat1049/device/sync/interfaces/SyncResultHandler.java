package com.traffic.gat1049.device.sync.interfaces;

import com.traffic.gat1049.device.adapter.model.SyncResult;
import com.traffic.gat1049.device.sync.model.SyncTask;

/**
 * 同步结果处理器接口
 * 处理同步完成后的结果
 */
public interface SyncResultHandler {

    /**
     * 处理同步成功结果
     * @param task 同步任务
     * @param result 同步结果
     */
    void handleSuccess(SyncTask task, SyncResult result);

    /**
     * 处理同步失败结果
     * @param task 同步任务
     * @param result 同步结果
     */
    void handleFailure(SyncTask task, SyncResult result);

    /**
     * 处理同步部分成功结果
     * @param task 同步任务
     * @param result 同步结果
     */
    void handlePartialSuccess(SyncTask task, SyncResult result);

    /**
     * 处理同步超时
     * @param task 同步任务
     */
    void handleTimeout(SyncTask task);

    /**
     * 处理同步异常
     * @param task 同步任务
     * @param exception 异常信息
     */
    void handleException(SyncTask task, Exception exception);
}
