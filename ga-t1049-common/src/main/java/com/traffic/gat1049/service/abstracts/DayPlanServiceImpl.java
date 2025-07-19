package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.model.enums.OperationType;
import com.traffic.gat1049.protocol.model.signal.Period;
import com.traffic.gat1049.service.interfaces.DayPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日计划服务实现
 */
@Service
public class DayPlanServiceImpl implements DayPlanService {

    private static final Logger logger = LoggerFactory.getLogger(DayPlanServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 日计划存储：crossId -> dayPlanNo -> DayPlanParam
    private final Map<String, Map<Integer, DayPlanParam>> dayPlanStorage = new ConcurrentHashMap<>();

    public DayPlanServiceImpl() throws BusinessException {
    }

    @Override
    public List<DayPlanParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataProvider.getDayPlansByCrossId(crossId);
    }

    @Override
    public DayPlanParam findByCrossIdAndDayPlanNo(String crossId, Integer dayPlanNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (dayPlanNo == null) {
            throw new ValidationException("dayPlanNo", "日计划号不能为空");
        }

        DayPlanParam dayPlan = dataProvider.getDayPlanByCrossIdAndNo(crossId, dayPlanNo.toString());
        if (dayPlan == null) {
            throw new DataNotFoundException("DayPlanParam", crossId + "-" + dayPlanNo);
        }

        return dayPlan;
    }

    @Override
    public DayPlanParam getDayPlanParam(String crossId, Integer dayPlanNo) throws BusinessException {
        // 兼容原PlanService接口的方法，直接调用标准方法
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return findByCrossIdAndDayPlanNo(crossId, dayPlanNo);
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
                return addDayPlan(dayPlanParam);
            case MODIFY:
                return modifyDayPlan(dayPlanParam);
            case DELETE:
                deleteDayPlan(crossId, dayPlanNo);
                return dayPlanParam;
            default:
                throw new BusinessException("UNSUPPORTED_OPERATION", "不支持的操作类型: " + operationType);
        }
    }

    @Override
    public List<DayPlanParam> findAllDayPlans() throws BusinessException {
        return dataProvider.getAllDayPlans();
    }

    @Override
    public void deleteDayPlan(String crossId, Integer dayPlanNo) throws BusinessException {
        Map<Integer, DayPlanParam> crossDayPlans = dayPlanStorage.get(crossId);
        if (crossDayPlans == null || !crossDayPlans.containsKey(dayPlanNo)) {
            throw new DataNotFoundException("DayPlanParam", crossId + "-" + dayPlanNo);
        }

        crossDayPlans.remove(dayPlanNo);
        logger.info("删除日计划: crossId={}, dayPlanNo={}", crossId, dayPlanNo);
    }

    @Override
    public DayPlanParam addDayPlan(DayPlanParam dayPlanParam) throws BusinessException {
        validateDayPlan(dayPlanParam);

        String crossId = dayPlanParam.getCrossId();
        Integer dayPlanNo = dayPlanParam.getDayPlanNo();

        dayPlanStorage.computeIfAbsent(crossId, k -> new ConcurrentHashMap<>())
                .put(dayPlanNo, dayPlanParam);

        logger.info("添加日计划: crossId={}, dayPlanNo={}", crossId, dayPlanNo);
        return dayPlanParam;
    }

    @Override
    public DayPlanParam modifyDayPlan(DayPlanParam dayPlanParam) throws BusinessException {
        validateDayPlan(dayPlanParam);

        String crossId = dayPlanParam.getCrossId();
        Integer dayPlanNo = dayPlanParam.getDayPlanNo();
        Map<Integer, DayPlanParam> crossDayPlans = dayPlanStorage.get(crossId);

        if (crossDayPlans == null || !crossDayPlans.containsKey(dayPlanNo)) {
            throw new DataNotFoundException("DayPlanParam", crossId + "-" + dayPlanNo);
        }

        crossDayPlans.put(dayPlanNo, dayPlanParam);

        logger.info("修改日计划: crossId={}, dayPlanNo={}", crossId, dayPlanNo);
        return dayPlanParam;
    }

    @Override
    public void validateDayPlan(DayPlanParam dayPlan) throws BusinessException {
        if (dayPlan.getCrossId() == null || dayPlan.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (dayPlan.getDayPlanNo() == null || dayPlan.getDayPlanNo() <= 0) {
            throw new ValidationException("dayPlanNo", "日计划号必须大于0");
        }

        if (dayPlan.getPeriodList() == null || dayPlan.getPeriodList().isEmpty()) {
            throw new ValidationException("periodList", "时段信息列表不能为空");
        }

        // 验证时段信息
        validatePeriods(dayPlan.getPeriodList());
    }

    @Override
    public boolean isDayPlanNameDuplicate(String crossId, String dayPlanName, Integer excludeDayPlanNo) throws BusinessException {
        return false;
    }

    @Override
    public DayPlanParam findByName(String crossId, String dayPlanName) throws BusinessException {
        return null;
    }

    @Override
    public List<DayPlanParam> batchSetDayPlanParams(OperationType operationType, List<DayPlanParam> dayPlanParams) throws BusinessException {
        return List.of();
    }

    @Override
    public DayPlanParam copyDayPlan(String sourceCrossId, Integer sourceDayPlanNo, String targetCrossId, Integer targetDayPlanNo, String targetDayPlanName) throws BusinessException {
        return null;
    }

    @Override
    public boolean isDayPlanInUse(String crossId, Integer dayPlanNo) throws BusinessException {
        return false;
    }

    @Override
    public List<Integer> getDayPlanUsage(String crossId, Integer dayPlanNo) throws BusinessException {
        return List.of();
    }

    @Override
    public void validatePeriodConfiguration(DayPlanParam dayPlanParam) throws BusinessException {

    }

    @Override
    public DayPlanParam optimizePeriodConfiguration(DayPlanParam dayPlanParam) throws BusinessException {
        return null;
    }

    /**
     * 验证时段信息列表 - 适配String类型的startTime
     */
    private void validatePeriods(List<Period> periods) throws BusinessException {
        if (periods == null || periods.isEmpty()) {
            throw new ValidationException("periodList", "时段信息列表不能为空");
        }

        // 检查时段是否按时间顺序排列
        for (int i = 0; i < periods.size() - 1; i++) {
            Period current = periods.get(i);
            Period next = periods.get(i + 1);

            if (current.getStartTime() == null || next.getStartTime() == null) {
                throw new ValidationException("startTime", "时段开始时间不能为空");
            }

            // 使用Period类的时间比较方法
            if (Period.compareTime(current.getStartTime(), next.getStartTime()) >= 0) {
                throw new ValidationException("periodList", "时段必须按开始时间升序排列");
            }
        }

        // 验证每个时段的有效性
        for (Period period : periods) {
            validatePeriod(period);
        }
    }

    /**
     * 验证单个时段信息 - 适配String类型的startTime
     */
    private void validatePeriod(Period period) throws BusinessException {
        if (period.getStartTime() == null || period.getStartTime().trim().isEmpty()) {
            throw new ValidationException("startTime", "时段开始时间不能为空");
        }

        if (period.getPlanNo() == null || period.getPlanNo() <= 0) {
            throw new ValidationException("planNo", "配时方案号必须大于0");
        }

        if (period.getCtrlMode() == null || period.getCtrlMode().trim().isEmpty()) {
            throw new ValidationException("ctrlMode", "控制方式不能为空");
        }

        // 使用Period类的验证方法检查时间格式
        if (!Period.isValidTimeFormat(period.getStartTime())) {
            throw new ValidationException("startTime", "时间格式必须为HH:MM:SS或HH:MM");
        }
    }
}