package com.youthlin.demo.mvc.dao;

import com.youthlin.demo.mvc.model.User;
import com.youthlin.ioc.annotaion.Dao;

import java.util.List;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-13 20:55.
 */
@Dao
public interface IUserDao {
    String getUserName(long id);

    void save(User user);

    User findById(long id);

    List<User> findByName(String name);

    List<User> findAll();
}
