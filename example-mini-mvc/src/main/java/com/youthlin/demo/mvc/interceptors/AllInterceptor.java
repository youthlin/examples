package com.youthlin.demo.mvc.interceptors;

import com.youthlin.ioc.annotaion.Bean;
import com.youthlin.mvc.support.Interceptor;
import com.youthlin.mvc.support.InterceptorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 创建：youthlin.chen
 * 时间：2017-08-16 00:05
 */
@Bean
public class AllInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AllInterceptor.class);

    @Override
    public boolean accept(String uri) {
        return true;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object controller) throws Exception {
        LOGGER.debug("pre handle. controller:{}", controller);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object controller, Object result) throws Exception {
        LOGGER.debug("post handle. controller:{}, ret:{}", controller, result);
    }

    @Override
    public Throwable afterCompletion(HttpServletRequest request, HttpServletResponse response, Object controller, Throwable e) throws Exception {
        LOGGER.debug("after completion.", e);
        return e;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
