package com.traffic.gat1049.application;

import com.sun.nio.sctp.NotificationHandler;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import com.traffic.gat1049.application.subscription.interfaces.SubscriptionService;
import com.traffic.gat1049.exception.MessageEncodingException;
import com.traffic.gat1049.protocol.handler.main.*;
import com.traffic.gat1049.protocol.handler.main.common.LogoutHandler;
import com.traffic.gat1049.protocol.handler.base.ProtocolHandler;
import com.traffic.gat1049.protocol.handler.subscription.NotifySubscribeHandler;
import com.traffic.gat1049.protocol.handler.subscription.NotifyUnsubscribeHandler;
import com.traffic.gat1049.protocol.processor.DefaultMessageProcessor;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.gat1049.service.interfaces.ServiceFactory;
import com.traffic.gat1049.application.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler注册管理器
 * 负责注册和管理所有的协议处理器
 */
public class HandlerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(HandlerRegistry.class);

    private final ServiceFactory serviceFactory;
    private final SessionManager sessionManager;
    private final MessageProcessor messageProcessor;
    private final SubscriptionManager subscriptionManager;
    private final List<ProtocolHandler> handlers;

    private NotifySubscribeHandler notifySubscribeHandler;
    private NotifyUnsubscribeHandler notifyUnsubscribeHandler;

    public HandlerRegistry(ServiceFactory serviceFactory, SessionManager sessionManager, SubscriptionManager subscriptionManager) throws MessageEncodingException {
        this.serviceFactory = serviceFactory;
        this.sessionManager = sessionManager;
        this.subscriptionManager = subscriptionManager;
        this.messageProcessor = new DefaultMessageProcessor();
        this.handlers = new ArrayList<>();

        // 注册所有处理器
        registerAllHandlers();
    }

    /**
     * 注册所有处理器
     */
    private void registerAllHandlers() {
        // 注册LoginHandler
        registerHandler(new LoginHandler(sessionManager));

        // 注册LogoutHandler
        registerHandler(new LogoutHandler(sessionManager));

        // 1. 查询处理器 - 处理配置参数和运行信息查询
        registerHandler(new TSCCommandHandler(serviceFactory, sessionManager));

        // 2. 流向控制处理器 - 处理锁定/解锁交通流向、阶段干预
        registerHandler(new FlowControlHandler(serviceFactory, sessionManager));

        // 3. 配时方案控制处理器 - 处理配时方案、日计划、调度参数设置
        registerHandler(new PlanControlHandler(serviceFactory));

        // 4. 数据上报控制处理器 - 处理数据上报开关控制
        registerHandler(new DataReportHandler(serviceFactory));

        // 5. 干线控制处理器 - 处理干线控制方式和推荐车速
        registerHandler(new RouteControlHandler(serviceFactory));

        // 6. 可变车道控制处理器 - 处理可变车道状态设置
        registerHandler(new VariableLaneHandler(serviceFactory));

        // 7. 控制方式处理器 - 处理路口控制方式设置
        registerHandler(new ControlModeHandler(serviceFactory));

        // 8. 状态推送处理器 - 处理系统主动推送的状态数据
        registerHandler(new StatePushHandler(serviceFactory));

        // 9. 订阅处理器 - 保存引用以便后续注入服务
        notifySubscribeHandler = new NotifySubscribeHandler(sessionManager);
        registerHandler(notifySubscribeHandler);

        // 10. 取消订阅处理器 - 同样保存引用
        notifyUnsubscribeHandler = new NotifyUnsubscribeHandler(sessionManager);
        registerHandler(notifyUnsubscribeHandler);

        // 11. 可变车道处理器
        registerHandler(new CtrlVarLaneHandler(serviceFactory));

        // 12. 重传运行信息处理器
        registerHandler(new RunInfoRetransHandler(serviceFactory));

        // 13. 路口控制方式方案处理器
        registerHandler(new CrossCtrlInfoHandler(serviceFactory));

        logger.info("Registered {} protocol handlers", handlers.size());
    }

    /**
     * 注册处理器
     */
    private void registerHandler(ProtocolHandler handler) {
        messageProcessor.registerHandler(handler);
        handlers.add(handler);
        logger.debug("Registered handler: {}", handler.getHandlerName());
    }
    /**
     * 注册Login处理器
     */
    private void registerLoginHandler() {
        LoginHandler loginHandler = new LoginHandler(sessionManager);
        registerHandler(loginHandler);
        logger.info("已注册LoginHandler");
    }

    /**
     * 注入订阅服务到订阅处理器
     */
    public void injectSubscriptionService(SubscriptionService subscriptionService) {
        if (notifySubscribeHandler != null) {
            notifySubscribeHandler.setSubscriptionService(subscriptionService);
            logger.info("订阅服务已注入到NotifySubscribeHandler");
        }

        if (notifyUnsubscribeHandler != null) {
            notifyUnsubscribeHandler.setSubscriptionService(subscriptionService);
            logger.info("订阅服务已注入到NotifyUnsubscribeHandler");
        }
    }
    /**
     * 获取NotifySubscribeHandler实例（用于外部注入）
     */
    public NotifySubscribeHandler getNotifySubscribeHandler() {
        return notifySubscribeHandler;
    }

    /**
     * 获取NotifyUnsubscribeHandler实例（用于外部注入）
     */
    public NotifyUnsubscribeHandler getNotifyUnsubscribeHandler() {
        return notifyUnsubscribeHandler;
    }
    /**
     * 获取消息处理器
     */
    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    /**
     * 获取已注册的处理器列表
     */
    public List<ProtocolHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }

    /**
     * 获取处理器数量
     */
    public int getHandlerCount() {
        return handlers.size();
    }

    /**
     * 根据名称查找处理器
     */
    public ProtocolHandler findHandler(String handlerName) {
        return handlers.stream()
                .filter(h -> h.getHandlerName().equals(handlerName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 移除处理器
     */
    public void removeHandler(String handlerName) {
        messageProcessor.removeHandler(handlerName);
        handlers.removeIf(h -> h.getHandlerName().equals(handlerName));
        logger.info("Removed handler: {}", handlerName);
    }

    /**
     * 重新加载所有处理器
     */
    public void reloadHandlers() {
        // 清空现有处理器
        for (ProtocolHandler handler : handlers) {
            messageProcessor.removeHandler(handler.getHandlerName());
        }
        handlers.clear();

        // 重新注册
        registerAllHandlers();
        logger.info("Reloaded all handlers");
    }
    /**
     * 关闭Handler注册器
     */
    public void shutdown() {
        logger.info("正在关闭HandlerRegistry...");

        // 清理所有处理器
        handlers.clear();

        logger.info("HandlerRegistry已关闭");
    }
}