package com.youthlin.demo.mvc.controller;

import com.youthlin.ioc.annotation.Controller;
import com.youthlin.ioc.context.Context;
import com.youthlin.mvc.annotation.URL;
import com.youthlin.mvc.listener.ContextLoaderListener;
import com.youthlin.mvc.view.jackson.JsonBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.util.Map;
import java.util.Set;

/**
 * 创建: youthlin.chen
 * 时间: 2017-12-15 17:20
 */
@Controller
@URL("debug")
public class DebugController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugController.class);

    @URL("beans")
    @JsonBody
    public Object showBeans() {
        Context context = ContextLoaderListener.getContext();
        Set<Class> classSet = context.getClazzBeanMap().keySet();
        Set<String> nameSet = context.getNameBeanMap().keySet();
        LOGGER.info("{}", context.getBean("$Proxy27"));
        return new Object[]{classSet, nameSet};
    }

    @URL("code")
    public String code(HttpServletRequest request, Map<String, String> map) {
        boolean sourceAvailable = false;
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler != null) {
            sourceAvailable = true;
        }
        map.put("sourceAvailable", String.valueOf(sourceAvailable));
        return "debug/code";
    }

}
