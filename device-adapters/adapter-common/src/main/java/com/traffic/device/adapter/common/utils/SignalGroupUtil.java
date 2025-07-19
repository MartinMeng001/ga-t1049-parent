package com.traffic.device.adapter.common.utils;

import java.util.Set;
import java.util.stream.Collectors;

public class SignalGroupUtil {
    private static final Set<Integer> mainSignalGroup = Set.of(1,2,9,10,17,18,25,26);
    private static final Set<Integer> NorthSouthStraight = Set.of(2,18);
    private static final Set<Integer> NorthSouthLeft = Set.of(1,17);
    private static final Set<Integer> EastWestStraight = Set.of(10,26);
    private static final Set<Integer> EastWestLeft = Set.of(9,25);
    private static final Set<Integer> NorthAll = Set.of(1,2);
    private static final Set<Integer> EastAll = Set.of(9,10);
    private static final Set<Integer> WestAll = Set.of(25,26);
    private static final Set<Integer> SouthAll = Set.of(17,18);
    private static final Set<Integer> NorthSouthAll = Set.of(1,2,17,18);
    private static final Set<Integer> EastWestAll = Set.of(9,10,25,26);

    private static final Set<Integer> rightSignalGroup = Set.of(3,11,19,27);
    private static final Set<Integer> NorthRight = Set.of(3);
    private static final Set<Integer> EastRight = Set.of(11);
    private static final Set<Integer> WestRight = Set.of(27);
    private static final Set<Integer> SouthRight = Set.of(19);
    private static final Set<Integer> NorthSouthRight = Set.of(3,19);
    private static final Set<Integer> EastWestRight = Set.of(11,27);

    private static final Set<Integer> nonMotorSignalGroup = Set.of(4,5,12,13,20,21,28,29);
    private static final Set<Integer> NorthNonMotor = Set.of(4);
    private static final Set<Integer> EastNonMotor = Set.of(12);
    private static final Set<Integer> WestNonMotor = Set.of(28);
    private static final Set<Integer> SouthNonMotor = Set.of(20);
    private static final Set<Integer> NorthNonMotorLeft = Set.of(5);
    private static final Set<Integer> EastNonMotorLeft = Set.of(13);
    private static final Set<Integer> WestNonMotorLeft = Set.of(29);
    private static final Set<Integer> SouthNonMotorLeft = Set.of(21);

    private static final Set<Integer> pedestrianSignalGroup = Set.of(7,8,15,16,23,24,31,32);
    private static final Set<Integer> NorthPedestrianIn = Set.of(7);
    private static final Set<Integer> EastPedestrianIn = Set.of(15);
    private static final Set<Integer> WestPedestrianIn = Set.of(31);
    private static final Set<Integer> SouthPedestrianIn = Set.of(23);
    private static final Set<Integer> NorthPedestrianOut = Set.of(8);
    private static final Set<Integer> EastPedestrianOut = Set.of(16);
    private static final Set<Integer> WestPedestrianOut = Set.of(32);
    private static final Set<Integer> SouthPedestrianOut = Set.of(24);
    private static final Set<Integer> NorthPedestrian = Set.of(7,8);
    private static final Set<Integer> EastPedestrian = Set.of(15,16);
    private static final Set<Integer> SouthPedestrian = Set.of(23,24);
    private static final Set<Integer> WestPedestrian = Set.of(31,32);
    private static final Set<Integer> NorthSouthPedestrian = Set.of(7,8,23,24);
    private static final Set<Integer> EastWestPedestrian = Set.of(15,16,31,32);

    /**
     * 判断传入的信号组是否属于主信号组，如果属于则返回主信号组号，否则返回-1
     *
     * @param signalGroup 待判断的信号组
     * @return 主信号组号，如果不属于主信号组则返回-1
     */
    public static int isMainSignalGroup(final Set<Integer> signalGroup) {
        if (signalGroup == null || signalGroup.isEmpty()) {
            return -1;
        }

        // 先调用findMainSignalGroup过滤出主信号组元素
        Set<Integer> mainElements = findMainSignalGroup(signalGroup);

        // 如果过滤后的元素为空，说明没有主信号组元素
        if (mainElements.isEmpty()) {
            return -1;
        }

        // 将过滤后的主信号组元素传入findMainSignalGroupNo判断组号
        return findMainSignalGroupNo(mainElements);
    }
    /*
    ** 查找主信号组，主信号组包含
    * 1-北左，2-北直，17-南左，18-南直，9-东左，10-东直，25-西左，26-西直
     */
    public static Set<Integer> findMainSignalGroup(final Set<Integer> signalGroup) {
        Set<Integer> result = signalGroup.stream()
                .filter(mainSignalGroup::contains)
                .collect(Collectors.toSet());
        return result;
    }
    /*
    ** 判断是否属于已定义的主信号组
     */
    public static int findMainSignalGroupNo(final Set<Integer> signalGroup) {
        int size = signalGroup.size();
        if(size!=2 && size!=4) return -1;   // 未知的信号组
        if(size==4){
            if(signalGroup.equals(NorthSouthAll)) return 88;
            if(signalGroup.equals(EastWestAll)) return 89;
        }else{
            if(signalGroup.equals(NorthSouthStraight)) return 80;
            if(signalGroup.equals(NorthSouthLeft)) return 81;
            if(signalGroup.equals(EastWestStraight)) return 82;
            if(signalGroup.equals(EastWestLeft)) return 83;
            if(signalGroup.equals(NorthAll)) return 84;
            if(signalGroup.equals(SouthAll)) return 85;
            if(signalGroup.equals(EastAll)) return 86;
            if(signalGroup.equals(WestAll)) return 87;
        }
        return -1;
    }
    /**
     * 判断传入的信号组是否属于右转信号组，如果属于则返回右转信号组号，否则返回-1
     *
     * @param signalGroup 待判断的信号组
     * @return 右转信号组号，如果不属于右转信号组则返回-1
     */
    public static int isRightSignalGroup(final Set<Integer> signalGroup) {
        if (signalGroup == null || signalGroup.isEmpty()) {
            return -1;
        }

        // 先调用findRightSignalGroup过滤出右转信号组元素
        Set<Integer> rightElements = findRightSignalGroup(signalGroup);

        // 如果过滤后的元素为空，说明没有右转信号组元素
        if (rightElements.isEmpty()) {
            return -1;
        }

        // 将过滤后的右转信号组元素传入findRightSignalGroupNo判断组号
        return findRightSignalGroupNo(rightElements);
    }
    /*
     ** 查找右转信号组，右转信号组包含
     * 3-北右，11-东右，19-南右，27-西右
     */
    public static Set<Integer> findRightSignalGroup(final Set<Integer> signalGroup) {
        Set<Integer> result = signalGroup.stream()
                .filter(rightSignalGroup::contains)
                .collect(Collectors.toSet());
        return result;
    }
    /*
     ** 判断是否属于已定义的右转信号组
     */
    public static int findRightSignalGroupNo(final Set<Integer> signalGroup) {
        int size = signalGroup.size();
        if(size!=2 && size!=4 && size!=1) return -1;   // 未知的信号组
        if(size==4){
            if(signalGroup.equals(rightSignalGroup)) return 107;
        }else if(size==2) {
            if (signalGroup.equals(NorthSouthRight)) return 105;
            if (signalGroup.equals(EastWestRight)) return 106;
        }else{
            if(signalGroup.equals(NorthRight)) return 101;
            if(signalGroup.equals(EastRight)) return 102;
            if(signalGroup.equals(WestRight)) return 104;
            if(signalGroup.equals(SouthRight)) return 103;
        }
        return -1;
    }

    /**
     * 判断传入的信号组是否属于行人信号组，如果属于则返回行人信号组号，否则返回-1
     *
     * @param signalGroup 待判断的信号组
     * @return 行人信号组号，如果不属于行人信号组则返回-1
     */
    public static int isPedestrianSignalGroup(final Set<Integer> signalGroup) {
        if (signalGroup == null || signalGroup.isEmpty()) {
            return -1;
        }

        // 先调用findPedestrianSignalGroup过滤出行人信号组元素
        Set<Integer> pedestrianElements = findPedestrianSignalGroup(signalGroup);

        // 如果过滤后的元素为空，说明没有行人信号组元素
        if (pedestrianElements.isEmpty()) {
            return -1;
        }

        // 将过滤后的行人信号组元素传入findPedestrianSignalGroupNo判断组号
        return findPedestrianSignalGroupNo(pedestrianElements);
    }
    /*
     ** 查找行人信号组，行人信号组包含
     * 7,8-北，15,16-东，23,24-南，31,32-西
     */
    public static Set<Integer> findPedestrianSignalGroup(final Set<Integer> signalGroup) {
        Set<Integer> result = signalGroup.stream()
                .filter(pedestrianSignalGroup::contains)
                .collect(Collectors.toSet());
        return result;
    }
    /*
     ** 判断是否属于已定义的行人信号组
     */
    public static int findPedestrianSignalGroupNo(final Set<Integer> signalGroup) {
        int size = signalGroup.size();
        if(size!=2 && size!=4 && size!=1) return -1;   // 未知的信号组
        if(size==4){
            if(signalGroup.equals(NorthSouthPedestrian)) return 143;
            if(signalGroup.equals(EastWestPedestrian)) return 144;
        }else if(size==2) {
            if (signalGroup.equals(NorthPedestrian)) return 139;
            if (signalGroup.equals(SouthPedestrian)) return 141;
            if (signalGroup.equals(EastPedestrian)) return 140;
            if (signalGroup.equals(WestPedestrian)) return 142;
            if(EastWestPedestrian.containsAll(signalGroup)) return 144;    // 处理行人灯组没有全部使用的情况
            if(NorthSouthPedestrian.containsAll(signalGroup)) return 143;
        }else{
            if(signalGroup.equals(NorthPedestrianIn)) return 131;
            if(signalGroup.equals(EastPedestrianIn)) return 133;
            if(signalGroup.equals(WestPedestrianIn)) return 137;
            if(signalGroup.equals(SouthPedestrianIn)) return 135;
            if(signalGroup.equals(NorthPedestrianOut)) return 132;
            if(signalGroup.equals(EastPedestrianOut)) return 134;
            if(signalGroup.equals(WestPedestrianOut)) return 138;
            if(signalGroup.equals(SouthPedestrianOut)) return 136;
        }
        return -1;
    }
}
