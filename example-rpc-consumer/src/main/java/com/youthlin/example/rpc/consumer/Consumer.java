package com.youthlin.example.rpc.consumer;

import com.youthlin.example.rpc.api.IHelloService;
import com.youthlin.example.rpc.bean.User;
import com.youthlin.ioc.context.ClasspathContext;
import com.youthlin.ioc.context.Context;
import com.youthlin.rpc.annotation.Rpc;
import com.youthlin.rpc.core.RpcFuture;
import com.youthlin.rpc.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.ProxyGenerator;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
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
        Context context = new ClasspathContext( "com.youthlin.example");
        final Consumer consumer = context.getBean(Consumer.class);
        LOGGER.info("started");
        LOGGER.info("sayHello: {}", consumer.sayHello("World"));
        LOGGER.info("compare: {}", consumer.helloService.compareTo(0));
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
        LOGGER.info("{}", System.nanoTime() - start);
        LOGGER.info("{}", consumer.helloService.toString());
        LOGGER.info("{}", consumer.helloService.getClass());
        LOGGER.info("{}", consumer.helloService.equals(consumer.helloService));

        consumer.printClassFile();
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

    private void printClassFile() {
        byte[] classFile = ProxyGenerator.generateProxyClass("$Proxy2", helloService.getClass().getInterfaces());
        FileOutputStream out = null;
        try {
            String path = Consumer.class.getResource("/").getPath();
            LOGGER.info("path={}", path);

            File file = new File(path, "com/sun/proxy/$Proxy2.class");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            LOGGER.info("path: {}", file.getAbsolutePath());
            out = new FileOutputStream(file);
            out.write(classFile);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            NetUtil.close(out);
        }
    }
}
