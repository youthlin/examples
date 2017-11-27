package com.youthlin.example.rpc.consumer;

import com.youthlin.example.rpc.api.IHelloService;
import com.youthlin.example.rpc.bean.User;
import com.youthlin.ioc.context.ClasspathContext;
import com.youthlin.ioc.context.Context;
import com.youthlin.ioc.spi.IPreScanner;
import com.youthlin.rpc.annotation.Rpc;
import com.youthlin.rpc.core.RpcFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-26 22:38
 */
@Resource
public class Consumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    @Rpc(config = Config.class)
    private IHelloService helloService;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ServiceLoader<IPreScanner> preScanners = ServiceLoader.load(IPreScanner.class);
        Context context = new ClasspathContext(preScanners.iterator(), null, "com.youthlin.example");
        final Consumer consumer = context.getBean(Consumer.class);
        LOGGER.info("sayHello: {}", consumer.sayHello("World"));
        LOGGER.info("sayHello: {}", consumer.sayHello("你好"));
        consumer.helloService.clear();
        List<User> userList = consumer.helloService.findAll();
        LOGGER.info("findAll: {}", userList);
        Future<List<User>> future = RpcFuture.get();
        try {
            LOGGER.info("{}", future.get());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warn("", e);
        }
        long start = System.nanoTime();
        LOGGER.info("start {}", start);
        consumer.helloService.aLongTimeMethod();
        System.out.println(System.nanoTime() - start);
        System.out.println(consumer.helloService.toString());
        System.out.println(consumer.helloService.getClass());
        System.out.println(consumer.helloService.equals(consumer.helloService));
    }

    private String sayHello(String name) {
        return helloService.sayHello(name);
    }

    private void test() {
        for (int i = 0; i < 100; i++) {
            final int count = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignore) {
                    }

                    long start = System.currentTimeMillis();
                    helloService.save(new User().setId((long) count).setName("Name" + count));
                    helloService.findAll();
                    Future<List<User>> future = RpcFuture.get();
                    try {
                        List<User> users = future.get(30 * count, TimeUnit.MILLISECONDS);
                        LOGGER.info("No.{} size:{} cost:{} {}", count, users.size(), System.currentTimeMillis() - start, users);
                    } catch (InterruptedException e) {
                        LOGGER.warn("中断异常", e);
                    } catch (ExecutionException e) {
                        LOGGER.warn("调用异常", e);
                    } catch (TimeoutException e) {
                        LOGGER.warn("超时异常", e);

                    }

                }
            }).start();
        }
    }

}
