package com.youthlin.demo.mvc.controller;

import com.youthlin.demo.mvc.dao.IUserDao;
import com.youthlin.demo.mvc.model.User;
import com.youthlin.demo.mvc.service.IUserService;
import com.youthlin.demo.mvc.support.JsonBody;
import com.youthlin.ioc.annotaion.Controller;
import com.youthlin.ioc.context.Context;
import com.youthlin.mvc.annotation.Method;
import com.youthlin.mvc.annotation.Param;
import com.youthlin.mvc.annotation.ResponseBody;
import com.youthlin.mvc.annotation.URL;
import com.youthlin.mvc.listener.ContextLoaderListener;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
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

    @URL("/sayHello")
    @JsonBody
    public String sayHello(@Param("id") Long id) {
        return userService.sayHello(id);
    }

    @URL("/void")
    @JsonBody
    public void aVoid() {
        LOGGER.debug("aVoid");
    }
}
