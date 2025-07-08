package com.traffic.gat1049.data.converter.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JSON字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonField {

    /**
     * 目标字段名
     */
    String target() default "";

    /**
     * JSON序列化类型
     */
    Class<?> type() default Object.class;
}
