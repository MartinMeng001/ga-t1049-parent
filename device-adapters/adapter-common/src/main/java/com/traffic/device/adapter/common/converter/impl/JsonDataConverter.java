package com.traffic.device.adapter.common.converter.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.traffic.device.adapter.common.converter.base.AbstractDataConverter;
import com.traffic.gat1049.device.adapter.model.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * JSON数据转换器
 * 适用于返回JSON格式数据的设备
 */
@Component
public class JsonDataConverter extends AbstractDataConverter {

    @Override
    protected void doConvertToDeviceStatus(Object rawData, DeviceStatusData statusData) {
//        Map<String, Object> dataMap = parseToMap(rawData);
//
//        // 基础状态信息
//        statusData.setControlMode(safeGetInteger(dataMap, "controlMode"));
//        statusData.setCurrentPlanNo(safeGetInteger(dataMap, "currentPlan"));
//        statusData.setCurrentStageNo(safeGetInteger(dataMap, "currentStage"));
//        statusData.setStageRemainingTime(safeGetInteger(dataMap, "remainTime"));
//        statusData.setFaultStatus(safeGetInteger(dataMap, "faultStatus"));
//
//        // 通信状态（如果能获取到数据，说明通信正常）
//        statusData.setCommunicationStatus(1);
//
//        // 处理信号组状态
//        Object signalGroupsData = dataMap.get("signalGroups");
//        if (signalGroupsData != null) {
//            List<SignalGroupStatus> signalGroupStatuses = convertSignalGroupStatuses(signalGroupsData);
//            statusData.setSignalGroupStatuses(signalGroupStatuses);
//        }
    }

    @Override
    protected void doConvertToRuntimeData(Object rawData, DeviceRuntimeData runtimeData) {
//        Map<String, Object> dataMap = parseToMap(rawData);
//
//        // 实时通道数据
//        Object channelsData = dataMap.get("channels");
//        if (channelsData != null) {
//            List<ChannelStatus> channels = convertChannelStatuses(channelsData);
//            runtimeData.setChannels(channels);
//        }
//
//        // 实时检测器数据
//        Object detectorsData = dataMap.get("detectors");
//        if (detectorsData != null) {
//            List<DetectorStatus> detectors = convertDetectorStatuses(detectorsData);
//            runtimeData.setDetectors(detectors);
//        }
    }

    @Override
    protected void doConvertToCommandResult(Object response, DeviceCommand command, CommandResult result) {
        Map<String, Object> responseMap = parseToMap(response);

        // 判断命令执行是否成功
        Boolean success = safeGetBoolean(responseMap, "success");
        if (success == null) {
            // 尝试其他常见的成功标识
            String status = safeGetString(responseMap, "status");
            success = "ok".equalsIgnoreCase(status) || "success".equalsIgnoreCase(status);
        }

        result.setSuccess(success != null ? success : false);

        // 获取返回消息
        String message = safeGetString(responseMap, "message");
        if (message == null) {
            message = safeGetString(responseMap, "msg");
        }
        if (message == null) {
            message = success != null && success ? "命令执行成功" : "命令执行失败";
        }
        result.setMessage(message);

        // 获取错误代码
        String errorCode = safeGetString(responseMap, "errorCode");
        if (errorCode == null) {
            errorCode = safeGetString(responseMap, "code");
        }
        result.setErrorCode(errorCode);

        // 获取返回数据
//        Object data = responseMap.get("data");
//        if (data != null) {
//            result.setData(data);
//        }
    }

    @Override
    public Object convertFromConfigData(DeviceConfigData configData) {
        // 将标准配置数据转换为JSON格式
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonConfig = mapper.createObjectNode();
//
//        if (configData.getCrossParam() != null) {
//            jsonConfig.put("crossParam", convertCrossParam(configData.getCrossParam()));
//        }
//
//        if (configData.getSignalGroups() != null) {
//            jsonConfig.put("signalGroups", convertSignalGroups(configData.getSignalGroups()));
//        }
//
//        if (configData.getStages() != null) {
//            jsonConfig.put("stages", convertStages(configData.getStages()));
//        }
//
//        if (configData.getPlans() != null) {
//            jsonConfig.put("plans", convertPlans(configData.getPlans()));
//        }

        return jsonConfig;
    }

    @Override
    protected boolean doValidateData(Object data) {
        if (data == null) {
            return false;
        }

        try {
            Map<String, Object> dataMap = parseToMap(data);
            return dataMap != null && !dataMap.isEmpty();
        } catch (Exception e) {
            logger.warn("数据验证失败", e);
            return false;
        }
    }

    @Override
    protected String doExtractErrorMessage(Object errorData) {
        try {
            Map<String, Object> errorMap = parseToMap(errorData);

            String message = safeGetString(errorMap, "error");
            if (message == null) {
                message = safeGetString(errorMap, "message");
            }
            if (message == null) {
                message = safeGetString(errorMap, "msg");
            }

            return message != null ? message : "未知错误";

        } catch (Exception e) {
            return errorData.toString();
        }
    }

    // ============ 私有辅助方法 ============

    /**
     * 将各种数据类型转换为Map
     */
    private Map<String, Object> parseToMap(Object data) {
        if (data == null) {
            return new HashMap<>();
        }

        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        return null;
//        if (data instanceof String) {
//            try {
//                JSONObject json = JSON.parseObject((String) data);
//                return json != null ? json : new HashMap<>();
//            } catch (Exception e) {
//                logger.warn("JSON解析失败: {}", data, e);
//                return new HashMap<>();
//            }
//        }
//
//        if (data instanceof JSONObject) {
//            return (JSONObject) data;
//        }
//
//        // 其他类型尝试转换为JSON
//        try {
//            String jsonStr = JSON.toJSONString(data);
//            JSONObject json = JSON.parseObject(jsonStr);
//            return json != null ? json : new HashMap<>();
//        } catch (Exception e) {
//            logger.warn("对象转换为JSON失败: {}", data.getClass(), e);
//            return new HashMap<>();
//        }
    }

    /**
     * 转换信号组状态
     */
//    private List<SignalGroupStatus> convertSignalGroupStatuses(Object signalGroupsData) {
//        List<SignalGroupStatus> statuses = new ArrayList<>();
//
//        try {
//            if (signalGroupsData instanceof List) {
//                List<?> list = (List<?>) signalGroupsData;
//                for (Object item : list) {
//                    Map<String, Object> itemMap = parseToMap(item);
//                    SignalGroupStatus status = new SignalGroupStatus();
//                    status.setSignalGroupNo(safeGetInteger(itemMap, "signalGroupNo"));
//                    status.setCurrentColor(safeGetInteger(itemMap, "currentColor"));
//                    status.setRemainingTime(safeGetInteger(itemMap, "remainingTime"));
//                    statuses.add(status);
//                }
//            }
//        } catch (Exception e) {
//            logger.warn("转换信号组状态失败", e);
//        }
//
//        return statuses;
//    }

    /**
     * 转换通道状态
     */
//    private List<ChannelStatus> convertChannelStatuses(Object channelsData) {
//        List<ChannelStatus> channels = new ArrayList<>();
//
//        try {
//            if (channelsData instanceof List) {
//                List<?> list = (List<?>) channelsData;
//                for (Object item : list) {
//                    Map<String, Object> itemMap = parseToMap(item);
//                    ChannelStatus channel = new ChannelStatus();
//                    channel.setChannelNo(safeGetInteger(itemMap, "channelNo"));
//                    channel.setStatus(safeGetInteger(itemMap, "status"));
//                    channel.setCurrent(safeGetInteger(itemMap, "current"));
//                    channels.add(channel);
//                }
//            }
//        } catch (Exception e) {
//            logger.warn("转换通道状态失败", e);
//        }
//
//        return channels;
//    }

    /**
     * 转换检测器状态
     */
//    private List<DetectorStatus> convertDetectorStatuses(Object detectorsData) {
//        List<DetectorStatus> detectors = new ArrayList<>();
//
//        try {
//            if (detectorsData instanceof List) {
//                List<?> list = (List<?>) detectorsData;
//                for (Object item : list) {
//                    Map<String, Object> itemMap = parseToMap(item);
//                    DetectorStatus detector = new DetectorStatus();
//                    detector.setDetectorNo(safeGetInteger(itemMap, "detectorNo"));
//                    detector.setStatus(safeGetInteger(itemMap, "status"));
//                    detector.setVehicleCount(safeGetInteger(itemMap, "vehicleCount"));
//                    detectors.add(detector);
//                }
//            }
//        } catch (Exception e) {
//            logger.warn("转换检测器状态失败", e);
//        }
//
//        return detectors;
//    }

    /**
     * 转换路口参数
     */
    private Object convertCrossParam(Object crossParam) {
        // 根据具体需求实现
        return crossParam;
    }

    /**
     * 转换信号组配置
     */
    private Object convertSignalGroups(List<?> signalGroups) {
        // 根据具体需求实现
        return signalGroups;
    }

    /**
     * 转换阶段配置
     */
    private Object convertStages(List<?> stages) {
        // 根据具体需求实现
        return stages;
    }

    /**
     * 转换配时方案
     */
    private Object convertPlans(List<?> plans) {
        // 根据具体需求实现
        return plans;
    }
}
