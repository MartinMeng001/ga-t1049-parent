package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.enums.LampStatus;
import com.traffic.gat1049.protocol.model.signal.SignalGroupParam;
import com.traffic.gat1049.protocol.model.runtime.CrossSignalGroupStatus;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;
import com.traffic.gat1049.service.interfaces.SignalGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 信号组服务实现
 */
public class SignalGroupServiceImpl implements SignalGroupService {

    private static final Logger logger = LoggerFactory.getLogger(SignalGroupServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 信号组参数存储 - 使用"crossId:signalGroupNo"作为key
    private final Map<String, SignalGroupParam> signalGroupStorage = new ConcurrentHashMap<>();

    // 信号组状态存储 - 使用crossId作为key，存储整个路口的信号组状态
    private final Map<String, CrossSignalGroupStatus> signalGroupStatusStorage = new ConcurrentHashMap<>();

    public SignalGroupServiceImpl() throws BusinessException {
        // 初始化一些示例数据
        //initializeSampleData();
    }

    @Override
    public List<SignalGroupParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataPrider.getSignalGroupsByCrossId(crossId);
//        return signalGroupStorage.values().stream()
//                .filter(signalGroup -> crossId.equals(signalGroup.getCrossId()))
//                .collect(Collectors.toList());
    }

    @Override
    public SignalGroupParam findByCrossIdAndSignalGroupNo(String crossId, Integer signalGroupNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (signalGroupNo == null) {
            throw new ValidationException("signalGroupNo", "信号组序号不能为空");
        }

        String key = buildKey(crossId, signalGroupNo);
        SignalGroupParam signalGroup = dataPrider.getSignalGroupByCrossIdAndNo(crossId, signalGroupNo.toString());//signalGroupStorage.get(key);
        if (signalGroup == null) {
            throw new DataNotFoundException("SignalGroupParam",
                    String.format("crossId=%s, signalGroupNo=%d", crossId, signalGroupNo));
        }

        return signalGroup;
    }

    @Override
    public SignalGroupParam save(SignalGroupParam signalGroupParam) throws BusinessException {
        if (signalGroupParam == null) {
            throw new ValidationException("signalGroupParam", "信号组参数不能为空");
        }

        validateSignalGroupParam(signalGroupParam);

//        signalGroupParam.setCreateTime(LocalDateTime.now());
//        signalGroupParam.setUpdateTime(LocalDateTime.now());

        String key = buildKey(signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());
        signalGroupStorage.put(key, signalGroupParam);

        // 初始化信号组状态
        initializeSignalGroupStatus(signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());

        logger.info("保存信号组参数: crossId={}, signalGroupNo={}, name={}",
                signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo(), signalGroupParam.getName());

        return signalGroupParam;
    }

    @Override
    public SignalGroupParam update(SignalGroupParam signalGroupParam) throws BusinessException {
        if (signalGroupParam == null) {
            throw new ValidationException("signalGroupParam", "信号组参数不能为空");
        }

        validateSignalGroupParam(signalGroupParam);

        String key = buildKey(signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo());
        if (!signalGroupStorage.containsKey(key)) {
            throw new DataNotFoundException("SignalGroupParam",
                    String.format("crossId=%s, signalGroupNo=%d",
                            signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo()));
        }

        //signalGroupParam.setUpdateTime(LocalDateTime.now());
        signalGroupStorage.put(key, signalGroupParam);

        logger.info("更新信号组参数: crossId={}, signalGroupNo={}, name={}",
                signalGroupParam.getCrossId(), signalGroupParam.getSignalGroupNo(), signalGroupParam.getName());

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

        CrossSignalGroupStatus status = signalGroupStatusStorage.get(crossId);
        if (status == null) {
            throw new DataNotFoundException("CrossSignalGroupStatus", crossId);
        }

        return status;
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

        crossSignalGroupStatus.setStateTime(LocalDateTime.now());
        signalGroupStatusStorage.put(crossId, crossSignalGroupStatus);

        logger.info("更新路口信号组状态: crossId={}, signalGroupCount={}",
                crossId, crossSignalGroupStatus.getSignalGroupStatusList().size());
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
        if (signalGroupParam.getCrossId() == null || signalGroupParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (signalGroupParam.getSignalGroupNo() == null) {
            throw new ValidationException("signalGroupNo", "信号组序号不能为空");
        }

        if (signalGroupParam.getSignalGroupNo() < 1 || signalGroupParam.getSignalGroupNo() > 99) {
            throw new ValidationException("signalGroupNo", "信号组序号必须在1-99之间");
        }

        if (signalGroupParam.getName() == null || signalGroupParam.getName().trim().isEmpty()) {
            throw new ValidationException("name", "信号组名称不能为空");
        }

        if (signalGroupParam.getLampGroupNoList() == null || signalGroupParam.getLampGroupNoList().isEmpty()) {
            throw new ValidationException("lampGroupNoList", "信号灯组序号列表不能为空");
        }

        if (signalGroupParam.getGreenFlushLen() != null && signalGroupParam.getGreenFlushLen() < 0) {
            throw new ValidationException("greenFlushLen", "绿闪时长不能为负数");
        }
    }

    /**
     * 初始化信号组状态
     */
    private void initializeSignalGroupStatus(String crossId, Integer signalGroupNo) {
        CrossSignalGroupStatus crossStatus = signalGroupStatusStorage.get(crossId);
        if (crossStatus == null) {
            crossStatus = new CrossSignalGroupStatus(crossId);
            crossStatus.setStateTime(LocalDateTime.now());
            signalGroupStatusStorage.put(crossId, crossStatus);
        }

        // 检查是否已存在该信号组状态
        boolean exists = crossStatus.getSignalGroupStatusList().stream()
                .anyMatch(status -> signalGroupNo.equals(status.getSignalGroupNo()));

        if (!exists) {
            SignalGroupStatus groupStatus = new SignalGroupStatus(signalGroupNo, LampStatus.RED);
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
        signalGroup.setGreenFlushLen(greenFlushLen);
        signalGroup.setLampGroupNoList(new ArrayList<>(lampGroupNos));
        save(signalGroup);
    }

    /**
     * 初始化路口信号组状态
     */
    private void initializeCrossSignalGroupStatus(String crossId, List<Integer> signalGroupNos) {
        CrossSignalGroupStatus crossStatus = new CrossSignalGroupStatus(crossId);
        crossStatus.setStateTime(LocalDateTime.now());

        List<SignalGroupStatus> statusList = new ArrayList<>();
        for (Integer signalGroupNo : signalGroupNos) {
            // 默认设置为红灯状态
            SignalGroupStatus groupStatus = new SignalGroupStatus(signalGroupNo, LampStatus.RED);
            statusList.add(groupStatus);
        }

        crossStatus.setSignalGroupStatusList(statusList);
        signalGroupStatusStorage.put(crossId, crossStatus);
    }
}