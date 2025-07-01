package com.traffic.server.service;

import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.runtime.*;
import com.traffic.gat1049.protocol.model.system.SysState;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * 服务端订阅服务（反向订阅）
 * 服务端向客户端发送订阅请求，然后接收客户端推送的数据
 */
//@Service
public class ServerSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(ServerSubscriptionService.class);

    // 已发送的订阅请求记录 clientId -> 订阅列表
    private final ConcurrentHashMap<String, List<SdoMsgEntity>> sentSubscriptions = new ConcurrentHashMap<>();

    // 接收到的推送数据处理器
    private final List<PushDataHandler> dataHandlers = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void initialize() {
        // 注册默认的数据处理器
        registerDefaultDataHandlers();
        logger.info("服务端订阅服务已初始化");
    }

    /**
     * 向客户端发送订阅请求
     * @param clientId 客户端标识
     * @param objNames 要订阅的对象名称列表
     * @return 订阅请求消息
     */
    public Message sendSubscriptionRequest(String clientId, String... objNames) {
        return sendSubscriptionRequest(clientId, Arrays.asList(objNames));
    }

    /**
     * 向客户端发送订阅请求
     * @param clientId 客户端标识
     * @param objNames 要订阅的对象名称列表
     * @return 订阅请求消息
     */
    public Message sendSubscriptionRequest(String clientId, List<String> objNames) {
        List<SdoMsgEntity> subscriptions = new CopyOnWriteArrayList<>();

        for (String objName : objNames) {
            SdoMsgEntity subscription = new SdoMsgEntity(
                    GatConstants.MessageType.PUSH,
                    GatConstants.Operation.NOTIFY,
                    objName
            );
            subscriptions.add(subscription);
        }

        // 记录已发送的订阅
        sentSubscriptions.put(clientId, subscriptions);

        // 创建订阅请求消息（服务端作为请求方）
        Message subscribeRequest = MessageBuilder.create()
                .request()
                .fromTicp()  // 服务端发送
                .toUtcs()    // 发送给客户端
                .subscribe(subscriptions.get(0))  // 先发送第一个订阅
                .build();

        logger.info("向客户端发送订阅请求: clientId={}, objNames={}", clientId, objNames);

        // 如果有多个订阅，需要发送多个请求
        if (subscriptions.size() > 1) {
            logger.info("需要发送 {} 个订阅请求", subscriptions.size());
        }

        return subscribeRequest;
    }

    /**
     * 订阅常用的交通信号数据
     */
    public List<Message> subscribeTrafficSignalData(String clientId) {
        List<String> commonObjects = Arrays.asList(
                GatConstants.ObjectName.CROSS_STATE,
                GatConstants.ObjectName.CROSS_CYCLE,
                GatConstants.ObjectName.CROSS_STAGE,
                GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS,
                GatConstants.ObjectName.CROSS_TRAFFIC_DATA,
                GatConstants.ObjectName.SIGNAL_CONTROLLER_ERROR,
                GatConstants.ObjectName.CROSS_MODE_PLAN
        );

        List<Message> requests = new CopyOnWriteArrayList<>();

        for (String objName : commonObjects) {
            SdoMsgEntity subscription = new SdoMsgEntity(
                    GatConstants.MessageType.PUSH,
                    GatConstants.Operation.NOTIFY,
                    objName
            );

            Message request = MessageBuilder.create()
                    .request()
                    .fromTicp()
                    .toUtcs()
                    .subscribe(subscription)
                    .build();

            requests.add(request);
        }

        // 记录订阅
        List<SdoMsgEntity> subscriptions = new CopyOnWriteArrayList<>();
        for (String objName : commonObjects) {
            subscriptions.add(new SdoMsgEntity(
                    GatConstants.MessageType.PUSH,
                    GatConstants.Operation.NOTIFY,
                    objName
            ));
        }
        sentSubscriptions.put(clientId, subscriptions);

        logger.info("向客户端订阅交通信号数据: clientId={}, objects={}", clientId, commonObjects.size());
        return requests;
    }

    /**
     * 取消对客户端的订阅
     */
    public Message sendUnsubscribeRequest(String clientId, String objName) {
        SdoMsgEntity unsubscription = new SdoMsgEntity(
                GatConstants.MessageType.PUSH,
                GatConstants.Operation.NOTIFY,
                objName
        );

        Message unsubscribeRequest = MessageBuilder.create()
                .request()
                .fromTicp()
                .toUtcs()
                .unsubscribe(unsubscription)
                .build();

        // 更新本地记录
        List<SdoMsgEntity> subscriptions = sentSubscriptions.get(clientId);
        if (subscriptions != null) {
            subscriptions.removeIf(sub -> objName.equals(sub.getObjName()));
        }

        logger.info("向客户端发送取消订阅请求: clientId={}, objName={}", clientId, objName);
        return unsubscribeRequest;
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
        logger.info("接收到客户端推送数据: type={}, seq={}", dataType, pushMessage.getSeq());

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

    private void handleSystemState(SysState sysState, Message message) {
        logger.info("处理系统状态推送: state={}, from={}",
                sysState.getValue(), message.getFrom().getSys());

        // 这里可以更新数据库、触发告警、通知监控系统等
        // 例如：systemStateService.updateState(sysState);
    }

    private void handleCrossState(CrossState crossState, Message message) {
        logger.info("处理路口状态推送: crossId={}, state={}, from={}",
                crossState.getCrossId(), crossState.getValue(), message.getFrom().getSys());

        // 处理路口状态变更
        // 例如：crossStateService.updateState(crossState);

        // 如果路口离线，可能需要触发告警
        if (!crossState.getValue().equals(com.traffic.gat1049.model.enums.SystemState.ONLINE)) {
            logger.warn("路口状态异常: crossId={}, state={}",
                    crossState.getCrossId(), crossState.getValue());
        }
    }

    private void handleCrossCycle(CrossCycle cycle, Message message) {
        logger.debug("处理路口周期推送: crossId={}, startTime={}, cycleLen={}",
                cycle.getCrossId(), cycle.getStartTime(), cycle.getLastCycleLen());

        // 处理周期数据，可用于性能分析、优化等
        // 例如：trafficAnalysisService.processCycleData(cycle);
    }

    private void handleCrossStage(CrossStage stage, Message message) {
        logger.debug("处理路口阶段推送: crossId={}, stageNo={}, remainTime={}",
                stage.getCrossId(), stage.getCurStageNo(), stage.getCurStageLen());

        // 处理阶段数据，可用于实时监控
        // 例如：realtimeMonitorService.updateStage(stage);
    }

    private void handleSignalGroupStatus(CrossSignalGroupStatus status, Message message) {
        logger.debug("处理信号组状态推送: crossId={}, groups={}",
                status.getCrossId(), status.getSignalGroupStatusList().size());

        // 处理信号组灯态数据
        // 例如：signalGroupService.updateStatus(status);
    }

    private void handleTrafficData(CrossTrafficData trafficData, Message message) {
        logger.info("处理交通流数据推送: crossId={}, endTime={}, lanes={}",
                trafficData.getCrossId(), trafficData.getEndTime(),
                trafficData.getDataList().size());

        // 保存交通流数据，用于分析和决策
        // 例如：trafficDataService.saveData(trafficData);

        // 进行交通流分析
        // 例如：trafficAnalysisService.analyzeTrafficFlow(trafficData);
    }

    private void handleSignalControllerError(SignalControllerError error, Message message) {
        logger.error("处理信号机故障推送: controllerId={}, errorType={}, desc={}",
                error.getSignalControllerId(), error.getErrorType(), error.getErrorDesc());

        // 处理故障信息，触发告警和修复流程
        // 例如：alarmService.triggerAlarm(error);
        // 例如：maintenanceService.createWorkOrder(error);
    }

    private void handleCrossModePlan(CrossModePlan modePlan, Message message) {
        logger.info("处理控制方式变更推送: crossId={}, mode={}, planNo={}",
                modePlan.getCrossId(), modePlan.getControlMode(), modePlan.getPlanNo());

        // 记录控制方式变更历史
        // 例如：controlHistoryService.recordModeChange(modePlan);
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
     * 注册默认的数据处理器
     */
    private void registerDefaultDataHandlers() {
        // 可以注册一些默认的处理器
        registerDataHandler(new DefaultTrafficDataHandler());
        registerDataHandler(new DefaultAlarmHandler());
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

                // 这里可以实现默认的数据处理逻辑
                // 比如数据验证、格式转换、存储等
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

                // 这里可以实现默认的告警处理逻辑
                // 比如发送邮件、短信、推送通知等
            }
        }
    }
}