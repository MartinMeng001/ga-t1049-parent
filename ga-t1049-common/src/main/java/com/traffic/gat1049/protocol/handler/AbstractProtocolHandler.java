package com.traffic.gat1049.protocol.handler;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.MessageValidationException;
import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.Message;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.protocol.validator.MessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 协议处理器抽象基类
 */
public abstract class AbstractProtocolHandler implements ProtocolHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final MessageValidator validator = new MessageValidator();

    @Override
    public Message handleMessage(Message message) throws GatProtocolException {
        try {
            // 验证消息
            validator.validate(message);

            // 记录日志
            logger.debug("Handling message: type={}, seq={}, operation={}",
                    message.getType(), message.getSeq(), ProtocolUtils.getOperationName(message));

            // 调用具体处理逻辑
            Message response = doHandle(message);

            if (response != null) {
                logger.debug("Generated response: type={}, seq={}",
                        response.getType(), response.getSeq());
            }

            return response;

        } catch (MessageValidationException e) {
            logger.error("Message validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error handling message", e);
            return createErrorResponse(message, GatConstants.ErrorCode.SYSTEM_ERROR,
                    "Internal server error: " + e.getMessage());
        }
    }

    /**
     * 具体的消息处理逻辑，由子类实现
     */
    protected abstract Message doHandle(Message message) throws GatProtocolException;

    /**
     * 创建成功响应
     */
    protected Message createSuccessResponse(Message request, Object data) {
        return MessageBuilder.create()
                .response()
                .fromUtcs()
                .toTicp()
                .seq(request.getSeq())
                .token(request.getToken())
                .operation(ProtocolUtils.getOperationName(request), data)
                .build();
    }

    /**
     * 创建错误响应
     */
    protected Message createErrorResponse(Message request, String errorCode, String errorMessage) {
        if (request == null) {
            return MessageBuilder.createErrorResponse(ProtocolUtils.generateSequence(), request.getToken(), errorCode, errorMessage);
        }

        return MessageBuilder.create()
                .error()
                .fromUtcs()
                .toTicp()
                .seq(request.getSeq())
                .token(request.getToken())
                .operation("Error", createErrorData(errorCode, errorMessage))
                .build();
    }

    /**
     * 创建错误数据对象
     */
    private Object createErrorData(String code, String message) {
        return new Object() {
            public String getCode() { return code; }
            public String getMessage() { return message; }
            public String getTimestamp() { return ProtocolUtils.formatDateTime(java.time.LocalDateTime.now()); }
        };
    }

    /**
     * 检查是否为查询请求
     */
    protected boolean isQueryRequest(Message message) {
        return ProtocolUtils.isRequest(message) && ProtocolUtils.isGetOperation(message);
    }

    /**
     * 检查是否为设置请求
     */
    protected boolean isSetRequest(Message message) {
        return ProtocolUtils.isRequest(message) && ProtocolUtils.isSetOperation(message);
    }

    /**
     * 提取TSCCmd对象
     */
    protected Object extractTscCmd(Message message) {
        return ProtocolUtils.getOperationData(message);
    }
}
