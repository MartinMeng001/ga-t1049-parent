package com.traffic.gat1049.utils;

import com.traffic.gat1049.protocol.model.signal.StageTiming;
import com.traffic.gat1049.protocol.model.signal.Adjust;
import com.traffic.gat1049.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 阶段配时自动生成器
 * 根据给定的周期时长和阶段数，生成平均分配时间的阶段配时列表
 */
public class StageTimingGenerator {

    private static final Logger logger = LoggerFactory.getLogger(StageTimingGenerator.class);

    // 默认配时参数
    private static final int DEFAULT_YELLOW_TIME = 3;  // 默认黄灯时间
    private static final int DEFAULT_ALL_RED_TIME = 2; // 默认全红时间
    private static final int MIN_GREEN_TIME = 5;       // 最小绿灯时间
    private static final int MIN_CYCLE_TIME = 30;      // 最小周期时间
    private static final int MAX_CYCLE_TIME = 300;     // 最大周期时间
    private static final int MAX_STAGE_COUNT = 8;      // 最大阶段数

    /**
     * 生成平均分配时间的阶段配时列表
     *
     * @param cycleLength 周期时长（秒）
     * @param stageCount  阶段数
     * @return 阶段配时列表
     * @throws ValidationException 参数验证失败时抛出
     */
    public static List<StageTiming> generateEvenStageTimingList(int cycleLength, int stageCount)
            throws ValidationException {
        return generateStageTimingList(cycleLength, stageCount, DEFAULT_YELLOW_TIME, DEFAULT_ALL_RED_TIME);
    }

    /**
     * 生成平均分配时间的阶段配时列表（可自定义黄灯和全红时间）
     *
     * @param cycleLength 周期时长（秒）
     * @param stageCount  阶段数
     * @param yellowTime  黄灯时间（秒）
     * @param allRedTime  全红时间（秒）
     * @return 阶段配时列表
     * @throws ValidationException 参数验证失败时抛出
     */
    public static List<StageTiming> generateStageTimingList(int cycleLength, int stageCount,
                                                            int yellowTime, int allRedTime)
            throws ValidationException {

        // 参数验证
        validateParameters(cycleLength, stageCount, yellowTime, allRedTime);

        // 计算每个阶段的固定时间（黄灯 + 全红）
        int fixedTimePerStage = yellowTime + allRedTime;
        int totalFixedTime = fixedTimePerStage * stageCount;

        // 计算可用于绿灯的总时间
        int totalGreenTime = cycleLength - totalFixedTime;

        // 验证绿灯时间是否足够
        if (totalGreenTime < MIN_GREEN_TIME * stageCount) {
            throw new ValidationException("cycleLength",
                    String.format("周期时长不足，无法为%d个阶段分配足够的绿灯时间。最小需要%d秒",
                            stageCount, totalFixedTime + MIN_GREEN_TIME * stageCount));
        }

        // 计算平均绿灯时间
        int baseGreenTime = totalGreenTime / stageCount;
        int remainingTime = totalGreenTime % stageCount;

        List<StageTiming> stageTimingList = new ArrayList<>();

        for (int i = 0; i < stageCount; i++) {
            int stageNo = i + 1;

            // 前面的阶段分配余数时间
            int greenTime = baseGreenTime + (i < remainingTime ? 1 : 0);

            StageTiming stageTiming = createStageTiming(stageNo, greenTime, yellowTime, allRedTime);
            stageTimingList.add(stageTiming);
        }

        logger.info("生成阶段配时列表完成: 周期{}秒, {}个阶段, 平均绿灯时间{}秒",
                cycleLength, stageCount, baseGreenTime);

        return stageTimingList;
    }

    /**
     * 生成带有感应控制参数的阶段配时列表
     *
     * @param cycleLength     周期时长（秒）
     * @param stageCount      阶段数
     * @param enableAdaptive  是否启用感应控制
     * @return 阶段配时列表
     * @throws ValidationException 参数验证失败时抛出
     */
    public static List<StageTiming> generateAdaptiveStageTimingList(int cycleLength, int stageCount,
                                                                    boolean enableAdaptive)
            throws ValidationException {

        List<StageTiming> stageTimingList = generateEvenStageTimingList(cycleLength, stageCount);

        if (enableAdaptive) {
            // 为每个阶段添加感应控制参数
            for (StageTiming stageTiming : stageTimingList) {
                int greenTime = stageTiming.getGreen();

                // 设置最小绿灯时间（绿灯时间的60%）
                int minGreen = Math.max(MIN_GREEN_TIME, (int)(greenTime * 0.6));
                stageTiming.setMinGreen(minGreen);

                // 设置最大绿灯时间（绿灯时间的150%）
                int maxGreen = (int)(greenTime * 1.5);
                stageTiming.setMaxGreen(maxGreen);

                logger.debug("阶段{}: 绿灯{}秒, 最小绿灯{}秒, 最大绿灯{}秒",
                        stageTiming.getStageNo(), greenTime, minGreen, maxGreen);
            }
        }

        return stageTimingList;
    }

    /**
     * 生成特定权重分配的阶段配时列表
     *
     * @param cycleLength 周期时长（秒）
     * @param weights     各阶段权重数组（权重越大，绿灯时间越长）
     * @return 阶段配时列表
     * @throws ValidationException 参数验证失败时抛出
     */
    public static List<StageTiming> generateWeightedStageTimingList(int cycleLength, int[] weights)
            throws ValidationException {

        if (weights == null || weights.length == 0) {
            throw new ValidationException("weights", "权重数组不能为空");
        }

        int stageCount = weights.length;
        validateParameters(cycleLength, stageCount, DEFAULT_YELLOW_TIME, DEFAULT_ALL_RED_TIME);

        // 计算权重总和
        int totalWeight = 0;
        for (int weight : weights) {
            if (weight <= 0) {
                throw new ValidationException("weights", "权重值必须大于0");
            }
            totalWeight += weight;
        }

        // 计算可用于绿灯的总时间
        int fixedTimePerStage = DEFAULT_YELLOW_TIME + DEFAULT_ALL_RED_TIME;
        int totalGreenTime = cycleLength - fixedTimePerStage * stageCount;

        List<StageTiming> stageTimingList = new ArrayList<>();
        int assignedGreenTime = 0;

        for (int i = 0; i < stageCount; i++) {
            int stageNo = i + 1;

            // 按权重分配绿灯时间
            int greenTime;
            if (i == stageCount - 1) {
                // 最后一个阶段分配剩余时间
                greenTime = totalGreenTime - assignedGreenTime;
            } else {
                greenTime = (totalGreenTime * weights[i]) / totalWeight;
                assignedGreenTime += greenTime;
            }

            // 确保最小绿灯时间
            greenTime = Math.max(greenTime, MIN_GREEN_TIME);

            StageTiming stageTiming = createStageTiming(stageNo, greenTime,
                    DEFAULT_YELLOW_TIME, DEFAULT_ALL_RED_TIME);
            stageTimingList.add(stageTiming);
        }

        logger.info("生成权重分配阶段配时列表完成: 周期{}秒, {}个阶段", cycleLength, stageCount);

        return stageTimingList;
    }

    /**
     * 创建单个阶段配时对象
     */
    private static StageTiming createStageTiming(int stageNo, int greenTime, int yellowTime, int allRedTime) {
        StageTiming stageTiming = new StageTiming();
        stageTiming.setStageNo(stageNo);
        stageTiming.setGreen(greenTime);
        stageTiming.setYellow(yellowTime);
        stageTiming.setAllRed(allRedTime);
        stageTiming.setAdjustList(new ArrayList<>()); // 初始化空的调整列表

        return stageTiming;
    }

    /**
     * 参数验证
     */
    private static void validateParameters(int cycleLength, int stageCount, int yellowTime, int allRedTime)
            throws ValidationException {

        if (cycleLength < MIN_CYCLE_TIME || cycleLength > MAX_CYCLE_TIME) {
            throw new ValidationException("cycleLength",
                    String.format("周期时长必须在%d-%d秒之间", MIN_CYCLE_TIME, MAX_CYCLE_TIME));
        }

        if (stageCount <= 0 || stageCount > MAX_STAGE_COUNT) {
            throw new ValidationException("stageCount",
                    String.format("阶段数必须在1-%d之间", MAX_STAGE_COUNT));
        }

        if (yellowTime <= 0) {
            throw new ValidationException("yellowTime", "黄灯时间必须大于0");
        }

        if (allRedTime < 0) {
            throw new ValidationException("allRedTime", "全红时间不能为负数");
        }
    }

    /**
     * 工具方法：打印阶段配时信息
     */
    public static void printStageTimingList(List<StageTiming> stageTimingList) {
        System.out.println("=== 阶段配时信息 ===");
        int totalTime = 0;

        for (StageTiming timing : stageTimingList) {
            int stageTotal = timing.getGreen() + timing.getYellow() + timing.getAllRed();
            totalTime += stageTotal;

            System.out.printf("阶段%d: 绿灯%d秒, 黄灯%d秒, 全红%d秒, 小计%d秒%n",
                    timing.getStageNo(), timing.getGreen(), timing.getYellow(),
                    timing.getAllRed(), stageTotal);

            if (timing.getMaxGreen() != null || timing.getMinGreen() != null) {
                System.out.printf("       感应参数: 最小绿灯%d秒, 最大绿灯%d秒%n",
                        timing.getMinGreen(), timing.getMaxGreen());
            }
        }

        System.out.printf("总周期时长: %d秒%n", totalTime);
        System.out.println("==================");
    }

    /**
     * 示例使用方法
     */
    public static void main(String[] args) {
        try {
            // 示例1: 生成4阶段120秒的平均配时
            System.out.println("示例1: 4阶段120秒平均配时");
            List<StageTiming> evenStages = generateEvenStageTimingList(120, 4);
            printStageTimingList(evenStages);

            // 示例2: 生成带感应控制的配时
            System.out.println("\n示例2: 带感应控制的配时");
            List<StageTiming> adaptiveStages = generateAdaptiveStageTimingList(140, 3, true);
            printStageTimingList(adaptiveStages);

            // 示例3: 权重分配配时（主路:次路 = 3:1）
            System.out.println("\n示例3: 权重分配配时");
            int[] weights = {3, 1, 3, 1}; // 主路权重3，次路权重1
            List<StageTiming> weightedStages = generateWeightedStageTimingList(120, weights);
            printStageTimingList(weightedStages);

        } catch (ValidationException e) {
            System.err.println("参数验证失败: " + e.getMessage());
        }
    }
}