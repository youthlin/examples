package com.youthlin.example.rpc.provider;

import com.youthlin.ioc.context.ClasspathContext;
import com.youthlin.ioc.context.Context;
import com.youthlin.ioc.spi.IPostScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-26 22:25
 */
public class Provider {
    private static final Logger LOGGER = LoggerFactory.getLogger(Provider.class);

    public static void main(String[] args) throws IOException {
        ServiceLoader<IPostScanner> postScanners = ServiceLoader.load(IPostScanner.class);
        Context context = new ClasspathContext(null, postScanners.iterator(), "com.youthlin.example");
        Map<Class, Object> beanMap = context.getClazzBeanMap();
        LOGGER.info("{}", beanMap);
        LOGGER.info("press any key and enter to exit.");
        System.in.read();
        System.exit(0);
    }
}
