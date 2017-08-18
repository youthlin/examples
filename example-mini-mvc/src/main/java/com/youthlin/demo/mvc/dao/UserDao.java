package com.youthlin.demo.mvc.dao;

import com.youthlin.demo.mvc.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-13 20:56.
 */
//@Dao
public class UserDao implements IUserDao {
    private Map<Long, User> userMap = new HashMap<>();

    @Override public String getUserName(long id) {
        User user = userMap.get(id);
        if (user != null) {
            return user.getName();
        }
        return "user " + id;
    }

    @Override public void save(User user) {
        userMap.put(user.getId(), user);
    }

    @Override public User findById(long id) {
        return userMap.get(id);
    }

    @Override public List<User> findByName(String name) {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Long, User> userEntry : userMap.entrySet()) {
            if (userEntry.getValue().getName().equals(name)) {
                userList.add(userEntry.getValue());
            }
        }
        return userList;
    }

    @Override public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        userList.addAll(userMap.values());
        return userList;
    }
}
