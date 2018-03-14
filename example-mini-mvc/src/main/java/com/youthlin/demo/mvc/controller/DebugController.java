package com.youthlin.demo.mvc.controller;

import com.youthlin.debug.JavaClassExecutor;
import com.youthlin.debug.compiler.JavaCompilerForString;
import com.youthlin.ioc.annotation.Controller;
import com.youthlin.ioc.context.Context;
import com.youthlin.mvc.annotation.URL;
import com.youthlin.mvc.listener.ContextLoaderListener;
import com.youthlin.mvc.view.jackson.JsonBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
    public String code(Part bytes, String code, String fileName, String cp,
            Map<String, String> map) throws IOException {
        map.put("sourceAvailable", String.valueOf(JavaCompilerForString.supportCompiler()));

        StringBuilder sb = new StringBuilder();
        String result = "";
        if (bytes != null) {
            InputStream in = bytes.getInputStream();
            int size = in.available();
            byte[] classBytes = new byte[size];
            int read = in.read(classBytes);
            LOGGER.info("read {} bytes", read);
            if (size > 0 && read > 0) {
                result = JavaClassExecutor.execute(classBytes);
                sb.append(result).append("\n\n");
            }
        }

        if (code != null && !code.isEmpty()) {
            if (JavaCompilerForString.supportCompiler()) {
                String classpath = "";
                if (cp != null && !cp.isEmpty()) {
                    classpath = cp + JavaClassExecutor.getPathSeparator();
                }
                classpath += JavaClassExecutor.getClasspath();
                StringWriter out = new StringWriter();
                byte[] classBytes = JavaCompilerForString.compile(fileName, code, classpath, out);
                if (classBytes.length > 0) {
                    result = JavaClassExecutor.execute(classBytes);
                } else {
                    result = out.toString();
                }
            } else {
                LOGGER.warn("服务器不支持即时编译");
                result = "服务器不支持即时编译";
            }
        }

        sb.append(result).append("\n\n");
        map.put("result", sb.toString());
        LOGGER.info("result:----------------------\n{}\n----------------------", sb.toString());
        return "debug/code";
    }

}
