package com.traffic.gat1049.device.adapter.annotation;

import com.traffic.gat1049.device.adapter.model.AdapterType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 适配器组件注解
 * 用于标识和配置适配器类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AdapterComponent {

    /**
     * 设备品牌
     */
    String brand();

    /**
     * 适配器类型
     */
    AdapterType adapterType() default AdapterType.SIGNAL_CONTROLLER;

    /**
     * 版本号
     */
    String version() default "1.0";

    /**
     * 描述信息
     */
    String description() default "";

    /**
     * 是否自动注册
     */
    boolean autoRegister() default true;

    /**
     * 优先级
     */
    int priority() default 0;
}
