package com.traffic.gat1049.utils;

import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.protocol.model.signal.Period;
import com.traffic.gat1049.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 日计划参数生成器
 * 更新版本 - 符合最新协议定义
 */
public class DayPlanParamGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DayPlanParamGenerator.class);

    /**
     * 生成标准工作日日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @param dayPlanName 日计划名称
     * @return 工作日日计划参数
     */
    public static DayPlanParam generateWorkdayDayPlan(String crossId, Integer dayPlanNo, String dayPlanName)
            throws ValidationException {

        validateBasicParams(crossId, dayPlanNo);

        DayPlanParam dayPlan = new DayPlanParam(crossId, dayPlanNo, dayPlanName);

        // 使用更新后的时间格式（HH24:MM）
        List<Period> periodList = new ArrayList<>();

        // 夜间低峰：00:00-06:00 (方案3-夜间方案)
        periodList.add(new Period("00:00", 3, "21"));

        // 早高峰：06:00-09:00 (方案1-高峰方案)
        periodList.add(new Period("06:00", 1, "21"));

        // 日间平峰：09:00-17:00 (方案2-平峰方案)
        periodList.add(new Period("09:00", 2, "21"));

        // 晚高峰：17:00-20:00 (方案1-高峰方案)
        periodList.add(new Period("17:00", 1, "21"));

        // 夜间过渡：20:00-00:00 (方案3-夜间方案)
        periodList.add(new Period("20:00", 3, "21"));

        dayPlan.setPeriodList(periodList);

        logger.info("生成标准工作日日计划: crossId={}, dayPlanNo={}, name={}", crossId, dayPlanNo, dayPlanName);
        return dayPlan;
    }

    /**
     * 生成周末日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @param dayPlanName 日计划名称
     * @return 周末日计划参数
     */
    public static DayPlanParam generateWeekendDayPlan(String crossId, Integer dayPlanNo, String dayPlanName)
            throws ValidationException {

        validateBasicParams(crossId, dayPlanNo);

        DayPlanParam dayPlan = new DayPlanParam(crossId, dayPlanNo, dayPlanName);

        List<Period> periodList = new ArrayList<>();

        // 夜间：00:00-08:00 (方案3-夜间方案)
        periodList.add(new Period("00:00", 3, "21"));

        // 日间：08:00-22:00 (方案2-平峰方案)
        periodList.add(new Period("08:00", 2, "21"));

        // 夜间：22:00-00:00 (方案3-夜间方案)
        periodList.add(new Period("22:00", 3, "21"));

        dayPlan.setPeriodList(periodList);

        logger.info("生成周末日计划: crossId={}, dayPlanNo={}, name={}", crossId, dayPlanNo, dayPlanName);
        return dayPlan;
    }

    /**
     * 生成节假日日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @param dayPlanName 日计划名称
     * @return 节假日日计划参数
     */
    public static DayPlanParam generateHolidayDayPlan(String crossId, Integer dayPlanNo, String dayPlanName)
            throws ValidationException {

        validateBasicParams(crossId, dayPlanNo);

        DayPlanParam dayPlan = new DayPlanParam(crossId, dayPlanNo, dayPlanName);

        List<Period> periodList = new ArrayList<>();

        // 夜间：00:00-08:00 (方案3-夜间方案)
        periodList.add(new Period("00:00", 3, "21"));

        // 延后的早高峰：08:00-11:00 (方案1-高峰方案)
        periodList.add(new Period("08:00", 1, "21"));

        // 平峰：11:00-18:00 (方案2-平峰方案)
        periodList.add(new Period("11:00", 2, "21"));

        // 延长的晚高峰：18:00-22:00 (方案1-高峰方案)
        periodList.add(new Period("18:00", 1, "21"));

        // 夜间：22:00-00:00 (方案3-夜间方案)
        periodList.add(new Period("22:00", 3, "21"));

        dayPlan.setPeriodList(periodList);

        logger.info("生成节假日日计划: crossId={}, dayPlanNo={}, name={}", crossId, dayPlanNo, dayPlanName);
        return dayPlan;
    }

    /**
     * 生成感应控制日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @param dayPlanName 日计划名称
     * @return 感应控制日计划参数
     */
    public static DayPlanParam generateActuatedDayPlan(String crossId, Integer dayPlanNo, String dayPlanName)
            throws ValidationException {

        validateBasicParams(crossId, dayPlanNo);

        DayPlanParam dayPlan = new DayPlanParam(crossId, dayPlanNo, dayPlanName);

        List<Period> periodList = new ArrayList<>();

        // 夜间感应：00:00-06:00
        periodList.add(new Period("00:00", 1, "22")); // 22-单点感应控制

        // 白天感应：06:00-22:00
        periodList.add(new Period("06:00", 2, "22"));

        // 夜间感应：22:00-00:00
        periodList.add(new Period("22:00", 1, "22"));

        dayPlan.setPeriodList(periodList);

        logger.info("生成感应控制日计划: crossId={}, dayPlanNo={}, name={}", crossId, dayPlanNo, dayPlanName);
        return dayPlan;
    }

    /**
     * 生成自适应控制日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @param dayPlanName 日计划名称
     * @return 自适应控制日计划参数
     */
    public static DayPlanParam generateAdaptiveDayPlan(String crossId, Integer dayPlanNo, String dayPlanName)
            throws ValidationException {

        validateBasicParams(crossId, dayPlanNo);

        DayPlanParam dayPlan = new DayPlanParam(crossId, dayPlanNo, dayPlanName);

        List<Period> periodList = new ArrayList<>();

        // 全天自适应控制
        periodList.add(new Period("00:00", 1, "23")); // 23-单点自适应控制

        dayPlan.setPeriodList(periodList);

        logger.info("生成自适应控制日计划: crossId={}, dayPlanNo={}, name={}", crossId, dayPlanNo, dayPlanName);
        return dayPlan;
    }

    /**
     * 生成特殊控制日计划（如施工、紧急情况等）
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @param dayPlanName 日计划名称
     * @param specialCtrlMode 特殊控制模式
     * @return 特殊控制日计划参数
     */
    public static DayPlanParam generateSpecialControlDayPlan(String crossId, Integer dayPlanNo, String dayPlanName, String specialCtrlMode)
            throws ValidationException {

        validateBasicParams(crossId, dayPlanNo);

        if (!isValidSpecialCtrlMode(specialCtrlMode)) {
            throw new ValidationException("specialCtrlMode", "无效的特殊控制模式: " + specialCtrlMode);
        }

        DayPlanParam dayPlan = new DayPlanParam(crossId, dayPlanNo, dayPlanName);

        List<Period> periodList = new ArrayList<>();

        // 全天特殊控制，方案号为0表示无方案的特殊控制
        periodList.add(new Period("00:00", 0, specialCtrlMode));

        dayPlan.setPeriodList(periodList);

        logger.info("生成特殊控制日计划: crossId={}, dayPlanNo={}, name={}, ctrlMode={}",
                crossId, dayPlanNo, dayPlanName, specialCtrlMode);
        return dayPlan;
    }

    /**
     * 生成自定义时段日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @param dayPlanName 日计划名称
     * @param periods 自定义时段列表
     * @return 自定义日计划参数
     */
    public static DayPlanParam generateCustomDayPlan(String crossId, Integer dayPlanNo, String dayPlanName, List<Period> periods)
            throws ValidationException {

        validateBasicParams(crossId, dayPlanNo);

        if (periods == null || periods.isEmpty()) {
            throw new ValidationException("periods", "时段列表不能为空");
        }

        DayPlanParam dayPlan = new DayPlanParam(crossId, dayPlanNo, dayPlanName);

        // 验证并排序时段
        List<Period> sortedPeriods = new ArrayList<>(periods);
        sortedPeriods.sort((p1, p2) -> Period.compareTime(p1.getStartTime(), p2.getStartTime()));

        // 验证时段的有效性
        validatePeriods(sortedPeriods);

        dayPlan.setPeriodList(sortedPeriods);

        logger.info("生成自定义日计划: crossId={}, dayPlanNo={}, name={}, periods={}",
                crossId, dayPlanNo, dayPlanName, periods.size());
        return dayPlan;
    }

    /**
     * 生成平均分配时段的日计划
     *
     * @param crossId 路口编号
     * @param dayPlanNo 日计划号
     * @param dayPlanName 日计划名称
     * @param periodCount 时段数量
     * @param planNumbers 配时方案号数组
     * @param ctrlMode 控制方式
     * @return 平均分配时段的日计划参数
     */
    public static DayPlanParam generateEvenPeriodDayPlan(String crossId, Integer dayPlanNo, String dayPlanName,
                                                         int periodCount, int[] planNumbers, String ctrlMode)
            throws ValidationException {

        validateBasicParams(crossId, dayPlanNo);

        if (periodCount < 1 || periodCount > 12) {
            throw new ValidationException("periodCount", "时段数量必须在1-12之间");
        }

        if (planNumbers == null || planNumbers.length == 0) {
            throw new ValidationException("planNumbers", "配时方案号数组不能为空");
        }

        if (!Period.isValidCtrlMode(ctrlMode)) {
            throw new ValidationException("ctrlMode", "无效的控制方式: " + ctrlMode);
        }

        DayPlanParam dayPlan = new DayPlanParam(crossId, dayPlanNo, dayPlanName);

        List<Period> periodList = new ArrayList<>();

        // 计算每个时段的时间间隔（分钟）
        int intervalMinutes = 24 * 60 / periodCount;

        for (int i = 0; i < periodCount; i++) {
            // 计算开始时间
            int startMinutes = i * intervalMinutes;
            String startTime = Period.minutesToTime(startMinutes);

            // 获取配时方案号
            int planNo = planNumbers[i % planNumbers.length];

            Period period = new Period(startTime, planNo, ctrlMode);
            periodList.add(period);
        }

        dayPlan.setPeriodList(periodList);

        logger.info("生成平均分配时段日计划: crossId={}, dayPlanNo={}, name={}, periods={}",
                crossId, dayPlanNo, dayPlanName, periodCount);
        return dayPlan;
    }

    /**
     * 复制日计划
     *
     * @param source 源日计划
     * @param targetCrossId 目标路口编号
     * @param targetDayPlanNo 目标日计划号
     * @param targetDayPlanName 目标日计划名称
     * @return 复制的日计划参数
     */
    public static DayPlanParam copyDayPlan(DayPlanParam source, String targetCrossId, Integer targetDayPlanNo, String targetDayPlanName)
            throws ValidationException {

        if (source == null) {
            throw new ValidationException("source", "源日计划不能为空");
        }

        validateBasicParams(targetCrossId, targetDayPlanNo);

        DayPlanParam target = new DayPlanParam(targetCrossId, targetDayPlanNo, targetDayPlanName);

        // 复制时段列表
        if (source.getPeriodList() != null) {
            List<Period> copiedPeriods = new ArrayList<>();
            for (Period period : source.getPeriodList()) {
                Period copiedPeriod = new Period(period.getStartTime(), period.getPlanNo(), period.getCtrlMode());
                copiedPeriods.add(copiedPeriod);
            }
            target.setPeriodList(copiedPeriods);
        }

        logger.info("复制日计划: from {}:{} to {}:{}",
                source.getCrossId(), source.getDayPlanNo(), targetCrossId, targetDayPlanNo);
        return target;
    }

    /**
     * 验证基本参数
     */
    private static void validateBasicParams(String crossId, Integer dayPlanNo) throws ValidationException {
        if (crossId == null || crossId.trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (dayPlanNo == null || dayPlanNo < 1 || dayPlanNo > 999) {
            throw new ValidationException("dayPlanNo", "日计划号必须在1-999之间");
        }
    }

    /**
     * 验证特殊控制模式
     */
    private static boolean isValidSpecialCtrlMode(String ctrlMode) {
        return "11".equals(ctrlMode) || "12".equals(ctrlMode) || "13".equals(ctrlMode);
    }

    /**
     * 验证时段列表
     */
    private static void validatePeriods(List<Period> periods) throws ValidationException {
        if (periods == null || periods.isEmpty()) {
            throw new ValidationException("periods", "时段列表不能为空");
        }

        // 验证每个时段的有效性
        for (int i = 0; i < periods.size(); i++) {
            Period period = periods.get(i);
            if (!period.isValid()) {
                throw new ValidationException("period[" + i + "]", "时段信息无效: " + period);
            }
        }

        // 验证时段是否按时间升序排列
        for (int i = 0; i < periods.size() - 1; i++) {
            Period current = periods.get(i);
            Period next = periods.get(i + 1);

            if (Period.compareTime(current.getStartTime(), next.getStartTime()) >= 0) {
                throw new ValidationException("periodOrder",
                        String.format("时段必须按开始时间升序排列: %s >= %s",
                                current.getStartTime(), next.getStartTime()));
            }
        }

        // 验证第一个时段必须从00:00开始
        if (!periods.get(0).getStartTime().equals("00:00")) {
            throw new ValidationException("firstPeriod", "第一个时段必须从00:00开始");
        }
    }

    /**
     * 打印日计划信息
     */
    public static void printDayPlanInfo(DayPlanParam dayPlan) {
        if (dayPlan == null) {
            System.out.println("日计划为空");
            return;
        }

        System.out.println("=== 日计划信息 ===");
        System.out.printf("路口编号: %s%n", dayPlan.getCrossId());
        System.out.printf("日计划号: %d%n", dayPlan.getDayPlanNo());
        System.out.printf("日计划名称: %s%n", dayPlan.getDayPlanName());
        System.out.printf("时段数量: %d%n", dayPlan.getPeriodList() != null ? dayPlan.getPeriodList().size() : 0);

        if (dayPlan.getPeriodList() != null) {
            System.out.println("\n时段配置:");
            for (int i = 0; i < dayPlan.getPeriodList().size(); i++) {
                Period period = dayPlan.getPeriodList().get(i);
                String endTime = "24:00";
                if (i < dayPlan.getPeriodList().size() - 1) {
                    endTime = dayPlan.getPeriodList().get(i + 1).getStartTime();
                }

                System.out.printf("时段%d: %s-%s, 方案号%d, 控制方式%s(%s)%n",
                        i + 1, period.getStartTime(), endTime, period.getPlanNo(),
                        period.getCtrlMode(), Period.getCtrlModeDescription(period.getCtrlMode()));
            }
        }
        System.out.println("==================");
    }
}