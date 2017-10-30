package com.youthlin.example.rpc.impl.service;

import com.youthlin.example.rpc.api.service.CallBackListener;
import com.youthlin.example.rpc.api.service.CallBackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2017-10-30 23:49
 */
public class CallBackServiceImpl implements CallBackService<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallBackServiceImpl.class);

    @Override public boolean process(String param, CallBackListener<String> callback) {
        try {
            LOGGER.info("开始处理 2s 后完成");
            Thread.sleep(2000);
            LOGGER.info("处理完成 调用回调方法");
            String finished = callback.finished("result");
            LOGGER.info("客户端回调完成 结果{}", finished);
        } catch (InterruptedException ignore) {
        }
        return true;
    }
}
