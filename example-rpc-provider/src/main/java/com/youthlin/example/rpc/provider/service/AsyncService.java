package com.youthlin.example.rpc.provider.service;

import com.youthlin.example.rpc.api.CallBack;
import com.youthlin.example.rpc.api.IAsyncService;
import com.youthlin.rpc.annotation.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-28 15:34
 */
@Rpc
public class AsyncService implements IAsyncService<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncService.class);

    @Override
    public void async(final CallBack<String> callBack) {
        LOGGER.info("new async request.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("start async, waiting 1s...");
                    Thread.sleep(1000);
                    LOGGER.info("async task done.");
                    callBack.onDone("Yes");
                } catch (Throwable t) {
                    LOGGER.info("async task exception.");
                    callBack.onException(t);
                } finally {
                    LOGGER.info("finally");
                }
            }
        }).start();
    }
}
