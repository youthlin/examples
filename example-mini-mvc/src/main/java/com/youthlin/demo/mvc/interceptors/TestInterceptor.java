package com.youthlin.demo.mvc.interceptors;

import com.youthlin.ioc.annotaion.Bean;
import com.youthlin.mvc.support.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 创建：youthlin.chen
 * 时间：2017-08-16 00:05
 */
@Bean
public class TestInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestInterceptor.class);

    @Override
    public boolean accept(String uri) {
        return uri.endsWith(".do");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object controller) throws Exception {
        LOGGER.debug(".do pre handle. controller:{}", controller);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object controller, Object result) throws Exception {
        LOGGER.debug(".do post handle. controller:{}, ret:{}", controller, result);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object controller, Throwable e) throws Exception {
        LOGGER.debug(".do after completion.", e);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
