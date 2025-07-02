package com.traffic.client.service;

import com.traffic.client.network.client.GatTcpClient;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.model.enums.ControllerErrorType;
import com.traffic.gat1049.model.enums.LampStatus;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.builder.PushBuilder;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.model.runtime.*;
import com.traffic.gat1049.protocol.model.system.SysState;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;
import com.traffic.gat1049.protocol.model.traffic.*;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.model.enums.SystemState;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.protocol.util.PushHandlingUtils;
import com.traffic.gat1049.protocol.util.ResultHandlingUtils;
import com.traffic.gat1049.service.abstracts.DefaultServiceFactory;
import com.traffic.gat1049.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Random;

/**
 * 客户端数据推送服务
 * 接收服务端的订阅请求，并向服务端推送相应的数据
 */
@Service
public class ClientDataPushService {

    private static final Logger logger = LoggerFactory.getLogger(ClientDataPushService.class);

    // 接收到的订阅请求记录 serverId -> 订阅列表
    private final ConcurrentHashMap<String, List<SdoMsgEntity>> receivedSubscriptions = new ConcurrentHashMap<>();

    // 数据推送执行器
    private final ScheduledExecutorService pushExecutor = Executors.newScheduledThreadPool(5);

    // 注入的GatTcpClient实例
    private final GatTcpClient gatTcpClient;
    private final DefaultServiceFactory serviceFactory;

    // 模拟数据生成器
    private final Random random = new Random();
    private final String crossId = "CROSS001"; // 模拟路口ID
    private final String controllerId = "SC001"; // 模拟信号机ID

    // 构造函数注入 GatTcpClient
    public ClientDataPushService(GatTcpClient gatTcpClient, DefaultServiceFactory serviceFactory) {
        this.gatTcpClient = gatTcpClient;
        this.serviceFactory = serviceFactory;
    }

    @PostConstruct
    public void initialize() {
        logger.info("客户端数据推送服务已初始化");
    }

    /**
     * 处理来自服务端的订阅请求
     * @param subscribeRequest 服务端发送的订阅请求
     * @return 订阅响应消息
     */
    public Message handleSubscriptionRequest(Message subscribeRequest) {
        if (!GatConstants.MessageType.REQUEST.equals(subscribeRequest.getType())) {
            logger.warn("接收到非请求类型的订阅消息: {}", subscribeRequest.getType());
            return createErrorResponse(subscribeRequest, "INVALID_MESSAGE_TYPE", "消息类型必须为REQUEST");
        }

        String operationName = ProtocolUtils.getOperationName(subscribeRequest);
        if (!GatConstants.Operation.SUBSCRIBE.equals(operationName)) {
            logger.warn("接收到非订阅操作的请求: {}", operationName);
            return createErrorResponse(subscribeRequest, "INVALID_OPERATION", "操作类型必须为Subscribe");
        }

        Object data = ProtocolUtils.getOperationData(subscribeRequest);
        if (!(data instanceof SdoMsgEntity)) {
            logger.warn("订阅请求数据格式错误: {}", data.getClass().getSimpleName());
            return createErrorResponse(subscribeRequest, "INVALID_DATA_FORMAT", "订阅数据格式错误");
        }

        SdoMsgEntity subscription = (SdoMsgEntity) data;

        // 验证订阅参数
        if (!isValidSubscription(subscription)) {
            return createErrorResponse(subscribeRequest, "INVALID_SUBSCRIPTION", "订阅参数无效");
        }

        // 记录订阅请求
        String serverId = getServerIdFromMessage(subscribeRequest);
        List<SdoMsgEntity> subscriptions = receivedSubscriptions.computeIfAbsent(
                serverId, k -> new CopyOnWriteArrayList<>());

        // 检查是否已存在相同订阅
        boolean exists = subscriptions.stream().anyMatch(existing ->
                isSameSubscription(existing, subscription));

        if (!exists) {
            subscriptions.add(subscription);
            logger.info("接受服务端订阅请求: serverId={}, objName={}", serverId, subscription.getObjName());

            // 启动对应的数据推送
            startDataPush(serverId, subscription);
        } else {
            logger.debug("订阅已存在: serverId={}, objName={}", serverId, subscription.getObjName());
        }

        // 创建成功响应
        return MessageBuilder.create()
                .response()
                .fromUtcs()  // 客户端响应
                .toTicp()    // 发送给服务端
                .seq(subscribeRequest.getSeq())
                .operation(GatConstants.Operation.SUBSCRIBE, subscription)
                .build();
    }

    /**
     * 处理来自服务端的取消订阅请求
     */
    public Message handleUnsubscriptionRequest(Message unsubscribeRequest) {
        Object data = ProtocolUtils.getOperationData(unsubscribeRequest);
        if (!(data instanceof SdoMsgEntity)) {
            return createErrorResponse(unsubscribeRequest, "INVALID_DATA_FORMAT", "取消订阅数据格式错误");
        }

        SdoMsgEntity unsubscription = (SdoMsgEntity) data;
        String serverId = getServerIdFromMessage(unsubscribeRequest);

        List<SdoMsgEntity> subscriptions = receivedSubscriptions.get(serverId);
        if (subscriptions != null) {
            boolean removed = subscriptions.removeIf(existing ->
                    isSameSubscription(existing, unsubscription));

            if (removed) {
                logger.info("取消订阅: serverId={}, objName={}", serverId, unsubscription.getObjName());
                // 停止相应的数据推送
                stopDataPush(serverId, unsubscription);
            }
        }

        return MessageBuilder.create()
                .response()
                .fromUtcs()
                .toTicp()
                .seq(unsubscribeRequest.getSeq())
                .operation(GatConstants.Operation.UNSUBSCRIBE, unsubscription)
                .build();
    }

    /**
     * 启动数据推送
     */
    private void startDataPush(String serverId, SdoMsgEntity subscription) {
        String objName = subscription.getObjName();

        switch (objName) {
            case GatConstants.ObjectName.SYS_STATE:
                startSystemStatePush(serverId);
                break;
            case GatConstants.ObjectName.CROSS_STATE:
                startCrossStatePush(serverId);
                break;
            case GatConstants.ObjectName.CROSS_CYCLE:
                startCrossCyclePush(serverId);
                break;
            case GatConstants.ObjectName.CROSS_STAGE:
                startCrossStagePush(serverId);
                break;
            case GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS:
                startSignalGroupStatusPush(serverId);
                break;
            case GatConstants.ObjectName.CROSS_TRAFFIC_DATA:
                startTrafficDataPush(serverId);
                break;
            case GatConstants.ObjectName.SIGNAL_CONTROLLER_ERROR:
                startErrorReportPush(serverId);
                break;
            case GatConstants.ObjectName.CROSS_MODE_PLAN:
                startModePlanPush(serverId);
                break;
            default:
                logger.warn("不支持的订阅对象: {}", objName);
        }
    }

    /**
     * 从Common模块获取真实数据
     * 参照TSC查询CrossSignalGroupStatus的方式
     */
    private Object getRealDataFromCommon(String objName) {
        try {
            switch (objName) {
                case "CrossSignalGroupStatus":
                    // 参照TSCCommandHandler.handleCrossSignalGroupStatus的实现
                    SignalGroupService signalGroupService = serviceFactory.getSignalGroupService();
                    return signalGroupService.getAllCrossSignalGroupStatus();

                case "CrossState":
                    // 参照TSCCommandHandler.handleCrossState的实现
                    CrossService crossService = serviceFactory.getCrossService();
                    return crossService.getAllCrossState();

                case "CrossCycle":
                    // 参照TSCCommandHandler.handleCrossCycle的实现
                    TrafficDataService trafficDataService = serviceFactory.getTrafficDataService();
                    return trafficDataService.getAllCrossCycle();

                case "CrossStage":
                    // 参照TSCCommandHandler.handleCrossStage的实现
                    return serviceFactory.getTrafficDataService().getAllCrossStage();

                case "SysState":
                    // 参照TSCCommandHandler.handleSysState的实现
                    SystemService systemService = serviceFactory.getSystemService();
                    return systemService.getSystemState();

                case "SignalControllerError":
                    // 参照TSCCommandHandler.handleSignalControllerError的实现
                    SignalControllerService controllerService = serviceFactory.getSignalControllerService();
                    return controllerService.getAllErrors();

                case "CrossTrafficData":
                    // 参照TSCCommandHandler.handleCrossTrafficData的实现
                    return serviceFactory.getTrafficDataService().findAll();

                case "StageTrafficData":
                    // 需要阶段号，这里使用当前阶段
                    CrossStage currentStage = serviceFactory.getTrafficDataService().getCrossStage(crossId);
                    if (currentStage != null) {
                        return serviceFactory.getTrafficDataService().getAllStageTrafficData(null, null);
                    }
                    break;
                case "CrossCtrlInfo":
                    return serviceFactory.getControlModeService().getAllControlModes();
                case "VarLaneStatus":
                    // 需要车道号，这里获取所有车道状态
                    return serviceFactory.getLaneService().getVarLanes();

                default:
                    logger.warn("不支持的订阅对象类型: {}", objName);
                    return null;
            }
        } catch (BusinessException e) {
            logger.error("从Common模块获取数据失败: objName={}, error={}", objName, e.getMessage());
            return null;
        }

        return null;
    }
    /**
     * 推送系统状态数据
     */
    private void startSystemStatePush(String serverId) {
        pushExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!isSubscriptionActive(serverId, GatConstants.ObjectName.SYS_STATE)) {
                    return;
                }

                Object data = getRealDataFromCommon(GatConstants.ObjectName.SYS_STATE);
                if (data != null) {
                    Message pushMessage = createPushMessageWithIndependentSystem(gatTcpClient.getUsername(), GatTcpClient.tocken, data);
                    sendPushMessage(serverId, pushMessage);
                    logger.debug("推送系统状态到服务端: serverId={}", serverId);
                }

            } catch (Exception e) {
                logger.error("推送系统状态失败", e);
            }
        }, 5, 30, TimeUnit.SECONDS);
    }

    /**
     * 推送路口状态数据
     */
    private void startCrossStatePush(String serverId) {
        pushExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!isSubscriptionActive(serverId, GatConstants.ObjectName.CROSS_STATE)) {
                    return;
                }

                Object data = getRealDataFromCommon(GatConstants.ObjectName.CROSS_STATE);
                if (data != null) {
                    Message pushMessage = createPushMessageWithIndependentSystem(gatTcpClient.getUsername(), GatTcpClient.tocken, data);
                    sendPushMessage(serverId, pushMessage);
                    logger.debug("推送路口状态到服务端: serverId={}, crossId={}", serverId, crossId);
                }
            } catch (Exception e) {
                logger.error("推送路口状态失败", e);
            }
        }, 10, 20, TimeUnit.SECONDS);
    }

    /**
     * 推送路口周期数据
     */
    private void startCrossCyclePush(String serverId) {
        pushExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!isSubscriptionActive(serverId, GatConstants.ObjectName.CROSS_CYCLE)) {
                    return;
                }

                Object data = getRealDataFromCommon(GatConstants.ObjectName.CROSS_CYCLE);
                if (data != null) {
                    Message pushMessage = createPushMessageWithIndependentSystem(gatTcpClient.getUsername(), GatTcpClient.tocken, data);
                    sendPushMessage(serverId, pushMessage);
                    logger.debug("推送路口周期到服务端: serverId={}",
                            serverId);
                }
            } catch (Exception e) {
                logger.error("推送路口周期失败", e);
            }
        }, 15, 60, TimeUnit.SECONDS);
    }

    /**
     * 推送路口阶段数据
     */
    private void startCrossStagePush(String serverId) {
        pushExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!isSubscriptionActive(serverId, GatConstants.ObjectName.CROSS_STAGE)) {
                    return;
                }

                Object data = getRealDataFromCommon(GatConstants.ObjectName.CROSS_STAGE);
                if (data != null) {
                    Message pushMessage = createPushMessageWithIndependentSystem(gatTcpClient.getUsername(), GatTcpClient.tocken, data);
                    sendPushMessage(serverId, pushMessage);
                    logger.debug("推送路口阶段到服务端: serverId={}",
                            serverId);
                }

            } catch (Exception e) {
                logger.error("推送路口阶段失败", e);
            }
        }, 20, 10, TimeUnit.SECONDS);
    }

    /**
     * 推送信号组状态数据
     */
    private void startSignalGroupStatusPush(String serverId) {
        pushExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!isSubscriptionActive(serverId, GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS)) {
                    return;
                }

                Object data = getRealDataFromCommon(GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS);
                if (data != null) {
                    Message pushMessage = createPushMessageWithIndependentSystem(gatTcpClient.getUsername(), GatTcpClient.tocken, data);
                    sendPushMessage(serverId, pushMessage);
                    logger.debug("推送信号组状态到服务端: serverId={}",
                            serverId);
                }

            } catch (Exception e) {
                logger.error("推送信号组状态失败", e);
            }
        }, 25, 5, TimeUnit.SECONDS);
    }

    /**
     * 推送交通流数据
     */
    private void startTrafficDataPush(String serverId) {
        pushExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!isSubscriptionActive(serverId, GatConstants.ObjectName.CROSS_TRAFFIC_DATA)) {
                    return;
                }

                Object data = getRealDataFromCommon(GatConstants.ObjectName.CROSS_TRAFFIC_DATA);
                if (data != null) {
                    Message pushMessage = createPushMessageWithIndependentSystem(gatTcpClient.getUsername(), GatTcpClient.tocken, data);
                    sendPushMessage(serverId, pushMessage);
                    logger.info("推送交通流数据到服务端: serverId={}",
                            serverId);
                }

            } catch (Exception e) {
                logger.error("推送交通流数据失败", e);
            }
        }, 30, 300, TimeUnit.SECONDS); // 每5分钟推送一次
    }

    /**
     * 推送信号机故障报告
     */
    private void startErrorReportPush(String serverId) {
        pushExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!isSubscriptionActive(serverId, GatConstants.ObjectName.SIGNAL_CONTROLLER_ERROR)) {
                    return;
                }

                // 模拟偶发故障（10%概率）
                //if (random.nextInt(10) == 0) {
                    Object data = getRealDataFromCommon(GatConstants.ObjectName.SIGNAL_CONTROLLER_ERROR);
                    if (data != null) {
                        Message pushMessage = createPushMessageWithIndependentSystem(gatTcpClient.getUsername(), GatTcpClient.tocken, data);
                        sendPushMessage(serverId, pushMessage);
                        logger.warn("推送信号机故障到服务端: serverId={}",
                                serverId);
                    }
                //}
            } catch (Exception e) {
                logger.error("推送故障报告失败", e);
            }
        }, 60, 120, TimeUnit.SECONDS); // 每2分钟检查一次
    }

    /**
     * 推送控制方式变更
     */
    private void startModePlanPush(String serverId) {
        pushExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!isSubscriptionActive(serverId, GatConstants.ObjectName.CROSS_MODE_PLAN)) {
                    return;
                }

                // 模拟控制方式变更（较少发生）
                //if (random.nextInt(20) == 0) {
                    Object data = getRealDataFromCommon(GatConstants.ObjectName.CROSS_MODE_PLAN);
                    if (data != null) {
                        Message pushMessage = createPushMessageWithIndependentSystem(gatTcpClient.getUsername(), GatTcpClient.tocken, data);
                        sendPushMessage(serverId, pushMessage);
                        logger.info("推送控制方式变更到服务端: serverId={}",
                                serverId);
                    }

                //}
            } catch (Exception e) {
                logger.error("推送控制方式变更失败", e);
            }
        }, 90, 600, TimeUnit.SECONDS); // 每10分钟检查一次
    }

    /**
     * 停止数据推送
     */
    private void stopDataPush(String serverId, SdoMsgEntity subscription) {
        // 实际实现中，这里应该取消对应的定时任务
        // 为了简化，这里只记录日志
        logger.info("停止数据推送: serverId={}, objName={}", serverId, subscription.getObjName());
    }

    /**
     * 创建推送消息
     */
    private Message createPushMessage(Object data) {
        return MessageBuilder.create()
                .push()
                .fromUtcs()  // 客户端推送
                .toTicp()    // 发送给服务端
                .notify(data)
                .build();
    }

    /**
     * 发送推送消息到服务端
     */
    private void sendPushMessage(String serverId, Message pushMessage) {
        try {
            // 使用注入的GatTcpClient发送消息
            if (gatTcpClient != null) {
                gatTcpClient.sendMessage(pushMessage);
                logger.debug("发送推送消息到服务端: serverId={}, seq={}, dataType={}",
                        serverId, pushMessage.getSeq(),
                        ProtocolUtils.getOperationData(pushMessage).getClass().getSimpleName());
            } else {
                logger.warn("GatTcpClient未初始化，无法发送推送消息");
            }
        } catch (Exception e) {
            logger.error("发送推送消息失败: serverId={}, seq={}", serverId, pushMessage.getSeq(), e);
        }
    }

    // ==================== 独立Push系统的多种使用方式 ====================

    /**
     * 方式一：使用PushHandlingUtils便捷方法（推荐）
     * 完全独立的系统，自动处理ArrayList
     */
    private Message createPushMessageWithIndependentSystem(Object data) {
        return PushHandlingUtils.createClientPushMessage(data);
    }
    private Message createPushMessageWithIndependentSystem(String username, String token, Object data) {
        return PushHandlingUtils.createClientPushMessage(username, token, data);
    }

    /**
     * 方式二：使用PushBuilder直接构建
     * 独立的构建器，与现有MessageBuilder完全分离
     */
    private Message createPushMessageWithBuilder(Object data) {
        return PushBuilder.clientPush()
                .toTicp()
                .withData(data)  // 自动处理ArrayList
                .build();
    }
    private Message createPushMessageWithBuilderWithToken(Object data, String username, String token) {
        return PushBuilder.clientPush(username)
                .toTicp()
                .token(token)
                .withData(data)  // 自动处理ArrayList
                .build();
    }

    /**
     * 方式三：使用智能推送构建器
     * 提供最大的灵活性
     */
    private Message createSmartPushMessage(Object data) {
        return PushHandlingUtils.smartClientPush()
                .toTicp()
                .withData(data)
                .build();
    }

    /**
     * 方式四：强制使用单数据模式
     */
    private Message createSingleDataPushMessage(Object data) {
        return PushBuilder.clientPush()
                .toTicp()
                .withSingleData(data)  // 强制单数据处理
                .build();
    }

    /**
     * 方式五：强制使用多数据模式
     */
    private Message createMultiDataPushMessage(Object data) {
        return PushBuilder.clientPush()
                .toTicp()
                .withMultiData(data)  // 强制多数据处理
                .build();
    }

    /**
     * 方式六：批量数据推送
     */
    private Message createBatchPushMessage(List<?> dataList) {
        return PushBuilder.clientPush()
                .toTicp()
                .withBatch(dataList)  // 批量推送
                .build();
    }

    /**
     * 方式七：条件推送
     */
    private Message createConditionalPushMessage(Object data, boolean condition) {
        return PushBuilder.clientPush()
                .toTicp()
                .notifyIf(condition, data)  // 条件推送
                .build();
    }

    /**
     * 方式八：链式调用的复杂推送
     */
    private Message createComplexPushMessage(Object data, String token) {
        return PushBuilder.clientPush()
                .toTicp("specificUser")
                .token(token)
                .notifyIfValid(data)  // 验证数据有效性后推送
                .build();
    }
    /**
     * 检查订阅是否仍然有效
     */
    private boolean isSubscriptionActive(String serverId, String objName) {
        List<SdoMsgEntity> subscriptions = receivedSubscriptions.get(serverId);
        if (subscriptions == null) {
            return false;
        }

        return subscriptions.stream().anyMatch(sub -> objName.equals(sub.getObjName()));
    }

    /**
     * 验证订阅参数
     */
    private boolean isValidSubscription(SdoMsgEntity subscription) {
        if (!GatConstants.MessageType.PUSH.equals(subscription.getMsgType())) {
            logger.warn("订阅消息类型必须为PUSH: {}", subscription.getMsgType());
            return false;
        }

        if (!GatConstants.Operation.NOTIFY.equals(subscription.getOperName())) {
            logger.warn("订阅操作名称必须为Notify: {}", subscription.getOperName());
            return false;
        }

        if (subscription.getObjName() == null || subscription.getObjName().trim().isEmpty()) {
            logger.warn("订阅对象名称不能为空");
            return false;
        }

        return true;
    }

    /**
     * 检查两个订阅是否相同
     */
    private boolean isSameSubscription(SdoMsgEntity sub1, SdoMsgEntity sub2) {
        return sub1.getMsgType().equals(sub2.getMsgType()) &&
                sub1.getOperName().equals(sub2.getOperName()) &&
                sub1.getObjName().equals(sub2.getObjName());
    }

    /**
     * 从消息中提取服务端ID
     */
    private String getServerIdFromMessage(Message message) {
        if (message.getFrom() != null && message.getFrom().getInstance() != null) {
            return message.getFrom().getInstance();
        }
        return "DEFAULT_SERVER";
    }

    /**
     * 创建错误响应
     */
    private Message createErrorResponse(Message request, String errorCode, String errorMessage) {
        return MessageBuilder.create()
                .error()
                .fromUtcs()
                .toTicp()
                .seq(request.getSeq())
                .operation("Error", createErrorData(errorCode, errorMessage))
                .build();
    }

    /**
     * 创建错误数据对象
     */
    private Object createErrorData(String errorCode, String errorMessage) {
        return new java.util.HashMap<String, Object>() {{
            put("errorCode", errorCode);
            put("errorMessage", errorMessage);
            put("timestamp", LocalDateTime.now());
        }};
    }

    /**
     * 获取当前活跃的订阅
     */
    public List<SdoMsgEntity> getActiveSubscriptions(String serverId) {
        return receivedSubscriptions.getOrDefault(serverId, new CopyOnWriteArrayList<>());
    }

    /**
     * 手动推送数据（用于测试）
     */
    public void manualPush(String serverId, String dataType) {
        switch (dataType.toUpperCase()) {
            case "SYSTEM_STATE":
                SysState sysState = new SysState(SystemState.ONLINE);
                sendPushMessage(serverId, createPushMessage(sysState));
                break;
            case "CROSS_STATE":
                CrossState crossState = new CrossState(crossId, SystemState.ONLINE);
                sendPushMessage(serverId, createPushMessage(crossState));
                break;
            case "TRAFFIC_DATA":
                // 创建模拟交通数据
                CrossTrafficData trafficData = new CrossTrafficData();
                trafficData.setCrossId(crossId);
                trafficData.setEndTimeFromLocalDateTime(LocalDateTime.now());
                sendPushMessage(serverId, createPushMessage(trafficData));
                break;
            default:
                logger.warn("不支持的手动推送数据类型: {}", dataType);
        }
    }

    /**
     * 关闭推送服务
     */
    public void shutdown() {
        if (pushExecutor != null) {
            pushExecutor.shutdown();
        }
        logger.info("客户端数据推送服务已关闭");
    }
    // 手动订阅 - 取消订阅方案
    private SdoMsgEntity createPushObject(String objName){
        return new SdoMsgEntity(
                GatConstants.MessageType.PUSH,
                GatConstants.Operation.NOTIFY,
                objName
        );
    }
    public void manualSubscribe(String serverId, String objName) {
        SdoMsgEntity subscription = createPushObject(objName);
        // 验证订阅参数
        if (!isValidSubscription(subscription)) {
            logger.warn("订阅参数无效 - manual");
            return;
        }
        // 记录订阅请求
        List<SdoMsgEntity> subscriptions = receivedSubscriptions.computeIfAbsent(
                serverId, k -> new CopyOnWriteArrayList<>());

        // 检查是否已存在相同订阅
        boolean exists = subscriptions.stream().anyMatch(existing ->
                isSameSubscription(existing, subscription));

        if (!exists) {
            subscriptions.add(subscription);
            logger.info("接受服务端订阅请求[manual]: serverId={}, objName={}", serverId, subscription.getObjName());

            // 启动对应的数据推送
            startDataPush(serverId, subscription);
        } else {
            logger.debug("订阅已存在[manual]: serverId={}, objName={}", serverId, subscription.getObjName());
        }
    }
    public void manualUnSubscribe(String serverId, String objName) {
        SdoMsgEntity unsubscription = createPushObject(objName);
        // 验证订阅参数
        if (!isValidSubscription(unsubscription)) {
            logger.warn("取消订阅参数无效 - manual");
            return;
        }
        List<SdoMsgEntity> subscriptions = receivedSubscriptions.get(serverId);
        if (subscriptions != null) {
            boolean removed = subscriptions.removeIf(existing ->
                    isSameSubscription(existing, unsubscription));
            if (removed) {
                logger.info("取消订阅[manual]: serverId={}, objName={}", serverId, unsubscription.getObjName());
                // 停止相应的数据推送
                stopDataPush(serverId, unsubscription);
            }
        }
    }
}