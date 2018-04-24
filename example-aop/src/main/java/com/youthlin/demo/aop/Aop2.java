package com.youthlin.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 创建: youthlin.chen
 * 时间: 2018-04-24 17:47
 */
@Aspect
@Component
@Order(3)
public class Aop2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(Aop2.class);

    @Around("execution(* com.youthlin.demo.service.*.*(..))")
    public Object process(ProceedingJoinPoint pjp) throws Throwable {
        LOGGER.info("around start {}", pjp);
        Object proceed = pjp.proceed();
        LOGGER.info("around end {}", proceed);
        return proceed;
    }
}
