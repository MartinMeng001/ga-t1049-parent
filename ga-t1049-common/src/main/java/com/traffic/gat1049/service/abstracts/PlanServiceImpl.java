package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.constants.BusinessConstants;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.protocol.model.signal.StageTiming;
import com.traffic.gat1049.protocol.model.runtime.CrossModePlan;
import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.model.enums.OperationType;
import com.traffic.gat1049.service.interfaces.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 配时方案服务实现
 */

public class PlanServiceImpl implements PlanService {

    private static final Logger logger = LoggerFactory.getLogger(PlanServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataPrider = ComprehensiveTestDataProviderImpl.getInstance();
    // 配时方案存储：crossId -> planNo -> PlanParam
    private final Map<String, Map<Integer, PlanParam>> planStorage = new ConcurrentHashMap<>();

    // 日计划存储：crossId -> dayPlanNo -> DayPlanParam
    private final Map<String, Map<Integer, DayPlanParam>> dayPlanStorage = new ConcurrentHashMap<>();

    // 调度存储：crossId -> scheduleNo -> ScheduleParam
    private final Map<String, Map<Integer, ScheduleParam>> scheduleStorage = new ConcurrentHashMap<>();

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
        return dataPrider.getPlansByCrossId(crossId);
//        Map<Integer, PlanParam> crossPlans = planStorage.get(crossId);
//        if (crossPlans == null) {
//            return new ArrayList<>();
//        }
//
//        return new ArrayList<>(crossPlans.values());
    }

    @Override
    public PlanParam findByCrossIdAndPlanNo(String crossId, Integer planNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (planNo == null) {
            throw new ValidationException("planNo", "方案序号不能为空");
        }
        return dataPrider.getPlanByCrossIdAndNo(crossId, planNo.toString());
//        Map<Integer, PlanParam> crossPlans = planStorage.get(crossId);
//        if (crossPlans == null) {
//            throw new DataNotFoundException("PlanParam", crossId + "-" + planNo);
//        }
//
//        PlanParam plan = crossPlans.get(planNo);
//        if (plan == null) {
//            throw new DataNotFoundException("PlanParam", crossId + "-" + planNo);
//        }
//
//        return plan;
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
    public DayPlanParam getDayPlanParam(String crossId, Integer dayPlanNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (dayPlanNo == null) {
            throw new ValidationException("dayPlanNo", "日计划号不能为空");
        }

        Map<Integer, DayPlanParam> crossDayPlans = dayPlanStorage.get(crossId);
        if (crossDayPlans == null) {
            throw new DataNotFoundException("DayPlanParam", crossId + "-" + dayPlanNo);
        }

        DayPlanParam dayPlan = crossDayPlans.get(dayPlanNo);
        if (dayPlan == null) {
            throw new DataNotFoundException("DayPlanParam", crossId + "-" + dayPlanNo);
        }

        return dayPlan;
    }

    @Override
    public DayPlanParam setDayPlanParam(OperationType operationType, DayPlanParam dayPlanParam) throws BusinessException {
        if (operationType == null) {
            throw new ValidationException("operationType", "操作类型不能为空");
        }
        if (dayPlanParam == null) {
            throw new ValidationException("dayPlanParam", "日计划参数不能为空");
        }

        validateDayPlan(dayPlanParam);

        String crossId = dayPlanParam.getCrossId();
        Integer dayPlanNo = dayPlanParam.getDayPlanNo();

        switch (operationType) {
            case ADD:
                return addDayPlan(crossId, dayPlanParam);
            case MODIFY:
                return modifyDayPlan(crossId, dayPlanParam);
            case DELETE:
                deleteDayPlan(crossId, dayPlanNo);
                return dayPlanParam;
            default:
                throw new BusinessException("UNSUPPORTED_OPERATION", "不支持的操作类型: " + operationType);
        }
    }

    @Override
    public ScheduleParam getScheduleParam(String crossId, Integer scheduleNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (scheduleNo == null) {
            throw new ValidationException("scheduleNo", "调度号不能为空");
        }

        Map<Integer, ScheduleParam> crossSchedules = scheduleStorage.get(crossId);
        if (crossSchedules == null) {
            throw new DataNotFoundException("ScheduleParam", crossId + "-" + scheduleNo);
        }

        ScheduleParam schedule = crossSchedules.get(scheduleNo);
        if (schedule == null) {
            throw new DataNotFoundException("ScheduleParam", crossId + "-" + scheduleNo);
        }

        return schedule;
    }

    @Override
    public ScheduleParam setScheduleParam(OperationType operationType, ScheduleParam scheduleParam) throws BusinessException {
        if (operationType == null) {
            throw new ValidationException("operationType", "操作类型不能为空");
        }
        if (scheduleParam == null) {
            throw new ValidationException("scheduleParam", "调度参数不能为空");
        }

        validateSchedule(scheduleParam);

        String crossId = scheduleParam.getCrossId();
        Integer scheduleNo = scheduleParam.getScheduleNo();

        switch (operationType) {
            case ADD:
                return addSchedule(crossId, scheduleParam);
            case MODIFY:
                return modifySchedule(crossId, scheduleParam);
            case DELETE:
                deleteSchedule(crossId, scheduleNo);
                return scheduleParam;
            default:
                throw new BusinessException("UNSUPPORTED_OPERATION", "不支持的操作类型: " + operationType);
        }
    }

    @Override
    public CrossModePlan getCurrentModePlan(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        CrossModePlan currentMode = currentModeStorage.get(crossId);
        if (currentMode == null) {
            // 返回默认的单点定时控制模式
            currentMode = new CrossModePlan(crossId, ControlMode.SINGLE_TIMING, 1);
            currentModeStorage.put(crossId, currentMode);
        }

        return currentMode;
    }

    @Override
    public void setControlMode(String crossId, ControlMode controlMode, Integer planNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (controlMode == null) {
            throw new ValidationException("controlMode", "控制方式不能为空");
        }

        CrossModePlan modePlan = new CrossModePlan(crossId, controlMode, planNo);
        modePlan.setStateTime(LocalDateTime.now());
        currentModeStorage.put(crossId, modePlan);

        logger.info("设置路口控制方式: crossId={}, controlMode={}, planNo={}",
                crossId, controlMode.getDescription(), planNo);
    }

    @Override
    public Integer setCenterPlan(ControlMode crossControlMode, PlanParam planParam) throws BusinessException {
        if (crossControlMode == null) {
            throw new ValidationException("crossControlMode", "控制方式不能为空");
        }
        if (planParam == null) {
            throw new ValidationException("planParam", "配时方案参数不能为空");
        }

        validatePlan(planParam);

        String crossId = planParam.getCrossId();

        // 为中心预案分配方案号（使用0表示临时方案）
        Integer assignedPlanNo = 0;
        planParam.setPlanNo(assignedPlanNo);
        planParam.setPlanName("中心预案-" + LocalDateTime.now().toString());
        //planParam.setUpdateTime(LocalDateTime.now());

        // 存储临时方案
        planStorage.computeIfAbsent(crossId, k -> new ConcurrentHashMap<>())
                .put(assignedPlanNo, planParam);

        // 设置当前控制方式
        setControlMode(crossId, crossControlMode, assignedPlanNo);

        logger.info("下发中心预案: crossId={}, controlMode={}, assignedPlanNo={}",
                crossId, crossControlMode.getDescription(), assignedPlanNo);

        return assignedPlanNo;
    }

    @Override
    public void deletePlan(String crossId, Integer planNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (planNo == null) {
            throw new ValidationException("planNo", "方案序号不能为空");
        }

        Map<Integer, PlanParam> crossPlans = planStorage.get(crossId);
        if (crossPlans == null || !crossPlans.containsKey(planNo)) {
            throw new DataNotFoundException("PlanParam", crossId + "-" + planNo);
        }

        crossPlans.remove(planNo);
        logger.info("删除配时方案: crossId={}, planNo={}", crossId, planNo);
    }

    @Override
    public void validatePlan(PlanParam planParam) throws BusinessException {
        if (planParam.getCrossId() == null || planParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (planParam.getCycleLen() == null) {
            throw new ValidationException("cycleLen", "周期长度不能为空");
        }

        if (planParam.getCycleLen() < BusinessConstants.SignalControl.MIN_CYCLE_TIME ||
                planParam.getCycleLen() > BusinessConstants.SignalControl.MAX_CYCLE_TIME) {
            throw new ValidationException("cycleLen",
                    String.format("周期长度必须在%d-%d秒之间",
                            BusinessConstants.SignalControl.MIN_CYCLE_TIME,
                            BusinessConstants.SignalControl.MAX_CYCLE_TIME));
        }

        if (planParam.getStageTimingList() == null || planParam.getStageTimingList().isEmpty()) {
            throw new ValidationException("stageTimingList", "阶段配时信息列表不能为空");
        }

        // 验证阶段配时
        int totalTime = 0;
        for (StageTiming timing : planParam.getStageTimingList()) {
            validateStageTiming(timing);
            totalTime += timing.getGreen() + timing.getYellow() + timing.getAllRed();
        }

        if (totalTime != planParam.getCycleLen()) {
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

    private void validateDayPlan(DayPlanParam dayPlan) throws BusinessException {
        if (dayPlan.getCrossId() == null || dayPlan.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (dayPlan.getDayPlanNo() == null || dayPlan.getDayPlanNo() <= 0) {
            throw new ValidationException("dayPlanNo", "日计划号必须大于0");
        }

        if (dayPlan.getPeriodList() == null || dayPlan.getPeriodList().isEmpty()) {
            throw new ValidationException("periodList", "时段信息列表不能为空");
        }
    }

    private void validateSchedule(ScheduleParam schedule) throws BusinessException {
        if (schedule.getCrossId() == null || schedule.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (schedule.getScheduleNo() == null || schedule.getScheduleNo() <= 0) {
            throw new ValidationException("scheduleNo", "调度号必须大于0");
        }

        if (schedule.getType() == null) {
            throw new ValidationException("type", "调度类型不能为空");
        }

        if (schedule.getDayPlanNo() == null || schedule.getDayPlanNo() <= 0) {
            throw new ValidationException("dayPlanNo", "日计划号必须大于0");
        }
    }

    private PlanParam addPlan(String crossId, PlanParam planParam) {
        Integer planNo = planParam.getPlanNo();
        if (planNo == null || planNo == 0) {
            // 自动分配方案号
            planNo = planNoGenerator.getAndIncrement();
            planParam.setPlanNo(planNo);
        }

//        planParam.setCreateTime(LocalDateTime.now());
//        planParam.setUpdateTime(LocalDateTime.now());

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

        //planParam.setUpdateTime(LocalDateTime.now());
        crossPlans.put(planNo, planParam);

        logger.info("修改配时方案: crossId={}, planNo={}, planName={}",
                crossId, planNo, planParam.getPlanName());

        return planParam;
    }

    private DayPlanParam addDayPlan(String crossId, DayPlanParam dayPlan) {
//        dayPlan.setCreateTime(LocalDateTime.now());
//        dayPlan.setUpdateTime(LocalDateTime.now());

        dayPlanStorage.computeIfAbsent(crossId, k -> new ConcurrentHashMap<>())
                .put(dayPlan.getDayPlanNo(), dayPlan);

        logger.info("添加日计划: crossId={}, dayPlanNo={}", crossId, dayPlan.getDayPlanNo());
        return dayPlan;
    }

    private DayPlanParam modifyDayPlan(String crossId, DayPlanParam dayPlan) throws BusinessException {
        Integer dayPlanNo = dayPlan.getDayPlanNo();
        Map<Integer, DayPlanParam> crossDayPlans = dayPlanStorage.get(crossId);

        if (crossDayPlans == null || !crossDayPlans.containsKey(dayPlanNo)) {
            throw new DataNotFoundException("DayPlanParam", crossId + "-" + dayPlanNo);
        }

        //dayPlan.setUpdateTime(LocalDateTime.now());
        crossDayPlans.put(dayPlanNo, dayPlan);

        logger.info("修改日计划: crossId={}, dayPlanNo={}", crossId, dayPlanNo);
        return dayPlan;
    }

    private void deleteDayPlan(String crossId, Integer dayPlanNo) throws BusinessException {
        Map<Integer, DayPlanParam> crossDayPlans = dayPlanStorage.get(crossId);
        if (crossDayPlans == null || !crossDayPlans.containsKey(dayPlanNo)) {
            throw new DataNotFoundException("DayPlanParam", crossId + "-" + dayPlanNo);
        }

        crossDayPlans.remove(dayPlanNo);
        logger.info("删除日计划: crossId={}, dayPlanNo={}", crossId, dayPlanNo);
    }

    private ScheduleParam addSchedule(String crossId, ScheduleParam schedule) {
//        schedule.setCreateTime(LocalDateTime.now());
//        schedule.setUpdateTime(LocalDateTime.now());

        scheduleStorage.computeIfAbsent(crossId, k -> new ConcurrentHashMap<>())
                .put(schedule.getScheduleNo(), schedule);

        logger.info("添加调度: crossId={}, scheduleNo={}", crossId, schedule.getScheduleNo());
        return schedule;
    }

    private ScheduleParam modifySchedule(String crossId, ScheduleParam schedule) throws BusinessException {
        Integer scheduleNo = schedule.getScheduleNo();
        Map<Integer, ScheduleParam> crossSchedules = scheduleStorage.get(crossId);

        if (crossSchedules == null || !crossSchedules.containsKey(scheduleNo)) {
            throw new DataNotFoundException("ScheduleParam", crossId + "-" + scheduleNo);
        }

        //schedule.setUpdateTime(LocalDateTime.now());
        crossSchedules.put(scheduleNo, schedule);

        logger.info("修改调度: crossId={}, scheduleNo={}", crossId, scheduleNo);
        return schedule;
    }

    private void deleteSchedule(String crossId, Integer scheduleNo) throws BusinessException {
        Map<Integer, ScheduleParam> crossSchedules = scheduleStorage.get(crossId);
        if (crossSchedules == null || !crossSchedules.containsKey(scheduleNo)) {
            throw new DataNotFoundException("ScheduleParam", crossId + "-" + scheduleNo);
        }

        crossSchedules.remove(scheduleNo);
        logger.info("删除调度: crossId={}, scheduleNo={}", crossId, scheduleNo);
    }
}
