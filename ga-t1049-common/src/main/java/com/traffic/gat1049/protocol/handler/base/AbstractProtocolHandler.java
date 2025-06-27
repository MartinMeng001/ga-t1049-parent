package com.traffic.gat1049.protocol.handler.base;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.MessageValidationException;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoError;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import com.traffic.gat1049.protocol.validator.MessageValidator;
import com.traffic.gat1049.protocol.validator.TokenValidator;
import com.traffic.gat1049.application.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 协议处理器抽象基类 - 集成Token验证
 * 现在集成了MessageValidator和TokenValidator两个协议层验证器
 */
public abstract class AbstractProtocolHandler implements ProtocolHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final MessageValidator messageValidator = new MessageValidator();
    protected TokenValidator tokenValidator; // 可选的token验证器

    @Override
    public Message handleMessage(Message message) throws GatProtocolException {
        try {
            // 1. 基础消息格式验证
            messageValidator.validate(message);

            // 2. Token验证（如果配置了TokenValidator且需要验证）
            if (tokenValidator != null && TokenValidator.requiresTokenValidation(message)) {
                tokenValidator.validateToken(message);
            }

            // 3. 记录日志
            logger.debug("Handling message: type={}, seq={}, operation={}",
                    message.getType(), message.getSeq(), ProtocolUtils.getOperationName(message));

            // 4. 调用具体处理逻辑
            Message response = doHandle(message);

            if (response != null) {
                logger.debug("Generated response: type={}, seq={}",
                        response.getType(), response.getSeq());
            }

            return response;

        } catch (MessageValidationException e) {
            logger.error("Message validation failed: {}", e.getMessage());
            return createErrorResponse(message, GatConstants.ErrorCode.INVALID_PARAMETER, e.getMessage(), "");
        } catch (Exception e) {
            logger.error("Unexpected error handling message", e);
            return createErrorResponse(message, GatConstants.ErrorCode.SYSTEM_ERROR,
                    "Internal server error: " + e.getMessage(), "");
        }
    }

    /**
     * 设置Token验证器（可选）
     * 不是所有Handler都需要token验证，比如LoginHandler就不需要
     */
    public void setTokenValidator(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    /**
     * 具体的消息处理逻辑，由子类实现
     */
    protected abstract Message doHandle(Message message) throws GatProtocolException;

    /**
     * 创建成功响应
     */
    protected Message createLoginSuccessResponse(Message message, String token, String username) {
        return MessageBuilder.createLoginSuccessResponse(message, token, username);
    }
    protected Message createSuccessResponse(Message request, Object data) {
        return MessageBuilder.responseFor(request)
                .success(data)
                .build();
//        return MessageBuilder.create()
//                .response()
//                .fromUtcs()
//                .toTicp()
//                .seq(request.getSeq())
//                .token(request.getToken())
//                .operation(ProtocolUtils.getOperationName(request), data)
//                .build();
    }

    /**
     * 创建错误响应
     */
    protected Message createErrorResponse(Message request, String errorCode, String errorMessage, String objName) {
        if (request == null) {
            return MessageBuilder.create()
                    .error()
                    .fromUtcs(request.getTo().getSubSys())
                    .toTicp()
                    .seq(request.getSeq())
                    .operation("Error", createErrorData(errorCode, errorMessage, objName))
                    .build();
        }

        return MessageBuilder.create()
                .error()
                .fromUtcs(request.getTo().getSubSys())
                .toTicp()
                .seq(request.getSeq())
                .token(request.getToken())
                .operation("Error", createErrorData(errorCode, errorMessage, objName))
                .build();
    }

    /**
     * 创建错误数据
     */
    private Object createErrorData(String errorCode, String errorMessage, String objName) {
        SdoError sdoError = new SdoError();
        sdoError.setErrObj(objName);
        sdoError.setErrDesc(errorMessage);
        sdoError.setErrType(errorCode);
        return sdoError;
    }

    /**
     * 获取当前消息的会话信息（如果有TokenValidator且消息需要验证）
     */
    protected SessionManager.SessionInfo getCurrentSession(Message message) throws MessageValidationException {
        if (tokenValidator != null && TokenValidator.requiresTokenValidation(message)) {
            return tokenValidator.validateAndGetSession(message);
        }
        return null;
    }

    /**
     * 检查当前消息是否为查询请求
     */
    protected boolean isQueryRequest(Message message) {
        return ProtocolUtils.isRequest(message) &&
                GatConstants.Operation.GET.equals(ProtocolUtils.getOperationName(message));
    }

    /**
     * 检查当前消息是否为设置请求
     */
    protected boolean isSetRequest(Message message) {
        return ProtocolUtils.isRequest(message) &&
                GatConstants.Operation.SET.equals(ProtocolUtils.getOperationName(message));
    }

    /**
     * 检查当前消息是否为通知消息
     */
    protected boolean isNotifyMessage(Message message) {
        return ProtocolUtils.isPush(message) &&
                GatConstants.Operation.NOTIFY.equals(ProtocolUtils.getOperationName(message));
    }
}

