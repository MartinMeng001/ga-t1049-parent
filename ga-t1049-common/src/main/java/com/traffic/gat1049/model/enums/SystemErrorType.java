package com.traffic.gat1049.model.enums;

/**
 * 系统预定义错误类型枚举
 * 对应文档表A.5
 */
public enum SystemErrorType {
    SDE_VERSION("SDE_Version", "版本号错误"),
    SDE_TOKEN("SDE_Token", "无效令牌，会话错误"),
    SDE_ADDRESS("SDE_Address", "地址错误"),
    SDE_MSG_TYPE("SDE_MsgType", "数据包类型错误"),
    SDE_OPER_NAME("SDE_OperName", "操作命令错误"),
    SDE_USER_NAME("SDE_UserName", "用户名错误"),
    SDE_PWD("SDE_Pwd", "口令错误"),
    SDE_NOT_ALLOW("SDE_NotAllow", "操作不允许"),
    SDE_FAILURE("SDE_Failure", "操作失败"),
    SDE_UNKNOWN("SDE_Unknown", "其他未知错误");

    private final String code;
    private final String description;

    SystemErrorType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static SystemErrorType fromCode(String code) {
        for (SystemErrorType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return SDE_UNKNOWN;
    }
}
