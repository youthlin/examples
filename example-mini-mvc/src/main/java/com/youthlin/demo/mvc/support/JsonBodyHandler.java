package com.youthlin.demo.mvc.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youthlin.ioc.annotaion.AnnotationUtil;
import com.youthlin.mvc.support.ResponseBodyHandler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-14 17:04.
 */
@Resource
public class JsonBodyHandler implements ResponseBodyHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override public boolean accept(Method controllerMethod) {
        return AnnotationUtil.getAnnotation(controllerMethod, JsonBody.class) != null;
    }

    @Override public void handler(HttpServletRequest request, HttpServletResponse response, Object result)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(objectMapper.writeValueAsBytes(result));
    }

    @Override public int getOrder() {
        return 0;
    }
}
