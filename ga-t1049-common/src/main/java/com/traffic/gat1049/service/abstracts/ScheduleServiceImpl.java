package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.model.enums.OperationType;
import com.traffic.gat1049.service.interfaces.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 调度服务实现
 */
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 调度存储：crossId -> scheduleNo -> ScheduleParam
    private final Map<String, Map<Integer, ScheduleParam>> scheduleStorage = new ConcurrentHashMap<>();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ScheduleServiceImpl() throws BusinessException {
    }

    @Override
    public List<ScheduleParam> findByCrossId(String crossId) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        return dataProvider.getSchedulesByCrossId(crossId);
    }

    @Override
    public ScheduleParam findByCrossIdAndScheduleNo(String crossId, Integer scheduleNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (scheduleNo == null) {
            throw new ValidationException("scheduleNo", "调度号不能为空");
        }

        ScheduleParam schedule = dataProvider.getScheduleByCrossIdAndNo(crossId, scheduleNo.toString());
        if (schedule == null) {
            throw new DataNotFoundException("ScheduleParam", crossId + "-" + scheduleNo);
        }

        return schedule;
    }

    @Override
    public ScheduleParam getScheduleParam(String crossId, Integer scheduleNo) throws BusinessException {
        // 兼容原PlanService接口的方法，直接调用标准方法
        return findByCrossIdAndScheduleNo(crossId, scheduleNo);
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
                return addSchedule(scheduleParam);
            case MODIFY:
                return modifySchedule(scheduleParam);
            case DELETE:
                deleteSchedule(crossId, scheduleNo);
                return scheduleParam;
            default:
                throw new BusinessException("UNSUPPORTED_OPERATION", "不支持的操作类型: " + operationType);
        }
    }

    @Override
    public List<ScheduleParam> findAllSchedules() throws BusinessException {
        return dataProvider.getAllSchedules();
    }

    @Override
    public void deleteSchedule(String crossId, Integer scheduleNo) throws BusinessException {
        Map<Integer, ScheduleParam> crossSchedules = scheduleStorage.get(crossId);
        if (crossSchedules == null || !crossSchedules.containsKey(scheduleNo)) {
            throw new DataNotFoundException("ScheduleParam", crossId + "-" + scheduleNo);
        }

        crossSchedules.remove(scheduleNo);
        logger.info("删除调度: crossId={}, scheduleNo={}", crossId, scheduleNo);
    }

    @Override
    public ScheduleParam addSchedule(ScheduleParam scheduleParam) throws BusinessException {
        validateSchedule(scheduleParam);

        String crossId = scheduleParam.getCrossId();
        Integer scheduleNo = scheduleParam.getScheduleNo();

        scheduleStorage.computeIfAbsent(crossId, k -> new ConcurrentHashMap<>())
                .put(scheduleNo, scheduleParam);

        logger.info("添加调度: crossId={}, scheduleNo={}", crossId, scheduleNo);
        return scheduleParam;
    }

    @Override
    public ScheduleParam modifySchedule(ScheduleParam scheduleParam) throws BusinessException {
        validateSchedule(scheduleParam);

        String crossId = scheduleParam.getCrossId();
        Integer scheduleNo = scheduleParam.getScheduleNo();
        Map<Integer, ScheduleParam> crossSchedules = scheduleStorage.get(crossId);

        if (crossSchedules == null || !crossSchedules.containsKey(scheduleNo)) {
            throw new DataNotFoundException("ScheduleParam", crossId + "-" + scheduleNo);
        }

        crossSchedules.put(scheduleNo, scheduleParam);

        logger.info("修改调度: crossId={}, scheduleNo={}", crossId, scheduleNo);
        return scheduleParam;
    }

    @Override
    public void validateSchedule(ScheduleParam schedule) throws BusinessException {
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

        // 验证日期格式和逻辑
        validateDateRange(schedule);

        // 验证星期设置
        validateWeekDay(schedule);
    }

    @Override
    public List<ScheduleParam> findByDayPlanNo(String crossId, Integer dayPlanNo) throws BusinessException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }
        if (dayPlanNo == null) {
            throw new ValidationException("dayPlanNo", "日计划号不能为空");
        }

        List<ScheduleParam> allSchedules = findByCrossId(crossId);
        return allSchedules.stream()
                .filter(schedule -> dayPlanNo.equals(schedule.getDayPlanNo()))
                .collect(Collectors.toList());
    }

    /**
     * 验证日期范围
     */
    private void validateDateRange(ScheduleParam schedule) throws BusinessException {
        String startDay = schedule.getStartDay();
        String endDay = schedule.getEndDay();

        if (startDay != null && !startDay.trim().isEmpty()) {
            if (!isValidDateFormat(startDay)) {
                throw new ValidationException("startDay", "开始日期格式错误，应为yyyy-MM-dd");
            }
        }

        if (endDay != null && !endDay.trim().isEmpty()) {
            if (!isValidDateFormat(endDay)) {
                throw new ValidationException("endDay", "结束日期格式错误，应为yyyy-MM-dd");
            }
        }

        // 如果开始和结束日期都存在，验证日期逻辑
        if (startDay != null && !startDay.trim().isEmpty() &&
                endDay != null && !endDay.trim().isEmpty()) {

            try {
                LocalDate start = LocalDate.parse(startDay, DATE_FORMATTER);
                LocalDate end = LocalDate.parse(endDay, DATE_FORMATTER);

                if (start.isAfter(end)) {
                    throw new ValidationException("dateRange", "开始日期不能晚于结束日期");
                }
            } catch (DateTimeParseException e) {
                throw new ValidationException("dateFormat", "日期解析失败: " + e.getMessage());
            }
        }
    }

    /**
     * 验证星期设置
     */
    private void validateWeekDay(ScheduleParam schedule) throws BusinessException {
        Integer weekDay = schedule.getWeekDay();

        if (weekDay != null) {
            // 星期值应该在0-127之间（按位表示星期一到星期日）
            if (weekDay < 0 || weekDay > 127) {
                throw new ValidationException("weekDay", "星期设置值应在0-127之间");
            }
        }
    }

    /**
     * 验证日期格式
     */
    private boolean isValidDateFormat(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 检查调度是否在指定日期生效
     */
    public boolean isScheduleActiveOnDate(ScheduleParam schedule, LocalDate date) {
        if (schedule == null || date == null) {
            return false;
        }

        // 检查日期范围
        String startDay = schedule.getStartDay();
        String endDay = schedule.getEndDay();

        if (startDay != null && !startDay.trim().isEmpty()) {
            LocalDate start = LocalDate.parse(startDay, DATE_FORMATTER);
            if (date.isBefore(start)) {
                return false;
            }
        }

        if (endDay != null && !endDay.trim().isEmpty()) {
            LocalDate end = LocalDate.parse(endDay, DATE_FORMATTER);
            if (date.isAfter(end)) {
                return false;
            }
        }

        // 检查星期设置
        Integer weekDay = schedule.getWeekDay();
        if (weekDay != null && weekDay > 0) {
            int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
            int bitPosition = dayOfWeek - 1; // 转换为0-6
            return (weekDay & (1 << bitPosition)) != 0;
        }

        return true;
    }
}