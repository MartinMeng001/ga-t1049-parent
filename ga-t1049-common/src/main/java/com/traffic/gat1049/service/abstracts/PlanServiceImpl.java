package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.constants.BusinessConstants;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.protocol.model.signal.StageTiming;
import com.traffic.gat1049.protocol.model.runtime.CrossModePlan;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.model.enums.OperationType;
import com.traffic.gat1049.service.interfaces.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 配时方案服务实现
 */
public class PlanServiceImpl implements PlanService {

    private static final Logger logger = LoggerFactory.getLogger(PlanServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 配时方案存储：crossId -> planNo -> PlanParam
    private final Map<String, Map<Integer, PlanParam>> planStorage = new ConcurrentHashMap<>();

    // 当前控制方式存储：crossId -> CrossModePlan
    private final Map<String, CrossModePlan> currentModeStorage = new ConcurrentHashMap<>();

    // 方案序号生成器
    private final AtomicInteger planNoGenerator = new AtomicInteger(1);

    public PlanServiceImpl() throws BusinessException {
    }

    @Override
    public List<PlanParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataProvider.getPlansByCrossId(crossId);
    }

    @Override
    public PlanParam findByCrossIdAndPlanNo(String crossId, Integer planNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (planNo == null) {
            throw new ValidationException("planNo", "方案序号不能为空");
        }
        return dataProvider.getPlanByCrossIdAndNo(crossId, planNo.toString());
    }

    @Override
    public PlanParam setPlanParam(OperationType operationType, PlanParam planParam) throws BusinessException {
        if (operationType == null) {
            throw new ValidationException("operationType", "操作类型不能为空");
        }
        if (planParam == null) {
            throw new ValidationException("planParam", "配时方案参数不能为空");
        }

        validatePlan(planParam);

        String crossId = planParam.getCrossId();

        switch (operationType) {
            case ADD:
                return addPlan(crossId, planParam);
            case MODIFY:
                return modifyPlan(crossId, planParam);
            case DELETE:
                deletePlan(crossId, planParam.getPlanNo());
                return planParam;
            default:
                throw new BusinessException("UNSUPPORTED_OPERATION", "不支持的操作类型: " + operationType);
        }
    }

    @Override
    public List<PlanParam> findAllPlans() throws BusinessException {
        return dataProvider.getAllPlans();
    }

    @Override
    public void deletePlan(String crossId, Integer planNo) throws BusinessException {
        Map<Integer, PlanParam> crossPlans = planStorage.get(crossId);
        if (crossPlans == null || !crossPlans.containsKey(planNo)) {
            throw new DataNotFoundException("PlanParam", crossId + "-" + planNo);
        }

        crossPlans.remove(planNo);
        logger.info("删除配时方案: crossId={}, planNo={}", crossId, planNo);
    }

    @Override
    public Integer setCenterPlan(ControlMode controlMode, PlanParam planParam) throws BusinessException {
        if (controlMode == null) {
            throw new ValidationException("controlMode", "控制模式不能为空");
        }
        if (planParam == null) {
            throw new ValidationException("planParam", "配时方案参数不能为空");
        }

        validatePlan(planParam);

        String crossId = planParam.getCrossId();
        Integer planNo = planParam.getPlanNo();

        if (planNo == null || planNo == 0) {
            // 自动分配方案号
            planNo = planNoGenerator.getAndIncrement();
            planParam.setPlanNo(planNo);
        }

        // 保存方案
        addPlan(crossId, planParam);

        // 设置当前控制模式
        CrossModePlan crossModePlan = new CrossModePlan();
        crossModePlan.setCrossId(crossId);
        crossModePlan.setControlMode(controlMode);
        crossModePlan.setPlanNo(planNo);
        //crossModePlan.setSetTime(LocalDateTime.now());

        currentModeStorage.put(crossId, crossModePlan);

        logger.info("设置中心控制方案: crossId={}, controlMode={}, planNo={}",
                crossId, controlMode, planNo);

        return planNo;
    }

    @Override
    public CrossModePlan getCurrentControlMode(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        CrossModePlan currentMode = currentModeStorage.get(crossId);
        if (currentMode == null) {
            throw new DataNotFoundException("CrossModePlan", crossId);
        }

        return currentMode;
    }

    private PlanParam addPlan(String crossId, PlanParam planParam) {
        Integer planNo = planParam.getPlanNo();
        if (planNo == null || planNo == 0) {
            // 自动分配方案号
            planNo = planNoGenerator.getAndIncrement();
            planParam.setPlanNo(planNo);
        }

        planStorage.computeIfAbsent(crossId, k -> new ConcurrentHashMap<>())
                .put(planNo, planParam);

        logger.info("添加配时方案: crossId={}, planNo={}, planName={}",
                crossId, planNo, planParam.getPlanName());

        return planParam;
    }

    private PlanParam modifyPlan(String crossId, PlanParam planParam) throws BusinessException {
        Integer planNo = planParam.getPlanNo();
        Map<Integer, PlanParam> crossPlans = planStorage.get(crossId);

        if (crossPlans == null || !crossPlans.containsKey(planNo)) {
            throw new DataNotFoundException("PlanParam", crossId + "-" + planNo);
        }

        crossPlans.put(planNo, planParam);

        logger.info("修改配时方案: crossId={}, planNo={}, planName={}",
                crossId, planNo, planParam.getPlanName());

        return planParam;
    }

    private void validatePlan(PlanParam plan) throws BusinessException {
        if (plan.getCrossId() == null || plan.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (plan.getPlanName() == null || plan.getPlanName().trim().isEmpty()) {
            throw new ValidationException("planName", "方案名称不能为空");
        }

        if (plan.getCycleLen() == null || plan.getCycleLen() <= 0) {
            throw new ValidationException("cycleLen", "周期长度必须大于0");
        }

        if (plan.getCycleLen() < BusinessConstants.SignalControl.MIN_CYCLE_TIME ||
                plan.getCycleLen() > BusinessConstants.SignalControl.MAX_CYCLE_TIME) {
            throw new ValidationException("cycleLen",
                    String.format("周期长度必须在%d-%d秒之间",
                            BusinessConstants.SignalControl.MIN_CYCLE_TIME,
                            BusinessConstants.SignalControl.MAX_CYCLE_TIME));
        }

        if (plan.getStageTimingList() == null || plan.getStageTimingList().isEmpty()) {
            throw new ValidationException("stageTimingList", "阶段时间列表不能为空");
        }

        // 验证阶段时间
        int totalTime = 0;
        for (StageTiming timing : plan.getStageTimingList()) {
            validateStageTiming(timing);
            totalTime += timing.getGreen() + timing.getYellow() + timing.getAllRed();
        }

        if (totalTime != plan.getCycleLen()) {
            throw new ValidationException("cycleLen", "所有阶段时间总和必须等于周期长度");
        }
    }

    private void validateStageTiming(StageTiming timing) throws BusinessException {
        if (timing.getStageNo() == null) {
            throw new ValidationException("stageNo", "阶段号不能为空");
        }

        if (timing.getGreen() == null || timing.getGreen() < BusinessConstants.SignalControl.MIN_GREEN_TIME) {
            throw new ValidationException("green",
                    String.format("绿灯时间不能少于%d秒", BusinessConstants.SignalControl.MIN_GREEN_TIME));
        }

        if (timing.getYellow() == null || timing.getYellow() < BusinessConstants.SignalControl.MIN_YELLOW_TIME) {
            throw new ValidationException("yellow",
                    String.format("黄灯时间不能少于%d秒", BusinessConstants.SignalControl.MIN_YELLOW_TIME));
        }

        if (timing.getAllRed() == null || timing.getAllRed() < BusinessConstants.SignalControl.MIN_ALL_RED_TIME) {
            throw new ValidationException("allRed",
                    String.format("全红时间不能少于%d秒", BusinessConstants.SignalControl.MIN_ALL_RED_TIME));
        }
    }
}