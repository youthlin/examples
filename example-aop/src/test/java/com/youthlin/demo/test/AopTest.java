package com.youthlin.demo.test;

import com.youthlin.demo.service.IHelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * 创建者： youthlin.chen 日期： 17-3-26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/app.xml" })
public class AopTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AopTest.class);
    @Resource
    private IHelloService helloService;

    @Test(expected = RuntimeException.class)
    public void logTest() {
        LOGGER.debug("{}", helloService.sayHello("Lin"));
    }

}
