// ================================================================
// 7. 配置类
// ================================================================
package com.traffic.gat1049.data.converter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 转换器配置类
 */
@Configuration
public class ConverterConfig {

    /**
     * 配置ObjectMapper用于JSON序列化
     */
    @Bean
    public ObjectMapper converterObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 注册Java 8时间模块
        mapper.registerModule(new JavaTimeModule());

        // 忽略null值
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 忽略未知属性
        mapper.configure(
                com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
        );

        return mapper;
    }
}