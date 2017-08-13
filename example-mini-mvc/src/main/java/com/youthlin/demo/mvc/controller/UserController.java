package com.youthlin.demo.mvc.controller;

import com.youthlin.demo.mvc.service.IUserService;
import com.youthlin.ioc.annotaion.Controller;
import com.youthlin.mvc.annotation.Method;
import com.youthlin.mvc.annotation.Param;
import com.youthlin.mvc.annotation.ResponseBody;
import com.youthlin.mvc.annotation.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-13 13:39.
 */
@Controller("userController")
@URL("/test")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Resource
    private IUserService userService;

    @URL("/mvc")
    public String hello(Map<String, Object> map, @Param("id") int id, HttpServletRequest request,
                        @Param(value = "a", required = false, defaultValue = "0") int a, @Param("c") char c, @Param("b") boolean b,
                        HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("xxx");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            out.println(entry.getKey() + "=" + entry.getValue());
        }
        LOGGER.debug("a int = {}", a);
        LOGGER.debug("b boolean = {}", b);
        LOGGER.debug("c char = {}", c);
        LOGGER.debug("l long = {}", id);
        LOGGER.debug("{}", userService.sayHello(id));
        ServletContext servletContext = request.getServletContext();
        Enumeration<String> attributeNames = servletContext.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String element = attributeNames.nextElement();
            Object attribute = servletContext.getAttribute(element);
            LOGGER.debug("attribute:{}={}", element, attribute);
        }
        return "redirect:/test/get?name=Lin&id=" + id;
    }

    @URL(value = "/get", method = {Method.GET})
    public String get(@Param("name") String name, @Param("id") int id,
                      @Param(name = "desc", required = false, defaultValue = "desc") String desc) {
        LOGGER.debug("name = {}, desc = {}", name, desc);
        return "hello";
    }

    @URL(value = "/post", method = Method.POST)
    public String post() {
        return "post";
    }

    @ResponseBody
    @URL("responseBody")
    public Object responseBody(Map<String,Object> map) {
        return map;
    }
}
