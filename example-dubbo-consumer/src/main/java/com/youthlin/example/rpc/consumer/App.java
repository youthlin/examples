package com.youthlin.example.rpc.consumer;

import com.youthlin.example.rpc.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

/**
 * @author youthlin.chen
 * @date 2019-07-16 14:42
 */
@Slf4j
public class App {

    public static void main(String[] args) {
        log.info("consumer start...");
        ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(new ApplicationConfig("hello-world-consumer"));
        referenceConfig.setRegistry(new RegistryConfig("multicast://224.5.6.7:1234?unicast=false"));
        referenceConfig.setInterface(HelloService.class);
        HelloService helloService = referenceConfig.get();
        log.info("service: " + helloService);
        System.out.println(helloService.sayHello("Lin"));
    }

}
