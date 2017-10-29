package com.youthlin.example.rpc.consumer;

import com.youthlin.example.rpc.api.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 创建: youthlin.chen
 * 时间: 2017-10-28 00:34
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:app.xml");
        context.start();
        HelloService helloService = context.getBean(HelloService.class);
        String hello = helloService.sayHello("Lin");
        LOGGER.info("---------------------------------------\n");
        LOGGER.info("result = {}", hello);
        LOGGER.info("---------------------------------------\n");
    }
}
