package com.youthlin.example.rpc.api;

import com.youthlin.example.rpc.bean.User;

import java.util.List;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-26 22:20
 */
public interface IHelloService {
    String sayHello(String name);

    void save(User user);

    List<User> findAll();

    void clear();

    void aLongTimeMethod();
}
