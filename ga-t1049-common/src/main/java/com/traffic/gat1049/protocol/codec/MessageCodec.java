package com.traffic.gat1049.protocol.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.traffic.gat1049.exception.MessageDecodingException;
import com.traffic.gat1049.exception.MessageEncodingException;
import com.traffic.gat1049.protocol.constants.GatConstants;
import com.traffic.gat1049.protocol.model.command.*;
import com.traffic.gat1049.protocol.model.core.Message;
import com.traffic.gat1049.protocol.model.core.Address;
import com.traffic.gat1049.protocol.model.core.MessageBody;
import com.traffic.gat1049.protocol.model.core.Operation;
import com.traffic.gat1049.protocol.model.intersection.*;
import com.traffic.gat1049.protocol.model.runtime.*;
import com.traffic.gat1049.protocol.model.sdo.*;
import com.traffic.gat1049.protocol.model.signal.*;
import com.traffic.gat1049.protocol.model.system.*;
import com.traffic.gat1049.protocol.model.traffic.CrossTrafficData;
import com.traffic.gat1049.protocol.model.traffic.StageTrafficData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * GA/T 1049.2 消息编解码器 - 改进版
 *
 * 改进点：
 * 1. 延迟初始化，避免构造函数抛异常
 * 2. 提供静态工厂方法
 * 3. 线程安全的单例模式
 * 4. 更好的错误处理
 */
public class MessageCodec {

    private static final Logger logger = LoggerFactory.getLogger(MessageCodec.class);

    // 单例实例
    private static volatile MessageCodec instance;

    private volatile JAXBContext jaxbContext;
    private final ObjectMapper objectMapper;
    private volatile boolean initialized = false;

    // 私有构造函数，不抛异常
    private MessageCodec() {
        this.objectMapper = createObjectMapper();
    }

    /**
     * 获取单例实例
     */
    public static MessageCodec getInstance() {
        if (instance == null) {
            synchronized (MessageCodec.class) {
                if (instance == null) {
                    instance = new MessageCodec();
                }
            }
        }
        return instance;
    }

    /**
     * 静态工厂方法 - 创建新实例
     */
    public static MessageCodec create() {
        return new MessageCodec();
    }

    /**
     * 延迟初始化JAXB上下文
     */
    private void ensureInitialized() throws MessageEncodingException {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    try {
                        //this.jaxbContext = JAXBContext.newInstance(Message.class);
                        Class<?>[] classes = {
                                Message.class,
                                Address.class,
                                MessageBody.class,
                                Operation.class,
                                SdoError.class,
                                SdoUser.class,
                                SdoHeartBeat.class,
                                SdoMsgEntity.class,
                                //com.traffic.gat1049.model.dto.response.LoginResponse.class,
                                //com.traffic.gat1049.model.dto.response.LogoutResponse.class,
                                SdoHeartBeat.class,
                                SdoTimeServer.class,
                                SdoTimeOut.class,
                                TSCCmd.class,
                                SysInfo.class,
                                SysState.class,
                                RegionParam.class,
                                SubRegionParam.class,
                                RouteCross.class,
                                RouteParam.class,
                                CrossParam.class,
                                SignalController.class,
                                PedestrianParam.class,
                                LampGroupParam.class,
                                DetectorParam.class,
                                LaneParam.class,
                                StageParam.class,
                                DayPlanParam.class,
                                Period.class,
                                PlanParam.class,
                                ScheduleParam.class,
                                SignalGroupParam.class,
                                SignalGroupStatus.class,
                                StageParam.class,
                                StageTiming.class,
                                Adjust.class,
                                CrossState.class,
                                SignalControllerError.class,
                                CrossCtrlInfo.class,
                                CrossCycle.class,
                                CrossStage.class,
                                CrossSignalGroupStatus.class,
                                CrossTrafficData.class,
                                StageTrafficData.class,
                                VarLaneStatus.class,
                                RouteCtrlInfo.class,
                                RouteSpeed.class,
                                LockFlowDirection.class,
                                UnlockFlowDirection.class,
                                AdjustStage.class,
                                CrossReportCtrl.class,
                                CenterPlan.class,
                                SetDayPlanParam.class,
                                CtrlVarLane.class,
                                CrossRunInfoRetrans.class,
                                SetScheduleParam.class
                                // 根据需要添加其他 SDO 类
                        };

                        this.jaxbContext = JAXBContext.newInstance(classes);
                        this.initialized = true;
                        logger.info("MessageCodec initialized successfully");
                    } catch (JAXBException e) {
                        logger.error("Failed to initialize JAXB context", e);
                        throw new MessageEncodingException("Failed to initialize JAXB context", e);
                    }
                }
            }
        }
    }

    /**
     * 将消息对象编码为XML字符串
     */
    public String encode(Message message) throws MessageEncodingException {
        if (message == null) {
            throw new MessageEncodingException("Message cannot be null");
        }

        ensureInitialized();

        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, GatConstants.DEFAULT_ENCODING);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);

            StringWriter writer = new StringWriter();
            //writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            //logger.info(message.toString());
            marshaller.marshal(message, writer);

            String result = writer.toString();
            logger.debug("Encoded message: seq={}, type={}", message.getSeq(), message.getType());
            logger.info(result);
            return result;

        } catch (JAXBException e) {
            logger.error("Failed to encode message: seq={}, type={}",
                    message.getSeq(), message.getType(), e);
            throw new MessageEncodingException("Failed to encode message to XML", e);
        }
    }

    /**
     * 将XML字符串解码为消息对象
     */
    public Message decode(String xmlContent) throws MessageDecodingException {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            throw new MessageDecodingException("XML content is null or empty");
        }

        try {
            ensureInitialized();
        } catch (MessageEncodingException e) {
            throw new MessageDecodingException("Failed to initialize codec", e);
        }

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xmlContent.trim());
            Message message = (Message) unmarshaller.unmarshal(reader);

            logger.debug("Decoded message: seq={}, type={}", message.getSeq(), message.getType());
            return message;

        } catch (JAXBException e) {
            logger.error("Failed to decode XML content: {}",
                    xmlContent.length() > 500 ? xmlContent.substring(0, 500) + "..." : xmlContent, e);
            throw new MessageDecodingException("Failed to decode XML to message object", e);
        }
    }

    /**
     * 尝试编码，返回结果而不抛异常
     */
    public CodecResult<String> tryEncode(Message message) {
        try {
            String result = encode(message);
            return CodecResult.success(result);
        } catch (Exception e) {
            return CodecResult.failure(e.getMessage(), e);
        }
    }

    /**
     * 尝试解码，返回结果而不抛异常
     */
    public CodecResult<Message> tryDecode(String xmlContent) {
        try {
            Message result = decode(xmlContent);
            return CodecResult.success(result);
        } catch (Exception e) {
            return CodecResult.failure(e.getMessage(), e);
        }
    }

    /**
     * 将对象转换为JSON字符串（用于调试和日志）
     */
    public String toJson(Object obj) throws MessageEncodingException {
        if (obj == null) {
            return "null";
        }

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new MessageEncodingException("Failed to convert object to JSON", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     */
    public <T> T fromJson(String json, Class<T> clazz) throws MessageDecodingException {
        if (json == null || json.trim().isEmpty()) {
            throw new MessageDecodingException("JSON content is null or empty");
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new MessageDecodingException("Failed to convert JSON to object", e);
        }
    }

    /**
     * 检查编解码器是否已初始化
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 强制重新初始化
     */
    public synchronized void reinitialize() throws MessageEncodingException {
        initialized = false;
        jaxbContext = null;
        ensureInitialized();
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * 编解码结果包装类
     */
    public static class CodecResult<T> {
        private final boolean success;
        private final T data;
        private final String errorMessage;
        private final Throwable exception;

        private CodecResult(boolean success, T data, String errorMessage, Throwable exception) {
            this.success = success;
            this.data = data;
            this.errorMessage = errorMessage;
            this.exception = exception;
        }

        public static <T> CodecResult<T> success(T data) {
            return new CodecResult<>(true, data, null, null);
        }

        public static <T> CodecResult<T> failure(String errorMessage, Throwable exception) {
            return new CodecResult<>(false, null, errorMessage, exception);
        }

        public boolean isSuccess() {
            return success;
        }

        public T getData() {
            return data;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public Throwable getException() {
            return exception;
        }

        public T getDataOrThrow() throws Exception {
            if (success) {
                return data;
            } else {
                if (exception instanceof Exception) {
                    throw (Exception) exception;
                } else {
                    throw new RuntimeException(errorMessage, exception);
                }
            }
        }
    }
}