package com.traffic.server.test;

import com.traffic.device.adapter.webservice.BasicWebServiceAdapter;
import com.traffic.gat1049.device.adapter.registry.AdapterRegistry;
import com.traffic.gat1049.device.management.DeviceManagementService;
import com.traffic.gat1049.device.adapter.interfaces.DeviceAdapter;
import com.traffic.gat1049.device.adapter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 设备管理控制器
 * 提供设备连接、管理和操作的REST接口
 */
@RestController
@RequestMapping("/api/test/devices")
public class TestDeviceController {
    private static final Logger logger = LoggerFactory.getLogger(TestDeviceController.class);

    @Autowired
    private AdapterRegistry adapterRegistry;

    @Autowired
    private DeviceManagementService deviceManagementService;

    /**
     * 获取所有已注册的适配器
     */
    @GetMapping("/adapters")
    public ResponseEntity<Map<String, Object>> getAdapters() {
        try {
            List<String> registeredBrands = adapterRegistry.getSupportedBrands();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("adapters", registeredBrands);
            result.put("count", registeredBrands.size());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("获取适配器列表失败", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "获取适配器列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 连接设备
     */
    @PostMapping("/connect")
    public ResponseEntity<Map<String, Object>> connectDevice(
            @RequestBody DeviceConnectionRequest request) {
        try {
            logger.info("接收设备连接请求: {}", request);

            // 调用设备管理服务连接设备
            ConnectionResult result = deviceManagementService.connectDevice(
                    request.getControllerId(),
                    request.getBrand(),
                    request.getIpServer(),
                    request.getPort()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("deviceId", result.getDeviceId());
            response.put("message", result.getMessage());

            if (!result.isSuccess()) {
                response.put("errorCode", result.getErrorCode());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("连接设备异常", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "连接设备异常: " + e.getMessage()
            ));
        }
    }

    /**
     * 断开设备连接
     */
    @PostMapping("/disconnect/{controllerId}")
    public ResponseEntity<Map<String, Object>> disconnectDevice(
            @PathVariable String controllerId) {
        try {
            DisconnectionResult result = deviceManagementService.disconnectDevice(controllerId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("deviceId", result.getDeviceId());
            response.put("message", result.getMessage());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("断开设备连接异常", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "断开连接异常: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取设备状态
     */
    @GetMapping("/status/{controllerId}")
    public ResponseEntity<Map<String, Object>> getDeviceStatus(
            @PathVariable String controllerId) {
        try {
            DeviceStatusData statusData = new DeviceStatusData();//deviceManagementService.getDeviceStatus(controllerId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("controllerId", controllerId);
            response.put("status", statusData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("获取设备状态异常", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "获取状态异常: " + e.getMessage()
            ));
        }
    }

    /**
     * 测试WebService适配器专用接口
     */
    @PostMapping("/webservice/test")
    public ResponseEntity<Map<String, Object>> testWebServiceAdapter(
            @RequestBody WebServiceTestRequest request) {
        try {
            // 获取双百适配器
            DeviceAdapter adapter = adapterRegistry.getAdapter("SHUNBY");
            if (adapter == null) {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "error", "未找到双百适配器"
                ));
            }

            // 如果是BasicWebServiceAdapter，可以直接调用测试方法
            if (adapter instanceof BasicWebServiceAdapter) {
                BasicWebServiceAdapter wsAdapter =
                        (BasicWebServiceAdapter) adapter;

                // 调用测试连接
                boolean connected = wsAdapter.connectDevice(request.getIpAddress(), request.getControllerId());

                Map<String, Object> response = new HashMap<>();
                response.put("success", connected);
                response.put("message", connected ? "WebService连接测试成功" : "WebService连接测试失败");
                response.put("ip", request.getIpAddress());
                response.put("controllerId", request.getControllerId());

                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "error", "适配器类型不匹配"
            ));

        } catch (Exception e) {
            logger.error("WebService测试异常", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "测试异常: " + e.getMessage()
            ));
        }
    }

    // ===================== 请求实体类 =====================

    /**
     * 设备连接请求
     */
    public static class DeviceConnectionRequest {
        private String controllerId;
        private String brand;
        private String ipServer;
        private String ipDevice;
        private Integer port;

        // Getters and Setters
        public String getControllerId() { return controllerId; }
        public void setControllerId(String controllerId) { this.controllerId = controllerId; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public String getIpServer() { return ipServer; }
        public void setIpServer(String ipAddress) { this.ipServer = ipAddress; }

        public String getIpDevice() { return ipDevice; }
        public void setIpDevice(String ipAddress) { this.ipDevice = ipAddress; }

        public Integer getPort() { return port; }
        public void setPort(Integer port) { this.port = port; }

        @Override
        public String toString() {
            return String.format("DeviceConnectionRequest{controllerId='%s', brand='%s', ip='%s', port=%d}",
                    controllerId, brand, ipServer, port);
        }
    }

    /**
     * WebService测试请求
     */
    public static class WebServiceTestRequest {
        private String ipAddress;
        private String controllerId;

        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

        public String getControllerId() { return controllerId; }
        public void setControllerId(String controllerId) { this.controllerId = controllerId; }
    }
}
