package com.youthlin.example.rpc.consumer.async;

import com.youthlin.ioc.context.ClasspathContext;
import com.youthlin.ioc.context.Context;
import com.youthlin.ioc.spi.IPreScanner;

import java.util.ServiceLoader;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-28 15:39
 */
public class ConsumerAsync {
    public static void main(String[] args) {
        ServiceLoader<IPreScanner> preScanners = ServiceLoader.load(IPreScanner.class);
        Context context = new ClasspathContext(preScanners.iterator(), null, "com.youthlin.example");
        Service service = context.getBean(Service.class);
        service.test();
    }


}
