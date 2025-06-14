package com.traffic.server.service;

import com.traffic.gat1049.application.connection.ConnectionManager;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.runtime.*;
import com.traffic.gat1049.protocol.model.system.SysState;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.protocol.codec.MessageCodec;
import com.traffic.server.network.client.ServerToClientSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 增强的服务端订阅服务（带实际发送功能）
 * 服务端向客户端发送订阅请求，然后接收客户端推送的数据
 */
@Service
public class EnhancedServerSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedServerSubscriptionService.class);

    // 已发送的订阅请求记录 clientId -> 订阅列表
    private final ConcurrentHashMap<String, List<SdoMsgEntity>> sentSubscriptions = new ConcurrentHashMap<>();

    // 接收到的推送数据处理器
    private final List<PushDataHandler> dataHandlers = new CopyOnWriteArrayList<>();

    // 消息编解码器
    private final MessageCodec messageCodec = MessageCodec.getInstance();

    // 网络发送器（需要注入实际的网络连接管理器）
    @Autowired(required = false)
    private ServerToClientSender clientSender;

    @Autowired
    private ConnectionManager connectionManager;

    @PostConstruct
    public void initialize() {
        // 注册默认的数据处理器
        registerDefaultDataHandlers();
        logger.info("增强版服务端订阅服务已初始化");
    }

    /**
     * 向客户端发送订阅请求（带实际网络发送）
     * @param clientId 客户端标识
     * @param objNames 要订阅的对象名称列表
     * @return 发送结果
     */
    public SubscriptionResult sendSubscriptionRequest(String clientId, String... objNames) {
        return sendSubscriptionRequest(clientId, Arrays.asList(objNames));
    }

    /**
     * 向客户端发送订阅请求（带实际网络发送）
     * @param clientId 客户端标识
     * @param objNames 要订阅的对象名称列表
     * @return 发送结果
     */
    public SubscriptionResult sendSubscriptionRequest(String clientId, List<String> objNames) {
        SubscriptionResult result = new SubscriptionResult();
        result.setClientId(clientId);
        result.setRequestedObjects(objNames);

        List<SdoMsgEntity> subscriptions = new CopyOnWriteArrayList<>();
        List<Message> sentMessages = new CopyOnWriteArrayList<>();
        String token = connectionManager.getToken(clientId);
        for (String objName : objNames) {
            try {
                // 创建订阅实体
                SdoMsgEntity subscription = new SdoMsgEntity(
                        GatConstants.MessageType.PUSH,
                        GatConstants.Operation.NOTIFY,
                        objName
                );
                subscriptions.add(subscription);

                // 创建订阅请求消息
                Message subscribeRequest = MessageBuilder.create()
                        .request()
                        .fromTicp()  // 服务端发送
                        .toUtcs()    // 发送给客户端
                        .token(token)
                        .subscribe(subscription)
                        .build();

                // 实际发送消息
                boolean sendSuccess = sendMessageToClient(clientId, subscribeRequest);

                if (sendSuccess) {
                    sentMessages.add(subscribeRequest);
                    result.addSuccessObject(objName, subscribeRequest.getSeq());
                    logger.info("✓ 向客户端发送订阅请求成功: clientId={}, objName={}, seq={}",
                            clientId, objName, subscribeRequest.getSeq());
                } else {
                    result.addFailedObject(objName, "网络发送失败");
                    logger.error("✗ 向客户端发送订阅请求失败: clientId={}, objName={}", clientId, objName);
                }

            } catch (Exception e) {
                result.addFailedObject(objName, e.getMessage());
                logger.error("✗ 创建订阅请求失败: clientId={}, objName={}", clientId, objName, e);
            }
        }

        // 记录成功发送的订阅
        if (!subscriptions.isEmpty()) {
            sentSubscriptions.put(clientId, subscriptions);
        }

        result.setSentMessages(sentMessages);
        result.setTotalRequested(objNames.size());
        result.setSuccessCount(result.getSuccessObjects().size());
        result.setFailedCount(result.getFailedObjects().size());

        logger.info("订阅请求发送完成: clientId={}, 总数={}, 成功={}, 失败={}",
                clientId, result.getTotalRequested(), result.getSuccessCount(), result.getFailedCount());

        return result;
    }

    /**
     * 实际发送消息到客户端
     */
    private boolean sendMessageToClient(String clientId, Message message) {
        try {
            if (clientSender == null) {
                logger.warn("客户端发送器未配置，使用模拟发送");
                return simulateSendMessage(clientId, message);
            }

            // 编码消息为XML
            String xmlMessage = messageCodec.encode(message);

            // 通过网络发送
            CompletableFuture<Boolean> sendFuture = clientSender.sendToClient(clientId, xmlMessage);

            // 等待发送结果（带超时）
            return sendFuture.get(10, TimeUnit.SECONDS);

        } catch (Exception e) {
            logger.error("向客户端发送消息失败: clientId={}, seq={}", clientId, message.getSeq(), e);
            return false;
        }
    }

    /**
     * 模拟发送消息（用于测试或无网络连接时）
     */
    private boolean simulateSendMessage(String clientId, Message message) {
        try {
            String xmlMessage = messageCodec.encode(message);
            logger.debug("模拟发送消息到客户端: clientId={}, messageLength={}", clientId, xmlMessage.length());

            // 模拟网络延迟
            Thread.sleep(10);

            // 模拟95%成功率
            return Math.random() > 0.05;

        } catch (Exception e) {
            logger.error("模拟发送消息失败", e);
            return false;
        }
    }

    /**
     * 订阅常用的交通信号数据（带实际发送）
     */
    public SubscriptionResult subscribeTrafficSignalData(String clientId) {
        List<String> commonObjects = Arrays.asList(
                GatConstants.ObjectName.CROSS_STATE,
                GatConstants.ObjectName.CROSS_CYCLE,
                GatConstants.ObjectName.CROSS_STAGE,
                GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS,
                GatConstants.ObjectName.CROSS_TRAFFIC_DATA,
                GatConstants.ObjectName.SIGNAL_CONTROLLER_ERROR,
                GatConstants.ObjectName.CROSS_MODE_PLAN
        );

        return sendSubscriptionRequest(clientId, commonObjects);
    }

    /**
     * 取消对客户端的订阅（带实际发送）
     */
    public SubscriptionResult sendUnsubscribeRequest(String clientId, String objName) {
        SubscriptionResult result = new SubscriptionResult();
        result.setClientId(clientId);
        result.setRequestedObjects(Arrays.asList(objName));

        String token = connectionManager.getToken(clientId);
        try {
            SdoMsgEntity unsubscription = new SdoMsgEntity(
                    GatConstants.MessageType.PUSH,
                    GatConstants.Operation.NOTIFY,
                    objName
            );

            Message unsubscribeRequest = MessageBuilder.create()
                    .request()
                    .fromTicp()
                    .toUtcs()
                    .token(token)
                    .unsubscribe(unsubscription)
                    .build();

            boolean sendSuccess = sendMessageToClient(clientId, unsubscribeRequest);

            if (sendSuccess) {
                result.addSuccessObject(objName, unsubscribeRequest.getSeq());

                // 更新本地记录
                List<SdoMsgEntity> subscriptions = sentSubscriptions.get(clientId);
                if (subscriptions != null) {
                    subscriptions.removeIf(sub -> objName.equals(sub.getObjName()));
                }

                logger.info("✓ 向客户端发送取消订阅请求成功: clientId={}, objName={}", clientId, objName);
            } else {
                result.addFailedObject(objName, "网络发送失败");
                logger.error("✗ 向客户端发送取消订阅请求失败: clientId={}, objName={}", clientId, objName);
            }

        } catch (Exception e) {
            result.addFailedObject(objName, e.getMessage());
            logger.error("✗ 创建取消订阅请求失败: clientId={}, objName={}", clientId, objName, e);
        }

        result.setTotalRequested(1);
        result.setSuccessCount(result.getSuccessObjects().size());
        result.setFailedCount(result.getFailedObjects().size());

        return result;
    }

    /**
     * 处理从客户端接收到的推送数据
     */
    public void handlePushMessage(Message pushMessage) {
        if (!GatConstants.MessageType.PUSH.equals(pushMessage.getType())) {
            logger.warn("接收到非推送类型消息: {}", pushMessage.getType());
            return;
        }

        String operationName = ProtocolUtils.getOperationName(pushMessage);
        if (!GatConstants.Operation.NOTIFY.equals(operationName)) {
            logger.warn("接收到非Notify操作的推送消息: {}", operationName);
            return;
        }

        Object data = ProtocolUtils.getOperationData(pushMessage);
        if (data == null) {
            logger.warn("推送消息中没有数据对象");
            return;
        }

        // 分发给相应的处理器
        String dataType = data.getClass().getSimpleName();
        logger.info("✓ 接收到客户端推送数据: type={}, seq={}", dataType, pushMessage.getSeq());

        // 处理不同类型的推送数据
        handlePushData(data, pushMessage);

        // 通知所有注册的处理器
        for (PushDataHandler handler : dataHandlers) {
            try {
                handler.onPushData(data, pushMessage);
            } catch (Exception e) {
                logger.error("数据处理器执行失败: " + handler.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * 处理推送数据的具体逻辑
     */
    private void handlePushData(Object data, Message message) {
        if (data instanceof SysState) {
            handleSystemState((SysState) data, message);
        } else if (data instanceof CrossState) {
            handleCrossState((CrossState) data, message);
        } else if (data instanceof CrossCycle) {
            handleCrossCycle((CrossCycle) data, message);
        } else if (data instanceof CrossStage) {
            handleCrossStage((CrossStage) data, message);
        } else if (data instanceof CrossSignalGroupStatus) {
            handleSignalGroupStatus((CrossSignalGroupStatus) data, message);
        } else if (data instanceof CrossTrafficData) {
            handleTrafficData((CrossTrafficData) data, message);
        } else if (data instanceof SignalControllerError) {
            handleSignalControllerError((SignalControllerError) data, message);
        } else if (data instanceof CrossModePlan) {
            handleCrossModePlan((CrossModePlan) data, message);
        } else {
            logger.warn("未知的推送数据类型: {}", data.getClass().getSimpleName());
        }
    }

    // 数据处理方法（简化版本）
    private void handleSystemState(SysState sysState, Message message) {
        logger.info("✓ 处理系统状态推送: state={}, from={}",
                sysState.getValue(), message.getFrom().getSys());
    }

    private void handleCrossState(CrossState crossState, Message message) {
        logger.info("✓ 处理路口状态推送: crossId={}, state={}",
                crossState.getCrossId(), crossState.getValue());
    }

    private void handleCrossCycle(CrossCycle cycle, Message message) {
        logger.debug("✓ 处理路口周期推送: crossId={}, cycleLen={}",
                cycle.getCrossId(), cycle.getLastCycleLen());
    }

    private void handleCrossStage(CrossStage stage, Message message) {
        logger.debug("✓ 处理路口阶段推送: crossId={}, stageNo={}",
                stage.getCrossId(), stage.getCurStageNo());
    }

    private void handleSignalGroupStatus(CrossSignalGroupStatus status, Message message) {
        logger.debug("✓ 处理信号组状态推送: crossId={}, groups={}",
                status.getCrossId(), status.getSignalGroupStatusList().size());
    }

    private void handleTrafficData(CrossTrafficData trafficData, Message message) {
        logger.info("✓ 处理交通流数据推送: crossId={}, lanes={}",
                trafficData.getCrossId(), trafficData.getDataList().size());
    }

    private void handleSignalControllerError(SignalControllerError error, Message message) {
        logger.error("✓ 处理信号机故障推送: controllerId={}, errorType={}",
                error.getSignalControllerId(), error.getErrorType());
    }

    private void handleCrossModePlan(CrossModePlan modePlan, Message message) {
        logger.info("✓ 处理控制方式变更推送: crossId={}, mode={}",
                modePlan.getCrossId(), modePlan.getControlMode());
    }

    /**
     * 注册推送数据处理器
     */
    public void registerDataHandler(PushDataHandler handler) {
        dataHandlers.add(handler);
        logger.info("注册推送数据处理器: {}", handler.getClass().getSimpleName());
    }

    /**
     * 获取已发送的订阅列表
     */
    public List<SdoMsgEntity> getSentSubscriptions(String clientId) {
        return sentSubscriptions.getOrDefault(clientId, new CopyOnWriteArrayList<>());
    }

    /**
     * 获取订阅统计信息
     */
    public SubscriptionStats getSubscriptionStats() {
        SubscriptionStats stats = new SubscriptionStats();
        stats.setTotalClients(sentSubscriptions.size());
        stats.setTotalSubscriptions(sentSubscriptions.values().stream()
                .mapToInt(List::size).sum());
        return stats;
    }

    /**
     * 注册默认的数据处理器
     */
    private void registerDefaultDataHandlers() {
        registerDataHandler(new DefaultTrafficDataHandler());
        registerDataHandler(new DefaultAlarmHandler());
    }

    /**
     * 订阅结果类
     */
    public static class SubscriptionResult {
        private String clientId;
        private List<String> requestedObjects;
        private List<Message> sentMessages = new CopyOnWriteArrayList<>();
        private ConcurrentHashMap<String, String> successObjects = new ConcurrentHashMap<>();
        private ConcurrentHashMap<String, String> failedObjects = new ConcurrentHashMap<>();
        private int totalRequested;
        private int successCount;
        private int failedCount;

        // Getters and Setters
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }

        public List<String> getRequestedObjects() { return requestedObjects; }
        public void setRequestedObjects(List<String> requestedObjects) { this.requestedObjects = requestedObjects; }

        public List<Message> getSentMessages() { return sentMessages; }
        public void setSentMessages(List<Message> sentMessages) { this.sentMessages = sentMessages; }

        public ConcurrentHashMap<String, String> getSuccessObjects() { return successObjects; }
        public ConcurrentHashMap<String, String> getFailedObjects() { return failedObjects; }

        public int getTotalRequested() { return totalRequested; }
        public void setTotalRequested(int totalRequested) { this.totalRequested = totalRequested; }

        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }

        public int getFailedCount() { return failedCount; }
        public void setFailedCount(int failedCount) { this.failedCount = failedCount; }

        public void addSuccessObject(String objName, String seq) {
            successObjects.put(objName, seq);
        }

        public void addFailedObject(String objName, String reason) {
            failedObjects.put(objName, reason);
        }

        public boolean isAllSuccess() {
            return failedCount == 0 && successCount > 0;
        }

        public double getSuccessRate() {
            return totalRequested > 0 ? (double) successCount / totalRequested : 0.0;
        }
    }

    /**
     * 订阅统计信息类
     */
    public static class SubscriptionStats {
        private int totalClients;
        private int totalSubscriptions;
        private long lastUpdateTime = System.currentTimeMillis();

        public int getTotalClients() { return totalClients; }
        public void setTotalClients(int totalClients) { this.totalClients = totalClients; }

        public int getTotalSubscriptions() { return totalSubscriptions; }
        public void setTotalSubscriptions(int totalSubscriptions) { this.totalSubscriptions = totalSubscriptions; }

        public long getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    }

    /**
     * 推送数据处理器接口
     */
    public interface PushDataHandler {
        void onPushData(Object data, Message message);
    }

    /**
     * 默认交通数据处理器
     */
    private static class DefaultTrafficDataHandler implements PushDataHandler {
        private static final Logger logger = LoggerFactory.getLogger(DefaultTrafficDataHandler.class);

        @Override
        public void onPushData(Object data, Message message) {
            if (data instanceof CrossTrafficData) {
                CrossTrafficData trafficData = (CrossTrafficData) data;
                logger.info("默认交通数据处理: crossId={}, dataCount={}",
                        trafficData.getCrossId(), trafficData.getDataList().size());
            }
        }
    }

    /**
     * 默认告警处理器
     */
    private static class DefaultAlarmHandler implements PushDataHandler {
        private static final Logger logger = LoggerFactory.getLogger(DefaultAlarmHandler.class);

        @Override
        public void onPushData(Object data, Message message) {
            if (data instanceof SignalControllerError) {
                SignalControllerError error = (SignalControllerError) data;
                logger.warn("默认告警处理: controllerId={}, errorType={}",
                        error.getSignalControllerId(), error.getErrorType());
            }
        }
    }
}