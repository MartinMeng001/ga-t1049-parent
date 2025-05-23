package com.traffic.gat1049.protocol.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.traffic.gat1049.model.constants.GatConstants;
import com.traffic.gat1049.protocol.exception.MessageDecodingException;
import com.traffic.gat1049.protocol.exception.MessageEncodingException;
import com.traffic.gat1049.protocol.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * GA/T 1049.2 消息编解码器
 */
public class MessageCodec {

    private static final Logger logger = LoggerFactory.getLogger(MessageCodec.class);

    private final JAXBContext jaxbContext;
    private final ObjectMapper objectMapper;

    public MessageCodec() throws MessageEncodingException {
        try {
            this.jaxbContext = JAXBContext.newInstance(Message.class);
            this.objectMapper = createObjectMapper();
        } catch (JAXBException e) {
            throw new MessageEncodingException("Failed to initialize JAXB context", e);
        }
    }

    /**
     * 将消息对象编码为XML字符串
     */
    public String encode(Message message) throws MessageEncodingException {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, GatConstants.DEFAULT_ENCODING);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);

            StringWriter writer = new StringWriter();
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            marshaller.marshal(message, writer);

            String result = writer.toString();
            logger.debug("Encoded message: {}", result);
            return result;

        } catch (JAXBException e) {
            logger.error("Failed to encode message: {}", message, e);
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
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xmlContent.trim());
            Message message = (Message) unmarshaller.unmarshal(reader);

            logger.debug("Decoded message: {}", message);
            return message;

        } catch (JAXBException e) {
            logger.error("Failed to decode XML content: {}", xmlContent, e);
            throw new MessageDecodingException("Failed to decode XML to message object", e);
        }
    }

    /**
     * 将对象转换为JSON字符串（用于调试和日志）
     */
    public String toJson(Object obj) throws MessageEncodingException {
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
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new MessageDecodingException("Failed to convert JSON to object", e);
        }
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
