package com.youthlin.example.proxy;

import com.youthlin.example.proxy.service.IUserService;
import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

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
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getName().equals("sayHello") && args != null && args.length == 1) {
                            return "Hello, " + args[0];
                        }
                        return null;
                    }
                });
        IUserService userService = (IUserService) Proxy.newProxyInstance(
                IUserService.class.getClassLoader(),
                new Class[]{IUserService.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return "YouthLin";
                    }
                });

        System.out.println("内部类接口 " + service.sayHello("YouthLin"));
        System.out.println("不同包接口 " + userService.getName());

        out(service);
        out(userService);
    }

    private static void out(Object service) throws IOException {
        Class<?> proxyClass = service.getClass();
        String proxyClassName = proxyClass.getName();
        //System.out.println(proxyClassName);
        proxyClassName = proxyClassName.substring(proxyClassName.lastIndexOf(".") + 1);
        //System.out.println(proxyClassName);
        byte[] proxyClassBytes = ProxyGenerator.generateProxyClass(proxyClassName, new Class[]{IHelloService.class});
        String path = Main.class.getResource("/").getPath();
        File file = new File(path, proxyClass.getName().replaceAll("\\.", "/") + ".class");
        if (!file.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        }
        System.out.println(service.getClass().getName() + " " + file.getAbsolutePath());
        FileOutputStream out = new FileOutputStream(file);
        out.write(proxyClassBytes);
        out.flush();
        out.close();
    }
}
