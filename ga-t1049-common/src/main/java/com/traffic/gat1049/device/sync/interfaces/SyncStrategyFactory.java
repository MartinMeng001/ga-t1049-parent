package com.traffic.gat1049.device.sync.interfaces;

import com.traffic.gat1049.device.sync.model.SyncType;

/**
 * 同步策略工厂接口
 */
public interface SyncStrategyFactory {

    /**
     * 获取同步策略
     * @param syncType 同步类型
     * @return 同步策略
     */
    //SyncStrategy getStrategy(SyncType syncType);

    /**
     * 注册同步策略
     * @param syncType 同步类型
     * @param strategy 同步策略
     */
    //void registerStrategy(SyncType syncType, SyncStrategy strategy);

    /**
     * 获取支持的同步类型
     * @return 同步类型列表
     */
    SyncType[] getSupportedTypes();
}
