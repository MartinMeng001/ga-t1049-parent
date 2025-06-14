package com.traffic.server.integration;

import com.traffic.gat1049.application.connection.ConnectionManager;
import com.traffic.gat1049.application.HandlerRegistry;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.handler.main.common.CommonOperationHandler;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.gat1049.service.abstracts.DefaultServiceFactory;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 服务端GA/T 1049.1集成配置
 */
@Component
public class ServerGat1049Integration {

//    @Autowired
    private DefaultServiceFactory serviceFactory;

    private SessionManager sessionManager;
    private SubscriptionManager subscriptionManager;
    private ConnectionManager connectionManager;
    private HandlerRegistry handlerRegistry;
    private MessageProcessor messageProcessor;

    @PostConstruct
    public void initialize() throws Exception {
        // 直接创建服务工厂实例
        serviceFactory = new DefaultServiceFactory();

        // 初始化核心组件
        sessionManager = new SessionManager();
        subscriptionManager = new SubscriptionManager();
        connectionManager = new ConnectionManager(sessionManager);

        // 创建处理器注册表
        handlerRegistry = new HandlerRegistry(serviceFactory, sessionManager, subscriptionManager);
        messageProcessor = handlerRegistry.getMessageProcessor();

        // 注册通用操作处理器
        CommonOperationHandler commonHandler = new CommonOperationHandler(sessionManager, subscriptionManager);
        messageProcessor.registerHandler(commonHandler);

        // 设置订阅推送回调
        subscriptionManager.setPushCallback(this::pushMessageToClient);

        //logger.info("GA/T 1049.1服务端集成初始化完成");
    }

    @PreDestroy
    public void destroy() {
        if (connectionManager != null) {
            connectionManager.shutdown();
        }
        if (sessionManager != null) {
            sessionManager.shutdown();
        }
        //logger.info("GA/T 1049.1服务端集成已关闭");
    }

    /**
     * 处理客户端消息
     */
    public String processClientMessage(String xmlMessage) {
        try {
            return messageProcessor.processMessage(xmlMessage);
        } catch (Exception e) {
            //logger.error("处理客户端消息失败", e);
            return createErrorResponse("处理消息失败: " + e.getMessage());
        }
    }

    /**
     * 推送消息给订阅的客户端
     */
    private void pushMessageToClient(String token, Message message) {
        // 这里实现实际的消息推送逻辑
        // 例如通过WebSocket、TCP连接等方式发送给客户端
        //logger.info("推送消息给客户端: token={}, seq={}", token, message.getSeq());
    }

    /**
     * 创建错误响应
     */
    private String createErrorResponse(String errorMessage) {
        // 创建标准的错误响应XML
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Message>\n" +
                "  <Version>2.0</Version>\n" +
                "  <Token></Token>\n" +
                "  <From><Sys>TICP</Sys></From>\n" +
                "  <To><Sys>UTCS</Sys></To>\n" +
                "  <Type>ERROR</Type>\n" +
                "  <Seq>" + Message.generateSequence() + "</Seq>\n" +
                "  <Body>\n" +
                "    <Operation order=\"1\" name=\"Error\">\n" +
                "      <SDO_Error>\n" +
                "        <ErrObj></ErrObj>\n" +
                "        <ErrType>SDE_Failure</ErrType>\n" +
                "        <ErrDesc>" + errorMessage + "</ErrDesc>\n" +
                "      </SDO_Error>\n" +
                "    </Operation>\n" +
                "  </Body>\n" +
                "</Message>";
    }

    // Getters for other components to access
    public SessionManager getSessionManager() { return sessionManager; }
    public SubscriptionManager getSubscriptionManager() { return subscriptionManager; }
    public ConnectionManager getConnectionManager() { return connectionManager; }
    public MessageProcessor getMessageProcessor() { return messageProcessor; }
}
