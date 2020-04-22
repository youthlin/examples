package com.youthlin.example.rpc.impl;

import com.youthlin.example.rpc.api.service.CallBackListener;
import com.youthlin.example.rpc.api.service.CallBackServiceProvider;
import com.youthlin.example.rpc.api.service.HelloService;
import com.youthlin.example.rpc.impl.service.CallBackServiceImpl;
import com.youthlin.example.rpc.impl.service.HelloServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ArgumentConfig;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.io.IOException;
import java.util.Collections;

/**
 * @author youthlin.chen
 * @date 2019-07-16 14:31
 */
@Slf4j
public class AppWithoutSpring {

    public static void main(String[] args) throws IOException {
        log.info("start...");
        final ApplicationConfig application = new ApplicationConfig("hello-world");
        final RegistryConfig registry = new RegistryConfig("zookeeper://localhost:2181");
        ServiceConfig<HelloService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(application);
        serviceConfig.setRegistry(registry);
        serviceConfig.setInterface(HelloService.class);
        serviceConfig.setRef(new HelloServiceImpl());
        serviceConfig.setGroup("hello");
        serviceConfig.setVersion("0.0.1");
        serviceConfig.export();


        ArgumentConfig arg = new ArgumentConfig();
        arg.setCallback(true);
        arg.setType(CallBackListener.class.getName());
        MethodConfig methodConfig = new MethodConfig();
        methodConfig.setArguments(Collections.singletonList(arg));
        methodConfig.setName("process");
        methodConfig.setAsync(true);
        methodConfig.setReturn(false);
        methodConfig.setTimeout(5000);

        ServiceConfig<CallBackServiceProvider> config = new ServiceConfig<>();
        config.setApplication(application);
        config.setRegistry(registry);
        config.setInterface(CallBackServiceProvider.class);
        config.setRef(new CallBackServiceImpl());
        config.setMethods(Collections.singletonList(methodConfig));
        config.export();

        log.info("started...");
        System.in.read();
    }

}
