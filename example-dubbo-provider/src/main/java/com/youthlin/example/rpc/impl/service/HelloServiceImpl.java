package com.youthlin.example.rpc.impl.service;

import com.youthlin.example.rpc.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;

/**
 * 创建: youthlin.chen
 * 时间: 2017-10-28 00:09
 */
@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        log.info("say hello. name={}, {}", name, RpcContext.getContext().getAttachments());
        return "Hello, " + name;
    }
}
