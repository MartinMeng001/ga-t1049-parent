package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 路口等级枚举
 */
public enum CrossGrade {
    LEVEL_1("11", "一级交叉口", "主干路与主干路相交交叉口"),
    LEVEL_2("12", "二级交叉口", "主干路与次干路相交交叉口"),
    LEVEL_3("13", "三级交叉口", "主干路与支路相交交叉口"),
    LEVEL_4("21", "四级交叉口", "次干路与次干路相交交叉口"),
    LEVEL_5("22", "五级交叉口", "次干路与支路相交交叉口"),
    LEVEL_6("31", "六级交叉口", "支路与支路相交交叉口"),
    OTHER("99", "其他交叉口", "其他");

    private final String code;
    private final String name;
    private final String description;

    CrossGrade(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public static CrossGrade fromCode(String code) {
        for (CrossGrade grade : values()) {
            if (grade.code.equals(code)) {
                return grade;
            }
        }
        throw new IllegalArgumentException("Unknown cross grade code: " + code);
    }
}
