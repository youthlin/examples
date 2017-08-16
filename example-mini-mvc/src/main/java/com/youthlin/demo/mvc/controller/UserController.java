package com.youthlin.demo.mvc.controller;

import com.youthlin.demo.mvc.service.IUserService;
import com.youthlin.demo.mvc.support.JsonBody;
import com.youthlin.ioc.annotaion.Controller;
import com.youthlin.mvc.annotation.HttpMethod;
import com.youthlin.mvc.annotation.Param;
import com.youthlin.mvc.annotation.ResponseBody;
import com.youthlin.mvc.annotation.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

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
}
