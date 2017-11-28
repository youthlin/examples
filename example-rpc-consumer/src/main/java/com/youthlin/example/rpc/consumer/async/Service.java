package com.youthlin.example.rpc.consumer.async;

import com.youthlin.example.rpc.api.CallBack;
import com.youthlin.example.rpc.api.IAsyncService;
import com.youthlin.rpc.annotation.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-28 16:09
 */
@Resource
public class Service implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    @Rpc(config = AsyncConfig.class)
    private IAsyncService<String> asyncService;

    public void test() {
        asyncService.async(new CallBack<String>() {
            @Override
            public void onDone(String result) {
                LOGGER.debug("done {}", result);
            }

            @Override
            public void onException(Throwable e) {
                LOGGER.debug("exception {}", e);
            }
        });
    }
}
