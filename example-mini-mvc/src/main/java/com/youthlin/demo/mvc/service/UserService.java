package com.youthlin.demo.mvc.service;

import com.youthlin.demo.mvc.dao.IUserDao;
import com.youthlin.demo.mvc.model.User;
import com.youthlin.ioc.annotaion.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-13 13:39.
 */
@Service
public class UserService {
    @Resource
    private IUserDao userDao;

    public User findById(long id) {
        return userDao.findById(id);
    }

    public List<User> listUsers() {
        return userDao.findAll();
    }

    public void saveUser(User user) {
        userDao.save(user);
    }

    public void editUser(User user) {
        userDao.edit(user);
    }

    public boolean deleteById(long id) {
        return userDao.deleteById(id) == 1;
    }

}
