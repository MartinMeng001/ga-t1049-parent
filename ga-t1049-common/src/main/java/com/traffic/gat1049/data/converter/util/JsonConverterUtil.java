package com.traffic.gat1049.data.converter.util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * JSON处理工具类
 * 专门用于转换器中的JSON序列化和反序列化操作
 */
public class JsonConverterUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonConverterUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将字符串列表序列化为JSON字符串
     */
    public static String serializeStringList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            logger.warn("序列化字符串列表失败: {}", list, e);
            return null;
        }
    }

    /**
     * 将JSON字符串反序列化为字符串列表
     */
    public static List<String> deserializeStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            logger.warn("反序列化字符串列表失败: {}", json, e);
            return new ArrayList<>();
        }
    }

    /**
     * 将整数列表序列化为JSON字符串
     */
    public static String serializeIntegerList(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            logger.warn("序列化整数列表失败: {}", list, e);
            return null;
        }
    }

    /**
     * 将JSON字符串反序列化为整数列表
     */
    public static List<Integer> deserializeIntegerList(String json) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
        } catch (Exception e) {
            logger.warn("反序列化整数列表失败: {}", json, e);
            return new ArrayList<>();
        }
    }

    /**
     * 将枚举代码列表转换为JSON（用于可变转向等场景）
     */
    public static <E extends Enum<E>> String serializeEnumCodes(List<E> enumList, EnumCodeExtractor<E> extractor) {
        if (enumList == null || enumList.isEmpty()) {
            return null;
        }

        try {
            List<String> codes = new ArrayList<>();
            for (E enumValue : enumList) {
                codes.add(extractor.getCode(enumValue));
            }
            return objectMapper.writeValueAsString(codes);
        } catch (Exception e) {
            logger.warn("序列化枚举代码列表失败: {}", enumList, e);
            return null;
        }
    }

    /**
     * 将JSON转换为枚举列表（用于可变转向等场景）
     */
    public static <E extends Enum<E>> List<E> deserializeEnumCodes(String json, EnumCodeParser<E> parser) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }

        try {
            List<String> codes = deserializeStringList(json);
            List<E> enumList = new ArrayList<>();
            for (String code : codes) {
                try {
                    enumList.add(parser.parseCode(code));
                } catch (Exception e) {
                    logger.warn("解析枚举代码失败: {}", code, e);
                }
            }
            return enumList;
        } catch (Exception e) {
            logger.warn("反序列化枚举代码列表失败: {}", json, e);
            return new ArrayList<>();
        }
    }

    /**
     * 枚举代码提取器接口
     */
    @FunctionalInterface
    public interface EnumCodeExtractor<E extends Enum<E>> {
        String getCode(E enumValue);
    }

    /**
     * 枚举代码解析器接口
     */
    @FunctionalInterface
    public interface EnumCodeParser<E extends Enum<E>> {
        E parseCode(String code);
    }
}
