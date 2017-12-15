package com.youthlin.demo.ioc;

import com.youthlin.demo.ioc.dao.IUserDao;
import com.youthlin.demo.ioc.service.UserService;
import com.youthlin.ioc.context.ClasspathContext;
import com.youthlin.ioc.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-11 15:14.
 */
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Context context = new ClasspathContext("com.youthlin");
        LOGGER.debug("ioc container bean count: {}\n{}\n{}", context.getBeanCount(), context.getClazzBeanMap(), context.getNameBeanMap());
        UserService userService = context.getBean(UserService.class);
        LOGGER.debug("user list: {}", userService.listUser());
        userService.saveUser(1L, "user 1");
        LOGGER.debug("user list: {}", userService.listUser());
        userService.saveUser(2L, "user 2");
        LOGGER.debug("user list: {}", userService.listUser());
        IUserDao userDao = context.getBean("UserDao", IUserDao.class);
        LOGGER.debug("userDao: {}", userDao);
        LOGGER.debug("{}", context.getBean(""));
    }
}
