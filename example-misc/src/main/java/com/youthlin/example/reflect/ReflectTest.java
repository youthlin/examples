package com.youthlin.example.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author : youthlin.chen @ 2019-11-02 15:19
 */
public class ReflectTest {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1);
        System.out.println("list:============");
        invoke(list);
        MyClass myClass = new MyClass();
        System.out.println("myClass:============");
        invoke(myClass);
        System.out.println("getAClassInner:============");
        invoke(myClass.getAClassInner());
        System.out.println("getAMethodClass:============");
        invoke(myClass.getAMethodClass());
        System.out.println("getALambda:============");
        invoke(myClass.getALambda());
    }

    private static void invoke(Object o) {
        Class<?> clazz = o.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(method);
            if (method.getParameterCount() == 0) {
                try {
                    // 如果该方法是不可见的，直接调用就会失败 https://mp.weixin.qq.com/s/AaJMxfg9cXY4Iz08o2jocg
                    method.setAccessible(true);
                    System.out.println("invoke: " + method.invoke(o));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("----------------");
        }
    }
}
