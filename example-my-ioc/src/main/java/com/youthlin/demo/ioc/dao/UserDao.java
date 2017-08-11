package com.youthlin.demo.ioc.dao;

import com.youthlin.demo.ioc.po.User;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-11 15:16.
 */
@Resource
public class UserDao implements IUserDao {
    private Map<Long, User> map = new HashMap<>();

    @Override public void save(User user) {
        map.put(user.getId(), user);
    }

    @Override public List<User> list() {
        List<User> list = new ArrayList<>(map.size());
        list.addAll(map.values());
        return list;
    }
}
