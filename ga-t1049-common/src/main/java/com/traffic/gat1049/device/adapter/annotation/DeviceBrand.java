package com.traffic.gat1049.device.adapter.annotation;

import java.lang.annotation.*;

/**
 * 设备品牌注解
 * 用于标识适配器支持的设备品牌
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeviceBrand {

    /**
     * 品牌名称
     */
    String value();

    /**
     * 品牌代码
     */
    String code() default "";

    /**
     * 支持的设备型号
     */
    String[] supportedModels() default {};

    /**
     * 厂商信息
     */
    String vendor() default "";
}
