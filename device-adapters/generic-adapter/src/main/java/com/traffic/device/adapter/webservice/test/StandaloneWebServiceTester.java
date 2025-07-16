package com.traffic.device.adapter.webservice.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 独立的WebService连接测试工具
 * 可以不依赖Spring框架直接运行
 */
public class StandaloneWebServiceTester {

    public static void main(String[] args) {
        StandaloneWebServiceTester tester = new StandaloneWebServiceTester();

        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入WebService服务器IP地址: ");
        String ip = scanner.nextLine().trim();

        if (ip.isEmpty()) {
            System.out.println("IP地址不能为空");
            return;
        }

        System.out.println("开始测试WebService连接: " + ip);
        tester.testConnection(ip);

        scanner.close();
    }

    public void testConnection(String ip) {
        try {
            String url = "http://" + ip + ":8080/SignalListenServer/SignalListenDelegate?wsdl";
            System.out.println("WebService URL: " + url);

            // 测试SayHello方法
            boolean helloResult = testSayHello(url);

            if (helloResult) {
                System.out.println("✅ SayHello测试成功");

                // 继续测试其他方法
                testOtherMethods(url);

            } else {
                System.out.println("❌ SayHello测试失败");
            }

        } catch (Exception e) {
            System.out.println("❌ 连接测试异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean testSayHello(String url) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("arg0", "");

            String result = callWebService(url, "SayHello", params);
            System.out.println("SayHello响应: " + result);

            JSONObject obj = JSON.parseObject(result);
            JSONArray rows = obj.getJSONArray("rows");

            if (rows != null) {
                System.out.println("获取到 " + rows.size() + " 个交叉口数据");
                return true;
            } else {
                System.out.println("响应中未找到rows数组");
                return false;
            }

        } catch (Exception e) {
            System.out.println("SayHello测试异常: " + e.getMessage());
            return false;
        }
    }

    private void testOtherMethods(String url) {
        System.out.println("\n开始测试其他WebService方法...");

        // 测试获取通道配置
        testMethod(url, "ChannelConfigurationGet", "通道配置获取");

        // 测试获取车道信息
        testMethod(url, "getBasicDataLanesDB", "车道信息获取");

        // 测试获取相位信息
        testMethod(url, "getBasicDataPhasesDB", "相位信息获取");

        // 测试获取配时方案
        testMethod(url, "GetSignalScheme", "配时方案获取");
    }

    private void testMethod(String url, String methodName, String description) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("arg0", "{\"SIGID\":1}"); // 测试用的JSON参数
            params.put("arg1", ""); // IP参数，某些方法需要

            String result = callWebService(url, methodName, params);

            if (result != null && !result.isEmpty()) {
                JSONObject obj = JSON.parseObject(result);
                if ("ok".equals(obj.getString("success"))) {
                    System.out.println("✅ " + description + " 成功");
                } else {
                    System.out.println("⚠️ " + description + " 失败: " + obj.getString("message"));
                }
            } else {
                System.out.println("⚠️ " + description + " 无响应数据");
            }

        } catch (Exception e) {
            System.out.println("❌ " + description + " 异常: " + e.getMessage());
        }
    }

    private String callWebService(String url, String methodName, HashMap<String, String> params) throws Exception {
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

        String nameSpace = "http://webservice/";

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
            System.out.println("解析XML响应失败: " + e.getMessage());
            return "";
        }
    }
}
