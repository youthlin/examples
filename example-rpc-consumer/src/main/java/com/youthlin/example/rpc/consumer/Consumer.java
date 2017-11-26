package com.youthlin.example.rpc.consumer;

import com.youthlin.example.rpc.api.IHelloService;
import com.youthlin.example.rpc.bean.User;
import com.youthlin.ioc.context.ClasspathContext;
import com.youthlin.ioc.context.Context;
import com.youthlin.ioc.spi.IPreScanner;
import com.youthlin.rpc.annotation.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ServiceLoader;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-26 22:38
 */
@Resource
public class Consumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    @Rpc
    private IHelloService helloService;

    public static void main(String[] args) {
        ServiceLoader<IPreScanner> preScanners = ServiceLoader.load(IPreScanner.class);
        Context context = new ClasspathContext(preScanners.iterator(), null, "com.youthlin.example");
        Consumer consumer = context.getBean(Consumer.class);
        LOGGER.info("sayHello: {}", consumer.sayHello("World"));
        LOGGER.info("findAll: {}", consumer.helloService.findAll());
        consumer.helloService.save(new User().setId(1L).setName("YouthLin"));
        LOGGER.info("findAll: {}", consumer.helloService.findAll());
    }

    private String sayHello(String name) {
        return helloService.sayHello(name);
    }
}
