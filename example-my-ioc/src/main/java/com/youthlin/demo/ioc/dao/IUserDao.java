package com.youthlin.demo.ioc.dao;

import com.youthlin.demo.ioc.po.User;

import java.util.List;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-11 15:15.
 */
public interface IUserDao {
    void save(User user);

    List<User> list();
}
