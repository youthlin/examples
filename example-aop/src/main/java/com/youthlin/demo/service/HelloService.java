package com.youthlin.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 创建者： youthlin.chen 日期： 17-3-26.
 */
@Service
public class HelloService implements IHelloService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloService.class);

    @SuppressWarnings("SameParameterValue")
    public String sayHello(String name) {
        LOGGER.debug("in say hello method");
        return "Hello, " + name;
    }

    public void voidFun() {
        LOGGER.debug("void method.");
        throw new RuntimeException("");
    }
}
