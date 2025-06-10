package com.traffic.gat1049.example;

import com.traffic.gat1049.connection.ConnectionManager;
import com.traffic.gat1049.handler.common.CommonOperationHandler;
import com.traffic.gat1049.model.entity.sdo.*;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.Message;
import com.traffic.gat1049.protocol.processor.DefaultMessageProcessor;
import com.traffic.gat1049.session.SessionManager;
import com.traffic.gat1049.subscription.SubscriptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GA/T 1049.1标准集成示例
 * 展示如何使用更新后的组件实现标准要求
 */
public class Gat1049IntegrationExample {

    private static final Logger logger = LoggerFactory.getLogger(Gat1049IntegrationExample.class);

    public static void main(String[] args) {
        logger.info("GA/T 1049.1交通信号控制系统通信协议示例");

        // 初始化各组件
        SessionManager sessionManager = new SessionManager();
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        ConnectionManager connectionManager = new ConnectionManager(sessionManager);

        try {
            // 创建消息处理器
            DefaultMessageProcessor messageProcessor = new DefaultMessageProcessor();

            // 注册通用操作处理器
            CommonOperationHandler commonHandler = new CommonOperationHandler(sessionManager, subscriptionManager);
            messageProcessor.registerHandler(commonHandler);

            // 设置订阅推送回调
            subscriptionManager.setPushCallback(new SubscriptionManager.MessagePushCallback() {
                @Override
                public void pushMessage(String token, Message message) {
                    logger.info("推送消息给订阅者: token={}, seq={}", token, message.getSeq());
                    // 这里可以实现实际的消息推送逻辑
                }
            });

            // 示例1：客户端登录流程
            demonstrateLoginFlow(messageProcessor);

            // 示例2：订阅流程
            demonstrateSubscriptionFlow(messageProcessor);

            // 示例3：心跳流程
            demonstrateHeartbeatFlow(messageProcessor);

            // 示例4：查询流程
            demonstrateQueryFlow(messageProcessor);

            // 示例5：错误处理流程
            demonstrateErrorHandling(messageProcessor);

        } catch (Exception e) {
            logger.error("示例执行失败", e);
        } finally {
            // 清理资源
            sessionManager.shutdown();
            connectionManager.shutdown();
        }
    }

    /**
     * 示例1：演示登录流程
     */
    private static void demonstrateLoginFlow(DefaultMessageProcessor processor) throws Exception {
        logger.info("\n=== 示例1：登录流程 ===");

        // 创建登录请求消息
        Message loginRequest = MessageBuilder.createLoginRequest("tsc_client", "tsc123");
        logger.info("发送登录请求: {}", loginRequest);

        // 处理登录请求
        String requestXml = encodeMessage(loginRequest);
        String responseXml = processor.processMessage(requestXml);

        if (responseXml != null) {
            Message loginResponse = decodeMessage(responseXml);
            logger.info("收到登录响应: {}", loginResponse);

            if ("RESPONSE".equals(loginResponse.getType())) {
                logger.info("登录成功，获得Token: {}", loginResponse.getToken());
            } else if ("ERROR".equals(loginResponse.getType())) {
                logger.error("登录失败");
            }
        }
    }

    /**
     * 示例2：演示订阅流程
     */
    private static void demonstrateSubscriptionFlow(DefaultMessageProcessor processor) throws Exception {
        logger.info("\n=== 示例2：订阅流程 ===");

        // 先登录获取token（简化处理）
        String token = "TKN_sample_token_for_demo";

        // 创建订阅请求
        Message subscribeRequest = MessageBuilder.createSubscribeRequest(
                token, "PUSH", "Notify", "CrossSignalGroupStatus");
        logger.info("发送订阅请求: {}", subscribeRequest);

        // 处理订阅请求
        String requestXml = encodeMessage(subscribeRequest);
        String responseXml = processor.processMessage(requestXml);

        if (responseXml != null) {
            Message subscribeResponse = decodeMessage(responseXml);
            logger.info("收到订阅响应: {}", subscribeResponse);
        }
    }

    /**
     * 示例3：演示心跳流程
     */
    private static void demonstrateHeartbeatFlow(DefaultMessageProcessor processor) throws Exception {
        logger.info("\n=== 示例3：心跳流程 ===");

        String token = "TKN_sample_token_for_demo";

        // 创建心跳消息
        Message heartbeatMessage = MessageBuilder.createHeartbeatMessage(token);
        logger.info("发送心跳消息: {}", heartbeatMessage);

        // 处理心跳消息（PUSH类型通常不需要响应）
        String heartbeatXml = encodeMessage(heartbeatMessage);
        String responseXml = processor.processMessage(heartbeatXml);

        if (responseXml == null) {
            logger.info("心跳消息处理完成（无需响应）");
        }
    }

    /**
     * 示例4：演示查询流程
     */
    private static void demonstrateQueryFlow(DefaultMessageProcessor processor) throws Exception {
        logger.info("\n=== 示例4：查询流程 ===");

        String token = "TKN_sample_token_for_demo";

        // 查询对时服务器
        SdoTimeServer timeServerQuery = new SdoTimeServer("", "", null);
        Message queryRequest = MessageBuilder.createQueryRequest(token, timeServerQuery);
        logger.info("发送查询请求: {}", queryRequest);

        // 处理查询请求
        String requestXml = encodeMessage(queryRequest);
        String responseXml = processor.processMessage(requestXml);

        if (responseXml != null) {
            Message queryResponse = decodeMessage(responseXml);
            logger.info("收到查询响应: {}", queryResponse);
        }
    }

    /**
     * 示例5：演示错误处理流程
     */
    private static void demonstrateErrorHandling(DefaultMessageProcessor processor) throws Exception {
        logger.info("\n=== 示例5：错误处理流程 ===");

        // 创建无效token的请求
        Message invalidRequest = MessageBuilder.create()
                .request()
                .fromUtcs()
                .toTicp()
                .token("INVALID_TOKEN")
                .get(new SdoTimeServer("", "", null))
                .build();

        logger.info("发送无效token请求: {}", invalidRequest);

        // 处理无效请求
        String requestXml = encodeMessage(invalidRequest);
        String responseXml = processor.processMessage(requestXml);

        if (responseXml != null) {
            Message errorResponse = decodeMessage(responseXml);
            logger.info("收到错误响应: {}", errorResponse);
        }
    }

    /**
     * 编码消息为XML（简化实现）
     */
    private static String encodeMessage(Message message) {
        // 这里应该使用实际的MessageCodec进行编码
        // 为了示例简化，返回简化的XML表示
        return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<Message>\n" +
                        "  <Version>%s</Version>\n" +
                        "  <Token>%s</Token>\n" +
                        "  <From><Sys>%s</Sys></From>\n" +
                        "  <To><Sys>%s</Sys></To>\n" +
                        "  <Type>%s</Type>\n" +
                        "  <Seq>%s</Seq>\n" +
                        "  <Body>...</Body>\n" +
                        "</Message>",
                message.getVersion(),
                message.getToken(),
                message.getFrom().getSys(),
                message.getTo().getSys(),
                message.getType(),
                message.getSeq());
    }

    /**
     * 解码XML为消息（简化实现）
     */
    private static Message decodeMessage(String xml) {
        // 这里应该使用实际的MessageCodec进行解码
        // 为了示例简化，返回模拟的消息对象
        Message message = new Message();
        message.setVersion("2.0");
        message.setType("RESPONSE");
        return message;
    }
}
// ========== 更新说明和使用指南 ==========

/**
 * GA/T 1049.1标准集成更新说明
 *
 * 本次更新根据GA/T 1049.1-2013标准文档完善了以下内容：
 *
 * 1. 系统预定义数据对象 (SDO)
 *    - SDO_Error: 系统错误对象，包含错误对象名、错误类型、错误描述
 *    - SDO_User: 用户信息对象，包含用户名和口令
 *    - SDO_MsgEntity: 订阅对象，包含消息类型、操作名称、对象名称
 *    - SDO_HeartBeat: 心跳对象，空元素对象
 *    - SDO_TimeOut: 超时对象，包含超时时间（秒）
 *    - SDO_TimeServer: 对时服务器对象，包含主机、协议、端口
 *
 * 2. 枚举类型完善
 *    - SystemErrorType: 系统预定义错误类型枚举（表A.5）
 *    - MessageType: 消息类型枚举（表1）
 *    - OperationName: 操作命令名称枚举（表A.3）
 *    - SystemType: 基础应用系统类型标识枚举（表A.2）
 *
 * 3. 协议模型更新
 *    - Message: 根节点消息结构，包含版本号、令牌、源地址、目的地址、类型、序列号、消息体
 *    - Address: 地址对象，包含系统类型、子系统、实例标识
 *    - MessageBody: 消息体，包含操作命令列表
 *    - Operation: 操作命令，包含顺序编号、名称、数据对象
 *
 * 4. 会话管理
 *    - SessionManager: 会话管理器，负责用户登录、会话维护、令牌验证
 *    - 支持会话超时检测和自动清理
 *    - 提供心跳更新机制
 *
 * 5. 订阅管理
 *    - SubscriptionManager: 订阅管理器，支持消息订阅和推送
 *    - 支持通配符订阅（使用*匹配任意值）
 *    - 提供消息推送回调机制
 *
 * 6. 连接管理
 *    - ConnectionManager: 连接状态管理器，监控客户端连接状态
 *    - 支持心跳检测和超时处理
 *    - 自动清理断开的连接
 *
 * 7. 通用操作处理
 *    - CommonOperationHandler: 处理标准定义的通用操作
 *    - 支持Login/Logout/Subscribe/Unsubscribe/Notify/Get/Set操作
 *    - 完整的错误处理和响应生成
 *
 * 8. 消息构建器增强
 *    - MessageBuilder: 符合标准的消息构建工具
 *    - 提供便捷的静态工厂方法
 *    - 自动生成符合标准的序列号
 *
 * 9. 常量定义完善
 *    - GatConstants: 根据标准文档更新所有常量定义
 *    - 包含消息类型、操作名称、系统地址、错误代码等
 *
 * 10. 协议规程实现
 *     - 支持REQUEST/RESPONSE/PUSH/ERROR四种消息类型
 *     - 实现连接建立、心跳维持、断线重连机制
 *     - 符合标准的超时处理和错误响应
 *
 * 使用示例：
 *
 * // 1. 初始化组件
 * SessionManager sessionManager = new SessionManager();
 * SubscriptionManager subscriptionManager = new SubscriptionManager();
 * DefaultMessageProcessor processor = new DefaultMessageProcessor();
 * CommonOperationHandler handler = new CommonOperationHandler(sessionManager, subscriptionManager);
 * processor.registerHandler(handler);
 *
 * // 2. 客户端登录
 * Message loginRequest = MessageBuilder.createLoginRequest("username", "password");
 * String responseXml = processor.processMessage(encodeToXml(loginRequest));
 *
 * // 3. 订阅消息
 * Message subscribeRequest = MessageBuilder.createSubscribeRequest(token, "PUSH", "Notify", "CrossState");
 * processor.processMessage(encodeToXml(subscribeRequest));
 *
 * // 4. 发送心跳
 * Message heartbeat = MessageBuilder.createHeartbeatMessage(token);
 * processor.processMessage(encodeToXml(heartbeat));
 *
 * // 5. 查询数据
 * Message queryRequest = MessageBuilder.createQueryRequest(token, new SdoTimeServer());
 * String queryResponse = processor.processMessage(encodeToXml(queryRequest));
 *
 * 重要变更说明：
 *
 * 1. 系统标识变更：
 *    - 原来使用TSC，现在改为UTCS（交通信号控制系统）
 *    - 这是为了符合GA/T 1049.1标准中表A.2的定义
 *
 * 2. 序列号格式：
 *    - 采用14位日期（YYYYMMDDHHMMSS）+6位顺序号
 *    - 确保序列号唯一性和可追溯性
 *
 * 3. 错误处理：
 *    - 使用标准预定义的错误类型
 *    - 错误响应包含完整的错误信息对象
 *
 * 4. 令牌管理：
 *    - 登录成功前令牌可为空字符串
 *    - 登录成功后令牌不能为空
 *    - 所有需要认证的操作都需要验证令牌
 *
 * 5. 消息长度限制：
 *    - 数据包长度不超过100000个字符
 *    - 超过限制的大容量数据通过URL引用
 *
 * 6. XML特殊字符处理：
 *    - 自动处理&、<、>、"、'等特殊字符的转义
 *
 * 7. 通信规程：
 *    - REQUEST类型需要应答，超时未收到应答视为失败
 *    - RESPONSE/PUSH/ERROR类型不需要应答
 *    - 心跳机制：连续3次未收到心跳视为连接中断
 *    - 重连机制：随机延迟1-60秒后重新连接
 *
 * 注意事项：
 *
 * 1. 版本兼容性：
 *    - 严格按照版本号验证协议兼容性
 *    - 不支持的版本返回版本错误
 *
 * 2. 安全考虑：
 *    - 密码在日志和响应中不显示明文
 *    - 令牌有时效性，定期更新
 *
 * 3. 性能优化：
 *    - 使用并发安全的数据结构
 *    - 定期清理过期会话和连接
 *
 * 4. 扩展性：
 *    - 支持自定义数据对象
 *    - 可以添加新的操作类型
 *    - 插件式的处理器架构
 */
