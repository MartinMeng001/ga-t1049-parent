package com.traffic.server.network;

import com.traffic.gat1049.application.connection.ConnectionManager;
import com.traffic.gat1049.protocol.processor.MessageProcessor;
import com.traffic.gat1049.application.session.SessionManager;
import com.traffic.gat1049.application.subscription.SubscriptionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * GA/T 1049管理REST API
 * 提供系统状态查询和管理功能
 */
@RestController
@RequestMapping("/api/gat1049")
public class Gat1049ManagementController {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private SubscriptionManager subscriptionManager;

    @Autowired
    private MessageProcessor messageProcessor;

    /**
     * 获取系统状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("onlineSessions", sessionManager.getOnlineSessionCount());
        status.put("onlineConnections", connectionManager.getOnlineConnectionCount());
        status.put("protocolVersion", "GA/T 1049.1-2013");
        status.put("systemType", "TICP");
        status.put("serverTime", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(status);
    }

    /**
     * 手动处理消息（用于测试）
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processMessage(@RequestBody String xmlMessage) {
        Map<String, Object> result = new HashMap<>();

        try {
            String response = messageProcessor.processMessage(xmlMessage);
            result.put("success", true);
            result.put("request", xmlMessage);
            result.put("response", response);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("request", xmlMessage);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getSessions() {
        Map<String, Object> result = new HashMap<>();
        result.put("sessionCount", sessionManager.getOnlineSessionCount());
        result.put("connectionCount", connectionManager.getOnlineConnectionCount());

        return ResponseEntity.ok(result);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("version", "2.0");
        health.put("protocol", "GA/T 1049.1-2013");
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }
}
