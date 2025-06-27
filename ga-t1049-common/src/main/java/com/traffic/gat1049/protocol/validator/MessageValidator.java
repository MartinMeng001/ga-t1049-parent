package com.traffic.gat1049.protocol.validator;

import com.traffic.gat1049.exception.MessageValidationException;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.core.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * GA/T 1049.2 消息验证器
 */
public class MessageValidator {

    private static final Logger logger = LoggerFactory.getLogger(MessageValidator.class);

    /**
     * 验证消息格式
     */
    public void validate(Message message) throws MessageValidationException {
        if (message == null) {
            throw new MessageValidationException("Message cannot be null");
        }

        validateVersion(message.getVersion());
        validateType(message.getType());
        validateSeq(message.getSeq());
        validateAddresses(message);
        validateBody(message);

        logger.debug("Message validation passed for seq: {}", message.getSeq());
    }

    /**
     * 验证版本号
     */
    private void validateVersion(String version) throws MessageValidationException {
        if (version == null || version.trim().isEmpty()) {
            throw new MessageValidationException("Version cannot be null or empty");
        }

        if (!GatConstants.PROTOCOL_VERSION.equals(version)) {
            throw new MessageValidationException(
                    "Unsupported protocol version: " + version +
                            ", expected: " + GatConstants.PROTOCOL_VERSION);
        }
    }

    /**
     * 验证消息类型
     */
    private void validateType(String type) throws MessageValidationException {
        if (type == null || type.trim().isEmpty()) {
            throw new MessageValidationException("Message type cannot be null or empty");
        }

        if (!isValidMessageType(type)) {
            throw new MessageValidationException("Invalid message type: " + type);
        }
    }

    /**
     * 验证序列号
     */
    private void validateSeq(String seq) throws MessageValidationException {
        if (seq == null || seq.trim().isEmpty()) {
            throw new MessageValidationException("Sequence number cannot be null or empty");
        }

        if (seq.length() > 64) {
            throw new MessageValidationException("Sequence number too long: " + seq.length());
        }
    }

    /**
     * 验证地址信息
     */
    private void validateAddresses(Message message) throws MessageValidationException {
        if (message.getFrom() == null) {
            throw new MessageValidationException("From address cannot be null");
        }

        if (message.getTo() == null) {
            throw new MessageValidationException("To address cannot be null");
        }

        if (message.getFrom().getSys() == null || message.getFrom().getSys().trim().isEmpty()) {
            throw new MessageValidationException("From system cannot be null or empty");
        }

        if (message.getTo().getSys() == null || message.getTo().getSys().trim().isEmpty()) {
            throw new MessageValidationException("To system cannot be null or empty");
        }
    }

    /**
     * 验证消息体
     */
    private void validateBody(Message message) throws MessageValidationException {
        if (message.getBody() == null) {
            throw new MessageValidationException("Message body cannot be null");
        }

        List<Operation> operations = message.getBody().getOperations();
        if (operations == null || operations.isEmpty()) {
            throw new MessageValidationException("Message body must contain at least one operation");
        }

        for (Operation operation : operations) {
            validateOperation(operation);
        }
    }

    /**
     * 验证操作
     */
    private void validateOperation(Operation operation) throws MessageValidationException {
        if (operation == null) {
            throw new MessageValidationException("Operation cannot be null");
        }

        if (operation.getName() == null || operation.getName().trim().isEmpty()) {
            throw new MessageValidationException("Operation name cannot be null or empty");
        }

        if (!isValidOperationName(operation.getName())) {
            throw new MessageValidationException("Invalid operation name: " + operation.getName());
        }

        if (operation.getData() == null) {
            throw new MessageValidationException("Operation data cannot be null");
        }
    }

    /**
     * 检查是否是有效的消息类型
     */
    private boolean isValidMessageType(String type) {
        return GatConstants.MessageType.REQUEST.equals(type) ||
                GatConstants.MessageType.RESPONSE.equals(type) ||
                GatConstants.MessageType.PUSH.equals(type) ||
                GatConstants.MessageType.ERROR.equals(type);
    }

    /**
     * 检查是否是有效的操作名称
     */
    private boolean isValidOperationName(String name) {
        return GatConstants.Operation.GET.equals(name) ||
                GatConstants.Operation.SET.equals(name) ||
                GatConstants.Operation.NOTIFY.equals(name) ||
                GatConstants.Operation.LOGIN.equals(name) ||
                GatConstants.Operation.LOGOUT.equals(name) ||
                GatConstants.Operation.SUBSCRIBE.equals(name) ||
                GatConstants.Operation.UNSUBSCRIBE.equals(name) ||
                "Error".equals(name);
    }

    /**
     * 验证对象名称
     */
    public void validateObjectName(String objName) throws MessageValidationException {
        if (objName == null || objName.trim().isEmpty()) {
            throw new MessageValidationException("Object name cannot be null or empty");
        }

        // 这里可以添加更多的对象名称验证逻辑
        // 比如检查是否在预定义的对象名称列表中
    }

    /**
     * 验证ID格式
     */
    public void validateId(String objName, String id) throws MessageValidationException {
        if (id == null || id.trim().isEmpty()) {// 为空表示查询所有,大多数情况的ID是不允许为空的，只有特定查询允许为空
            // 系统信息和区域信息ID允许为空
            if (!GatConstants.ObjectName.SYS_INFO.equals(objName) ||
                    !GatConstants.ObjectName.REGION_PARAM.equals(objName)) {
                throw new MessageValidationException("ID cannot be null or empty for object: " + objName);
            }
            return;
        }

        // 根据对象类型验证ID格式
        switch (objName) {
            case GatConstants.ObjectName.SIGNAL_CONTROLLER:
                if (!id.matches(GatConstants.IdFormat.SIGNAL_CONTROLLER_ID_PATTERN)) {
                    throw new MessageValidationException("Invalid signal controller ID format: " + id);
                }
                break;
            case GatConstants.ObjectName.REGION_PARAM:
                if (!id.matches(GatConstants.IdFormat.REGION_ID_PATTERN)) {
                    throw new MessageValidationException("Invalid region ID format: " + id);
                }
                break;
            case GatConstants.ObjectName.SUB_REGION_PARAM:
                if (!id.matches(GatConstants.IdFormat.SUB_REGION_ID_PATTERN)) {
                    throw new MessageValidationException("Invalid sub-region ID format: " + id);
                }
                break;
            default:
                // 其他对象类型的验证可以在这里添加
                break;
        }
    }
}
