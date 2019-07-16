package com.youthlin.example.rpc.impl;

import com.youthlin.example.rpc.api.service.HelloService;
import com.youthlin.example.rpc.impl.service.HelloServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.io.IOException;

/**
 * @author youthlin.chen
 * @date 2019-07-16 14:31
 */
@Slf4j
public class AppWithoutSpring {

    public static void main(String[] args) throws IOException {
        log.info("start...");
        ServiceConfig<HelloService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(new ApplicationConfig("hello-world"));
        serviceConfig.setRegistry(new RegistryConfig("multicast://224.5.6.7:1234"));
        serviceConfig.setInterface(HelloService.class);
        serviceConfig.setRef(new HelloServiceImpl());
        serviceConfig.export();
        log.info("started...");
        System.in.read();
    }

}
