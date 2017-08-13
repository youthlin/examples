package com.youthlin.demo.mvc.dao;

import com.youthlin.ioc.annotaion.Dao;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-13 20:56.
 */
@Dao
public class UserDao implements IUserDao {
    @Override public String getUserName(long id) {
        return "user " + id;
    }
}
