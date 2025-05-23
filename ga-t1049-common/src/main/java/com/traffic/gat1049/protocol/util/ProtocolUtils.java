package com.traffic.gat1049.protocol.util;

import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.protocol.model.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 协议工具类
 */
public final class ProtocolUtils {

    private static final AtomicLong sequenceCounter = new AtomicLong(0);
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(GatConstants.TimeFormat.DATETIME_PATTERN);

    private ProtocolUtils() {
        // 防止实例化
    }

    /**
     * 生成序列号
     */
    public static String generateSequence() {
        long timestamp = System.currentTimeMillis() / 1000; // 秒级时间戳
        long counter = sequenceCounter.incrementAndGet() % 10000; // 4位计数器
        return String.format("%d%04d", timestamp, counter);
    }

    /**
     * 格式化时间
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 解析时间
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * 检查消息是否为请求类型
     */
    public static boolean isRequest(Message message) {
        return message != null && GatConstants.MessageType.REQUEST.equals(message.getType());
    }

    /**
     * 检查消息是否为响应类型
     */
    public static boolean isResponse(Message message) {
        return message != null && GatConstants.MessageType.RESPONSE.equals(message.getType());
    }

    /**
     * 检查消息是否为推送类型
     */
    public static boolean isPush(Message message) {
        return message != null && GatConstants.MessageType.PUSH.equals(message.getType());
    }

    /**
     * 检查消息是否为错误类型
     */
    public static boolean isError(Message message) {
        return message != null && GatConstants.MessageType.ERROR.equals(message.getType());
    }

    /**
     * 检查消息是否来自TICP
     */
    public static boolean isFromTicp(Message message) {
        return message != null && message.getFrom() != null &&
                GatConstants.SystemAddress.TICP.equals(message.getFrom().getSys());
    }

    /**
     * 检查消息是否来自TSC
     */
    public static boolean isFromTsc(Message message) {
        return message != null && message.getFrom() != null &&
                GatConstants.SystemAddress.TSC.equals(message.getFrom().getSys());
    }

    /**
     * 提取消息中的第一个操作
     */
    public static com.traffic.gat1049.protocol.model.Operation getFirstOperation(Message message) {
        if (message == null || message.getBody() == null ||
                message.getBody().getOperations() == null ||
                message.getBody().getOperations().isEmpty()) {
            return null;
        }
        return message.getBody().getOperations().get(0);
    }

    /**
     * 获取操作名称
     */
    public static String getOperationName(Message message) {
        com.traffic.gat1049.protocol.model.Operation operation = getFirstOperation(message);
        return operation != null ? operation.getName() : null;
    }

    /**
     * 获取操作数据
     */
    public static Object getOperationData(Message message) {
        com.traffic.gat1049.protocol.model.Operation operation = getFirstOperation(message);
        return operation != null ? operation.getData() : null;
    }

    /**
     * 检查是否为查询操作
     */
    public static boolean isGetOperation(Message message) {
        return GatConstants.Operation.GET.equals(getOperationName(message));
    }

    /**
     * 检查是否为设置操作
     */
    public static boolean isSetOperation(Message message) {
        return GatConstants.Operation.SET.equals(getOperationName(message));
    }

    /**
     * 检查是否为通知操作
     */
    public static boolean isNotifyOperation(Message message) {
        return GatConstants.Operation.NOTIFY.equals(getOperationName(message));
    }

    /**
     * 验证路口编号格式
     */
    public static boolean isValidCrossId(String crossId) {
        if (crossId == null || crossId.trim().isEmpty()) {
            return false;
        }
        // 路口编号：区域编号+5位数字，总长度为14位
        return crossId.matches("\\d{14}");
    }

    /**
     * 验证信号机编号格式
     */
    public static boolean isValidSignalControllerId(String signalControllerId) {
        if (signalControllerId == null || signalControllerId.trim().isEmpty()) {
            return false;
        }
        return signalControllerId.matches(GatConstants.IdFormat.SIGNAL_CONTROLLER_ID_PATTERN);
    }

    /**
     * 验证区域编号格式
     */
    public static boolean isValidRegionId(String regionId) {
        if (regionId == null || regionId.trim().isEmpty()) {
            return false;
        }
        return regionId.matches(GatConstants.IdFormat.REGION_ID_PATTERN);
    }

    /**
     * 验证子区编号格式
     */
    public static boolean isValidSubRegionId(String subRegionId) {
        if (subRegionId == null || subRegionId.trim().isEmpty()) {
            return false;
        }
        return subRegionId.matches(GatConstants.IdFormat.SUB_REGION_ID_PATTERN);
    }

    /**
     * 清理XML内容
     */
    public static String cleanXmlContent(String xmlContent) {
        if (xmlContent == null) {
            return null;
        }

        // 移除BOM字符
        if (xmlContent.startsWith("\uFEFF")) {
            xmlContent = xmlContent.substring(1);
        }

        // 移除多余的空白字符
        return xmlContent.trim();
    }

    /**
     * 检查XML内容是否有效
     */
    public static boolean isValidXmlContent(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            return false;
        }

        String cleaned = cleanXmlContent(xmlContent);
        return cleaned.startsWith("<?xml") && cleaned.contains("<Message>");
    }
}
