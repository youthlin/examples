package com.youthlin.example.rpc.consumer;

import com.youthlin.rpc.core.ProxyFactory;
import com.youthlin.rpc.core.SimpleProxyFactory;
import com.youthlin.rpc.core.config.AbstractConsumerConfig;
import com.youthlin.rpc.util.NetUtil;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-27 11:19
 */
public class Config extends AbstractConsumerConfig {
    @Override
    public String host() {
        return "100.81.140.61";
    }

    @Override
    public int port() {
        return NetUtil.DEFAULT_PORT;
    }

    @Override
    public Class<? extends ProxyFactory> proxy() {
        return SimpleProxyFactory.class;
    }
}
