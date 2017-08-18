package com.youthlin.demo.mvc.controller;

import com.youthlin.demo.mvc.model.User;
import com.youthlin.demo.mvc.service.IUserService;
import com.youthlin.ioc.annotaion.Controller;
import com.youthlin.mvc.annotation.HttpMethod;
import com.youthlin.mvc.annotation.Param;
import com.youthlin.mvc.annotation.ResponseBody;
import com.youthlin.mvc.annotation.URL;
import com.youthlin.mvc.support.jackson.JsonBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
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

    @PostConstruct
    public void postConstruct() {
        System.out.println("PostConstruct {}");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("PreDestroy {}");
    }

    @URL(value = "hello")
    public String hello(Map<String, Object> map) {
        map.put("name", "Lin");
        return "hello";
    }

    @URL("/sayHello")
    @JsonBody
    public String sayHello(@Param("id") Long id) {
        return userService.sayHello(id);
    }

    @JsonBody
    @URL(value = "/get", method = HttpMethod.GET)
    public String get() {
        return "get";
    }

    @URL("/void")
    @JsonBody
    public void aVoid() {
        LOGGER.debug("aVoid");
    }

    // curl -i -X TRACE http://127.0.0.1:8080/test/post
    @URL(value = "/post", method = HttpMethod.POST)
    @ResponseBody
    public void post() {

    }

    @URL(value = "get.do", method = HttpMethod.GET)
    @JsonBody
    public String getDo() {
        return "get.do";
    }

    @URL("th")
    public String th(Map<String, Object> map) {
        map.put("user", new User()
                .setName((String) map.get("name")));
        return "th";
    }
}
