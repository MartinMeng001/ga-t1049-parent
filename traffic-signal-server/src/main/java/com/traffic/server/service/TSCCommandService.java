package com.traffic.server.service;

import com.traffic.gat1049.application.connection.ConnectionManager;
import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.MessageEncodingException;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.codec.MessageCodec;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.command.TSCCmd;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.server.network.client.ServerToClientSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TSCCommandService {
    private static final Logger logger = LoggerFactory.getLogger(EnhancedServerSubscriptionService.class);
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
        logger.info("命令处理服务已初始化");
    }

    /**
     * 查询TSC
     *
     * @return 响应消息
     */
    public void queryTSCInfo(String clientId, String objName, String id, Integer no) throws GatProtocolException {
        String token = connectionManager.getToken(clientId);
        Message message = sendTSCQuery(objName, token, id, no);
        if(message == null) return;
        sendMessage(clientId, message);
    }
    /**
     * 查询系统信息
     *
     * @return 响应消息
     */
    public void querySystemInfo(String clientId) throws GatProtocolException {
        String token = connectionManager.getToken(clientId);
        Message message = sendTSCQuery(GatConstants.ObjectName.SYS_INFO, token, null, null);
        if(message == null) return;
        sendMessage(clientId, message);
    }
    /**
     * 查询系统状态
     *
     * @return 响应消息
     */
    public void querySystemState(String clientId) throws GatProtocolException {
        String token = connectionManager.getToken(clientId);
        Message message = sendTSCQuery(GatConstants.ObjectName.SYS_STATE, token, null, null);
        if(message == null) return;
        sendMessage(clientId, message);
    }
    /**
     * 通用TSC查询方法
     *
     * @param objName 对象名称
     * @param id 对象编号（可选）
     * @param no 索引号（可选）
     * @return 响应消息
     */
    public Message sendTSCQuery(String objName, String token, String id, Integer no) throws GatProtocolException {
        validateParameters(objName, token);

        try {
            // 1. 创建TSCCmd
            TSCCmd tscCmd = new TSCCmd();
            tscCmd.setObjName(objName);
            tscCmd.setId(id);
            tscCmd.setNo(no);

            // 2. 构建查询消息
            Message queryMessage = MessageBuilder.create()
                    .request()
                    .fromUtcs()
                    .toTicp()
                    .token(token)
                    .get(tscCmd)
                    .build();

            logger.info("发送查询请求: objName={}, id={}, no={}, seq={}",
                    objName, id, no, queryMessage.getSeq());

            return queryMessage;

        } catch (Exception e) {
            logger.error("发送TSC查询失败: objName={}, id={}, no={}", objName, id, no, e);
            throw new GatProtocolException("SEND_REQUEST_FAILED", "发送查询请求失败: " + e.getMessage(), e);
        }
    }
    /**
     * 发送消息并接收响应
     */
    private void sendMessage(String clientId, Message request) throws GatProtocolException {
        try {
            // 1. 编码请求
            String requestXml = messageCodec.encode(request);
            //logger.debug("发送XML请求 (长度={}): {}", requestXml.length(),
            //        requestXml.length() > 500 ? requestXml.substring(0, 500) + "..." : requestXml);
            logger.debug("发送XML请求 (长度={}): {}", requestXml.length(),
                    requestXml);
            // 2. 发送网络请求
            clientSender.sendToClient(clientId, requestXml)
                    .thenAccept(success -> {
                        if (success) {
                            System.out.println("✅ 消息发送成功到客户端: " + clientId);
                        } else {
                            System.out.println("❌ 消息发送失败到客户端: " + clientId);
                        }
                    });
        } catch (MessageEncodingException e) {
            throw new GatProtocolException("ENCODING_ERROR", "消息编码失败", e);
        }  catch (Exception e) {
            throw new GatProtocolException("NETWORK_ERROR", "网络通信失败", e);
        }
    }
    /**
     * 验证参数
     */
    private void validateParameters(String objName, String sessionToken) throws GatProtocolException {
        if (objName == null || objName.trim().isEmpty()) {
            throw new GatProtocolException("INVALID_PARAMETER", "对象名称不能为空");
        }

        if (sessionToken == null || sessionToken.trim().isEmpty()) {
            throw new GatProtocolException("INVALID_TOKEN", "会话令牌不能为空");
        }
    }
}
