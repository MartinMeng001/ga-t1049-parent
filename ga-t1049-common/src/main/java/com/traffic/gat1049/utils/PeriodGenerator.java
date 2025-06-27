package com.traffic.gat1049.utils;

import com.traffic.gat1049.protocol.model.signal.Period;
import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.model.enums.ControlMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 时段配置自动生成器
 * 根据给定的时段条数，生成一个标准的periodList
 */
public class PeriodGenerator {

    private static final Logger logger = LoggerFactory.getLogger(PeriodGenerator.class);

    // 默认控制方式
    private static final String DEFAULT_CTRL_MODE = "21"; // 单点定时控制
    private static final int MIN_PERIOD_COUNT = 1;
    private static final int MAX_PERIOD_COUNT = 12; // 一天最多12个时段
    private static final int MINUTES_PER_DAY = 24 * 60; // 一天的分钟数

    /**
     * 生成平均分配的时段列表
     *
     * @param periodCount 时段数量
     * @return 时段列表
     * @throws ValidationException 参数验证失败时抛出
     */
    public static List<Period> generateEvenPeriodList(int periodCount) throws ValidationException {
        return generatePeriodList(periodCount, generateDefaultPlanNumbers(periodCount), DEFAULT_CTRL_MODE);
    }

    /**
     * 生成指定方案号的时段列表
     *
     * @param periodCount 时段数量
     * @param planNumbers 配时方案号数组
     * @return 时段列表
     * @throws ValidationException 参数验证失败时抛出
     */
    public static List<Period> generatePeriodList(int periodCount, int[] planNumbers) throws ValidationException {
        return generatePeriodList(periodCount, planNumbers, DEFAULT_CTRL_MODE);
    }

    /**
     * 生成完全自定义的时段列表
     *
     * @param periodCount 时段数量
     * @param planNumbers 配时方案号数组
     * @param ctrlMode    控制方式
     * @return 时段列表
     * @throws ValidationException 参数验证失败时抛出
     */
    public static List<Period> generatePeriodList(int periodCount, int[] planNumbers, String ctrlMode)
            throws ValidationException {

        // 参数验证
        validateParameters(periodCount, planNumbers, ctrlMode);

        List<Period> periodList = new ArrayList<>();

        // 计算每个时段的时间间隔（分钟）
        int intervalMinutes = MINUTES_PER_DAY / periodCount;

        for (int i = 0; i < periodCount; i++) {
            // 计算开始时间
            int startMinutes = i * intervalMinutes;
            String startTime = minutesToTimeString(startMinutes);

            // 获取配时方案号
            int planNo = planNumbers[i % planNumbers.length];

            Period period = new Period(startTime, planNo, ctrlMode);
            periodList.add(period);
        }

        logger.info("生成时段列表完成: {}个时段, 时间间隔{}小时", periodCount, intervalMinutes / 60.0);

        return periodList;
    }

    /**
     * 生成标准工作日时段配置（常用模板）
     * 包含：早高峰、平峰、晚高峰、夜间等典型时段
     *
     * @return 工作日时段列表
     */
    public static List<Period> generateWorkdayPeriodList() {
        List<Period> periodList = new ArrayList<>();

        // 夜间低峰：00:00-06:00 (方案3-夜间方案)
        periodList.add(new Period("00:00:00", 3, "21"));

        // 早高峰：06:00-09:00 (方案1-高峰方案)
        periodList.add(new Period("06:00:00", 1, "21"));

        // 日间平峰：09:00-17:00 (方案2-平峰方案)
        periodList.add(new Period("09:00:00", 2, "21"));

        // 晚高峰：17:00-20:00 (方案1-高峰方案)
        periodList.add(new Period("17:00:00", 1, "21"));

        // 夜间过渡：20:00-00:00 (方案3-夜间方案)
        periodList.add(new Period("20:00:00", 3, "21"));

        logger.info("生成标准工作日时段配置: 5个时段");
        return periodList;
    }

    /**
     * 生成周末时段配置
     *
     * @return 周末时段列表
     */
    public static List<Period> generateWeekendPeriodList() {
        List<Period> periodList = new ArrayList<>();

        // 夜间：00:00-08:00 (方案3-夜间方案)
        periodList.add(new Period("00:00:00", 3, "21"));

        // 日间：08:00-22:00 (方案2-平峰方案)
        periodList.add(new Period("08:00:00", 2, "21"));

        // 夜间：22:00-00:00 (方案3-夜间方案)
        periodList.add(new Period("22:00:00", 3, "21"));

        logger.info("生成周末时段配置: 3个时段");
        return periodList;
    }

    /**
     * 生成感应控制时段配置
     *
     * @return 感应控制时段列表
     */
    public static List<Period> generateActuatedPeriodList() {
        List<Period> periodList = new ArrayList<>();

        // 夜间感应：00:00-06:00
        periodList.add(new Period("00:00:00", 1, "22")); // 22-单点感应控制

        // 白天感应：06:00-22:00
        periodList.add(new Period("06:00:00", 2, "22"));

        // 夜间感应：22:00-00:00
        periodList.add(new Period("22:00:00", 1, "22"));

        logger.info("生成感应控制时段配置: 3个时段");
        return periodList;
    }

    /**
     * 生成自适应控制时段配置
     *
     * @return 自适应控制时段列表
     */
    public static List<Period> generateAdaptivePeriodList() {
        List<Period> periodList = new ArrayList<>();

        // 全天自适应控制
        periodList.add(new Period("00:00:00", 1, "23")); // 23-单点自适应控制

        logger.info("生成自适应控制时段配置: 1个时段");
        return periodList;
    }

    /**
     * 生成权重分配时段列表
     * 根据不同时段的重要性分配不同的方案
     *
     * @param timeWeights 时段权重配置 {开始时间, 方案号, 权重}
     * @return 时段列表
     */
    public static List<Period> generateWeightedPeriodList(TimeWeight[] timeWeights) throws ValidationException {
        if (timeWeights == null || timeWeights.length == 0) {
            throw new ValidationException("timeWeights", "时段权重配置不能为空");
        }

        List<Period> periodList = new ArrayList<>();

        for (TimeWeight tw : timeWeights) {
            if (!Period.isValidTimeFormat(tw.startTime)) {
                throw new ValidationException("startTime", "时间格式不正确: " + tw.startTime);
            }

            String ctrlMode = DEFAULT_CTRL_MODE;
            // 根据权重决定控制方式
            if (tw.weight >= 0.8) {
                ctrlMode = "23"; // 高权重使用自适应控制
            } else if (tw.weight >= 0.5) {
                ctrlMode = "22"; // 中等权重使用感应控制
            }

            Period period = new Period(tw.startTime, tw.planNo, ctrlMode);
            periodList.add(period);
        }

        logger.info("生成权重分配时段列表: {}个时段", timeWeights.length);
        return periodList;
    }

    /**
     * 生成特殊事件时段配置（如节假日、大型活动等）
     *
     * @param eventType 事件类型
     * @return 特殊事件时段列表
     */
    public static List<Period> generateSpecialEventPeriodList(EventType eventType) {
        List<Period> periodList = new ArrayList<>();

        switch (eventType) {
            case FESTIVAL:
                // 节假日配置 - 延后启动，延长高峰
                periodList.add(new Period("00:00:00", 3, "21")); // 夜间
                periodList.add(new Period("08:00:00", 1, "21")); // 延后的早高峰
                periodList.add(new Period("11:00:00", 2, "21")); // 平峰
                periodList.add(new Period("18:00:00", 1, "21")); // 延长的晚高峰
                periodList.add(new Period("22:00:00", 3, "21")); // 夜间
                break;

            case LARGE_EVENT:
                // 大型活动 - 特殊应急配时
                periodList.add(new Period("00:00:00", 4, "53")); // 全天应急方案，指定方案控制
                break;

            case CONSTRUCTION:
                // 施工期间 - 手动控制为主
                periodList.add(new Period("00:00:00", 5, "51")); // 全天手动控制
                break;

            case EMERGENCY:
                // 紧急情况 - 黄闪或全红
                periodList.add(new Period("00:00:00", 0, "13")); // 全部黄闪
                break;

            default:
                return generateWorkdayPeriodList();
        }

        logger.info("生成{}特殊事件时段配置: {}个时段", eventType, periodList.size());
        return periodList;
    }

    /**
     * 参数验证
     */
    private static void validateParameters(int periodCount, int[] planNumbers, String ctrlMode)
            throws ValidationException {

        if (periodCount < MIN_PERIOD_COUNT || periodCount > MAX_PERIOD_COUNT) {
            throw new ValidationException("periodCount",
                    String.format("时段数量必须在%d-%d之间", MIN_PERIOD_COUNT, MAX_PERIOD_COUNT));
        }

        if (planNumbers == null || planNumbers.length == 0) {
            throw new ValidationException("planNumbers", "配时方案号数组不能为空");
        }

        for (int planNo : planNumbers) {
            if (planNo < 0) {
                throw new ValidationException("planNumbers", "配时方案号不能为负数");
            }
        }

        if (ctrlMode == null || ctrlMode.trim().isEmpty()) {
            throw new ValidationException("ctrlMode", "控制方式不能为空");
        }
    }

    /**
     * 生成默认的配时方案号序列
     */
    private static int[] generateDefaultPlanNumbers(int periodCount) {
        int[] planNumbers = new int[periodCount];
        for (int i = 0; i < periodCount; i++) {
            // 循环使用方案1-3
            planNumbers[i] = (i % 3) + 1;
        }
        return planNumbers;
    }

    /**
     * 将分钟数转换为时间字符串
     */
    private static String minutesToTimeString(int minutes) {
        int hours = (minutes / 60) % 24;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }

    /**
     * 工具方法：打印时段列表信息
     */
    public static void printPeriodList(List<Period> periodList) {
        System.out.println("=== 时段配置信息 ===");

        for (int i = 0; i < periodList.size(); i++) {
            Period period = periodList.get(i);
            String endTime = "24:00:00";
            if (i < periodList.size() - 1) {
                endTime = periodList.get(i + 1).getStartTime();
            }

            System.out.printf("时段%d: %s-%s, 方案号%d, 控制方式%s%n",
                    i + 1, period.getStartTime(), endTime,
                    period.getPlanNo(), period.getCtrlMode());
        }

        System.out.println("==================");
    }

    /**
     * 时段权重配置类
     */
    public static class TimeWeight {
        public String startTime;
        public int planNo;
        public double weight; // 权重值 0.0-1.0

        public TimeWeight(String startTime, int planNo, double weight) {
            this.startTime = startTime;
            this.planNo = planNo;
            this.weight = weight;
        }
    }

    /**
     * 事件类型枚举
     */
    public enum EventType {
        FESTIVAL,      // 节假日
        LARGE_EVENT,   // 大型活动
        CONSTRUCTION,  // 施工
        EMERGENCY      // 紧急情况
    }

    /**
     * 示例使用方法
     */
    public static void main(String[] args) {
        try {
            // 示例1: 生成4个时段的平均分配
            System.out.println("示例1: 4时段平均分配");
            List<Period> evenPeriods = generateEvenPeriodList(4);
            printPeriodList(evenPeriods);

            // 示例2: 生成标准工作日配置
            System.out.println("\n示例2: 标准工作日配置");
            List<Period> workdayPeriods = generateWorkdayPeriodList();
            printPeriodList(workdayPeriods);

            // 示例3: 生成感应控制配置
            System.out.println("\n示例3: 感应控制配置");
            List<Period> actuatedPeriods = generateActuatedPeriodList();
            printPeriodList(actuatedPeriods);

            // 示例4: 生成指定方案号的时段
            System.out.println("\n示例4: 指定方案号时段");
            int[] customPlans = {1, 2, 1, 3}; // 早高峰、平峰、晚高峰、夜间
            List<Period> customPeriods = generatePeriodList(4, customPlans);
            printPeriodList(customPeriods);

            // 示例5: 生成权重分配时段
            System.out.println("\n示例5: 权重分配时段");
            TimeWeight[] weights = {
                    new TimeWeight("06:00:00", 1, 0.9), // 早高峰-高权重
                    new TimeWeight("09:00:00", 2, 0.5), // 平峰-中权重
                    new TimeWeight("17:00:00", 1, 0.9), // 晚高峰-高权重
                    new TimeWeight("20:00:00", 3, 0.3)  // 夜间-低权重
            };
            List<Period> weightedPeriods = generateWeightedPeriodList(weights);
            printPeriodList(weightedPeriods);

        } catch (ValidationException e) {
            System.err.println("参数验证失败: " + e.getMessage());
        }
    }
}