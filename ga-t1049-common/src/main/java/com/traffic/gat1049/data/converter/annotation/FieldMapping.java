// ================================================================
// 2. 转换器注解定义
// ================================================================
package com.traffic.gat1049.data.converter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段映射注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMapping {

    /**
     * 目标字段名
     */
    String target() default "";

    /**
     * 是否可更新
     */
    boolean updatable() default true;

    /**
     * 转换器类型
     */
    Class<?> converter() default Object.class;
}
