package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficData;
import com.traffic.gat1049.service.interfaces.PushNotificationService;
import com.traffic.gat1049.protocol.model.runtime.*;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.protocol.constants.GatConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * 推送通知服务实现
 * 负责向订阅的客户端推送各类运行数据
 */

public class PushNotificationServiceImpl implements PushNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);

    // 推送统计信息
    private final AtomicLong totalPushCount = new AtomicLong(0);
    private final AtomicLong successPushCount = new AtomicLong(0);
    private final AtomicLong failedPushCount = new AtomicLong(0);
    private volatile long lastPushTime = System.currentTimeMillis();

    // 活跃连接管理（简化实现，实际应该从连接管理器获取）
    private final ConcurrentHashMap<String, ClientConnection> activeConnections = new ConcurrentHashMap<>();

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // 在实际项目中，这里应该注入网络连接管理器
    // @Autowired
    // private NetworkConnectionManager connectionManager;

    @Override
    public void pushSignalControllerError(SignalControllerError error) throws BusinessException {
        logger.info("推送信号机故障信息: controllerId={}, errorType={}, errorDesc={}",
                error.getSignalControllerId(), error.getErrorType(), error.getErrorDesc());

        try {
            Message pushMessage = createPushMessage(error);
            sendToAllSubscribers(pushMessage, "SignalControllerError");

            logger.debug("信号机故障信息推送完成: controllerId={}", error.getSignalControllerId());

        } catch (Exception e) {
            logger.error("推送信号机故障信息失败: " + e.getMessage(), e);
            throw new BusinessException("Failed to push signal controller error: " + e.getMessage());
        }
    }

    @Override
    public void pushCrossModePlan(CrossModePlan modePlan) throws BusinessException {
        logger.info("推送路口控制方式方案: crossId={}, controlMode={}, planNo={}",
                modePlan.getCrossId(), modePlan.getControlMode(), modePlan.getPlanNo());

        try {
            Message pushMessage = createPushMessage(modePlan);
            sendToAllSubscribers(pushMessage, "CrossModePlan");

            logger.debug("路口控制方式方案推送完成: crossId={}", modePlan.getCrossId());

        } catch (Exception e) {
            logger.error("推送路口控制方式方案失败: " + e.getMessage(), e);
            throw new BusinessException("Failed to push cross mode plan: " + e.getMessage());
        }
    }

    @Override
    public void pushCrossTrafficData(CrossTrafficData trafficData) throws BusinessException {
        logger.info("推送路口交通流数据: crossId={}, endTime={}, dataCount={}",
                trafficData.getCrossId(), trafficData.getEndTime(),
                trafficData.getDataList() != null ? trafficData.getDataList().size() : 0);

        try {
            Message pushMessage = createPushMessage(trafficData);
            sendToAllSubscribers(pushMessage, "CrossTrafficData");

            logger.debug("路口交通流数据推送完成: crossId={}", trafficData.getCrossId());

        } catch (Exception e) {
            logger.error("推送路口交通流数据失败: " + e.getMessage(), e);
            throw new BusinessException("Failed to push cross traffic data: " + e.getMessage());
        }
    }

    @Override
    public void pushCrossCycle(CrossCycle cycle) throws BusinessException {
        logger.info("推送路口周期数据: crossId={}, startTime={}, cycleLen={}",
                cycle.getCrossId(), cycle.getStartTime(), cycle.getLastCycleLen());

        try {
            Message pushMessage = createPushMessage(cycle);
            sendToAllSubscribers(pushMessage, "CrossCycle");

            logger.debug("路口周期数据推送完成: crossId={}", cycle.getCrossId());

        } catch (Exception e) {
            logger.error("推送路口周期数据失败: " + e.getMessage(), e);
            throw new BusinessException("Failed to push cross cycle: " + e.getMessage());
        }
    }

    @Override
    public void pushCrossStage(CrossStage stage) throws BusinessException {
        logger.info("推送路口阶段数据: crossId={}, curStageNo={}, curStageLen={}",
                stage.getCrossId(), stage.getCurStageNo(), stage.getCurStageLen());

        try {
            Message pushMessage = createPushMessage(stage);
            sendToAllSubscribers(pushMessage, "CrossStage");

            logger.debug("路口阶段数据推送完成: crossId={}", stage.getCrossId());

        } catch (Exception e) {
            logger.error("推送路口阶段数据失败: " + e.getMessage(), e);
            throw new BusinessException("Failed to push cross stage: " + e.getMessage());
        }
    }

    @Override
    public void pushSignalGroupStatus(CrossSignalGroupStatus signalGroupStatus) throws BusinessException {
        logger.info("推送信号组灯态数据: crossId={}, groupCount={}",
                signalGroupStatus.getCrossId(),
                signalGroupStatus.getSignalGroupStatusList() != null ?
                        signalGroupStatus.getSignalGroupStatusList().size() : 0);

        try {
            Message pushMessage = createPushMessage(signalGroupStatus);
            sendToAllSubscribers(pushMessage, "CrossSignalGroupStatus");

            logger.debug("信号组灯态数据推送完成: crossId={}", signalGroupStatus.getCrossId());

        } catch (Exception e) {
            logger.error("推送信号组灯态数据失败: " + e.getMessage(), e);
            throw new BusinessException("Failed to push signal group status: " + e.getMessage());
        }
    }

    @Override
    public void pushStageTrafficData(StageTrafficData stageTrafficData) throws BusinessException {
        logger.info("推送阶段交通流数据: crossId={}, stageNo={}, startTime={}, endTime={}",
                stageTrafficData.getCrossId(), stageTrafficData.getStageNo(),
                stageTrafficData.getStartTime(), stageTrafficData.getEndTime());

        try {
            Message pushMessage = createPushMessage(stageTrafficData);
            sendToAllSubscribers(pushMessage, "StageTrafficData");

            logger.debug("阶段交通流数据推送完成: crossId={}", stageTrafficData.getCrossId());

        } catch (Exception e) {
            logger.error("推送阶段交通流数据失败: " + e.getMessage(), e);
            throw new BusinessException("Failed to push stage traffic data: " + e.getMessage());
        }
    }

    @Override
    public void pushBatch(List<? extends Object> dataList, String dataType) throws BusinessException {
        if (dataList == null || dataList.isEmpty()) {
            logger.warn("批量推送数据列表为空: dataType={}", dataType);
            return;
        }

        logger.info("开始批量推送数据: dataType={}, count={}", dataType, dataList.size());

        int successCount = 0;
        int failCount = 0;

        for (Object data : dataList) {
            try {
                // 根据数据类型调用相应的推送方法
                pushSingleData(data, dataType);
                successCount++;

            } catch (Exception e) {
                failCount++;
                logger.error("批量推送中单个数据失败: dataType={}, error={}", dataType, e.getMessage());
                // 继续处理其他数据，不中断整个批量操作
            }
        }

        logger.info("批量推送完成: dataType={}, total={}, success={}, failed={}",
                dataType, dataList.size(), successCount, failCount);

        if (failCount > 0) {
            throw new BusinessException(String.format(
                    "Batch push partially failed: %d/%d failed for dataType=%s",
                    failCount, dataList.size(), dataType));
        }
    }

    /**
     * 推送单个数据对象
     */
    private void pushSingleData(Object data, String dataType) throws BusinessException {
        switch (dataType) {
            case "SignalControllerError":
                if (data instanceof SignalControllerError) {
                    pushSignalControllerError((SignalControllerError) data);
                }
                break;
            case "CrossModePlan":
                if (data instanceof CrossModePlan) {
                    pushCrossModePlan((CrossModePlan) data);
                }
                break;
            case "CrossTrafficData":
                if (data instanceof CrossTrafficData) {
                    pushCrossTrafficData((CrossTrafficData) data);
                }
                break;
            case "CrossCycle":
                if (data instanceof CrossCycle) {
                    pushCrossCycle((CrossCycle) data);
                }
                break;
            case "CrossStage":
                if (data instanceof CrossStage) {
                    pushCrossStage((CrossStage) data);
                }
                break;
            case "CrossSignalGroupStatus":
                if (data instanceof CrossSignalGroupStatus) {
                    pushSignalGroupStatus((CrossSignalGroupStatus) data);
                }
                break;
            case "StageTrafficData":
                if (data instanceof StageTrafficData) {
                    pushStageTrafficData((StageTrafficData) data);
                }
                break;
            default:
                throw new BusinessException("Unsupported data type for push: " + dataType);
        }
    }

    @Override
    public boolean isServiceAvailable() {
        // 简化实现：检查基本状态
        try {
            return activeConnections.size() >= 0; // 基本可用性检查
        } catch (Exception e) {
            logger.error("检查推送服务状态失败", e);
            return false;
        }
    }

    @Override
    public PushStatistics getPushStatistics() {
        return new PushStatistics(
                totalPushCount.get(),
                successPushCount.get(),
                failedPushCount.get(),
                lastPushTime,
                activeConnections.size()
        );
    }

    /**
     * 创建推送消息
     */
    private Message createPushMessage(Object data) {
        // 使用MessageBuilder创建推送消息
        return MessageBuilder.create()
                .push()                    // 推送类型消息
                .fromTicp()               // 从TICP系统发送
                .toUtcs()                 // 发送给UTCS系统
                .notify(data)             // Notify操作，携带数据
                .build();
    }

    /**
     * 向所有订阅者发送消息
     */
    private void sendToAllSubscribers(Message pushMessage, String dataType) {
        totalPushCount.incrementAndGet();
        lastPushTime = System.currentTimeMillis();

        try {
            // 获取订阅了该数据类型的客户端
            List<String> subscribers = getSubscribersForDataType(dataType);

            if (subscribers.isEmpty()) {
                logger.debug("没有找到订阅 {} 数据的客户端", dataType);
                return;
            }

            logger.debug("向 {} 个客户端推送 {} 数据", subscribers.size(), dataType);

            // 向每个订阅者发送消息
            for (String clientId : subscribers) {
                try {
                    sendToClient(clientId, pushMessage);
                    successPushCount.incrementAndGet();

                } catch (Exception e) {
                    failedPushCount.incrementAndGet();
                    logger.error("向客户端 {} 推送数据失败: {}", clientId, e.getMessage());
                }
            }

        } catch (Exception e) {
            failedPushCount.incrementAndGet();
            logger.error("推送消息处理失败: " + e.getMessage(), e);
            throw new RuntimeException("Failed to send push message", e);
        }
    }

    /**
     * 获取订阅指定数据类型的客户端列表
     * 实际实现中应该从订阅管理器获取
     */
    private List<String> getSubscribersForDataType(String dataType) {
        // 简化实现：返回所有活跃连接的客户端ID
        // 实际实现应该根据订阅信息进行过滤
        return activeConnections.keySet().stream().collect(Collectors.toList());
    }

    /**
     * 向指定客户端发送消息
     */
    private void sendToClient(String clientId, Message message) {
        // 实际实现中应该通过网络连接管理器发送
        // connectionManager.sendMessage(clientId, message);

        // 模拟发送逻辑
        logger.debug("模拟向客户端 {} 发送推送消息: seq={}", clientId, message.getSeq());

        // 更新客户端连接的最后活跃时间
        ClientConnection connection = activeConnections.get(clientId);
        if (connection != null) {
            connection.updateLastActiveTime();
        }
    }

    /**
     * 添加客户端连接（用于测试和管理）
     */
    public void addClientConnection(String clientId, String clientInfo) {
        activeConnections.put(clientId, new ClientConnection(clientId, clientInfo));
        logger.info("添加客户端连接: clientId={}, info={}", clientId, clientInfo);
    }

    /**
     * 移除客户端连接
     */
    public void removeClientConnection(String clientId) {
        ClientConnection removed = activeConnections.remove(clientId);
        if (removed != null) {
            logger.info("移除客户端连接: clientId={}", clientId);
        }
    }

    /**
     * 获取活跃连接数
     */
    public int getActiveConnectionCount() {
        return activeConnections.size();
    }

    /**
     * 重置统计信息
     */
    public void resetStatistics() {
        totalPushCount.set(0);
        successPushCount.set(0);
        failedPushCount.set(0);
        lastPushTime = System.currentTimeMillis();
        logger.info("推送统计信息已重置");
    }

    /**
     * 客户端连接信息
     */
    private static class ClientConnection {
        private final String clientId;
        private final String clientInfo;
        private final long createTime;
        private volatile long lastActiveTime;

        public ClientConnection(String clientId, String clientInfo) {
            this.clientId = clientId;
            this.clientInfo = clientInfo;
            this.createTime = System.currentTimeMillis();
            this.lastActiveTime = System.currentTimeMillis();
        }

        public void updateLastActiveTime() {
            this.lastActiveTime = System.currentTimeMillis();
        }

        public String getClientId() { return clientId; }
        public String getClientInfo() { return clientInfo; }
        public long getCreateTime() { return createTime; }
        public long getLastActiveTime() { return lastActiveTime; }
    }
}
