package com.traffic.client.service;

import com.traffic.client.network.client.GatTcpClient;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.model.enums.SystemType;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoTimeOut;
import com.traffic.gat1049.protocol.model.sdo.SdoTimeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 客户端演示服务
 */
@Service
public class ClientDemoService {

    private static final Logger logger = LoggerFactory.getLogger(ClientDemoService.class);
    private String token;

    @Autowired
    private SessionManager sessionManager;

    public void demonstrateGat1049Operations(GatTcpClient client) {
        logger.info("\n=== 演示GA/T 1049.1标准操作 ===");

        try {
            // 1. 登录操作
            performLogin(client);

            if (token != null) {
                // 2. 订阅操作
                //performSubscribe(client);

                // 3. 查询对时服务器
                //performQuery(client);

                // 4. 设置超时时间
                //performSetTimeout(client);
            }

        } catch (Exception e) {
            logger.error("演示GA/T 1049.1操作失败", e);
        }
    }

    private void performLogin_Main(GatTcpClient client) throws Exception {
        Message loginRequest = MessageBuilder.createLoginRequest(client.getUsername(), client.getPassword());
        Message loginResponse = client.sendRequest(loginRequest, 10, TimeUnit.SECONDS);

        if (loginResponse != null && "RESPONSE".equals(loginResponse.getType())) {
            token = loginResponse.getToken();
            GatTcpClient.tocken = token;
            logger.info("登录成功，获得Token: {}", token);
            sessionManager.registerLogin(client.getUsername(), SystemType.UTCS, client.getHost(), token);
        } else {
            logger.error("登录失败");
        }
    }
    private void performLogin(GatTcpClient client) throws Exception {
        Message loginRequest = MessageBuilder.createLoginRequest(client.getUsername(), client.getPassword());
        client.sendMessage(loginRequest);

        /*
        测试目的
         */
        token = "sdsb-test";//loginResponse.getToken();
        GatTcpClient.tocken = token;
        logger.info("测试登录成功，获得Token: {}", token);
        sessionManager.registerLogin(client.getUsername(), SystemType.UTCS, client.getHost(), token);
    }

    private void performSubscribe(GatTcpClient client) throws Exception {
        Message subscribeRequest = MessageBuilder.createSubscribeRequest(
                token, "PUSH", "Notify", "CrossSignalGroupStatus", client.getUsername());
        Message subscribeResponse = client.sendRequest(subscribeRequest, 10, TimeUnit.SECONDS);

        if (subscribeResponse != null && "RESPONSE".equals(subscribeResponse.getType())) {
            logger.info("订阅成功");
        }
    }

    private void performQuery(GatTcpClient client) throws Exception {
        SdoTimeServer timeServerQuery = new SdoTimeServer("", "", null);
        Message queryRequest = MessageBuilder.createQueryRequest(token, timeServerQuery, client.getUsername());
        Message queryResponse = client.sendRequest(queryRequest, 10, TimeUnit.SECONDS);

        if (queryResponse != null) {
            logger.info("查询响应: {}", queryResponse.getType());
        }
    }

    private void performSetTimeout(GatTcpClient client) throws Exception {
        SdoTimeOut timeout = new SdoTimeOut(360);
        Message setRequest = MessageBuilder.createSetRequest(token, timeout);
        Message setResponse = client.sendRequest(setRequest, 10, TimeUnit.SECONDS);

        if (setResponse != null) {
            logger.info("设置响应: {}", setResponse.getType());
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
