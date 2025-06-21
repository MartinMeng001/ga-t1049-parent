package com.traffic.gat1049.protocol.util;

import com.traffic.gat1049.protocol.model.core.Operation;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.core.MessageBody;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.protocol.constants.GatConstants;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 统一处理单个对象和ArrayList结果的工具类
 * 基于ResponseBuilder的设计理念，使用reverseAddress处理地址
 */
public class ResultHandlingUtils {

    /**
     * 推荐的统一处理方法
     * 使用ResponseBuilder的reverseAddress机制，最合理的实现方式
     */
    public static Message createSuccessResponse(Message request, Object result) {
        // 使用MessageBuilder的ResponseBuilder，自动处理地址反转
        return MessageBuilder.responseFor(request)
                .success(processResult(request, result))
                .build();
    }

    /**
     * 处理结果数据，智能转换为合适的格式
     */
    private static Object processResult(Message request, Object result) {
        // 获取请求操作信息
        Operation requestOperation = ProtocolUtils.getFirstOperation(request);
        if (requestOperation == null) {
            return result; // 如果无法获取请求操作，直接返回原结果
        }

        // 根据Operation版本和结果类型智能处理
        return convertResultForOperation(result);
    }

    /**
     * 根据Operation版本智能转换结果
     */
    private static Object convertResultForOperation(Object result) {
        if (result == null) {
            return null;
        }

        // 检查Operation是否支持多数据对象
        if (ProtocolUtils.supportsMultipleData()) {
            // 多数据对象版本：使用包装器对象
            return new MultiDataWrapper(result);
        } else {
            // 单数据对象版本：按原来的方式处理
            return convertResultForSingleData(result);
        }
    }

    /**
     * 为单数据对象版本转换结果
     */
    private static Object convertResultForSingleData(Object result) {
        if (result instanceof Collection) {
            Collection<?> collection = (Collection<?>) result;
            if (collection.isEmpty()) {
                return null;
            } else if (collection.size() == 1) {
                return collection.iterator().next();
            } else {
                // 多个元素保持为列表
                return new ArrayList<>(collection);
            }
        } else if (result.getClass().isArray()) {
            Object[] array = (Object[]) result;
            if (array.length == 0) {
                return null;
            } else if (array.length == 1) {
                return array[0];
            } else {
                // 转换为列表
                List<Object> list = new ArrayList<>();
                for (Object item : array) {
                    if (item != null) {
                        list.add(item);
                    }
                }
                return list;
            }
        }
        return result;
    }

    /**
     * 使用自定义操作名称创建响应
     */
    public static Message createSuccessResponse(Message request, String operationName, Object result) {
        return MessageBuilder.responseFor(request)
                .success(operationName, processResult(request, result))
                .build();
    }

    /**
     * 创建错误响应（也使用reverseAddress）
     */
    public static Message createErrorResponse(Message request, String errorCode, String errorMessage) {
        return MessageBuilder.responseFor(request)
                .error(errorCode, errorMessage)
                .build();
    }

    /**
     * 创建错误响应（基于异常）
     */
    public static Message createErrorResponse(Message request, Exception exception) {
        String errorCode = "PROCESSING_ERROR";
        String errorMessage = exception.getMessage();

        // 可以根据异常类型确定更具体的错误代码
        if (exception instanceof ValidationException) {
            errorCode = "VALIDATION_ERROR";
        } else if (exception instanceof BusinessException) {
            errorCode = "BUSINESS_ERROR";
        } else if (exception instanceof SecurityException) {
            errorCode = "SECURITY_ERROR";
        }

        return createErrorResponse(request, errorCode, errorMessage);
    }

    /**
     * 强制使用多数据对象方式处理
     */
    public static Message createMultiDataResponse(Message request, Object result) {
        // 直接使用ResponseBuilder，但操作数据使用多数据包装器
        return MessageBuilder.responseFor(request)
                .success(new MultiDataWrapper(result))
                .build();
    }

    /**
     * 强制使用单数据对象方式处理
     */
    public static Message createSingleDataResponse(Message request, Object result) {
        return MessageBuilder.responseFor(request)
                .success(convertResultForSingleData(result))
                .build();
    }

    /**
     * 批量操作响应（明确表示这是批量结果）
     */
    public static Message createBatchResponse(Message request, Collection<?> results) {
        return MessageBuilder.responseFor(request)
                .success(new MultiDataWrapper(results))
                .build();
    }

    /**
     * 多数据包装器 - 用于多数据对象Operation
     * 这个类的实例会被识别并特殊处理
     */
    public static class MultiDataWrapper {
        private final Object wrappedResult;

        public MultiDataWrapper(Object result) {
            this.wrappedResult = result;
        }

        public Object getWrappedResult() {
            return wrappedResult;
        }

        /**
         * 获取标准化的数据列表
         */
        public List<Object> getDataList() {
            List<Object> dataList = new ArrayList<>();

            if (wrappedResult == null) {
                return dataList;
            }

            if (wrappedResult instanceof Collection) {
                Collection<?> collection = (Collection<?>) wrappedResult;
                for (Object item : collection) {
                    if (item != null) {
                        dataList.add(item);
                    }
                }
            } else if (wrappedResult.getClass().isArray()) {
                Object[] array = (Object[]) wrappedResult;
                for (Object item : array) {
                    if (item != null) {
                        dataList.add(item);
                    }
                }
            } else {
                dataList.add(wrappedResult);
            }

            return dataList;
        }
    }

    /**
     * 检查对象是否为多数据包装器
     */
    public static boolean isMultiDataWrapper(Object obj) {
        return obj instanceof MultiDataWrapper;
    }

    /**
     * 从包装器中提取数据列表
     */
    public static List<Object> extractDataList(Object obj) {
        if (obj instanceof MultiDataWrapper) {
            return ((MultiDataWrapper) obj).getDataList();
        } else {
            // 不是包装器，按原来的方式处理
            List<Object> list = new ArrayList<>();
            if (obj != null) {
                list.add(obj);
            }
            return list;
        }
    }

    // ==================== 高级处理方法 ====================

    /**
     * 智能响应构建器 - 提供更多控制选项
     */
    public static SmartResponseBuilder smartResponseFor(Message request) {
        return new SmartResponseBuilder(request);
    }

    /**
     * 智能响应构建器类
     */
    public static class SmartResponseBuilder {
        private final Message request;
        private final MessageBuilder.ResponseBuilder responseBuilder;

        public SmartResponseBuilder(Message request) {
            this.request = request;
            this.responseBuilder = MessageBuilder.responseFor(request);
        }

        /**
         * 自动处理结果（推荐方式）
         */
        public SmartResponseBuilder withResult(Object result) {
            Object processedResult = processResult(request, result);
            responseBuilder.success(processedResult);
            return this;
        }

        /**
         * 指定操作名称并处理结果
         */
        public SmartResponseBuilder withResult(String operationName, Object result) {
            Object processedResult = processResult(request, result);
            responseBuilder.success(operationName, processedResult);
            return this;
        }

        /**
         * 强制使用单数据模式
         */
        public SmartResponseBuilder withSingleData(Object result) {
            Object processedResult = convertResultForSingleData(result);
            responseBuilder.success(processedResult);
            return this;
        }

        /**
         * 强制使用多数据模式
         */
        public SmartResponseBuilder withMultiData(Object result) {
            MultiDataWrapper wrapper = new MultiDataWrapper(result);
            responseBuilder.success(wrapper);
            return this;
        }

        /**
         * 添加错误信息
         */
        public SmartResponseBuilder withError(String errorCode, String errorMessage) {
            responseBuilder.error(errorCode, errorMessage);
            return this;
        }

        /**
         * 基于异常添加错误信息
         */
        public SmartResponseBuilder withError(Exception exception) {
            String errorCode = determineErrorCode(exception);
            responseBuilder.error(errorCode, exception.getMessage());
            return this;
        }

        /**
         * 构建最终消息
         */
        public Message build() {
            return responseBuilder.build();
        }

        private String determineErrorCode(Exception exception) {
            if (exception instanceof ValidationException) {
                return "VALIDATION_ERROR";
            } else if (exception instanceof BusinessException) {
                return "BUSINESS_ERROR";
            } else if (exception instanceof SecurityException) {
                return "SECURITY_ERROR";
            } else {
                return "PROCESSING_ERROR";
            }
        }
    }

    // ==================== 便捷方法 ====================

    /**
     * 快速成功响应
     */
    public static Message success(Message request, Object result) {
        return createSuccessResponse(request, result);
    }

    /**
     * 快速错误响应
     */
    public static Message error(Message request, String errorCode, String errorMessage) {
        return createErrorResponse(request, errorCode, errorMessage);
    }

    /**
     * 快速异常响应
     */
    public static Message error(Message request, Exception exception) {
        return createErrorResponse(request, exception);
    }

    // ==================== 自定义异常类（如果不存在的话） ====================

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class BusinessException extends Exception {
        public BusinessException(String message) {
            super(message);
        }
    }
}
