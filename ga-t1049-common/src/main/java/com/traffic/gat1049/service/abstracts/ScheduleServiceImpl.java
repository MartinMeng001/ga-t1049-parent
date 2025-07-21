package com.traffic.gat1049.service.abstracts;

import com.traffic.gat1049.data.provider.impl.ComprehensiveTestDataProviderImpl;
import com.traffic.gat1049.exception.BusinessException;
import com.traffic.gat1049.exception.DataNotFoundException;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.enums.ScheduleType;
import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.model.enums.OperationType;
import com.traffic.gat1049.service.interfaces.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 调度服务实现 - 使用MonthDay统一处理startDay和endDay
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private ComprehensiveTestDataProviderImpl dataProvider = ComprehensiveTestDataProviderImpl.getInstance();

    // 调度存储：crossId -> scheduleNo -> ScheduleParam
    private final Map<String, Map<Integer, ScheduleParam>> scheduleStorage = new ConcurrentHashMap<>();

    // 统一使用MM-dd格式
    private static final DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

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
    public boolean existsScheduleNo(String crossId, Integer scheduleNo) throws BusinessException {
        return false;
    }

    @Override
    public Integer getNextAvailableScheduleNo(String crossId) throws BusinessException {
        return 0;
    }

    @Override
    public List<ScheduleParam> batchAddSchedules(List<ScheduleParam> scheduleParams) throws BusinessException {
        return List.of();
    }

    @Override
    public void batchDeleteSchedules(String crossId, List<Integer> scheduleNos) throws BusinessException {

    }

    @Override
    public List<ScheduleParam> copySchedulesToCross(String sourceCrossId, String targetCrossId, List<Integer> scheduleNos) throws BusinessException {
        return List.of();
    }

    @Override
    public List<ScheduleParam> createStandardScheduleTemplate(String crossId, Integer workdayDayPlanNo, Integer weekendDayPlanNo, Integer holidayDayPlanNo) throws BusinessException {
        return List.of();
    }

    @Override
    public List<ScheduleParam> resetToDefaultSchedules(String crossId, boolean keepExisting) throws BusinessException {
        return List.of();
    }

    @Override
    public ScheduleStatistics getScheduleStatistics(String crossId) throws BusinessException {
        return null;
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

    @Override
    public List<ScheduleParam> findByScheduleType(String crossId, ScheduleType scheduleType) throws BusinessException {
        return List.of();
    }

    @Override
    public List<ScheduleParam> findByScheduleNameLike(String crossId, String scheduleName) throws BusinessException {
        return List.of();
    }

    @Override
    public ScheduleParam getEffectiveSchedule(String crossId, LocalDate targetDate) throws BusinessException {
        return null;
    }

    @Override
    public ScheduleParam getCurrentEffectiveSchedule(String crossId) throws BusinessException {
        return null;
    }

    @Override
    public List<ScheduleParam> predictScheduleForDays(String crossId, int days) throws BusinessException {
        return List.of();
    }

    /**
     * 验证日期范围 - 使用MonthDay统一处理
     */
    private void validateDateRange(ScheduleParam schedule) throws BusinessException {
        String startDay = schedule.getStartDay();
        String endDay = schedule.getEndDay();

        MonthDay startMonthDay = null;
        MonthDay endMonthDay = null;

        // 验证开始日期
        if (startDay != null && !startDay.trim().isEmpty()) {
            if (!isValidMonthDayFormat(startDay)) {
                throw new ValidationException("startDay", "开始日期格式错误，应为MM-dd");
            }
            startMonthDay = parseMonthDay(startDay);
        }

        // 验证结束日期
        if (endDay != null && !endDay.trim().isEmpty()) {
            if (!isValidMonthDayFormat(endDay)) {
                throw new ValidationException("endDay", "结束日期格式错误，应为MM-dd");
            }
            endMonthDay = parseMonthDay(endDay);
        }

        // 验证日期逻辑
        if (startMonthDay != null && endMonthDay != null) {
            validateMonthDayRange(startMonthDay, endMonthDay);
        }
    }

    /**
     * 验证MonthDay范围的逻辑
     */
    private void validateMonthDayRange(MonthDay startMonthDay, MonthDay endMonthDay) throws BusinessException {
        // 对于跨年的情况（如12-25到01-05），我们认为是有效的
        // 只有当开始日期和结束日期在同一年内且开始日期晚于结束日期时才报错

        // 如果开始月份小于结束月份，或者月份相同但开始日期小于等于结束日期，则正常
        if (startMonthDay.getMonthValue() < endMonthDay.getMonthValue() ||
                (startMonthDay.getMonthValue() == endMonthDay.getMonthValue() &&
                        startMonthDay.getDayOfMonth() <= endMonthDay.getDayOfMonth())) {
            // 正常情况，不需要额外验证
            return;
        }

        // 跨年情况也是合理的（如12-25到01-05表示12月25日到次年1月5日）
        // 所以我们不抛出异常，只是记录日志
        logger.debug("检测到跨年日期范围: {} 到 {}", startMonthDay, endMonthDay);
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
     * 验证MM-dd格式
     */
    private boolean isValidMonthDayFormat(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }

        try {
            MonthDay.parse(date, MONTH_DAY_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 解析MonthDay，支持多种格式
     */
    private MonthDay parseMonthDay(String date) throws BusinessException {
        if (date == null || date.trim().isEmpty()) {
            throw new ValidationException("date", "日期不能为空");
        }

        try {
            // 首先尝试MM-dd格式
            return MonthDay.parse(date, MONTH_DAY_FORMATTER);
        } catch (DateTimeParseException e) {
            // 如果失败，尝试其他可能的格式
            try {
                // 尝试M-d格式（如1-1）
                if (date.matches("\\d{1,2}-\\d{1,2}")) {
                    String[] parts = date.split("-");
                    int month = Integer.parseInt(parts[0]);
                    int day = Integer.parseInt(parts[1]);
                    return MonthDay.of(month, day);
                }
            } catch (Exception ex) {
                // 忽略
            }

            throw new ValidationException("date", "日期格式错误，应为MM-dd格式: " + date);
        }
    }

    /**
     * 格式化MonthDay为标准字符串
     */
    private String formatMonthDay(MonthDay monthDay) {
        if (monthDay == null) {
            return null;
        }
        return monthDay.format(MONTH_DAY_FORMATTER);
    }

    /**
     * 检查调度是否在指定日期生效 - 使用MonthDay重构
     */
    public boolean isScheduleActiveOnDate(ScheduleParam schedule, LocalDate date) {
        if (schedule == null || date == null) {
            return false;
        }

        // 将LocalDate转换为MonthDay进行比较
        MonthDay targetMonthDay = MonthDay.from(date);

        // 检查日期范围
        String startDay = schedule.getStartDay();
        String endDay = schedule.getEndDay();

        if (startDay != null && !startDay.trim().isEmpty()) {
            try {
                MonthDay startMonthDay = parseMonthDay(startDay);
                MonthDay endMonthDay = null;

                if (endDay != null && !endDay.trim().isEmpty()) {
                    endMonthDay = parseMonthDay(endDay);
                }

                // 检查日期是否在范围内
                if (!isDateInRange(targetMonthDay, startMonthDay, endMonthDay)) {
                    return false;
                }
            } catch (BusinessException e) {
                logger.warn("解析调度日期失败: {}", e.getMessage());
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

    /**
     * 检查目标日期是否在MonthDay范围内
     */
    private boolean isDateInRange(MonthDay target, MonthDay start, MonthDay end) {
        if (start == null) {
            return true;
        }

        if (end == null) {
            // 只有开始日期，检查是否大于等于开始日期
            return target.compareTo(start) >= 0;
        }

        // 处理跨年情况
        if (start.compareTo(end) <= 0) {
            // 正常范围：start <= target <= end
            return target.compareTo(start) >= 0 && target.compareTo(end) <= 0;
        } else {
            // 跨年范围：target >= start OR target <= end
            return target.compareTo(start) >= 0 || target.compareTo(end) <= 0;
        }
    }

    /**
     * 工具方法：获取当前年份的调度有效期
     */
    public boolean isScheduleActiveToday(ScheduleParam schedule) {
        return isScheduleActiveOnDate(schedule, LocalDate.now());
    }

    /**
     * 工具方法：获取调度在指定年份的有效日期范围
     */
    public List<LocalDate> getScheduleActiveDates(ScheduleParam schedule, int year) {
        List<LocalDate> activeDates = new ArrayList<>();

        if (schedule == null) {
            return activeDates;
        }

        // 遍历整年的每一天
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        LocalDate current = startOfYear;
        while (!current.isAfter(endOfYear)) {
            if (isScheduleActiveOnDate(schedule, current)) {
                activeDates.add(current);
            }
            current = current.plusDays(1);
        }

        return activeDates;
    }

    /**
     * 工具方法：打印调度的有效日期信息
     */
    public void printScheduleInfo(ScheduleParam schedule) {
        if (schedule == null) {
            return;
        }

        logger.info("调度信息: crossId={}, scheduleNo={}, type={}",
                schedule.getCrossId(), schedule.getScheduleNo(), schedule.getType());

        if (schedule.getStartDay() != null) {
            logger.info("开始日期: {}", schedule.getStartDay());
        }

        if (schedule.getEndDay() != null) {
            logger.info("结束日期: {}", schedule.getEndDay());
        }

        if (schedule.getWeekDay() != null) {
            logger.info("星期设置: {} ({})", schedule.getWeekDay(), formatWeekDayDescription(schedule.getWeekDay()));
        }

        logger.info("日计划号: {}", schedule.getDayPlanNo());
    }

    /**
     * 格式化星期描述
     */
    private String formatWeekDayDescription(Integer weekDay) {
        if (weekDay == null || weekDay == 0) {
            return "无";
        }

        StringBuilder sb = new StringBuilder();
        String[] dayNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        for (int i = 0; i < 7; i++) {
            if ((weekDay & (1 << i)) != 0) {
                if (sb.length() > 0) {
                    sb.append("、");
                }
                sb.append(dayNames[i]);
            }
        }

        return sb.toString();
    }
}