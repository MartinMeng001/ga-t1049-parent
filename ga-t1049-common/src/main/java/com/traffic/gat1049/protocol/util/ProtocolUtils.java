package com.traffic.gat1049.protocol.util;

import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.core.Operation;
import com.traffic.gat1049.protocol.model.core.MessageBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;

/**
 * 协议工具类 - 增量修改版本
 * 保留所有原有方法，新增多数据对象支持方法
 */
public final class ProtocolUtils {

    private static final AtomicLong sequenceCounter = new AtomicLong(0);
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(GatConstants.TimeFormat.DATETIME_PATTERN);

    private ProtocolUtils() {
        // 防止实例化
    }

    // ==================== 原有方法 - 保持不变 ====================

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
     * 获取消息体
     */
    public static MessageBody getMessageBody(Message message) {
        return message != null ? message.getBody() : null;
    }

    /**
     * 获取第一个操作
     */
    public static Operation getFirstOperation(Message message) {
        MessageBody body = getMessageBody(message);
        if (body != null && body.getOperations() != null && !body.getOperations().isEmpty()) {
            return body.getOperations().get(0);
        }
        return null;
    }

    /**
     * 获取操作名称
     */
    public static String getOperationName(Message message) {
        Operation operation = getFirstOperation(message);
        return operation != null ? operation.getName() : null;
    }

    /**
     * 获取操作数据 - 原有方法，向后兼容
     */
    public static Object getOperationData(Message message) {
        Operation operation = getFirstOperation(message);
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
     * 检查是否为登录操作
     */
    public static boolean isLoginOperation(Message message) {
        return GatConstants.Operation.LOGIN.equals(getOperationName(message));
    }

    /**
     * 检查是否为登出操作
     */
    public static boolean isLogoutOperation(Message message) {
        return GatConstants.Operation.LOGOUT.equals(getOperationName(message));
    }

    /**
     * 检查是否为订阅操作
     */
    public static boolean isSubscribeOperation(Message message) {
        return GatConstants.Operation.SUBSCRIBE.equals(getOperationName(message));
    }

    /**
     * 检查是否为取消订阅操作
     */
    public static boolean isUnsubscribeOperation(Message message) {
        return GatConstants.Operation.UNSUBSCRIBE.equals(getOperationName(message));
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

    // ==================== 新增方法 - 多数据对象支持 ====================

    /**
     * 获取操作的所有数据对象列表
     * 新增方法：如果Operation支持多数据对象，返回列表；否则返回包含单个对象的列表
     */
    public static List<Object> getOperationDataList(Message message) {
        Operation operation = getFirstOperation(message);
        if (operation == null) {
            return new ArrayList<>();
        }

        // 检查Operation是否有getDataList方法（多数据对象版本）
        try {
            @SuppressWarnings("unchecked")
            List<Object> dataList = (List<Object>) operation.getClass().getMethod("getDataList").invoke(operation);
            return dataList != null ? dataList : new ArrayList<>();
        } catch (Exception e) {
            // 如果没有getDataList方法，说明是单数据对象版本，包装成列表返回
            Object data = operation.getData();
            List<Object> result = new ArrayList<>();
            if (data != null) {
                result.add(data);
            }
            return result;
        }
    }

    /**
     * 获取指定操作的所有数据对象列表
     */
    public static List<Object> getOperationDataList(Message message, int operationIndex) {
        MessageBody body = getMessageBody(message);
        if (body != null && body.getOperations() != null &&
                operationIndex >= 0 && operationIndex < body.getOperations().size()) {

            Operation operation = body.getOperations().get(operationIndex);

            // 检查Operation是否有getDataList方法
            try {
                @SuppressWarnings("unchecked")
                List<Object> dataList = (List<Object>) operation.getClass().getMethod("getDataList").invoke(operation);
                return dataList != null ? dataList : new ArrayList<>();
            } catch (Exception e) {
                // 单数据对象版本
                Object data = operation.getData();
                List<Object> result = new ArrayList<>();
                if (data != null) {
                    result.add(data);
                }
                return result;
            }
        }
        return new ArrayList<>();
    }

    /**
     * 获取指定类型的数据对象列表
     */
    public static <T> List<T> getOperationDataByType(Message message, Class<T> clazz) {
        List<Object> allData = getOperationDataList(message);
        List<T> result = new ArrayList<>();

        for (Object data : allData) {
            if (clazz.isInstance(data)) {
                result.add(clazz.cast(data));
            }
        }

        return result;
    }

    /**
     * 获取第一个指定类型的数据对象
     */
    public static <T> T getFirstOperationDataByType(Message message, Class<T> clazz) {
        List<Object> allData = getOperationDataList(message);

        for (Object data : allData) {
            if (clazz.isInstance(data)) {
                return clazz.cast(data);
            }
        }

        return null;
    }

    /**
     * 获取所有操作的指定类型数据对象
     */
    public static <T> List<T> getAllOperationDataByType(Message message, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        MessageBody body = getMessageBody(message);

        if (body != null && body.getOperations() != null) {
            for (int i = 0; i < body.getOperations().size(); i++) {
                List<Object> operationData = getOperationDataList(message, i);
                for (Object data : operationData) {
                    if (clazz.isInstance(data)) {
                        result.add(clazz.cast(data));
                    }
                }
            }
        }

        return result;
    }

    /**
     * 检查操作是否包含指定类型的数据
     */
    public static boolean hasOperationDataType(Message message, Class<?> clazz) {
        List<Object> allData = getOperationDataList(message);

        for (Object data : allData) {
            if (clazz.isInstance(data)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取操作数据对象数量
     */
    public static int getOperationDataCount(Message message) {
        return getOperationDataList(message).size();
    }

    /**
     * 创建带有多个数据对象的操作（如果Operation支持）
     */
    public static Operation createOperationWithMultipleData(int order, String operationName, List<Object> dataList) {
        try {
            // 尝试使用多数据对象构造函数
            Class<?> operationClass = Operation.class;
            Operation operation = (Operation) operationClass.getConstructor(Integer.class, String.class, List.class)
                    .newInstance(order, operationName, dataList);
            return operation;
        } catch (Exception e) {
            // 如果不支持多数据对象，创建单数据对象并添加第一个数据
            Operation operation = new Operation(order, operationName, dataList.isEmpty() ? null : dataList.get(0));
            return operation;
        }
    }

    /**
     * 创建带有单个数据对象的操作（向后兼容）
     */
    public static Operation createOperationWithSingleData(int order, String operationName, Object data) {
        return new Operation(order, operationName, data);
    }

    /**
     * 向操作添加数据对象（如果Operation支持）
     */
    public static void addDataToOperation(Operation operation, Object data) {
        if (operation == null || data == null) {
            return;
        }

        try {
            // 尝试使用addData方法
            operation.getClass().getMethod("addData", Object.class).invoke(operation, data);
        } catch (Exception e) {
            // 如果不支持addData方法，说明是单数据对象版本，忽略此操作
            // 或者可以选择抛出异常提示不支持
        }
    }

    /**
     * 向操作添加多个数据对象
     */
    public static void addDataListToOperation(Operation operation, List<Object> dataList) {
        if (operation != null && dataList != null) {
            for (Object data : dataList) {
                addDataToOperation(operation, data);
            }
        }
    }

    /**
     * 打印操作的详细信息（调试用）
     */
    public static void printOperationDetails(Operation operation) {
        if (operation == null) {
            System.out.println("Operation is null");
            return;
        }

        System.out.println("=== Operation Details ===");
        System.out.println("Order: " + operation.getOrder());
        System.out.println("Name: " + operation.getName());

        // 尝试获取数据对象数量
        try {
            int count = (Integer) operation.getClass().getMethod("getDataCount").invoke(operation);
            System.out.println("Data Count: " + count);

            @SuppressWarnings("unchecked")
            List<Object> dataList = (List<Object>) operation.getClass().getMethod("getDataList").invoke(operation);
            for (int i = 0; i < dataList.size(); i++) {
                Object data = dataList.get(i);
                System.out.println("Data[" + i + "]: " +
                        (data != null ? data.getClass().getSimpleName() + " -> " + data.toString() : "null"));
            }
        } catch (Exception e) {
            // 单数据对象版本
            Object data = operation.getData();
            System.out.println("Data Count: " + (data != null ? 1 : 0));
            System.out.println("Data: " + (data != null ? data.getClass().getSimpleName() + " -> " + data.toString() : "null"));
        }

        System.out.println("=== End Operation Details ===");
    }

    /**
     * 打印消息的详细信息（调试用）
     */
    public static void printMessageDetails(Message message) {
        if (message == null) {
            System.out.println("Message is null");
            return;
        }

        System.out.println("=== Message Details ===");
        System.out.println("Version: " + message.getVersion());
        System.out.println("Token: " + message.getToken());
        System.out.println("Type: " + message.getType());
        System.out.println("Seq: " + message.getSeq());

        MessageBody body = message.getBody();
        if (body != null && body.getOperations() != null) {
            System.out.println("Operations Count: " + body.getOperations().size());
            for (int i = 0; i < body.getOperations().size(); i++) {
                System.out.println("--- Operation " + i + " ---");
                printOperationDetails(body.getOperations().get(i));
            }
        }
        System.out.println("=== End Message Details ===");
    }

    /**
     * 检查Operation是否支持多数据对象
     */
    public static boolean supportsMultipleData(Operation operation) {
        if (operation == null) {
            return false;
        }

        try {
            operation.getClass().getMethod("getDataList");
            operation.getClass().getMethod("addData", Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查Operation类是否支持多数据对象
     */
    public static boolean supportsMultipleData() {
        try {
            Operation.class.getMethod("getDataList");
            Operation.class.getMethod("addData", Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}