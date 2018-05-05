package com.youthlin.example.cglib.bean;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-09 10:26
 */
public class FiledRecorder<T> implements MethodInterceptor {
    private static final String SET = "set";
    private static final String INIT = "init";
    private static final String CHANGED = "changed";
    private Map<String, String> fieldStatusMap = new HashMap<>();
    private Map<String, Object> fieldValueMap = new HashMap<>();

    public FiledRecorder(Class<T> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = field.getName();
            fieldStatusMap.put(fieldName, INIT);
            fieldValueMap.put(fieldName, null);
        }
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();
        if (methodName.length() > 3 && methodName.startsWith(SET) && objects != null && objects.length == 1) {
            String filedName = methodName.substring(SET.length());
            filedName = filedName.substring(0, 1).toLowerCase() + filedName.substring(1);
            Object newValue = objects[0];
            if (!Objects.equals(newValue, fieldValueMap.get(filedName))) {
                fieldValueMap.put(filedName, newValue);
                fieldStatusMap.put(filedName, CHANGED);
            }
        }
        return methodProxy.invokeSuper(o, objects);
    }

    public Map<String, String> getFieldStatusMap() {
        return fieldStatusMap;
    }

    public Map<String, Object> getFieldValueMap() {
        return fieldValueMap;
    }
}
