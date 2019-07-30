package com.youthlin.example.rpc.consumer;

import com.youthlin.example.rpc.api.service.CallBackListener;
import com.youthlin.example.rpc.api.service.CallBackServiceProvider;
import com.youthlin.example.rpc.api.service.HelloService;
import org.apache.dubbo.rpc.RpcContext;
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

        @SuppressWarnings("unchecked")
        CallBackServiceProvider callBackService = context.getBean(CallBackServiceProvider.class);
        Boolean result = callBackService.process("param1", new CallBackListener() {
            @Override
            public String finished(String data) {
                LOGGER.info("客户端回调 结果{}", data);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignore) {
                }
                LOGGER.info("回调结束");
                return data + " finished";
            }
        });
        LOGGER.info("result {}", result);
        try {
            Object o = RpcContext.getContext().getFuture().get();
            LOGGER.info("future result {}", o);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
