package com.traffic.gat1049.utils;

import com.traffic.gat1049.protocol.model.signal.ScheduleParam;
import com.traffic.gat1049.model.enums.ScheduleType;
import com.traffic.gat1049.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 调度参数自动生成器
 * 根据传入的crossId、scheduleNo和dayPlanNo，生成一个ScheduleParam
 */
public class ScheduleParamGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleParamGenerator.class);

    // 日期格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
    private static final DateTimeFormatter FULL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        return generateScheduleParam(crossId, scheduleNo, dayPlanNo, DEFAULT_SCHEDULE_TYPE);
    }

    /**
     * 生成指定类型的调度参数
     *
     * @param crossId      路口编号
     * @param scheduleNo   调度号
     * @param dayPlanNo    日计划号
     * @param scheduleType 调度类型
     * @return 调度参数
     * @throws ValidationException 参数验证失败时抛出
     */
    public static ScheduleParam generateScheduleParam(String crossId, Integer scheduleNo, Integer dayPlanNo,
                                                      ScheduleType scheduleType) throws ValidationException {

        // 参数验证
        validateBasicParameters(crossId, scheduleNo, dayPlanNo, scheduleType);

        ScheduleParam scheduleParam = new ScheduleParam();
        scheduleParam.setCrossId(crossId);
        scheduleParam.setScheduleNo(scheduleNo);
        scheduleParam.setDayPlanNo(dayPlanNo);
        scheduleParam.setType(scheduleType);

        // 根据调度类型设置默认的时间范围和星期
        setDefaultScheduleSettings(scheduleParam, scheduleType);

        logger.info("生成调度参数: crossId={}, scheduleNo={}, dayPlanNo={}, type={}",
                crossId, scheduleNo, dayPlanNo, scheduleType.getDescription());

        return scheduleParam;
    }

    /**
     * 生成全年有效的调度参数
     *
     * @param crossId    路口编号
     * @param scheduleNo 调度号
     * @param dayPlanNo  日计划号
     * @return 调度参数
     */
    public static ScheduleParam generateYearlySchedule(String crossId, Integer scheduleNo, Integer dayPlanNo)
            throws ValidationException {

        ScheduleParam scheduleParam = generateScheduleParam(crossId, scheduleNo, dayPlanNo, ScheduleType.SPECIAL_DAY);

        // 设置全年有效
        scheduleParam.setStartDay("01-01");
        scheduleParam.setEndDay("12-31");
        scheduleParam.setWeekDay(null); // 特殊日调度不需要星期设置

        logger.info("生成全年调度参数: crossId={}, scheduleNo={}, dayPlanNo={}", crossId, scheduleNo, dayPlanNo);

        return scheduleParam;
    }

    /**
     * 生成工作日调度参数（周一到周五）
     *
     * @param crossId    路口编号
     * @param scheduleNo 调度号
     * @param dayPlanNo  日计划号
     * @return 调度参数
     */
    public static ScheduleParam generateWorkdaySchedule(String crossId, Integer scheduleNo, Integer dayPlanNo)
            throws ValidationException {

        ScheduleParam scheduleParam = generateScheduleParam(crossId, scheduleNo, dayPlanNo, ScheduleType.WEEK);

        // 设置工作日（周一到周五）
        // 使用位掩码：1=周一, 2=周二, 4=周三, 8=周四, 16=周五
        // 工作日 = 1+2+4+8+16 = 31
        scheduleParam.setWeekDay(31);
        scheduleParam.setStartDay("01-01");
        scheduleParam.setEndDay("12-31");

        logger.info("生成工作日调度参数: crossId={}, scheduleNo={}, dayPlanNo={}", crossId, scheduleNo, dayPlanNo);

        return scheduleParam;
    }

    /**
     * 生成周末调度参数（周六和周日）
     *
     * @param crossId    路口编号
     * @param scheduleNo 调度号
     * @param dayPlanNo  日计划号
     * @return 调度参数
     */
    public static ScheduleParam generateWeekendSchedule(String crossId, Integer scheduleNo, Integer dayPlanNo)
            throws ValidationException {

        ScheduleParam scheduleParam = generateScheduleParam(crossId, scheduleNo, dayPlanNo, ScheduleType.WEEK);

        // 设置周末（周六和周日）
        // 使用位掩码：32=周六, 64=周日
        // 周末 = 32+64 = 96
        scheduleParam.setWeekDay(96);
        scheduleParam.setStartDay("01-01");
        scheduleParam.setEndDay("12-31");

        logger.info("生成周末调度参数: crossId={}, scheduleNo={}, dayPlanNo={}", crossId, scheduleNo, dayPlanNo);

        return scheduleParam;
    }

    /**
     * 生成特定日期范围的调度参数
     *
     * @param crossId    路口编号
     * @param scheduleNo 调度号
     * @param dayPlanNo  日计划号
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return 调度参数
     */
    public static ScheduleParam generateDateRangeSchedule(String crossId, Integer scheduleNo, Integer dayPlanNo,
                                                          LocalDate startDate, LocalDate endDate)
            throws ValidationException {

        if (startDate == null || endDate == null) {
            throw new ValidationException("dateRange", "开始日期和结束日期不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("dateRange", "开始日期不能晚于结束日期");
        }

        ScheduleParam scheduleParam = generateScheduleParam(crossId, scheduleNo, dayPlanNo, ScheduleType.SPECIAL_DAY);

        // 设置日期范围
        scheduleParam.setStartDay(startDate.format(DATE_FORMATTER));
        scheduleParam.setEndDay(endDate.format(DATE_FORMATTER));
        scheduleParam.setWeekDay(null); // 特殊日调度不需要星期设置

        logger.info("生成日期范围调度参数: crossId={}, scheduleNo={}, dayPlanNo={}, 日期范围={} 到 {}",
                crossId, scheduleNo, dayPlanNo, startDate, endDate);

        return scheduleParam;
    }

    /**
     * 生成节假日调度参数
     *
     * @param crossId     路口编号
     * @param scheduleNo  调度号
     * @param dayPlanNo   日计划号
     * @param holidayType 节假日类型
     * @return 调度参数
     */
    public static ScheduleParam generateHolidaySchedule(String crossId, Integer scheduleNo, Integer dayPlanNo,
                                                        HolidayType holidayType) throws ValidationException {

        ScheduleParam scheduleParam = generateScheduleParam(crossId, scheduleNo, dayPlanNo, ScheduleType.SPECIAL_DAY);

        // 根据节假日类型设置不同的日期范围
        switch (holidayType) {
            case SPRING_FESTIVAL:
                scheduleParam.setStartDay("02-10");
                scheduleParam.setEndDay("02-17");
                break;
            case NATIONAL_DAY:
                scheduleParam.setStartDay("10-01");
                scheduleParam.setEndDay("10-07");
                break;
            case LABOR_DAY:
                scheduleParam.setStartDay("05-01");
                scheduleParam.setEndDay("05-03");
                break;
            case QINGMING:
                scheduleParam.setStartDay("04-03");
                scheduleParam.setEndDay("04-05");
                break;
            case DRAGON_BOAT:
                scheduleParam.setStartDay("06-22");
                scheduleParam.setEndDay("06-24");
                break;
            case MID_AUTUMN:
                scheduleParam.setStartDay("09-15");
                scheduleParam.setEndDay("09-17");
                break;
            case NEW_YEAR:
                scheduleParam.setStartDay("01-01");
                scheduleParam.setEndDay("01-03");
                break;
            default:
                scheduleParam.setStartDay("01-01");
                scheduleParam.setEndDay("01-01");
        }

        scheduleParam.setWeekDay(null);

        logger.info("生成{}调度参数: crossId={}, scheduleNo={}, dayPlanNo={}",
                holidayType.getDescription(), crossId, scheduleNo, dayPlanNo);

        return scheduleParam;
    }

    /**
     * 生成特定星期的调度参数
     *
     * @param crossId    路口编号
     * @param scheduleNo 调度号
     * @param dayPlanNo  日计划号
     * @param weekDays   星期数组（1-7表示周一到周日）
     * @return 调度参数
     */
    public static ScheduleParam generateWeekDaySchedule(String crossId, Integer scheduleNo, Integer dayPlanNo,
                                                        int[] weekDays) throws ValidationException {

        if (weekDays == null || weekDays.length == 0) {
            throw new ValidationException("weekDays", "星期数组不能为空");
        }

        ScheduleParam scheduleParam = generateScheduleParam(crossId, scheduleNo, dayPlanNo, ScheduleType.WEEK);

        // 计算星期位掩码
        int weekDayMask = 0;
        for (int day : weekDays) {
            if (day < 1 || day > 7) {
                throw new ValidationException("weekDays", "星期值必须在1-7之间");
            }
            weekDayMask |= (1 << (day - 1));
        }

        scheduleParam.setWeekDay(weekDayMask);
        scheduleParam.setStartDay("01-01");
        scheduleParam.setEndDay("12-31");

        logger.info("生成特定星期调度参数: crossId={}, scheduleNo={}, dayPlanNo={}, 星期掩码={}",
                crossId, scheduleNo, dayPlanNo, weekDayMask);

        return scheduleParam;
    }

    /**
     * 批量生成标准调度参数集合
     * 包含工作日、周末、节假日等常用调度
     *
     * @param crossId 路口编号
     * @return 标准调度参数列表
     */
    public static List<ScheduleParam> generateStandardScheduleSet(String crossId) throws ValidationException {
        List<ScheduleParam> scheduleList = new ArrayList<>();

        // 1. 工作日调度（使用日计划1）
        scheduleList.add(generateWorkdaySchedule(crossId, 1, 1));

        // 2. 周末调度（使用日计划2）
        scheduleList.add(generateWeekendSchedule(crossId, 2, 2));

        // 3. 春节调度（使用日计划3）
        scheduleList.add(generateHolidaySchedule(crossId, 3, 3, HolidayType.SPRING_FESTIVAL));

        // 4. 国庆调度（使用日计划4）
        scheduleList.add(generateHolidaySchedule(crossId, 4, 4, HolidayType.NATIONAL_DAY));

        // 5. 劳动节调度（使用日计划5）
        scheduleList.add(generateHolidaySchedule(crossId, 5, 5, HolidayType.LABOR_DAY));

        logger.info("生成标准调度参数集合: crossId={}, 共{}个调度", crossId, scheduleList.size());

        return scheduleList;
    }

    /**
     * 根据调度类型设置默认的调度设置
     */
    private static void setDefaultScheduleSettings(ScheduleParam scheduleParam, ScheduleType scheduleType) {
        switch (scheduleType) {
            case SPECIAL_DAY:
                // 特殊日调度：默认当前年份全年
                scheduleParam.setStartDay("01-01");
                scheduleParam.setEndDay("12-31");
                scheduleParam.setWeekDay(null);
                break;

            case WEEK_PERIOD:
                // 时间段周调度：默认全年，工作日
                scheduleParam.setStartDay("01-01");
                scheduleParam.setEndDay("12-31");
                scheduleParam.setWeekDay(31); // 工作日（周一到周五）
                break;

            case WEEK:
                // 周调度：默认全年，每天
                scheduleParam.setStartDay("01-01");
                scheduleParam.setEndDay("12-31");
                scheduleParam.setWeekDay(127); // 全周（周一到周日）= 1+2+4+8+16+32+64
                break;

            default:
                scheduleParam.setStartDay("01-01");
                scheduleParam.setEndDay("12-31");
                scheduleParam.setWeekDay(127);
        }
    }

    /**
     * 参数验证
     */
    private static void validateBasicParameters(String crossId, Integer scheduleNo, Integer dayPlanNo,
                                                ScheduleType scheduleType) throws ValidationException {

        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (scheduleNo == null || scheduleNo < 1 || scheduleNo > 999) {
            throw new ValidationException("scheduleNo", "调度号必须在1-999之间");
        }

        if (dayPlanNo == null || dayPlanNo < 1) {
            throw new ValidationException("dayPlanNo", "日计划号必须大于0");
        }

        if (scheduleType == null) {
            throw new ValidationException("scheduleType", "调度类型不能为空");
        }
    }

    /**
     * 工具方法：打印调度参数信息
     */
    public static void printScheduleParam(ScheduleParam scheduleParam) {
        System.out.println("=== 调度参数信息 ===");
        System.out.printf("路口编号: %s%n", scheduleParam.getCrossId());
        System.out.printf("调度号: %d%n", scheduleParam.getScheduleNo());
        System.out.printf("调度类型: %s (%s)%n", scheduleParam.getType().getCode(), scheduleParam.getType().getDescription());
        System.out.printf("日期范围: %s 到 %s%n", scheduleParam.getStartDay(), scheduleParam.getEndDay());
        System.out.printf("星期设置: %s%n", formatWeekDay(scheduleParam.getWeekDay()));
        System.out.printf("日计划号: %d%n", scheduleParam.getDayPlanNo());
        System.out.println("==================");
    }

    /**
     * 格式化星期显示
     */
    private static String formatWeekDay(Integer weekDay) {
        if (weekDay == null) {
            return "无限制";
        }

        if (weekDay == 0) {
            return "无效";
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

    /**
     * 节假日类型枚举
     */
    public enum HolidayType {
        SPRING_FESTIVAL("春节"),
        NATIONAL_DAY("国庆节"),
        LABOR_DAY("劳动节"),
        QINGMING("清明节"),
        DRAGON_BOAT("端午节"),
        MID_AUTUMN("中秋节"),
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
            System.out.println("示例1: 基础调度参数");
            ScheduleParam basicSchedule = generateScheduleParam(crossId, 1, 1);
            printScheduleParam(basicSchedule);

            // 示例2: 生成工作日调度
            System.out.println("\n示例2: 工作日调度");
            ScheduleParam workdaySchedule = generateWorkdaySchedule(crossId, 2, 2);
            printScheduleParam(workdaySchedule);

            // 示例3: 生成周末调度
            System.out.println("\n示例3: 周末调度");
            ScheduleParam weekendSchedule = generateWeekendSchedule(crossId, 3, 3);
            printScheduleParam(weekendSchedule);

            // 示例4: 生成节假日调度
            System.out.println("\n示例4: 春节调度");
            ScheduleParam holidaySchedule = generateHolidaySchedule(crossId, 4, 4, HolidayType.SPRING_FESTIVAL);
            printScheduleParam(holidaySchedule);

            // 示例5: 生成日期范围调度
            System.out.println("\n示例5: 日期范围调度");
            LocalDate startDate = LocalDate.of(2025, 7, 1);
            LocalDate endDate = LocalDate.of(2025, 8, 31);
            ScheduleParam dateRangeSchedule = generateDateRangeSchedule(crossId, 5, 5, startDate, endDate);
            printScheduleParam(dateRangeSchedule);

            // 示例6: 生成特定星期调度（周一、周三、周五）
            System.out.println("\n示例6: 特定星期调度");
            int[] weekDays = {1, 3, 5}; // 周一、周三、周五
            ScheduleParam weekDaySchedule = generateWeekDaySchedule(crossId, 6, 6, weekDays);
            printScheduleParam(weekDaySchedule);

            // 示例7: 生成标准调度集合
            System.out.println("\n示例7: 标准调度集合");
            List<ScheduleParam> standardSchedules = generateStandardScheduleSet(crossId);
            System.out.printf("生成了%d个标准调度参数%n", standardSchedules.size());

        } catch (ValidationException e) {
            System.err.println("参数验证失败: " + e.getMessage());
        }
    }
}