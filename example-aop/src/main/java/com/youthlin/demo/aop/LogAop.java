package com.youthlin.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

/**
 * 创建者： youthlin.chen 日期： 17-3-26.
 */
@Aspect//声明切面
@Component//让 Spring 扫描到
public class LogAop {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAop.class);

    @SuppressWarnings("unused")
    @Pointcut("execution(* com.youthlin.demo.service.*.*(..))")//定义连接点
    private void pointcut() {
    }

    @SuppressWarnings("unused")
    @Pointcut("execution(* com.youthlin.demo.service.*.*(..)) || execution(* com.youthlin.demo.service1.*.*(..))")//定义连接点
    private void pointcut1() {
    }

    @Around("pointcut1()")//环绕通知
    public Object processTx(ProceedingJoinPoint pjp) throws Throwable {
        LOGGER.debug("环绕通知 之前");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object proceed = pjp.proceed();
        stopWatch.stop();
        LOGGER.debug("环绕通知 之后. {}ms", stopWatch.getTotalTimeMillis());
        return proceed;
    }

    @Before("pointcut()")//前置通知
    public void before(JoinPoint jp) {
        Object[] args = jp.getArgs();
        LOGGER.debug("前置通知 {} args = {}", jp.getSignature(), Arrays.toString(args));
    }

    @AfterReturning(pointcut = "pointcut()", returning = "result")//后置返回通知
    public void afterReturning(Object result) {
        LOGGER.debug("后置返回通知 result = {}", result);
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "throwable")//后置异常通知
    public void afterThrowing(Throwable throwable) {
        LOGGER.debug("后置异常通知", throwable);
    }

    @After("pointcut()")//后置通知
    public void after() {
        LOGGER.debug("后置通知");
    }
}
