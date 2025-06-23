package com.traffic.gat1049.service.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.runtime.CrossModePlan;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.service.interfaces.ControlModeService;
import com.traffic.gat1049.service.interfaces.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 控制模式服务实现
 */
public class ControlModeServiceImpl implements ControlModeService {

    private static final Logger logger = LoggerFactory.getLogger(ControlModeServiceImpl.class);

    private ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();
    // 当前控制模式存储：crossId -> CrossModePlan
    private final Map<String, CrossModePlan> currentModeStorage = new ConcurrentHashMap<>();

    // 控制模式历史记录存储：crossId -> List<CrossModePlan>
    private final Map<String, LinkedList<CrossModePlan>> modeHistoryStorage = new ConcurrentHashMap<>();

    // 临时方案存储：crossId -> planNo -> PlanParam（用于中心预案）
    private final Map<String, Map<Integer, PlanParam>> temporaryPlanStorage = new ConcurrentHashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    // 临时方案号生成器
    private final AtomicInteger temporaryPlanNoGenerator = new AtomicInteger(-1);

    // 历史记录保留数量
    private static final int MAX_HISTORY_SIZE = 50;

    // 注入PlanService用于验证方案
    private PlanService planService;

    public ControlModeServiceImpl() throws BusinessException {
        // 初始化默认控制模式
        //initializeDefaultModes();
    }

    public ControlModeServiceImpl(PlanService planService) throws BusinessException {
        this.planService = planService;
        initializeDefaultModes();
    }

    @Override
    public CrossModePlan getCurrentModePlan(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        Object obj = dataProvider.getCrossModePlanById(crossId);
        CrossModePlan currentMode = OBJECT_MAPPER.convertValue(obj, CrossModePlan.class);
        if (currentMode == null) {
            throw new DataNotFoundException("CrossModePlan", crossId);
        }

        return currentMode;
    }

    @Override
    public void setControlMode(String crossId, ControlMode controlMode, Integer planNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (controlMode == null) {
            throw new ValidationException("controlMode", "控制模式不能为空");
        }

        CrossModePlan modePlan = new CrossModePlan(crossId, controlMode, planNo);
        setControlMode(modePlan);
    }

    @Override
    public void setControlMode(CrossModePlan modePlan) throws BusinessException {
        validateControlMode(modePlan);

        String crossId = modePlan.getCrossId();
        ControlMode controlMode = modePlan.getControlMode();
        Integer planNo = modePlan.getPlanNo();

        // 检查是否可以切换到目标模式
        if (!canSwitchToMode(crossId, controlMode)) {
            throw new BusinessException("SWITCH_FORBIDDEN",
                    String.format("不能切换到控制模式: %s", controlMode.getDescription()));
        }

        // 保存当前模式到历史记录
        CrossModePlan currentMode = currentModeStorage.get(crossId);
        if (currentMode != null) {
            addToHistory(crossId, currentMode);
        }

        // 设置新的控制模式
        //modePlan.setStateTime(LocalDateTime.now());
        currentModeStorage.put(crossId, modePlan);

        logger.info("设置路口控制模式: crossId={}, controlMode={}, planNo={}",
                crossId, controlMode.getDescription(), planNo);

        // 执行特定控制模式的逻辑
        executeControlModeLogic(modePlan);
    }

    @Override
    public List<CrossModePlan> getAllControlModes() throws BusinessException {
        List<Object> objs = dataProvider.getAllCrossModePlans();

        return objs.stream()
                .map(obj -> {
                    try {
                        return OBJECT_MAPPER.convertValue(obj, CrossModePlan.class);
                    } catch (IllegalArgumentException e) {
                        logger.warn("转换 ControlModePlan 失败: {}", obj, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<CrossModePlan> findByControlMode(ControlMode controlMode) throws BusinessException {
        if (controlMode == null) {
            throw new ValidationException("controlMode", "控制模式不能为空");
        }

        return currentModeStorage.values().stream()
                .filter(modePlan -> controlMode.equals(modePlan.getControlMode()))
                .collect(Collectors.toList());
    }

    @Override
    public void batchSetControlMode(List<String> crossIds, ControlMode controlMode, Integer planNo) throws BusinessException {
        if (crossIds == null || crossIds.isEmpty()) {
            throw new ValidationException("crossIds", "路口编号列表不能为空");
        }
        if (controlMode == null) {
            throw new ValidationException("controlMode", "控制模式不能为空");
        }

        List<String> failedCrossIds = new ArrayList<>();

        for (String crossId : crossIds) {
            try {
                setControlMode(crossId, controlMode, planNo);
            } catch (BusinessException e) {
                logger.warn("批量设置控制模式失败: crossId={}, error={}", crossId, e.getMessage());
                failedCrossIds.add(crossId);
            }
        }

        if (!failedCrossIds.isEmpty()) {
            throw new BusinessException("BATCH_OPERATION_PARTIAL_FAILED",
                    String.format("部分路口设置失败: %s", String.join(",", failedCrossIds)));
        }

        logger.info("批量设置控制模式成功: controlMode={}, crossCount={}",
                controlMode.getDescription(), crossIds.size());
    }

    @Override
    public boolean isSpecialControlMode(ControlMode controlMode) {
        return controlMode == ControlMode.CANCEL ||
                controlMode == ControlMode.LIGHT_OFF ||
                controlMode == ControlMode.ALL_RED ||
                controlMode == ControlMode.ALL_YELLOW_FLASH;
    }

    @Override
    public void validateControlMode(CrossModePlan modePlan) throws BusinessException {
        if (modePlan == null) {
            throw new ValidationException("modePlan", "控制模式方案不能为空");
        }

        if (modePlan.getCrossId() == null || modePlan.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (modePlan.getControlMode() == null) {
            throw new ValidationException("controlMode", "控制模式不能为空");
        }

        ControlMode mode = modePlan.getControlMode();
        Integer planNo = modePlan.getPlanNo();

        // 特殊控制模式不需要方案号
        if (isSpecialControlMode(mode)) {
            if (planNo != null && planNo != 0) {
                logger.warn("特殊控制模式{}忽略方案号{}", mode.getDescription(), planNo);
                modePlan.setPlanNo(null);
            }
        } else {
            // 非特殊控制模式需要有效的方案号
            if (planNo == null || planNo == 0) {
                throw new ValidationException("planNo",
                        String.format("控制模式%s需要有效的方案号", mode.getDescription()));
            }

            // 如果不是临时方案，验证方案是否存在
            if (planNo > 0 && planService != null) {
                try {
                    planService.findByCrossIdAndPlanNo(modePlan.getCrossId(), planNo);
                } catch (DataNotFoundException e) {
                    throw new ValidationException("planNo",
                            String.format("方案号%d不存在", planNo));
                }
            }
        }
    }

    @Override
    public void resetToDefaultMode(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        // 重置为默认的单点定时控制模式
        setControlMode(crossId, ControlMode.SINGLE_TIMING, 1);

        logger.info("重置路口{}为默认控制模式", crossId);
    }

    @Override
    public List<CrossModePlan> getControlModeHistory(String crossId, int limit) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (limit <= 0) {
            throw new ValidationException("limit", "记录数量限制必须大于0");
        }

        LinkedList<CrossModePlan> history = modeHistoryStorage.get(crossId);
        if (history == null || history.isEmpty()) {
            return new ArrayList<>();
        }

        return history.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public boolean canSwitchToMode(String crossId, ControlMode targetMode) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (targetMode == null) {
            throw new ValidationException("targetMode", "目标控制模式不能为空");
        }

        // 获取当前控制模式
        CrossModePlan currentMode = getCurrentModePlan(crossId);
        ControlMode currentControlMode = currentMode.getControlMode();

        // 如果目标模式与当前模式相同，可以切换（实际是刷新）
        if (targetMode.equals(currentControlMode)) {
            return true;
        }

        // 根据业务逻辑判断是否可以切换
        // 这里可以添加具体的切换规则，比如：
        // 1. 某些模式不能直接切换
        // 2. 需要满足特定条件才能切换
        // 3. 检查设备状态等

        // 示例：从故障模式不能直接切换到正常运行模式
        if (currentControlMode == ControlMode.LIGHT_OFF &&
                (targetMode == ControlMode.SINGLE_TIMING || targetMode == ControlMode.SINGLE_ACTUATED)) {
            logger.warn("从关灯模式不能直接切换到正常运行模式，路口: {}", crossId);
            return false;
        }

        return true;
    }

    /**
     * 初始化默认控制模式
     */
    private void initializeDefaultModes() {
        // 可以在这里预设一些路口的默认控制模式
        logger.info("控制模式服务初始化完成");
    }

    /**
     * 添加控制模式到历史记录
     */
    private void addToHistory(String crossId, CrossModePlan modePlan) {
        LinkedList<CrossModePlan> history = modeHistoryStorage.computeIfAbsent(crossId, k -> new LinkedList<>());

        // 添加到历史记录头部
        history.addFirst(modePlan);

        // 保持历史记录数量限制
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeLast();
        }
    }

    /**
     * 执行特定控制模式的逻辑
     */
    private void executeControlModeLogic(CrossModePlan modePlan) {
        String crossId = modePlan.getCrossId();
        ControlMode mode = modePlan.getControlMode();

        switch (mode) {
            case LIGHT_OFF:
                logger.info("执行关灯控制: crossId={}", crossId);
                // 执行关灯逻辑
                break;

            case ALL_RED:
                logger.info("执行全红控制: crossId={}", crossId);
                // 执行全红逻辑
                break;

            case ALL_YELLOW_FLASH:
                logger.info("执行黄闪控制: crossId={}", crossId);
                // 执行黄闪逻辑
                break;

            case MANUAL:
                logger.info("执行手动控制: crossId={}", crossId);
                // 执行手动控制逻辑
                break;

            case SINGLE_TIMING:
                logger.info("执行单点定时控制: crossId={}, planNo={}", crossId, modePlan.getPlanNo());
                // 执行单点定时控制逻辑
                break;

            case SINGLE_ACTUATED:
                logger.info("执行单点感应控制: crossId={}, planNo={}", crossId, modePlan.getPlanNo());
                // 执行单点感应控制逻辑
                break;

            default:
                logger.info("执行{}控制: crossId={}, planNo={}",
                        mode.getDescription(), crossId, modePlan.getPlanNo());
                break;
        }
    }

    /**
     * 设置PlanService依赖（用于依赖注入）
     */
    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }
}