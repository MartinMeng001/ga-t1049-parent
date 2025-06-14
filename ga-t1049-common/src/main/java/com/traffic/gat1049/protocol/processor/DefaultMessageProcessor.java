package com.traffic.gat1049.protocol.processor;

import com.traffic.gat1049.exception.GatProtocolException;
import com.traffic.gat1049.exception.MessageDecodingException;
import com.traffic.gat1049.exception.MessageEncodingException;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.builder.MessageBuilder;
import com.traffic.gat1049.protocol.codec.MessageCodec;
import com.traffic.gat1049.protocol.handler.base.ProtocolHandler;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.sdo.SdoError;
import com.traffic.gat1049.protocol.util.ProtocolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 默认消息处理器实现
 */
public class DefaultMessageProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageProcessor.class);

    private final MessageCodec codec;
    private final List<ProtocolHandler> handlers;
    private String tempToken = "";

    public DefaultMessageProcessor() throws MessageEncodingException {
        this.codec = MessageCodec.getInstance();
        this.handlers = new CopyOnWriteArrayList<>();
    }

    @Override
    public String processMessage(String xmlContent) throws GatProtocolException {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            throw new MessageDecodingException("XML content is null or empty");
        }

        // 清理XML内容
        String cleanedXml = ProtocolUtils.cleanXmlContent(xmlContent);
        if (!ProtocolUtils.isValidXmlContent(cleanedXml)) {
            throw new MessageDecodingException("Invalid XML content format");
        }

        logger.debug("Processing XML message: {}", cleanedXml);

        try {
            // 解码消息
            Message message = codec.decode(cleanedXml);

            // 处理消息
            Message response = processMessage(message);

            // 编码响应
            if (response != null) {
                String responseXml = codec.encode(response);
                logger.debug("Generated response XML: {}", responseXml);
                return responseXml;
            }

            return null;

        } catch (MessageDecodingException | MessageEncodingException e) {
            logger.error("Codec error processing message", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing XML message", e);
            throw new GatProtocolException("Failed to process XML message", e);
        }
    }

    @Override
    public Message processMessage(Message message) throws GatProtocolException {
        if (message == null) {
            throw new GatProtocolException("Message cannot be null");
        }

        logger.debug("Processing message: type={}, seq={}, from={}, to={}",
                message.getType(), message.getSeq(),
                message.getFrom() != null ? message.getFrom().getSys() : "null",
                message.getTo() != null ? message.getTo().getSys() : "null");

        try {
            // 查找合适的处理器
            ProtocolHandler handler = findHandler(message);

            if (handler == null) {  // 无需返回
                logger.warn("No handler found for message: type={}, operation={}",
                        message.getType(), ProtocolUtils.getOperationName(message));
                return null;
            }

            // 处理消息
            Message response = handler.handleMessage(message);
            tempToken = response.getToken();
            if (response != null) {
                logger.debug("Message processed successfully by handler: {}", handler.getHandlerName());
            } else {
                logger.debug("Handler {} processed message without response", handler.getHandlerName());
            }

            return response;

        } catch (GatProtocolException e) {
            logger.error("Protocol error processing message", e);
            return createErrorResponse(message, e);
        } catch (Exception e) {
            logger.error("Unexpected error processing message", e);
            return createErrorResponse(message, new GatProtocolException("Internal processing error", e));
        }
    }

    @Override
    public void registerHandler(ProtocolHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        // 移除同名的处理器
        removeHandler(handler.getHandlerName());

        // 添加新处理器
        handlers.add(handler);
        logger.info("Registered protocol handler: {}", handler.getHandlerName());
    }

    @Override
    public void removeHandler(String handlerName) {
        if (handlerName == null || handlerName.trim().isEmpty()) {
            return;
        }

        handlers.removeIf(handler -> handlerName.equals(handler.getHandlerName()));
        logger.info("Removed protocol handler: {}", handlerName);
    }

    @Override
    public String getTempToken() {
        return tempToken;
    }

    /**
     * 查找合适的处理器
     */
    private ProtocolHandler findHandler(Message message) {
        for (ProtocolHandler handler : handlers) {
            if (handler.supports(message)) {
                return handler;
            }
        }
        return null;
    }

    /**
     * 创建不支持的操作响应
     */
    private Message createUnsupportedResponse(Message request) {
        return MessageBuilder.create()
                .error()
                .fromUtcs()
                .toTicp()
                .seq(request.getSeq())
                .token(request.getToken())
                .operation("Error", createErrorData(
                        GatConstants.ErrorCode.OPERATION_FAILED,
                        "Unsupported operation: " + ProtocolUtils.getOperationName(request)))
                .build();
    }

    /**
     * 创建错误响应
     */
    private Message createErrorResponse(Message request, GatProtocolException exception) {
        String errorCode = exception.getErrorCode() != null ?
                exception.getErrorCode() : GatConstants.ErrorCode.SYSTEM_ERROR;
        String errorMessage = exception.getErrorMessage() != null ?
                exception.getErrorMessage() : exception.getMessage();

        return MessageBuilder.create()
                .error()
                .fromUtcs()
                .toTicp()
                .seq(request != null ? request.getSeq() : ProtocolUtils.generateSequence())
                .token(request != null ? request.getToken() : null)
                .operation("Error", createErrorData(errorCode, errorMessage))
                .build();
    }

    /**
     * 创建错误数据对象
     */
//    private Object createErrorData(String code, String message) {
//        return new Object() {
//            public String getCode() { return code; }
//            public String getMessage() { return message; }
//            public String getTimestamp() { return ProtocolUtils.formatDateTime(java.time.LocalDateTime.now()); }
//        };
//    }
    private Object createErrorData(String code, String message) {
        return new SdoError("", code, message);
    }

    /**
     * 获取已注册的处理器数量
     */
    public int getHandlerCount() {
        return handlers.size();
    }

    /**
     * 获取所有处理器名称
     */
    public List<String> getHandlerNames() {
        return handlers.stream()
                .map(ProtocolHandler::getHandlerName)
                .collect(java.util.stream.Collectors.toList());
    }
}
