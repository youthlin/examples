package com.youthlin.demo.mvc.dao;

import com.youthlin.ioc.annotaion.Bean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-19 00:18.
 */
@Bean
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {
    String value() default "";
}
