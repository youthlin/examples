package com.youthlin.example.proxy;

import com.youthlin.example.proxy.service.IUserService;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//import sun.misc.ProxyGenerator;
//import java.io.File;
//import java.io.FileOutputStream;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-28 11:49
 */
public class Main {
    private interface IHelloService {
        String sayHello(String name);
    }

    public static void main(String[] args) throws IOException {
        IHelloService service = (IHelloService) Proxy.newProxyInstance(
                IHelloService.class.getClassLoader(),
                new Class[]{IHelloService.class},
                (proxy, method, args1) -> {
                    if ("sayHello".equals(method.getName()) && args1 != null && args1.length == 1) {
                        return "Hello, " + args1[0];
                    }
                    return null;
                });
        IUserService userService = (IUserService) Proxy.newProxyInstance(
                IUserService.class.getClassLoader(),
                new Class[]{IUserService.class},
                (proxy, method, args12) -> "YouthLin");

        System.out.println("内部类接口 " + service.sayHello("YouthLin"));
        System.out.println("不同包接口 " + userService.getName());

        writeProxyClass(service.getClass());
        writeProxyClass(userService.getClass());
    }

    private static void writeProxyClass(Class<?> proxyClass) throws IOException {
        /*
        String proxyClassName = proxyClass.getName();
        proxyClassName = proxyClassName.substring(proxyClassName.lastIndexOf(".") + 1);
        byte[] proxyClassBytes = ProxyGenerator.generateProxyClass(proxyClassName, new Class[]{IHelloService.class});
        String path = Main.class.getResource("/").getPath() + "/generate/";
        File file = new File(path, proxyClass.getName().replaceAll("\\.", "/") + ".class");
        if (!file.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        }
        System.out.println(proxyClass.getName() + " " + file.getAbsolutePath());
        FileOutputStream out = new FileOutputStream(file);
        out.write(proxyClassBytes);
        out.flush();
        out.close();
        */
    }

}
