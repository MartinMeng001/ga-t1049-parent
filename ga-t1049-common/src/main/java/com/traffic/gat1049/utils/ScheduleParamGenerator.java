package com.traffic.gat1049.utils;

import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.model.enums.ScheduleType;
import com.traffic.gat1049.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 调度参数自动生成器（更新版本）
 * 根据最新的ScheduleParam定义生成调度参数
 */
public class ScheduleParamGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleParamGenerator.class);

    // 日期格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    // 默认值
    private static final ScheduleType DEFAULT_SCHEDULE_TYPE = ScheduleType.WEEK;

    /**
     * 生成基础调度参数
     *
     * @param crossId    路口编号
     * @param scheduleNo 调度号
     * @param dayPlanNo  日计划号
     * @return 调度参数
     * @throws ValidationException 参数验证失败时抛出
     */
    public static ScheduleParam generateScheduleParam(String crossId, Integer scheduleNo, Integer dayPlanNo)
            throws ValidationException {
        return generateScheduleParam(crossId, scheduleNo, "默认调度", DEFAULT_SCHEDULE_TYPE, dayPlanNo);
    }

    /**
     * 生成指定类型的调度参数
     *
     * @param crossId      路口编号
     * @param scheduleNo   调度号
     * @param scheduleName 调度名称
     * @param scheduleType 调度类型
     * @param dayPlanNo    日计划号
     * @return 调度参数
     * @throws ValidationException 参数验证失败时抛出
     */
    public static ScheduleParam generateScheduleParam(String crossId, Integer scheduleNo, String scheduleName,
                                                      ScheduleType scheduleType, Integer dayPlanNo)
            throws ValidationException {

        // 参数验证
        validateBasicParameters(crossId, scheduleNo, scheduleName, scheduleType, dayPlanNo);

        ScheduleParam scheduleParam = new ScheduleParam();
        scheduleParam.setCrossId(crossId);
        scheduleParam.setScheduleNo(scheduleNo);
        scheduleParam.setScheduleName(scheduleName);
        scheduleParam.setType(scheduleType);
        scheduleParam.setDayPlanNo(dayPlanNo);

        // 根据调度类型设置默认的时间范围和星期
        setDefaultScheduleSettings(scheduleParam, scheduleType);

        logger.info("生成调度参数: crossId={}, scheduleNo={}, name={}, type={}, dayPlanNo={}",
                crossId, scheduleNo, scheduleName, scheduleType.getDescription(), dayPlanNo);

        return scheduleParam;
    }

    /**
     * 生成工作日调度参数（周一到周五）
     */
    public static List<ScheduleParam> generateWorkdaySchedules(String crossId, Integer baseDayPlanNo)
            throws ValidationException {

        List<ScheduleParam> schedules = new ArrayList<>();

        for (int weekDay = 1; weekDay <= 5; weekDay++) {
            ScheduleParam schedule = new ScheduleParam();
            schedule.setCrossId(crossId);
            schedule.setScheduleNo(weekDay); // 使用周几作为调度号
            schedule.setScheduleName("工作日调度-" + getWeekDayName(weekDay));
            schedule.setType(ScheduleType.WEEK);
            schedule.setStartDay("01-01");
            schedule.setEndDay("12-31");
            schedule.setWeekDay(weekDay);
            schedule.setDayPlanNo(baseDayPlanNo);

            schedules.add(schedule);
        }

        logger.info("生成工作日调度参数: crossId={}, 共{}个调度", crossId, schedules.size());
        return schedules;
    }

    /**
     * 生成周末调度参数（周六和周日）
     */
    public static List<ScheduleParam> generateWeekendSchedules(String crossId, Integer baseDayPlanNo)
            throws ValidationException {

        List<ScheduleParam> schedules = new ArrayList<>();

        for (int weekDay = 6; weekDay <= 7; weekDay++) {
            ScheduleParam schedule = new ScheduleParam();
            schedule.setCrossId(crossId);
            schedule.setScheduleNo(weekDay); // 使用周几作为调度号
            schedule.setScheduleName("周末调度-" + getWeekDayName(weekDay));
            schedule.setType(ScheduleType.WEEK);
            schedule.setStartDay("01-01");
            schedule.setEndDay("12-31");
            schedule.setWeekDay(weekDay);
            schedule.setDayPlanNo(baseDayPlanNo);

            schedules.add(schedule);
        }

        logger.info("生成周末调度参数: crossId={}, 共{}个调度", crossId, schedules.size());
        return schedules;
    }

    /**
     * 生成特殊日期调度参数
     */
    public static ScheduleParam generateSpecialDaySchedule(String crossId, Integer scheduleNo, String scheduleName,
                                                           String startDay, String endDay, Integer dayPlanNo)
            throws ValidationException {

        validateBasicParameters(crossId, scheduleNo, scheduleName, ScheduleType.SPECIAL_DAY, dayPlanNo);
        validateDateFormat(startDay, "开始日期");
        validateDateFormat(endDay, "结束日期");

        ScheduleParam schedule = new ScheduleParam();
        schedule.setCrossId(crossId);
        schedule.setScheduleNo(scheduleNo);
        schedule.setScheduleName(scheduleName);
        schedule.setType(ScheduleType.SPECIAL_DAY);
        schedule.setStartDay(startDay);
        schedule.setEndDay(endDay);
        schedule.setDayPlanNo(dayPlanNo);
        // 特殊日调度不设置weekDay

        logger.info("生成特殊日调度参数: crossId={}, scheduleNo={}, name={}, period={}-{}",
                crossId, scheduleNo, scheduleName, startDay, endDay);

        return schedule;
    }

    /**
     * 生成节假日调度参数
     */
    public static ScheduleParam generateHolidaySchedule(String crossId, Integer scheduleNo,
                                                        HolidayType holidayType, Integer dayPlanNo)
            throws ValidationException {

        String scheduleName = holidayType.getDescription() + "调度";
        String[] period = getHolidayPeriod(holidayType);

        return generateSpecialDaySchedule(crossId, scheduleNo, scheduleName,
                period[0], period[1], dayPlanNo);
    }

    /**
     * 生成全年默认调度参数
     */
    public static ScheduleParam generateYearlyDefaultSchedule(String crossId, Integer scheduleNo, Integer dayPlanNo)
            throws ValidationException {

        return generateSpecialDaySchedule(crossId, scheduleNo, "全年默认调度",
                "01-01", "12-31", dayPlanNo);
    }

    /**
     * 生成标准调度集合（工作日+周末+默认）
     */
    public static List<ScheduleParam> generateStandardScheduleSet(String crossId) throws ValidationException {
        List<ScheduleParam> schedules = new ArrayList<>();

        // 工作日调度（调度号1-5）
        schedules.addAll(generateWorkdaySchedules(crossId, 1));

        // 周末调度（调度号6-7）
        schedules.addAll(generateWeekendSchedules(crossId, 2));

        // 默认调度（调度号999，优先级最低）
        schedules.add(generateYearlyDefaultSchedule(crossId, 999, 3));

        logger.info("生成标准调度集合: crossId={}, 共{}个调度", crossId, schedules.size());
        return schedules;
    }

    // ========== 私有辅助方法 ==========

    /**
     * 基础参数验证
     */
    private static void validateBasicParameters(String crossId, Integer scheduleNo, String scheduleName,
                                                ScheduleType scheduleType, Integer dayPlanNo)
            throws ValidationException {

        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (scheduleNo == null || scheduleNo < 1 || scheduleNo > 999) {
            throw new ValidationException("scheduleNo", "调度号必须在1-999范围内");
        }

        if (scheduleName != null && scheduleName.length() > 50) {
            throw new ValidationException("scheduleName", "调度名称长度不能超过50个字符");
        }

        if (scheduleType == null) {
            throw new ValidationException("scheduleType", "调度类型不能为空");
        }

        if (dayPlanNo == null || dayPlanNo < 1 || dayPlanNo > 999) {
            throw new ValidationException("dayPlanNo", "日计划号必须在1-999范围内");
        }
    }

    /**
     * 验证日期格式
     */
    private static void validateDateFormat(String date, String fieldName) throws ValidationException {
        if (date == null || !date.matches("\\d{2}-\\d{2}")) {
            throw new ValidationException(fieldName, fieldName + "格式错误，应为MM-DD");
        }

        try {
            // 验证月日的有效性（使用当前年份）
            LocalDate.parse("2025-" + date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName, fieldName + "无效：" + date);
        }
    }

    /**
     * 根据调度类型设置默认设置
     */
    private static void setDefaultScheduleSettings(ScheduleParam scheduleParam, ScheduleType scheduleType) {
        switch (scheduleType) {
            case SPECIAL_DAY:
                scheduleParam.setStartDay("01-01");
                scheduleParam.setEndDay("12-31");
                // 特殊日调度不设置weekDay
                break;
            case WEEK_PERIOD:
            case WEEK:
                scheduleParam.setStartDay("01-01");
                scheduleParam.setEndDay("12-31");
                scheduleParam.setWeekDay(1); // 默认周一
                break;
        }
    }

    /**
     * 获取周几的中文名称
     */
    private static String getWeekDayName(int weekDay) {
        String[] names = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return weekDay >= 1 && weekDay <= 7 ? names[weekDay] : "未知";
    }

    /**
     * 获取节假日时间段
     */
    private static String[] getHolidayPeriod(HolidayType holidayType) {
        switch (holidayType) {
            case SPRING_FESTIVAL:
                return new String[]{"02-09", "02-17"}; // 春节（示例）
            case NATIONAL_DAY:
                return new String[]{"10-01", "10-07"}; // 国庆节
            case LABOR_DAY:
                return new String[]{"05-01", "05-05"}; // 劳动节
            case NEW_YEAR:
                return new String[]{"01-01", "01-03"}; // 元旦
            default:
                return new String[]{"01-01", "01-01"}; // 默认单天
        }
    }

    /**
     * 节假日类型枚举
     */
    public enum HolidayType {
        SPRING_FESTIVAL("春节"),
        NATIONAL_DAY("国庆节"),
        LABOR_DAY("劳动节"),
        NEW_YEAR("元旦");

        private final String description;

        HolidayType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 示例使用方法
     */
    public static void main(String[] args) {
        try {
            String crossId = "001001001";

            // 示例1: 生成基础调度参数
            System.out.println("=== 示例1: 基础调度参数 ===");
            ScheduleParam basicSchedule = generateScheduleParam(crossId, 1, 1);
            System.out.println(basicSchedule);

            // 示例2: 生成工作日调度集合
            System.out.println("\n=== 示例2: 工作日调度集合 ===");
            List<ScheduleParam> workdaySchedules = generateWorkdaySchedules(crossId, 2);
            workdaySchedules.forEach(System.out::println);

            // 示例3: 生成周末调度集合
            System.out.println("\n=== 示例3: 周末调度集合 ===");
            List<ScheduleParam> weekendSchedules = generateWeekendSchedules(crossId, 3);
            weekendSchedules.forEach(System.out::println);

            // 示例4: 生成节假日调度
            System.out.println("\n=== 示例4: 节假日调度 ===");
            ScheduleParam holidaySchedule = generateHolidaySchedule(crossId, 10, HolidayType.SPRING_FESTIVAL, 4);
            System.out.println(holidaySchedule);

            // 示例5: 生成标准调度集合
            System.out.println("\n=== 示例5: 标准调度集合 ===");
            List<ScheduleParam> standardSchedules = generateStandardScheduleSet(crossId);
            System.out.printf("生成了%d个标准调度参数：\n", standardSchedules.size());
            standardSchedules.forEach(System.out::println);

        } catch (ValidationException e) {
            System.err.println("参数验证失败: " + e.getMessage());
        }
    }
}