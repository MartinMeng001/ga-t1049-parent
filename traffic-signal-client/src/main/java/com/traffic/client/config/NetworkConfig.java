package com.traffic.client.config;

import com.traffic.client.network.client.GatTcpClient;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

/**
 * 客户端网络连接配置
 */
@Configuration
@DependsOn("messageProcessingConfig")
public class NetworkConfig {

    @Bean
    public GatTcpClient gatTcpClient(Environment env, MessageProcessor messageProcessor) throws Exception {
        String host = env.getProperty("tcp.client.server-host", "localhost");
        int port = env.getProperty("tcp.client.server-port", Integer.class, 9999);
        String clientId = env.getProperty("client.cross-id", "110100001");
        String username = env.getProperty("client.username", "sdsb");
        String password = env.getProperty("client.password", "sdsb");

        return new GatTcpClient(host, port, clientId, messageProcessor, username, password);
    }
}
