package com.traffic.gat1049.protocol.handler.subscription;

import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.handler.base.TokenRequiredHandler;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoMsgEntity;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.application.session.SessionManager;

import java.util.Arrays;
import java.util.List;

/**
 * Notify订阅处理器
 * 专门处理msgType为PUSH、operName为Notify的订阅请求
 * 支持的对象名称包括：CrossCycle, CrossModePlan, SignalControllerError, CrossState, SysState等
 */
public class NotifySubscribeHandler extends TokenRequiredHandler {

    private final SubscriptionManager subscriptionManager;

    /**
     * 支持的对象名称列表
     * 这些对象都支持PUSH/Notify类型的订阅
     */
    private static final List<String> SUPPORTED_OBJECT_NAMES = Arrays.asList(
            GatConstants.ObjectName.CROSS_CYCLE,           // CrossCycle - 路口周期信息
            GatConstants.ObjectName.CROSS_MODE_PLAN,       // CrossModePlan - 路口控制方式和方案
            GatConstants.ObjectName.SIGNAL_CONTROLLER_ERROR, // SignalControllerError - 信号机故障信息
            GatConstants.ObjectName.CROSS_STATE,           // CrossState - 路口状态
            GatConstants.ObjectName.SYS_STATE,             // SysState - 系统状态
            GatConstants.ObjectName.CROSS_STAGE,           // CrossStage - 路口阶段信息
            GatConstants.ObjectName.CROSS_SIGNAL_GROUP_STATUS, // CrossSignalGroupStatus - 信号组状态
            GatConstants.ObjectName.CROSS_TRAFFIC_DATA,    // CrossTrafficData - 路口交通流数据
            GatConstants.ObjectName.STAGE_TRAFFIC_DATA,    // StageTrafficData - 阶段交通流数据
            GatConstants.ObjectName.VAR_LANE_STATUS,       // VarLaneStatus - 可变车道状态
            GatConstants.ObjectName.ROUTE_CONTROL_MODE,    // RouteControlMode - 路线控制方式
            GatConstants.ObjectName.ROUTE_SPEED            // RouteSpeed - 路线速度
    );

    public NotifySubscribeHandler(SubscriptionManager subscriptionManager, SessionManager sessionManager) {
        super(sessionManager);
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    public boolean supports(Message message) {
        // 只处理订阅请求
        if (!ProtocolUtils.isRequest(message)) {
            return false;
        }

        String operationName = ProtocolUtils.getOperationName(message);
        if (!GatConstants.Operation.SUBSCRIBE.equals(operationName)) {
            return false;
        }

        Object data = ProtocolUtils.getOperationData(message);
        if (!(data instanceof SdoMsgEntity)) {
            return false;
        }

        SdoMsgEntity subscription = (SdoMsgEntity) data;

        // 检查是否是PUSH/Notify类型的订阅
        return GatConstants.MessageType.PUSH.equals(subscription.getMsgType()) &&
                GatConstants.Operation.NOTIFY.equals(subscription.getOperName()) &&
                isSupportedObjectName(subscription.getObjName());
    }

    @Override
    public String getHandlerName() {
        return "NotifySubscribeHandler";
    }

//    @Override
//    protected Message doHandle(Message message) throws GatProtocolException {
//        String objName = GatConstants.Operation.SUBSCRIBE;
//        try {
//            // 验证token
//            String token = message.getToken();
//            if (token == null || !sessionManager.validateToken(token)) {
//                return createErrorResponse(message,
//                        GatConstants.ErrorCode.SDE_TOKEN,
//                        "无效令牌或会话已过期", objName);
//            }
//
//            // 获取订阅数据
//            SdoMsgEntity subscription = (SdoMsgEntity) ProtocolUtils.getOperationData(message);
//
//            // 验证订阅参数
//            validateSubscription(subscription);
//
//            // 执行订阅
//            boolean success = subscriptionManager.subscribe(token, subscription);
//
//            if (success) {
//                logger.info("成功添加Notify订阅: token={}, objName={}", token, subscription.getObjName());
//                return createSuccessResponse(message, subscription);
//            } else {
//                logger.warn("订阅失败: token={}, subscription={}", token, subscription);
//                return createErrorResponse(message,
//                        GatConstants.ErrorCode.SDE_FAILURE,
//                        "订阅失败", objName);
//            }
//
//        } catch (ValidationException e) {
//            logger.error("订阅参数验证失败: {}", e.getMessage());
//            return createErrorResponse(message,
//                    GatConstants.ErrorCode.SDE_OPER_NAME,
//                    e.getMessage(), objName);
//        } catch (Exception e) {
//            logger.error("处理Notify订阅请求失败", e);
//            return createErrorResponse(message,
//                    GatConstants.ErrorCode.SDE_FAILURE,
//                    "订阅处理失败: " + e.getMessage(), objName);
//        }
//    }

    @Override
    protected Message doHandleWithSession(Message message, SessionManager.SessionInfo sessionInfo) {
        String objName = GatConstants.Operation.SUBSCRIBE;
        try {
            if(!supports(message)) {
                logger.info("{}消息无需处理", message.getType());
                return null;
            }
            // 获取订阅数据
            SdoMsgEntity subscription = (SdoMsgEntity) ProtocolUtils.getOperationData(message);
            String token = message.getToken();
            // 验证订阅参数
            validateSubscription(subscription);

            // 执行订阅
            boolean success = subscriptionManager.subscribe(token, subscription);

            if (success) {
                logger.info("成功添加Notify订阅: token={}, objName={}", token, subscription.getObjName());
                return createSuccessResponse(message, subscription);
            } else {
                logger.warn("订阅失败: token={}, subscription={}", token, subscription);
                return createErrorResponse(message,
                        GatConstants.ErrorCode.SDE_FAILURE,
                        "订阅失败", objName);
            }

        } catch (ValidationException e) {
            logger.error("订阅参数验证失败: {}", e.getMessage());
            return createErrorResponse(message,
                    GatConstants.ErrorCode.SDE_OPER_NAME,
                    e.getMessage(), objName);
        } catch (Exception e) {
            logger.error("处理Notify订阅请求失败", e);
            return createErrorResponse(message,
                    GatConstants.ErrorCode.SDE_FAILURE,
                    "订阅处理失败: " + e.getMessage(), objName);
        }
    }

    /**
     * 验证订阅参数
     */
    private void validateSubscription(SdoMsgEntity subscription) throws ValidationException {
        if (subscription == null) {
            throw new ValidationException("subscription", "订阅对象不能为空");
        }

        // 验证消息类型必须是PUSH
        if (!GatConstants.MessageType.PUSH.equals(subscription.getMsgType())) {
            throw new ValidationException("msgType",
                    "消息类型必须为PUSH，当前值: " + subscription.getMsgType());
        }

        // 验证操作名称必须是Notify
        if (!GatConstants.Operation.NOTIFY.equals(subscription.getOperName())) {
            throw new ValidationException("operName",
                    "操作名称必须为Notify，当前值: " + subscription.getOperName());
        }

        // 验证对象名称
        if (subscription.getObjName() == null || subscription.getObjName().trim().isEmpty()) {
            throw new ValidationException("objName", "对象名称不能为空");
        }

        if (!isSupportedObjectName(subscription.getObjName())) {
            throw new ValidationException("objName",
                    "不支持的对象名称: " + subscription.getObjName() +
                            "。支持的对象名称: " + SUPPORTED_OBJECT_NAMES);
        }
    }

    /**
     * 检查是否是支持的对象名称
     */
    private boolean isSupportedObjectName(String objName) {
        if (objName == null) {
            return false;
        }
        return SUPPORTED_OBJECT_NAMES.contains(objName) || "*".equals(objName);
    }

    /**
     * 创建订阅成功响应
     */
    private Message createSuccessResponse(Message request, SdoMsgEntity subscription) {
        return super.createSuccessResponse(request, subscription);
    }

    /**
     * 获取支持的对象名称列表（用于外部查询）
     */
    public static List<String> getSupportedObjectNames() {
        return SUPPORTED_OBJECT_NAMES;
    }

    /**
     * 创建标准的Notify订阅对象
     */
    public static SdoMsgEntity createNotifySubscription(String objName) {
        return new SdoMsgEntity(
                GatConstants.MessageType.PUSH,
                GatConstants.Operation.NOTIFY,
                objName
        );
    }

    /**
     * 批量创建多个对象的订阅
     */
    public static List<SdoMsgEntity> createMultipleNotifySubscriptions(String... objNames) {
        return Arrays.stream(objNames)
                .map(NotifySubscribeHandler::createNotifySubscription)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 创建所有支持对象的订阅（使用通配符）
     */
    public static SdoMsgEntity createAllNotifySubscription() {
        return new SdoMsgEntity(
                GatConstants.MessageType.PUSH,
                GatConstants.Operation.NOTIFY,
                "*"
        );
    }
}