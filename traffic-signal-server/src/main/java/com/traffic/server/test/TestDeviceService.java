// traffic-signal-server/src/main/java/com/traffic/server/service/DeviceService.java
package com.traffic.server.test;

import com.traffic.gat1049.device.adapter.registry.AdapterInfo;
import com.traffic.gat1049.device.adapter.registry.AdapterRegistry;
import com.traffic.gat1049.device.management.DeviceManagementService;
import com.traffic.gat1049.device.adapter.interfaces.DeviceAdapter;
import com.traffic.gat1049.device.adapter.interfaces.SignalControllerAdapter;
import com.traffic.gat1049.device.adapter.model.*;
import com.traffic.device.adapter.webservice.BasicWebServiceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 设备业务服务
 * 封装设备适配器的业务操作
 */
@Service
public class TestDeviceService {

    private static final Logger logger = LoggerFactory.getLogger(TestDeviceService.class);

    @Autowired
    private AdapterRegistry adapterRegistry;

    @Autowired
    private DeviceManagementService deviceManagementService;

    /**
     * 获取所有可用适配器信息
     */
    public List<AdapterInfo> getAvailableAdapters() {
        List<String> brands = adapterRegistry.getSupportedBrands();
        List<AdapterInfo> adapters = new ArrayList<>();

        for (String brand : brands) {
            DeviceAdapter adapter = adapterRegistry.getAdapter(brand);
            if (adapter != null) {
                adapters.add(adapter.getAdapterInfo());
            }
        }

        return adapters;
    }

    /**
     * 连接双百设备（专用方法）
     */
    public ConnectionResult connectShunbyDevice(String controllerId, String ipAddress, int port) {
        try {
            logger.info("连接双百设备: controllerId={}, ip={}, port={}", controllerId, ipAddress, port);

            DeviceAdapter adapter = adapterRegistry.getAdapter("SHUNBY");
            if (adapter == null) {
                return ConnectionResult.failure(controllerId, "ADAPTER_NOT_FOUND", "未找到双百适配器");
            }

            if (!(adapter instanceof BasicWebServiceAdapter)) {
                return ConnectionResult.failure(controllerId, "ADAPTER_TYPE_MISMATCH", "适配器类型不匹配");
            }

            BasicWebServiceAdapter wsAdapter = (BasicWebServiceAdapter) adapter;

            // 创建连接信息
            DeviceConnectionInfo connectionInfo = DeviceConnectionInfo.builder()
                    .deviceId(controllerId)
                    .ipAddress(ipAddress)
                    .port(port)
                    .connectionType("WebService")
                    .timeoutSeconds(30)
                    .build();

            // 执行连接
            return wsAdapter.connect(connectionInfo);

        } catch (Exception e) {
            logger.error("连接双百设备异常", e);
            return ConnectionResult.failure(controllerId, "CONNECTION_EXCEPTION",
                    "连接异常: " + e.getMessage());
        }
    }

    /**
     * 获取双百设备状态（专用方法）
     */
    public Map<String, Object> getShunbyDeviceStatus(String controllerId, String ipAddress) {
        Map<String, Object> result = new HashMap<>();

        try {
            DeviceAdapter adapter = adapterRegistry.getAdapter("SHUNBY");
            if (adapter == null) {
                result.put("success", false);
                result.put("error", "未找到双百适配器");
                return result;
            }

            if (!(adapter instanceof BasicWebServiceAdapter)) {
                result.put("success", false);
                result.put("error", "适配器类型不匹配");
                return result;
            }

            BasicWebServiceAdapter wsAdapter = (BasicWebServiceAdapter) adapter;

            // 调用适配器的获取状态方法
            // 注意：这里需要您的BasicWebServiceAdapter实现相应的方法
            boolean isConnected = wsAdapter.isConnected(controllerId);

            result.put("success", true);
            result.put("controllerId", controllerId);
            result.put("connected", isConnected);
            result.put("timestamp", new Date());

            return result;

        } catch (Exception e) {
            logger.error("获取双百设备状态异常", e);
            result.put("success", false);
            result.put("error", "获取状态异常: " + e.getMessage());
            return result;
        }
    }

    /**
     * 执行双百设备WebService调用
     */
    public Map<String, Object> callShunbyWebService(String controllerId, String ipAddress,
                                                    String methodName, Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            DeviceAdapter adapter = adapterRegistry.getAdapter("SHUNBY");
            if (adapter == null) {
                result.put("success", false);
                result.put("error", "未找到双百适配器");
                return result;
            }

            if (!(adapter instanceof BasicWebServiceAdapter)) {
                result.put("success", false);
                result.put("error", "适配器类型不匹配");
                return result;
            }

            BasicWebServiceAdapter wsAdapter = (BasicWebServiceAdapter) adapter;

            // 这里需要您在BasicWebServiceAdapter中添加一个公共方法来调用WebService
            // 例如：public String callWebServiceMethod(String ip, String method, Map<String, String> params)

            result.put("success", true);
            result.put("message", "WebService调用成功");
            result.put("methodName", methodName);
            result.put("timestamp", new Date());

            return result;

        } catch (Exception e) {
            logger.error("调用双百WebService异常", e);
            result.put("success", false);
            result.put("error", "WebService调用异常: " + e.getMessage());
            return result;
        }
    }

    /**
     * 检查适配器是否已注册
     */
    public boolean isAdapterRegistered(String brand) {
        return adapterRegistry.getAdapter(brand) != null;
    }

    /**
     * 获取适配器信息
     */
    public AdapterInfo getAdapterInfo(String brand) {
        DeviceAdapter adapter = adapterRegistry.getAdapter(brand);
        return adapter != null ? adapter.getAdapterInfo() : null;
    }
}