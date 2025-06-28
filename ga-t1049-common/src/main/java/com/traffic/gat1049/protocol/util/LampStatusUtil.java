package com.traffic.gat1049.protocol.util;

/**
 * 灯色状态工具类
 * 根据GA/T 1049.2标准表B.15灯色状态取值表
 */
public class LampStatusUtil {

    /**
     * 灯色状态枚举
     * 表B.15灯色状态取值表
     */
    public enum LampStatusValue {
        NO_LAMP(0, "无灯"),
        OFF(1, "灭灯"),
        ON(2, "亮灯"),
        FLASH(3, "闪灯");

        private final int code;
        private final String description;

        LampStatusValue(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static LampStatusValue fromCode(int code) {
            for (LampStatusValue value : values()) {
                if (value.code == code) {
                    return value;
                }
            }
            throw new IllegalArgumentException("未知的灯色状态代码: " + code);
        }
    }

    /**
     * 信号灯组类型枚举
     */
    public enum SignalGroupType {
        NORMAL(0, "普通信号灯组"),
        TRAM_61(61, "有轨电车专用信号-类型61"),
        TRAM_62(62, "有轨电车专用信号-类型62"),
        TRAM_63(63, "有轨电车专用信号-类型63");

        private final int type;
        private final String description;

        SignalGroupType(int type, String description) {
            this.type = type;
            this.description = description;
        }

        public int getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public boolean isTramType() {
            return type == 61 || type == 62 || type == 63;
        }
    }

    /**
     * 创建标准信号灯组状态（红、黄、绿）
     *
     * @param red 红灯状态
     * @param yellow 黄灯状态
     * @param green 绿灯状态
     * @return 3位字符的灯色状态
     */
    public static String createNormalLampStatus(LampStatusValue red, LampStatusValue yellow, LampStatusValue green) {
        return String.valueOf(red.getCode()) + yellow.getCode() + green.getCode();
    }

    /**
     * 创建有轨电车专用信号状态（禁止通行、过渡、通行）
     *
     * @param prohibit 禁止通行状态
     * @param transition 过渡状态
     * @param pass 通行状态
     * @return 3位字符的灯色状态
     */
    public static String createTramLampStatus(LampStatusValue prohibit, LampStatusValue transition, LampStatusValue pass) {
        return String.valueOf(prohibit.getCode()) + transition.getCode() + pass.getCode();
    }

    /**
     * 解析灯色状态
     *
     * @param lampStatus 3位字符的灯色状态
     * @param signalGroupType 信号灯组类型
     * @return 解析结果说明
     */
    public static String parseLampStatus(String lampStatus, SignalGroupType signalGroupType) {
        if (lampStatus == null || lampStatus.length() != 3) {
            throw new IllegalArgumentException("灯色状态必须是3位字符");
        }

        try {
            int first = Character.getNumericValue(lampStatus.charAt(0));
            int second = Character.getNumericValue(lampStatus.charAt(1));
            int third = Character.getNumericValue(lampStatus.charAt(2));

            LampStatusValue firstStatus = LampStatusValue.fromCode(first);
            LampStatusValue secondStatus = LampStatusValue.fromCode(second);
            LampStatusValue thirdStatus = LampStatusValue.fromCode(third);

            if (signalGroupType.isTramType()) {
                return String.format("有轨电车信号 - 禁止通行:%s, 过渡:%s, 通行:%s",
                        firstStatus.getDescription(),
                        secondStatus.getDescription(),
                        thirdStatus.getDescription());
            } else {
                return String.format("标准信号 - 红灯:%s, 黄灯:%s, 绿灯:%s",
                        firstStatus.getDescription(),
                        secondStatus.getDescription(),
                        thirdStatus.getDescription());
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的灯色状态格式: " + lampStatus, e);
        }
    }

    /**
     * 常用灯色状态常量
     */
    public static class CommonLampStatus {
        /** 红灯亮 */
        public static final String RED_ON = createNormalLampStatus(LampStatusValue.ON, LampStatusValue.OFF, LampStatusValue.OFF);

        /** 黄灯亮 */
        public static final String YELLOW_ON = createNormalLampStatus(LampStatusValue.OFF, LampStatusValue.ON, LampStatusValue.OFF);

        /** 绿灯亮 */
        public static final String GREEN_ON = createNormalLampStatus(LampStatusValue.OFF, LampStatusValue.OFF, LampStatusValue.ON);

        /** 黄灯闪烁 */
        public static final String YELLOW_FLASH = createNormalLampStatus(LampStatusValue.OFF, LampStatusValue.FLASH, LampStatusValue.OFF);

        /** 全灭 */
        public static final String ALL_OFF = createNormalLampStatus(LampStatusValue.OFF, LampStatusValue.OFF, LampStatusValue.OFF);

        /** 红黄同时亮（准备绿灯） */
        public static final String RED_YELLOW_ON = createNormalLampStatus(LampStatusValue.ON, LampStatusValue.ON, LampStatusValue.OFF);
    }

    /**
     * 验证灯色状态格式
     *
     * @param lampStatus 灯色状态字符串
     * @return 是否有效
     */
    public static boolean isValidLampStatus(String lampStatus) {
        if (lampStatus == null || lampStatus.length() != 3) {
            return false;
        }

        for (char c : lampStatus.toCharArray()) {
            try {
                int value = Character.getNumericValue(c);
                LampStatusValue.fromCode(value);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
}
