package com.youthlin.example.rpc.impl.service;

import com.youthlin.example.rpc.api.service.HelloService;

/**
 * 创建: youthlin.chen
 * 时间: 2017-10-28 00:09
 */
public class HelloServiceImpl implements HelloService {
    @Override public String sayHello(String name) {
        return "Hello, " + name;
    }
}
