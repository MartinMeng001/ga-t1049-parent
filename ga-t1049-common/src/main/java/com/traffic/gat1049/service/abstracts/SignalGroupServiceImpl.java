package com.traffic.gat1049.service.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.protocol.model.runtime.CrossSignalGroupStatus;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;
import com.traffic.gat1049.protocol.util.LampStatusUtil;
import com.traffic.gat1049.service.interfaces.SignalGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 信号组服务实现
 * 更新以支持GA/T 1049.2最新标准
 */
public class SignalGroupServiceImpl implements SignalGroupService {

    private static final Logger logger = LoggerFactory.getLogger(SignalGroupServiceImpl.class);

    /** 时间格式：精确到毫秒 YYYY-MM-DD hh:mm:ss.SSS */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();

    // 信号组参数存储 - 使用"crossId:signalGroupNo"作为key
    private final Map<String, SignalGroupParam> signalGroupStorage = new ConcurrentHashMap<>();

    // 信号组状态存储 - 使用crossId作为key，存储整个路口的信号组状态
    private final Map<String, CrossSignalGroupStatus> signalGroupStatusStorage = new ConcurrentHashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public SignalGroupServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        initializeSampleData();
    }

    @Override
    public List<SignalGroupParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataPrider.getSignalGroupsByCrossId(crossId);
    }

    @Override
    public SignalGroupParam findByCrossIdAndSignalGroupNo(String crossId, Integer signalGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (signalGroupNo == null) {
            throw new ValidationException("signalGroupNo", "信号组序号不能为空");
        }

        SignalGroupParam signalGroup = dataPrider.getSignalGroupByCrossIdAndNo(crossId, signalGroupNo.toString());
        if (signalGroup == null) {
            throw new DataNotFoundException("SignalGroupParam",
                    String.format("crossId=%s, signalGroupNo=%d", crossId, signalGroupNo));
        }

        return signalGroup;
    }

    @Override
    public SignalGroupParam save(SignalGroupParam signalGroupParam) throws BusinessException {
        validateSignalGroupParam(signalGroupParam);

        String key = buildKey(signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

        if (signalGroupStorage.containsKey(key)) {
            throw new ValidationException("signalGroupParam",
                    String.format("信号组已存在: crossId=%s, signalGroupNo=%d",
                            signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo()));
        }

        signalGroupStorage.put(key, signalGroupParam);

        // 初始化信号组状态
        initializeSignalGroupStatus(signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

        logger.info("保存信号组参数: crossId={}, signalGroupNo={}",
                signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

        return signalGroupParam;
    }

    @Override
    public SignalGroupParam update(SignalGroupParam signalGroupParam) throws BusinessException {
        validateSignalGroupParam(signalGroupParam);

        String key = buildKey(signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

        if (!signalGroupStorage.containsKey(key)) {
            throw new DataNotFoundException("SignalGroupParam",
                    String.format("crossId=%s, signalGroupNo=%d",
                            signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo()));
        }

        signalGroupStorage.put(key, signalGroupParam);

        logger.info("更新信号组参数: crossId={}, signalGroupNo={}",
                signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

        return signalGroupParam;
    }

    @Override
    public void delete(String crossId, Integer signalGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (signalGroupNo == null) {
            throw new ValidationException("signalGroupNo", "信号组序号不能为空");
        }

        String key = buildKey(crossId, signalGroupNo);
        SignalGroupParam removed = signalGroupStorage.remove(key);
        if (removed == null) {
            throw new DataNotFoundException("SignalGroupParam",
                    String.format("crossId=%s, signalGroupNo=%d", crossId, signalGroupNo));
        }

        // 同时从路口信号组状态中移除该信号组
        removeSignalGroupFromStatus(crossId, signalGroupNo);

        logger.info("删除信号组参数: crossId={}, signalGroupNo={}", crossId, signalGroupNo);
    }

    @Override
    public CrossSignalGroupStatus getCrossSignalGroupStatus(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        Object obj = dataPrider.getCrossSignalGroupStatusById(crossId);
        CrossSignalGroupStatus status = OBJECT_MAPPER.convertValue(obj, CrossSignalGroupStatus.class);
        if (status == null) {
            throw new DataNotFoundException("CrossSignalGroupStatus", crossId);
        }

        return status;
    }

    @Override
    public List<CrossSignalGroupStatus> getAllCrossSignalGroupStatus() throws BusinessException {
        List<Object> objs = dataPrider.getAllCrossSignalGroupStatus();
        return objs.stream()
                .map(obj -> {
                    try {
                        return OBJECT_MAPPER.convertValue(obj, CrossSignalGroupStatus.class);
                    } catch (IllegalArgumentException e) {
                        logger.warn("转换 CrossSignalGroupStatus 失败: {}", obj, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void updateCrossSignalGroupStatus(CrossSignalGroupStatus crossSignalGroupStatus) throws BusinessException {
        if (crossSignalGroupStatus == null) {
            throw new ValidationException("crossSignalGroupStatus", "路口信号组状态不能为空");
        }

        String crossId = crossSignalGroupStatus.getCrossId();
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (crossSignalGroupStatus.getSignalGroupStatusList() == null ||
                crossSignalGroupStatus.getSignalGroupStatusList().isEmpty()) {
            throw new ValidationException("signalGroupStatusList", "信号组状态列表不能为空");
        }

        // 如果没有设置时间，则使用当前时间
        if (crossSignalGroupStatus.getLampStatusTime() == null || crossSignalGroupStatus.getLampStatusTime().trim().isEmpty()) {
            crossSignalGroupStatus.setLampStatusTime(getCurrentTimeString());
        }

        // 验证灯色状态格式
        validateSignalGroupStatusList(crossSignalGroupStatus.getSignalGroupStatusList());

        signalGroupStatusStorage.put(crossId, crossSignalGroupStatus);

        logger.info("更新路口信号组状态: crossId={}, lampStatusTime={}, signalGroupCount={}",
                crossId, crossSignalGroupStatus.getLampStatusTime(),
                crossSignalGroupStatus.getSignalGroupStatusList().size());
    }

    @Override
    public SignalGroupStatus getSignalGroupStatus(String crossId, Integer signalGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (signalGroupNo == null) {
            throw new ValidationException("signalGroupNo", "信号组序号不能为空");
        }

        CrossSignalGroupStatus crossStatus = getCrossSignalGroupStatus(crossId);

        return crossStatus.getSignalGroupStatusList().stream()
                .filter(status -> signalGroupNo.equals(status.getSignalGroupNo()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("SignalGroupStatus",
                        String.format("crossId=%s, signalGroupNo=%d", crossId, signalGroupNo)));
    }

    /**
     * 构建存储key
     */
    private String buildKey(String crossId, Integer signalGroupNo) {
        return crossId + ":" + signalGroupNo;
    }

    /**
     * 验证信号组参数
     */
    private void validateSignalGroupParam(SignalGroupParam signalGroupParam) throws BusinessException {
        // 验证路口编号
        if (signalGroupParam.getCrossId() == null || signalGroupParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        // 验证信号组序号 - 更新范围为1-999
        if (signalGroupParam.getSignalGroupNo() == null) {
            throw new ValidationException("signalGroupNo", "信号组序号不能为空");
        }
        if (signalGroupParam.getSignalGroupNo() < 1 || signalGroupParam.getSignalGroupNo() > 999) {
            throw new ValidationException("signalGroupNo", "信号组序号必须在1-999之间");
        }

        // 验证信号组名称 - 添加长度限制
        if (signalGroupParam.getName() != null && signalGroupParam.getName().length() > 50) {
            throw new ValidationException("name", "信号组名称最大长度为50");
        }

        // 验证绿闪时长
        if (signalGroupParam.getGreenFlashLen() != null && signalGroupParam.getGreenFlashLen() < 0) {
            throw new ValidationException("greenFlashLen", "绿闪时长不能为负数");
        }

        // 验证最大绿灯时长
        if (signalGroupParam.getMaxGreen() != null && signalGroupParam.getMaxGreen() < 0) {
            throw new ValidationException("maxGreen", "最大绿灯时长不能为负数");
        }

        // 验证最小绿灯时长
        if (signalGroupParam.getMinGreen() != null && signalGroupParam.getMinGreen() < 0) {
            throw new ValidationException("minGreen", "最小绿灯时长不能为负数");
        }

        // 验证最大绿灯时长和最小绿灯时长的逻辑关系
        String timingValidationResult = signalGroupParam.validateGreenTimingLogic();
        if (timingValidationResult != null) {
            throw new ValidationException("greenTiming", timingValidationResult);
        }

        // 验证信号灯组序号列表
        if (signalGroupParam.getLampGroupNoList() == null || signalGroupParam.getLampGroupNoList().isEmpty()) {
            throw new ValidationException("lampGroupNoList", "信号灯组序号列表不能为空，包含至少1个信号灯组序号");
        }

        // 验证信号灯组序号列表中的每个值
        for (Integer lampGroupNo : signalGroupParam.getLampGroupNoList()) {
            if (lampGroupNo == null || lampGroupNo < 1) {
                throw new ValidationException("lampGroupNoList", "信号灯组序号必须大于0");
            }
        }

        // 检查信号灯组序号列表中是否有重复值
        Set<Integer> uniqueLampGroupNos = new HashSet<>(signalGroupParam.getLampGroupNoList());
        if (uniqueLampGroupNos.size() != signalGroupParam.getLampGroupNoList().size()) {
            throw new ValidationException("lampGroupNoList", "信号灯组序号列表中不能有重复值");
        }
    }

    /**
     * 验证信号组状态列表
     */
    private void validateSignalGroupStatusList(List<SignalGroupStatus> statusList) throws BusinessException {
        for (SignalGroupStatus status : statusList) {
            if (status.getSignalGroupNo() == null) {
                throw new ValidationException("signalGroupNo", "信号组序号不能为空");
            }

            if (status.getLampStatus() == null || status.getLampStatus().trim().isEmpty()) {
                throw new ValidationException("lampStatus", "灯色状态不能为空");
            }

            // 验证灯色状态格式
            if (!LampStatusUtil.isValidLampStatus(status.getLampStatus())) {
                throw new ValidationException("lampStatus",
                        String.format("无效的灯色状态格式: %s，应为3位数字", status.getLampStatus()));
            }

            // 剩余时长不能为负数
            if (status.getRemainTime() != null && status.getRemainTime() < 0) {
                throw new ValidationException("remainTime", "剩余时长不能为负数");
            }
        }
    }

    /**
     * 初始化信号组状态
     */
    private void initializeSignalGroupStatus(String crossId, Integer signalGroupNo) {
        CrossSignalGroupStatus crossStatus = signalGroupStatusStorage.get(crossId);
        if (crossStatus == null) {
            crossStatus = new CrossSignalGroupStatus(crossId, getCurrentTimeString());
            signalGroupStatusStorage.put(crossId, crossStatus);
        }

        // 检查是否已存在该信号组状态
        boolean exists = crossStatus.getSignalGroupStatusList().stream()
                .anyMatch(status -> signalGroupNo.equals(status.getSignalGroupNo()));

        if (!exists) {
            // 默认设置为红灯状态，剩余时间60秒
            SignalGroupStatus groupStatus = new SignalGroupStatus(signalGroupNo,
                    LampStatusUtil.CommonLampStatus.RED_ON, 60);
            crossStatus.getSignalGroupStatusList().add(groupStatus);
        }
    }

    /**
     * 从状态中移除信号组
     */
    private void removeSignalGroupFromStatus(String crossId, Integer signalGroupNo) {
        CrossSignalGroupStatus crossStatus = signalGroupStatusStorage.get(crossId);
        if (crossStatus != null) {
            crossStatus.getSignalGroupStatusList().removeIf(
                    status -> signalGroupNo.equals(status.getSignalGroupNo())
            );
        }
    }

    /**
     * 获取当前时间字符串
     * 格式：YYYY-MM-DD hh:mm:ss.SSS
     */
    private String getCurrentTimeString() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        try {
            // 为路口1创建4个信号组
            String crossId1 = "11010000100001";
            createSampleSignalGroup(crossId1, 1, "东西直行", 3, Arrays.asList(1, 2));
            createSampleSignalGroup(crossId1, 2, "东西左转", 3, Arrays.asList(3, 4));
            createSampleSignalGroup(crossId1, 3, "南北直行", 3, Arrays.asList(5, 6));
            createSampleSignalGroup(crossId1, 4, "南北左转", 3, Arrays.asList(7, 8));

            // 为路口2创建3个信号组
            String crossId2 = "11010000100002";
            createSampleSignalGroup(crossId2, 1, "主路直行", 3, Arrays.asList(1, 2));
            createSampleSignalGroup(crossId2, 2, "主路左转", 3, Arrays.asList(3, 4));
            createSampleSignalGroup(crossId2, 3, "支路", 3, Arrays.asList(5, 6));

            // 初始化信号组状态
            initializeCrossSignalGroupStatus(crossId1, Arrays.asList(1, 2, 3, 4));
            initializeCrossSignalGroupStatus(crossId2, Arrays.asList(1, 2, 3));

            logger.info("信号组示例数据初始化完成");
        } catch (BusinessException e) {
            logger.error("信号组示例数据初始化失败", e);
        }
    }

    /**
     * 创建示例信号组
     */
    private void createSampleSignalGroup(String crossId, Integer signalGroupNo, String name,
                                         Integer greenFlushLen, List<Integer> lampGroupNos) throws BusinessException {
        SignalGroupParam signalGroup = new SignalGroupParam(crossId, signalGroupNo);
        signalGroup.setName(name);
        signalGroup.setGreenFlashLen(greenFlushLen);
        signalGroup.setLampGroupNoList(new ArrayList<>(lampGroupNos));
        save(signalGroup);
    }

    /**
     * 初始化路口信号组状态
     */
    private void initializeCrossSignalGroupStatus(String crossId, List<Integer> signalGroupNos) {
        CrossSignalGroupStatus crossStatus = new CrossSignalGroupStatus(crossId, getCurrentTimeString());

        List<SignalGroupStatus> statusList = new ArrayList<>();
        for (Integer signalGroupNo : signalGroupNos) {
            // 默认设置为红灯状态，剩余时间根据信号组序号模拟不同值
            int remainTime = 60 + (signalGroupNo * 10); // 模拟不同的剩余时间
            String lampStatus = LampStatusUtil.CommonLampStatus.RED_ON;

            // 为第一个信号组设置绿灯状态作为示例
            if (signalGroupNo == 1) {
                lampStatus = LampStatusUtil.CommonLampStatus.GREEN_ON;
                remainTime = 45;
            }

            SignalGroupStatus groupStatus = new SignalGroupStatus(signalGroupNo, lampStatus, remainTime);
            statusList.add(groupStatus);
        }

        crossStatus.setSignalGroupStatusList(statusList);
        signalGroupStatusStorage.put(crossId, crossStatus);

        logger.info("初始化路口信号组状态: crossId={}, 信号组数量={}", crossId, signalGroupNos.size());
    }

    /**
     * 批量更新信号组状态（用于信号机状态变化时）
     */
    public void batchUpdateSignalGroupStatus(String crossId, Map<Integer, String> signalGroupLampStatus,
                                             Map<Integer, Integer> signalGroupRemainTime) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        CrossSignalGroupStatus crossStatus = signalGroupStatusStorage.get(crossId);
        if (crossStatus == null) {
            throw new DataNotFoundException("CrossSignalGroupStatus", crossId);
        }

        // 更新时间戳
        crossStatus.setLampStatusTime(getCurrentTimeString());

        // 批量更新各信号组状态
        for (SignalGroupStatus groupStatus : crossStatus.getSignalGroupStatusList()) {
            Integer groupNo = groupStatus.getSignalGroupNo();

            // 更新灯色状态
            if (signalGroupLampStatus.containsKey(groupNo)) {
                String newLampStatus = signalGroupLampStatus.get(groupNo);
                if (LampStatusUtil.isValidLampStatus(newLampStatus)) {
                    groupStatus.setLampStatus(newLampStatus);
                } else {
                    logger.warn("无效的灯色状态，跳过更新: crossId={}, signalGroupNo={}, lampStatus={}",
                            crossId, groupNo, newLampStatus);
                }
            }

            // 更新剩余时间
            if (signalGroupRemainTime.containsKey(groupNo)) {
                Integer remainTime = signalGroupRemainTime.get(groupNo);
                if (remainTime != null && remainTime >= 0) {
                    groupStatus.setRemainTime(remainTime);
                }
            }
        }

        logger.info("批量更新信号组状态完成: crossId={}, 更新信号组数量={}",
                crossId, signalGroupLampStatus.size());
    }

    /**
     * 模拟信号灯状态循环变化（用于测试）
     */
    public void simulateSignalLightCycle(String crossId) throws BusinessException {
        CrossSignalGroupStatus crossStatus = signalGroupStatusStorage.get(crossId);
        if (crossStatus == null) {
            logger.warn("路口信号组状态不存在: {}", crossId);
            return;
        }

        crossStatus.setLampStatusTime(getCurrentTimeString());

        for (SignalGroupStatus groupStatus : crossStatus.getSignalGroupStatusList()) {
            // 简单的状态循环：红->绿->黄->红
            String currentStatus = groupStatus.getLampStatus();
            String newStatus;
            int newRemainTime;

            if (LampStatusUtil.CommonLampStatus.RED_ON.equals(currentStatus)) {
                newStatus = LampStatusUtil.CommonLampStatus.GREEN_ON;
                newRemainTime = 30;
            } else if (LampStatusUtil.CommonLampStatus.GREEN_ON.equals(currentStatus)) {
                newStatus = LampStatusUtil.CommonLampStatus.YELLOW_ON;
                newRemainTime = 3;
            } else if (LampStatusUtil.CommonLampStatus.YELLOW_ON.equals(currentStatus)) {
                newStatus = LampStatusUtil.CommonLampStatus.RED_ON;
                newRemainTime = 60;
            } else {
                // 其他状态保持不变，只更新剩余时间
                newStatus = currentStatus;
                newRemainTime = Math.max(0, (groupStatus.getRemainTime() != null ? groupStatus.getRemainTime() : 60) - 1);
            }

            groupStatus.setLampStatus(newStatus);
            groupStatus.setRemainTime(newRemainTime);
        }

        logger.debug("模拟信号灯状态变化: crossId={}", crossId);
    }
}