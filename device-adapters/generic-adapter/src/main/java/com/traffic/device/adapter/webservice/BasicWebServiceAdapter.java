package com.traffic.device.adapter.webservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.traffic.device.adapter.common.converter.impl.DataConverter;
import com.traffic.device.adapter.common.utils.SignalGroupUtil;
import com.traffic.device.adapter.webservice.deviceprotocol.u5.BasicPhaseTable;
import com.traffic.device.adapter.webservice.deviceprotocol.u5.SchemeData5U;
import com.traffic.gat1049.device.adapter.annotation.AdapterComponent;
import com.traffic.gat1049.device.adapter.annotation.DeviceBrand;
import com.traffic.gat1049.device.adapter.annotation.ProtocolVersion;
import com.traffic.gat1049.device.adapter.base.BaseSignalControllerAdapter;
import com.traffic.gat1049.device.adapter.model.*;
import com.traffic.gat1049.device.adapter.registry.AdapterInfo;
import com.traffic.gat1049.device.adapter.registry.AdapterRegistry;
import com.traffic.gat1049.protocol.model.intersection.LampGroupParam;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.service.interfaces.LampGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础WebService信号机适配器
 * 第一步实现：专注于通过WebService获取数据，不依赖任何未定义的接口
 */
@AdapterComponent(brand = "SHUNBY", version = "1.0", description = "双百平台适配器")
@DeviceBrand("SHUNBY")
@ProtocolVersion({"1.0", "2.0"})
public class BasicWebServiceAdapter extends BaseSignalControllerAdapter<JsonNode> {

    private static final Logger logger = LoggerFactory.getLogger(BasicWebServiceAdapter.class);

    // 维护设备连接状态
    private final Map<String, Boolean> connectionStatus = new ConcurrentHashMap<>();

    // WebService配置
    private String defaultPort = "8080";
    private String servicePath = "/SignalListenServer/SignalListenDelegate?wsdl";
    private String nameSpace = "http://webservice/";

    @Autowired
    private AdapterRegistry adapterRegistry;

    @Autowired
    private DataConverter dataConverter;

    @PostConstruct
    public void autoRegister() {
        // 适配器初始化后自动注册到注册表
        logger.info("开始自动注册适配器: {}", this.getClass().getSimpleName());
        adapterRegistry.register(this);
    }
    @Override
    protected AdapterInfo createAdapterInfo() {
        return AdapterInfo.builder()
                .adapterName("双百平台适配器")
                .brand("SHUNBY")
                .adapterType(AdapterType.SIGNAL_CONTROLLER)
                .version("1.0")
                .description("用于连接双百品牌信号机设备")
                .build();
    }
    @Override
    protected void doInitialize() throws Exception {
        // 双百特定的初始化逻辑
        logger.info("双百平台适配器...");
    }

    @Override
    protected void doStart() throws Exception {

    }

    @Override
    protected void doStop() throws Exception {

    }

    @Override
    protected void doDestroy() throws Exception {

    }

    @Override
    public ConnectionResult connect(DeviceConnectionInfo deviceInfo) {
        try {
            // 实现双百设备的连接逻辑
            String serverIP = "";
            if(deviceInfo.getIpServer()!=null&&!deviceInfo.getIpServer().equals("")){serverIP = deviceInfo.getIpServer();}
            else serverIP = deviceInfo.getIpAddress();
            boolean connected = connectDevice(serverIP, deviceInfo.getDeviceId());

            if (connected) {
                return ConnectionResult.builder()
                        .success(true)
                        .deviceId(deviceInfo.getDeviceId())
                        .message("双百设备连接成功")
                        .connectionInfo(deviceInfo)  // 这里可以设置连接信息
                        .connectionTimeMs(System.currentTimeMillis()) // 可以记录连接耗时
                        .createTime(LocalDateTime.now())
                        .build();
            } else {
                return ConnectionResult.failure(deviceInfo.getDeviceId(),
                        "CONNECTION_FAILED", "双百设备连接失败");
            }

        } catch (Exception e) {
            logger.error("连接双百设备异常", e);
            return ConnectionResult.failure(deviceInfo.getDeviceId(),
                    "CONNECTION_EXCEPTION", "连接异常: " + e.getMessage());
        }
    }
    @Override
    public SyncResult readConfigData(JsonNode inputParam) {
        try {
            boolean result = getDeviceStatus(inputParam);
            if (result) {
                return SyncResult.success("temp", "读取方案成功");
            }
            return SyncResult.failure("temp", "READ_FAILUE", "读取方案失败");
        }catch (Exception e){
            logger.error("双百设备方案读取异常", e);
            return SyncResult.failure("temp", "READ_EXCEPTION", "读取参数异常:" + e.getMessage());
        }
    }

    @Override
    public DisconnectionResult disconnect(String controllerId) {
        return null;
    }

    @Override
    public SyncResult syncConfigToDevice(String controllerId, DeviceConfigData configData) {
        return null;
    }

    @Override
    public DeviceStatusData readDeviceStatus(String controllerId) {
        return null;
    }

    @Override
    public DeviceRuntimeData readRuntimeData(String controllerId) {
        return null;
    }

    @Override
    public CommandResult sendCommand(String controllerId, DeviceCommand command) {
        return null;
    }

    @Override
    public DeviceCapabilities getDeviceCapabilities(String controllerId) {
        return null;
    }

    @Override
    public DevicePlanData toStdPlan(Object controllerPlan) {
        try {
            if (controllerPlan instanceof SchemeData5U) {
                SchemeData5U schemeData5U = (SchemeData5U) controllerPlan;
                List<LampGroupParam> all_lamps = dataConverter.getLampGroupStandardData(schemeData5U.getCrossId());
                // 转换灯组
                // 转换阶段
                // 转换方案
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public SyncResult execToControllerPlan(DevicePlanData plan) {
        return null;
    }

    /**
     * 连接设备
     * @param ip 设备IP地址
     * @param controllerId 控制器ID
     * @return 连接是否成功
     */
    public boolean connectDevice(String ip, String controllerId) {
        try {
            logger.info("正在连接WebService信号机: {}", ip);

            // 测试HelloWorld连接
            boolean connected = testHelloWorld(ip);

            // 维护连接状态
            connectionStatus.put(controllerId, connected);

            if (connected) {
                logger.info("✅ WebService信号机连接成功: {}", ip);
            } else {
                logger.error("❌ WebService信号机连接失败: {}", ip);
            }

            return connected;

        } catch (Exception e) {
            logger.error("连接WebService信号机异常: {}", ip, e);
            connectionStatus.put(controllerId, false);
            return false;
        }
    }

    /**
     * 断开连接
     */
    public void disconnectDevice(String controllerId) {
        try {
            connectionStatus.remove(controllerId);
            logger.info("WebService信号机断开连接: {}", controllerId);
        } catch (Exception e) {
            logger.error("断开WebService信号机连接异常: {}", controllerId, e);
        }
    }

    /**
     * 检查连接状态
     */
    public boolean isDeviceConnected(String controllerId) {
        return connectionStatus.getOrDefault(controllerId, false);
    }

    /**
     * 获取配时方案数据
     * @param param 传入参数对象
     * @return 设备方案读取成功或者失败
     */
    public boolean getDeviceStatus(JsonNode param) {
        try {
            String sigId = param.get("sigid").asText();
            String ip = param.get("ip").asText();
            String crossid = param.get("crossid").asText();
            String ipServer = param.get("ipServer").asText();
            logger.info("开始获取设备状态: IP={}, SigId={}", ip, sigId);

            // 创建查询请求
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("SIGID", sigId);
            node.put("SCHEMEID", param.get("SCHEMEID").asInt());
            node.put("CTRLMODE", param.get("CTRLMODE").asInt());
            node.put("SCHEMEID4DB", param.get("SCHEMEID4DB").asInt());

            logger.info("查询设备状态请求: {}", mapper.writeValueAsString(node));

            // 调用WebService获取配时方案信息（包含状态）
            boolean response = callGetSignalScheme(node, crossid, ip, ipServer);

            if (response) {
                logger.info("设备状态响应: {}", response);
                //return response;
                return true;
            } else {
                logger.warn("未获取到设备状态数据: IP={}, SigId={}", ip, sigId);
                //return null;
                return false;
            }

        } catch (Exception e) {
            logger.error("读取WebService信号机状态异常: ", e);
            //return null;
            return false;
        }
    }

    /**
     * 获取设备实时数据
     * @param ip 设备IP
     * @param sigId 信号机ID
     * @return 包含各种实时数据的Map
     */
//    public Map<String, JSONObject> getDeviceRuntimeData(String ip, Integer sigId) {
//        try {
//            logger.info("开始获取设备实时数据: IP={}, SigId={}", ip, sigId);
//
//            JSONObject request = new JSONObject();
//            request.put("SIGID", sigId);
//
//            // 获取多种实时数据
//            Map<String, JSONObject> runtimeDataMap = new HashMap<>();
//
//            // 1. 获取通道配置
//            JSONObject channelData = callGetChannels5U(request, ip);
//            if (channelData != null) {
//                runtimeDataMap.put("channels", channelData);
//                logger.info("✅ 通道配置获取成功");
//            }
//
//            // 2. 获取车道信息
//            JSONObject lanesData = callGetCrossLanes5U(request, ip);
//            if (lanesData != null) {
//                runtimeDataMap.put("lanes", lanesData);
//                logger.info("✅ 车道信息获取成功");
//            }
//
//            // 3. 获取相位信息
//            JSONObject phasesData = callGetCrossPhases5U(request, ip);
//            if (phasesData != null) {
//                runtimeDataMap.put("phases", phasesData);
//                logger.info("✅ 相位信息获取成功");
//            }
//
//            return runtimeDataMap;
//
//        } catch (Exception e) {
//            logger.error("读取WebService信号机实时数据异常: IP={}, SigId={}", ip, sigId, e);
//            return new HashMap<>();
//        }
//    }

    /**
     * 获取当前连接的设备数量
     */
    public int getConnectedDeviceCount() {
        return (int) connectionStatus.values().stream().filter(connected -> connected).count();
    }

    /**
     * 获取所有连接状态
     */
    public Map<String, Boolean> getAllConnectionStatus() {
        return new HashMap<>(connectionStatus);
    }

    // ================================================================
    // WebService调用方法（基于您提供的原始代码改造）
    // ================================================================

    /**
     * 测试HelloWorld连接
     */
    private boolean testHelloWorld(String ip) {
        try {
            logger.info("测试HelloWorld连接: {}", ip);

            HashMap<String, String> params = new HashMap<>();
            params.put("arg0", "");

            String result = callWebService(ip, "SayHello", params);
            logger.info("HelloWorld响应: {}", result);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(result);
            JsonNode dataNode = jsonNode.get("rows");
            if(dataNode!=null && dataNode.isArray()){
                ArrayNode rows = (ArrayNode) dataNode;
                if (rows != null) {
                    logger.info("成功获取到{}个交叉口数据", rows.size());
                    return true;
                } else {
                    logger.warn("HelloWorld响应中未找到rows数组");
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("HelloWorld测试失败: {}", ip, e);
            return false;
        }
    }

    /**
     * 获取配时方案（包含设备状态信息）
     */
    public boolean callGetSignalScheme(JsonNode request, String crossId, String ip, String ipServer) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> params = new HashMap<>();
            params.put("arg0", mapper.writeValueAsString(request));
            params.put("arg1", ip);

            String result = callWebService(ipServer, "GetSignalScheme", params);
            JsonNode response = mapper.readTree(result);

            if (response != null && "ok".equals(response.get("success").asText())) {
                BasicPhaseTable basicPhaseTable = new BasicPhaseTable();
                List<LampGroupParam> allLamps = dataConverter.getLampGroupStandardData(crossId);
                basicPhaseTable.setAllLamps(allLamps);
                SchemeData5U scheme5U = new SchemeData5U(basicPhaseTable);
                scheme5U.setSchemeInfo(response, crossId);
//                JsonNode phases = response.path("resultA").path("PhaseList");//response.get("PhaseList");
//                if (phases == null || !phases.isArray()) return false;
//                ArrayNode stageArray = (ArrayNode) phases;
//                // 转换灯组
//                Set<Integer> allLamps = new HashSet<>();
//                for(int i=0;i<stageArray.size();i++){
//                    JsonNode stage = stageArray.get(i);
//                    JsonNode lightgroups = stage.get("LanePassage");
//                    if (lightgroups == null || !lightgroups.isObject()) continue;
//                    Set<Integer> lamps = new HashSet<>();
//                    JsonNode north = lightgroups.get("North");
//                    JsonNode east = lightgroups.get("East");
//                    JsonNode south = lightgroups.get("South");
//                    JsonNode west = lightgroups.get("West");
//                    boolean updatedAll = false;
//                    if(i==0) updatedAll = true;
//                    parseDirection(crossId, "North", north, lamps, allLamps, updatedAll);
//                    parseDirection(crossId, "East", east, lamps, allLamps, updatedAll);
//                    parseDirection(crossId, "South", south, lamps, allLamps, updatedAll);
//                    parseDirection(crossId, "West", west, lamps,allLamps, updatedAll);
//                    allLamps.removeAll(lamps);
//                    logger.info("The stage {} lamps list is: {}", i+1, lamps);
//                    int mainSignalGroupNo = SignalGroupUtil.isMainSignalGroup(lamps);
//                    int rightSignalGroupNo = SignalGroupUtil.isRightSignalGroup(lamps);
//                    int pedestrianSignalGroupNo = SignalGroupUtil.isPedestrianSignalGroup(lamps);
//                    logger.info("The mainSignalGroupNo {}, rightSignalGroupNo {}, pedestrianSignalGroupNo {}", mainSignalGroupNo, rightSignalGroupNo, pedestrianSignalGroupNo);
//                }
//                if(allLamps.isEmpty()) return true;
                //logger.info("The remain stage lamps list is: {}", allLamps);
                // 转换信号组
                // 转换方案参数
                //return response;
                return true;
            } else {
                logger.warn("获取配时方案失败: {}", response != null ? response.get("message").asText() : "无响应");
                //return null;
            }
        } catch (Exception e) {
            logger.error("获取配时方案异常", e);
            //return null;
        }
        return false;
    }
    protected void parseDirection(String crossId, String direction, JsonNode data, Set<Integer> lamps, Set<Integer> allLamps, boolean updatedAll){
        try {
            parseFlow(crossId, direction, "Straight", data, lamps, allLamps, updatedAll);
            parseFlow(crossId, direction, "TurnLeft", data, lamps, allLamps, updatedAll);
            parseFlow(crossId, direction, "TurnRight", data, lamps, allLamps, updatedAll);
            parseFlow(crossId, direction, "Sidewalk", data, lamps, allLamps, updatedAll);
            parseFlow(crossId, direction, "NonMotorized", data, lamps, allLamps, updatedAll);
            parseFlow(crossId, direction, "TurnRound", data, lamps, allLamps, updatedAll);
            parseFlow(crossId, direction, "Extend1", data, lamps, allLamps, updatedAll);
            parseFlow(crossId, direction, "Extend2", data, lamps, allLamps, updatedAll);

        }catch (Exception e){e.printStackTrace();}
    }
    protected void parseFlow(String crossId, String direction, String flow, JsonNode data, Set<Integer> lamps, Set<Integer> allLamps, boolean updatedAll) throws Exception {
        if(!"Red".equals(data.get(flow).asText())){
            lamps.add(dataConverter.getLightGroupNoByDirectionFlow(crossId, direction, flow));
        }
        if(updatedAll){
            allLamps.add(dataConverter.getLightGroupNoByDirectionFlow(crossId, direction, flow));
        }
    }
    /**
     * 获取通道配置
     */
//    private JSONObject callGetChannels5U(JSONObject request, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", request.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService(ip, "ChannelConfigurationGet", params);
//            JSONObject response = JSON.parseObject(result);
//
//            if (response != null && "ok".equals(response.getString("success"))) {
//                return response;
//            } else {
//                logger.warn("获取通道配置失败: {}", response != null ? response.getString("message") : "无响应");
//                return null;
//            }
//        } catch (Exception e) {
//            logger.error("获取通道配置异常", e);
//            return null;
//        }
//    }

    /**
     * 获取车道信息
     */
//    private JSONObject callGetCrossLanes5U(JSONObject request, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", request.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService(ip, "getBasicDataLanesDB", params);
//            JSONObject response = JSON.parseObject(result);
//
//            if (response != null && "ok".equals(response.getString("success"))) {
//                return response;
//            } else {
//                logger.warn("获取车道信息失败: {}", response != null ? response.getString("message") : "无响应");
//                return null;
//            }
//        } catch (Exception e) {
//            logger.error("获取车道信息异常", e);
//            return null;
//        }
//    }

    /**
     * 获取相位信息
     */
//    private JSONObject callGetCrossPhases5U(JSONObject request, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", request.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService(ip, "getBasicDataPhasesDB", params);
//            JSONObject response = JSON.parseObject(result);
//
//            if (response != null && "ok".equals(response.getString("success"))) {
//                return response;
//            } else {
//                logger.warn("获取相位信息失败: {}", response != null ? response.getString("message") : "无响应");
//                return null;
//            }
//        } catch (Exception e) {
//            logger.error("获取相位信息异常", e);
//            return null;
//        }
//    }

    // ================================================================
    // 核心WebService调用方法（基于您的DynamicWebService改造）
    // ================================================================

    /**
     * 核心WebService调用方法
     */
    private String callWebService(String ip, String methodName, HashMap<String, String> params) throws Exception {
        String url = "http://" + ip + ":" + defaultPort + servicePath;

        logger.debug("调用WebService: URL={}, Method={}, Params={}", url, methodName, params);

        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        try {
            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(methodName, params), url);

            // Process the SOAP Response
            String result = extractResponseValue(soapResponse);

            logger.debug("WebService响应: {}", result);

            return result;

        } finally {
            soapConnection.close();
        }
    }

    /**
     * 创建SOAP请求
     */
    private SOAPMessage createSOAPRequest(String methodName, HashMap<String, String> params) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("web", nameSpace);

        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElement = soapBody.addChildElement("web:" + methodName);

        // 添加参数
        for (String key : params.keySet()) {
            soapBodyElement.addChildElement(key).addTextNode(params.get(key));
        }

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader(methodName, nameSpace + methodName);

        soapMessage.saveChanges();
        return soapMessage;
    }

    /**
     * 提取响应值
     */
    private String extractResponseValue(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(sourceContent, result);

        String xmlStr = stringWriter.toString();
        return getValueByTag(xmlStr, "return");
    }

    /**
     * 从XML中提取指定标签的值
     */
    private String getValueByTag(String xmlStr, String tag) {
        try {
            int iStart = xmlStr.indexOf("<" + tag + ">") + tag.length() + 2;
            int iEnd = xmlStr.indexOf("</" + tag + ">");
            return xmlStr.substring(iStart, iEnd);
        } catch (Exception e) {
            logger.error("解析XML响应失败", e);
            return "";
        }
    }

    // ================================================================
    // 便捷方法：提供简化的数据解析
    // ================================================================

    /**
     * 解析设备状态响应，提取关键信息
     */
//    public Map<String, Object> parseDeviceStatus(JSONObject statusResponse) {
//        Map<String, Object> statusInfo = new HashMap<>();
//
//        if (statusResponse == null) {
//            return statusInfo;
//        }
//
//        try {
//            statusInfo.put("success", statusResponse.getString("success"));
//            statusInfo.put("timestamp", LocalDateTime.now().toString());
//
//            if (statusResponse.containsKey("resultA")) {
//                JSONObject resultA = statusResponse.getJSONObject("resultA");
//                if (resultA != null) {
//                    statusInfo.put("currentScheme", resultA.getInteger("SchemeNo"));
//                    statusInfo.put("currentStage", resultA.getInteger("CurrentStage"));
//                    statusInfo.put("remainTime", resultA.getInteger("RemainTime"));
//                    statusInfo.put("controlMode", resultA.getInteger("ControlMode"));
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error("解析设备状态响应异常", e);
//            statusInfo.put("error", e.getMessage());
//        }
//
//        return statusInfo;
//    }

    /**
     * 解析运行时数据，提取关键信息
     */
//    public Map<String, Object> parseRuntimeData(Map<String, JSONObject> runtimeDataMap) {
//        Map<String, Object> runtimeInfo = new HashMap<>();
//
//        try {
//            runtimeInfo.put("timestamp", LocalDateTime.now().toString());
//            runtimeInfo.put("dataTypes", runtimeDataMap.keySet());
//
//            // 统计每种数据类型的条目数
//            for (Map.Entry<String, JSONObject> entry : runtimeDataMap.entrySet()) {
//                String dataType = entry.getKey();
//                JSONObject data = entry.getValue();
//
//                if (data != null && "ok".equals(data.getString("success"))) {
//                    runtimeInfo.put(dataType + "_status", "success");
//
//                    // 尝试提取数据条目数
//                    if (data.containsKey("resultA")) {
//                        JSONObject resultA = data.getJSONObject("resultA");
//                        if (resultA != null) {
//                            runtimeInfo.put(dataType + "_data", resultA.toString());
//                        }
//                    }
//                } else {
//                    runtimeInfo.put(dataType + "_status", "failed");
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error("解析运行时数据异常", e);
//            runtimeInfo.put("error", e.getMessage());
//        }
//
//        return runtimeInfo;
//    }

    @Override
    public String[] getSupportedDeviceTypes() {
        return new String[0];
    }

    @Override
    public String getSupportedProtocolVersion() {
        return "";
    }
}
