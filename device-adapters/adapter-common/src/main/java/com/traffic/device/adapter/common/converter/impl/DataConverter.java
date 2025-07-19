package com.traffic.device.adapter.common.converter.impl;

import com.traffic.device.adapter.common.converter.base.AbstractDataConverter;
import com.traffic.gat1049.device.adapter.model.*;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.service.interfaces.LampGroupService;
import com.traffic.gat1049.service.interfaces.SignalGroupService;
import com.traffic.gat1049.protocol.model.intersection.LampGroupParam;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LampGroupType;
import com.traffic.gat1049.exception.BusinessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 增强型数据转换器
 * 支持从ga-t1049-common注入基础数据进行转换
 */
@Component
public class DataConverter extends AbstractDataConverter {

    private static final Logger logger = LoggerFactory.getLogger(DataConverter.class);

    // 注入ga-t1049-common的基础数据服务
    @Autowired
    private LampGroupService lampGroupService;

    @Autowired
    private SignalGroupService signalGroupService;

    // 缓存基础数据，避免频繁查询
    private final Map<String, List<LampGroupParam>> lampGroupCache = new HashMap<>();
    private final Map<String, List<SignalGroupParam>> signalGroupCache = new HashMap<>();
    private final Map<String, Map<Integer, LampGroupParam>> lampGroupMapCache = new HashMap<>();

    @Override
    protected void doConvertToDeviceStatus(Object rawData, DeviceStatusData statusData) {
//        Map<String, Object> dataMap = parseToMap(rawData);
//        String controllerId = statusData.getControllerId();
//
//        try {
//            // 基础状态信息
//            statusData.setControlMode(safeGetInteger(dataMap, "controlMode"));
//            statusData.setCurrentPlanNo(safeGetInteger(dataMap, "currentPlan"));
//            statusData.setCurrentStageNo(safeGetInteger(dataMap, "currentStage"));
//            statusData.setStageRemainingTime(safeGetInteger(dataMap, "remainTime"));
//            statusData.setFaultStatus(safeGetInteger(dataMap, "faultStatus"));
//            statusData.setCommunicationStatus(1); // 能获取到数据说明通信正常
//
//            // 处理信号组状态 - 使用基础数据进行转换和验证
//            Object signalGroupsData = dataMap.get("signalGroups");
//            if (signalGroupsData != null) {
//                List<SignalGroupStatus> signalGroupStatuses =
//                        convertSignalGroupStatusesWithValidation(controllerId, signalGroupsData);
//                statusData.setSignalGroupStatuses(signalGroupStatuses);
//            }
//
//        } catch (Exception e) {
//            logger.error("转换设备状态失败: controllerId={}", controllerId, e);
//            throw new RuntimeException("设备状态转换失败", e);
//        }
    }

    @Override
    protected void doConvertToRuntimeData(Object rawData, DeviceRuntimeData runtimeData) {
//        Map<String, Object> dataMap = parseToMap(rawData);
//        String controllerId = runtimeData.getControllerId();
//
//        try {
//            // 处理灯组实时数据
//            Object lampGroupsData = dataMap.get("lampGroups");
//            if (lampGroupsData != null) {
//                List<LampGroupStatus> lampGroupStatuses =
//                        convertLampGroupStatusesWithStandardData(controllerId, lampGroupsData);
//                runtimeData.setLampGroups(lampGroupStatuses);
//            }
//
//            // 处理通道数据
//            Object channelsData = dataMap.get("channels");
//            if (channelsData != null) {
//                List<ChannelStatus> channels = convertChannelStatuses(channelsData);
//                runtimeData.setChannels(channels);
//            }
//
//            // 处理检测器数据
//            Object detectorsData = dataMap.get("detectors");
//            if (detectorsData != null) {
//                List<DetectorStatus> detectors = convertDetectorStatuses(detectorsData);
//                runtimeData.setDetectors(detectors);
//            }
//
//        } catch (Exception e) {
//            logger.error("转换实时数据失败: controllerId={}", controllerId, e);
//            throw new RuntimeException("实时数据转换失败", e);
//        }
    }

    @Override
    protected void doConvertToCommandResult(Object response, DeviceCommand command, CommandResult result) {
//        Map<String, Object> responseMap = parseToMap(response);
//
//        // 基础转换逻辑
//        Boolean success = safeGetBoolean(responseMap, "success");
//        if (success == null) {
//            String status = safeGetString(responseMap, "status");
//            success = "ok".equalsIgnoreCase(status) || "success".equalsIgnoreCase(status);
//        }
//
//        result.setSuccess(success != null ? success : false);
//        result.setMessage(extractMessage(responseMap));
//        result.setErrorCode(safeGetString(responseMap, "errorCode"));
//        result.setData(responseMap.get("data"));
    }

    @Override
    public Object convertFromConfigData(DeviceConfigData configData) {
        Map<String, Object> deviceConfig = new HashMap<>();

        try {
            String crossId = extractCrossIdFromConfig(configData);

            // 转换路口参数
            if (configData.getCrossParam() != null) {
                deviceConfig.put("crossParam", configData.getCrossParam());
            }

            // 转换灯组配置 - 结合标准数据
//            if (!CollectionUtils.isEmpty(configData.getLampGroups())) {
//                List<Object> enhancedLampGroups = enhanceLampGroupConfig(crossId, configData.getLampGroups());
//                deviceConfig.put("lampGroups", enhancedLampGroups);
//            }
//
//            // 转换信号组配置 - 结合标准数据
//            if (!CollectionUtils.isEmpty(configData.getSignalGroups())) {
//                List<Object> enhancedSignalGroups = enhanceSignalGroupConfig(crossId, configData.getSignalGroups());
//                deviceConfig.put("signalGroups", enhancedSignalGroups);
//            }

            // 转换配时方案
            if (!CollectionUtils.isEmpty(configData.getPlans())) {
                deviceConfig.put("plans", configData.getPlans());
            }

            // 转换阶段配置
            if (!CollectionUtils.isEmpty(configData.getStages())) {
                deviceConfig.put("stages", configData.getStages());
            }

            // 转换检测器配置
            if (!CollectionUtils.isEmpty(configData.getDetectors())) {
                deviceConfig.put("detectors", configData.getDetectors());
            }

        } catch (Exception e) {
            logger.error("转换配置数据失败", e);
            throw new RuntimeException("配置数据转换失败", e);
        }

        return deviceConfig;
    }

    // ============ 基础数据辅助方法 ============

    /**
     * 获取路口的灯组标准数据
     */
    public List<LampGroupParam> getLampGroupStandardData(String crossId) {
        try {
            if (!lampGroupCache.containsKey(crossId)) {
                List<LampGroupParam> lampGroups = lampGroupService.findAllBasicByCrossId(crossId);
                lampGroupCache.put(crossId, lampGroups);

                // 同时构建Map缓存，便于按序号查找
                Map<Integer, LampGroupParam> lampGroupMap = lampGroups.stream()
                        .collect(Collectors.toMap(LampGroupParam::getLampGroupNo, lg -> lg));
                lampGroupMapCache.put(crossId, lampGroupMap);

                logger.debug("缓存路口灯组数据: crossId={}, count={}", crossId, lampGroups.size());
            }
            return lampGroupCache.get(crossId);
        } catch (BusinessException e) {
            logger.error("获取灯组标准数据失败: crossId={}", crossId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取路口的信号组标准数据
     */
    public List<SignalGroupParam> getSignalGroupStandardData(String crossId) {
        try {
            if (!signalGroupCache.containsKey(crossId)) {
                List<SignalGroupParam> signalGroups = signalGroupService.findByCrossId(crossId);
                signalGroupCache.put(crossId, signalGroups);
                logger.debug("缓存路口信号组数据: crossId={}, count={}", crossId, signalGroups.size());
            }
            return signalGroupCache.get(crossId);
        } catch (BusinessException e) {
            logger.error("获取信号组标准数据失败: crossId={}", crossId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据灯组序号获取灯组标准信息
     */
    public LampGroupParam getLampGroupByNo(String crossId, Integer lampGroupNo) {
        getLampGroupStandardData(crossId); // 确保数据已缓存
        Map<Integer, LampGroupParam> lampGroupMap = lampGroupMapCache.get(crossId);
        return lampGroupMap != null ? lampGroupMap.get(lampGroupNo) : null;
    }

    /**
     * 根据方向和类型获取灯组
     */
    public List<LampGroupParam> getLampGroupsByDirectionAndType(String crossId, Direction direction, LampGroupType type) {
        List<LampGroupParam> allLampGroups = getLampGroupStandardData(crossId);
        return allLampGroups.stream()
                .filter(lg -> direction.equals(lg.getDirection()) && type.equals(lg.getType()))
                .collect(Collectors.toList());
    }

    // ============ 转换逻辑增强方法 ============

    /**
     * 转换信号组状态并进行验证
     */
//    private List<SignalGroupStatus> convertSignalGroupStatusesWithValidation(String controllerId, Object signalGroupsData) {
//        List<SignalGroupStatus> statuses = new ArrayList<>();
//        String crossId = extractCrossIdFromControllerId(controllerId);
//
//        try {
//            // 获取标准信号组数据
//            List<SignalGroupParam> standardSignalGroups = getSignalGroupStandardData(crossId);
//            Set<Integer> validSignalGroupNos = standardSignalGroups.stream()
//                    .map(SignalGroupParam::getSignalGroupNo)
//                    .collect(Collectors.toSet());
//
//            if (signalGroupsData instanceof List) {
//                List<?> list = (List<?>) signalGroupsData;
//                for (Object item : list) {
//                    Map<String, Object> itemMap = parseToMap(item);
//                    Integer signalGroupNo = safeGetInteger(itemMap, "signalGroupNo");
//
//                    // 验证信号组序号是否有效
//                    if (signalGroupNo != null && validSignalGroupNos.contains(signalGroupNo)) {
//                        SignalGroupStatus status = new SignalGroupStatus();
//                        status.setSignalGroupNo(signalGroupNo);
//                        status.setLampStatus(safeGetString(itemMap, "lampStatus"));
//                        status.setRemainTime(safeGetInteger(itemMap, "remainTime"));
//                        statuses.add(status);
//                    } else {
//                        logger.warn("无效的信号组序号: controllerId={}, signalGroupNo={}", controllerId, signalGroupNo);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error("转换信号组状态失败: controllerId={}", controllerId, e);
//        }
//
//        return statuses;
//    }

    /**
     * 转换灯组状态并结合标准数据
     */
//    private List<LampGroupStatus> convertLampGroupStatusesWithStandardData(String controllerId, Object lampGroupsData) {
//        List<LampGroupStatus> statuses = new ArrayList<>();
//        String crossId = extractCrossIdFromControllerId(controllerId);
//
//        try {
//            if (lampGroupsData instanceof List) {
//                List<?> list = (List<?>) lampGroupsData;
//                for (Object item : list) {
//                    Map<String, Object> itemMap = parseToMap(item);
//                    Integer lampGroupNo = safeGetInteger(itemMap, "lampGroupNo");
//
//                    if (lampGroupNo != null) {
//                        // 获取灯组标准信息
//                        LampGroupParam standardLampGroup = getLampGroupByNo(crossId, lampGroupNo);
//
//                        LampGroupStatus status = new LampGroupStatus();
//                        status.setLampGroupNo(lampGroupNo);
//                        status.setStatus(safeGetInteger(itemMap, "status"));
//                        status.setCurrent(safeGetInteger(itemMap, "current"));
//                        status.setVoltage(safeGetInteger(itemMap, "voltage"));
//
//                        // 添加标准数据信息
//                        if (standardLampGroup != null) {
//                            status.setDirection(standardLampGroup.getDirection());
//                            status.setType(standardLampGroup.getType());
//                        }
//
//                        statuses.add(status);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error("转换灯组状态失败: controllerId={}", controllerId, e);
//        }
//
//        return statuses;
//    }

    /**
     * 增强灯组配置
     */
//    private List<Object> enhanceLampGroupConfig(String crossId, List<?> lampGroups) {
//        List<Object> enhanced = new ArrayList<>();
//
//        // 获取标准灯组数据
//        List<LampGroupParam> standardLampGroups = getLampGroupStandardData(crossId);
//        Map<Integer, LampGroupParam> standardMap = standardLampGroups.stream()
//                .collect(Collectors.toMap(LampGroupParam::getLampGroupNo, lg -> lg));
//
//        for (Object lampGroup : lampGroups) {
//            Map<String, Object> enhancedLampGroup = new HashMap<>();
//            Map<String, Object> originalMap = parseToMap(lampGroup);
//
//            // 复制原始数据
//            enhancedLampGroup.putAll(originalMap);
//
//            // 添加标准数据
//            Integer lampGroupNo = safeGetInteger(originalMap, "lampGroupNo");
//            if (lampGroupNo != null && standardMap.containsKey(lampGroupNo)) {
//                LampGroupParam standard = standardMap.get(lampGroupNo);
//                enhancedLampGroup.put("standardDirection", standard.getDirection());
//                enhancedLampGroup.put("standardType", standard.getType());
//                enhancedLampGroup.put("standardChannelNos", standard.getChannelNoList());
//            }
//
//            enhanced.add(enhancedLampGroup);
//        }
//
//        return enhanced;
//    }

    /**
     * 增强信号组配置
     */
//    private List<Object> enhanceSignalGroupConfig(String crossId, List<?> signalGroups) {
//        List<Object> enhanced = new ArrayList<>();
//
//        // 获取标准信号组数据
//        List<SignalGroupParam> standardSignalGroups = getSignalGroupStandardData(crossId);
//        Map<Integer, SignalGroupParam> standardMap = standardSignalGroups.stream()
//                .collect(Collectors.toMap(SignalGroupParam::getSignalGroupNo, sg -> sg));
//
//        for (Object signalGroup : signalGroups) {
//            Map<String, Object> enhancedSignalGroup = new HashMap<>();
//            Map<String, Object> originalMap = parseToMap(signalGroup);
//
//            // 复制原始数据
//            enhancedSignalGroup.putAll(originalMap);
//
//            // 添加标准数据
//            Integer signalGroupNo = safeGetInteger(originalMap, "signalGroupNo");
//            if (signalGroupNo != null && standardMap.containsKey(signalGroupNo)) {
//                SignalGroupParam standard = standardMap.get(signalGroupNo);
//                enhancedSignalGroup.put("standardName", standard.getName());
//                enhancedSignalGroup.put("standardLampGroups", standard.getLampGroupNoList());
//                enhancedSignalGroup.put("minGreen", standard.getMinGreen());
//                enhancedSignalGroup.put("maxGreen", standard.getMaxGreen());
//            }
//
//            enhanced.add(enhancedSignalGroup);
//        }
//
//        return enhanced;
//    }

    // ============ 工具方法 ============

    /**
     * 从控制器ID提取路口ID
     */
    private String extractCrossIdFromControllerId(String controllerId) {
        // 根据您的业务规则实现
        // 例如：控制器ID可能包含路口ID，或者需要查询映射表
        return controllerId; // 简化实现，根据实际情况调整
    }

    /**
     * 从配置数据中提取路口ID
     */
    private String extractCrossIdFromConfig(DeviceConfigData configData) {
        if (configData.getCrossParam() != null) {
            // 假设CrossParam有getCrossId方法
            return configData.getCrossParam().toString(); // 根据实际结构调整
        }
        return null;
    }
    /*
    标准1049，双百定义灯组
     */
    public int getLightGroupNoByDirectionFlow(String crossId, String direction, String flow) throws Exception {
        Direction direction5U = Direction.from5UDirection(direction);
        LampGroupType lampGroupType5U = LampGroupType.from5UType(flow);
        LampGroupParam lampGroupParam = lampGroupService.findByCrossIdAndDirectionAndType(crossId, direction5U, lampGroupType5U);
        return lampGroupParam.getLampGroupNo();
    }
    /**
     * 清除缓存
     */
    public void clearCache() {
        lampGroupCache.clear();
        //signalGroupCache.clear();
        lampGroupMapCache.clear();
        logger.info("清除转换器缓存");
    }

    /**
     * 清除指定路口的缓存
     */
    public void clearCache(String crossId) {
        lampGroupCache.remove(crossId);
        //signalGroupCache.remove(crossId);
        lampGroupMapCache.remove(crossId);
        logger.info("清除转换器缓存: crossId={}", crossId);
    }

    // 继承自父类的其他方法保持不变...
}
