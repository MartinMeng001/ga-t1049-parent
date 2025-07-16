package com.traffic.gat1049.device.adapter.annotation;

import java.lang.annotation.*;

/**
 * 协议版本注解
 * 用于标识适配器支持的协议版本
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProtocolVersion {

    /**
     * 支持的协议版本
     */
    String[] value();

    /**
     * 最小版本
     */
    String minVersion() default "";

    /**
     * 最大版本
     */
    String maxVersion() default "";
}
