package com.traffic.device.adapter.webservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * WebService客户端
 * 封装原有的DynamicWebService功能
 */
@Component
public class WebServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(WebServiceClient.class);

    private String serverUrl = "localhost:8080"; // 默认地址
    private String nameSpace = "http://webservice/";

    public void updateServerUrl(String serverIp) {
        this.serverUrl = serverIp + ":8080"; // 假设端口为8080
    }

    public boolean testConnection() {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("arg0", "");

            String result = callWebService("SayHello", params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonnode = mapper.readTree(result);
            JsonNode arraynode = jsonnode.get("rows");

            return jsonnode != null && arraynode != null;
        } catch (Exception e) {
            logger.error("WebService连接测试失败", e);
            return false;
        }
    }

//    public JSONObject getChannels5U(JSONObject reqObj, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", reqObj.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService("ChannelConfigurationGet", params);
//            return JSON.parseObject(result);
//        } catch (Exception e) {
//            logger.error("获取通道配置失败", e);
//            return null;
//        }
//    }

//    public JSONObject getSchemes5U(JSONObject reqObj, String ip, boolean beSensor) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", reqObj.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService("GetSignalScheme", params);
//            return JSON.parseObject(result);
//        } catch (Exception e) {
//            logger.error("获取配时方案失败", e);
//            return null;
//        }
//    }

//    public int setSchemes5U(JSONObject reqObj, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", reqObj.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService("SetSignalSchemeNew", params);
//            JSONObject obj = JSON.parseObject(result);
//
//            return "ok".equals(obj.getString("success")) ? 1 : 0;
//        } catch (Exception e) {
//            logger.error("设置配时方案失败", e);
//            return 0;
//        }
//    }

//    public int setDayPlan5U(JSONObject reqObj, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", reqObj.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService("DailyPlanSet", params);
//            JSONObject obj = JSON.parseObject(result);
//
//            return "ok".equals(obj.getString("success")) ? 1 : 0;
//        } catch (Exception e) {
//            logger.error("设置日计划失败", e);
//            return 0;
//        }
//    }

//    public int setWeekSchedule5U(JSONObject reqObj, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", reqObj.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService("DailyScheduleForWeeklySet", params);
//            JSONObject obj = JSON.parseObject(result);
//
//            return "ok".equals(obj.getString("success")) ? 1 : 0;
//        } catch (Exception e) {
//            logger.error("设置周计划失败", e);
//            return 0;
//        }
//    }

//    public JSONObject getCrossLanes5U(JSONObject reqObj, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", reqObj.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService("getBasicDataLanesDB", params);
//            return JSON.parseObject(result);
//        } catch (Exception e) {
//            logger.error("获取车道信息失败", e);
//            return null;
//        }
//    }

//    public JSONObject getCrossPhases5U(JSONObject reqObj, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", reqObj.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService("getBasicDataPhasesDB", params);
//            return JSON.parseObject(result);
//        } catch (Exception e) {
//            logger.error("获取相位信息失败", e);
//            return null;
//        }
//    }

//    public int setCustomGuardControl(JSONObject guard) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", guard.toJSONString());
//
//            String result = callWebService("SetCustomGuardControl", params);
//            JSONObject obj = JSON.parseObject(result);
//
//            return "ok".equals(obj.getString("success")) ? 1 : 0;
//        } catch (Exception e) {
//            logger.error("设置自定义手动控制失败", e);
//            return 0;
//        }
//    }

//    public int setGuardControl(JSONObject guard) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", guard.toJSONString());
//
//            String result = callWebService("SetGuardControl", params);
//            JSONObject obj = JSON.parseObject(result);
//
//            return "ok".equals(obj.getString("success")) ? 1 : 0;
//        } catch (Exception e) {
//            logger.error("设置手动控制失败", e);
//            return 0;
//        }
//    }

//    public int setSpecialGuardControl(JSONObject reqObj, String ip) {
//        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("arg0", reqObj.toJSONString());
//            params.put("arg1", ip);
//
//            String result = callWebService("SpecialHoursSet", params);
//            JSONObject obj = JSON.parseObject(result);
//
//            return "ok".equals(obj.getString("success")) ? 1 : 0;
//        } catch (Exception e) {
//            logger.error("设置特殊时段控制失败", e);
//            return 0;
//        }
//    }

//    public int guardControl(String ip, int mode, int period) {
//        JSONObject ret = new JSONObject();
//        ret.put("CheckSignalIP", ip);
//        ret.put("mode", mode);
//        ret.put("period", period);
//        return setGuardControl(ret);
//    }

    // 核心WebService调用方法
    private String callWebService(String methodName, HashMap<String, String> params) throws Exception {
        String url = "http://" + serverUrl + "/SignalListenServer/SignalListenDelegate?wsdl";

        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(methodName, params), url);

        // Process the SOAP Response
        String result = extractResponseValue(soapResponse);
        soapConnection.close();

        return result;
    }

    private SOAPMessage createSOAPRequest(String methodName, HashMap<String, String> params) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("web", nameSpace);

        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElement = soapBody.addChildElement("web:" + methodName);

        for (String key : params.keySet()) {
            soapBodyElement.addChildElement(key).addTextNode(params.get(key));
        }

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader(methodName, nameSpace + methodName);

        soapMessage.saveChanges();
        return soapMessage;
    }

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
}
