package com.youthlin.demo.ioc.service;

import com.youthlin.demo.ioc.dao.IUserDao;
import com.youthlin.demo.ioc.po.User;

import javax.annotation.Resource;
import java.util.List;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-11 15:16.
 */
@Resource
public class UserService {
    @Resource
    private IUserDao userDao;

    public void saveUser(long id, String name) {
        userDao.save(new User().setId(id).setName(name));
    }

    public List<User> listUser() {
        return userDao.list();
    }
}
