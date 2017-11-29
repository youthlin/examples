package com.youthlin.example.rpc.consumer;

import com.youthlin.rpc.core.ProxyFactory;
import com.youthlin.rpc.core.SimpleProxyFactory;
import com.youthlin.rpc.core.config.AbstractConsumerConfig;
import com.youthlin.rpc.util.NetUtil;

import java.lang.reflect.Method;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-27 11:19
 */
public class Config extends AbstractConsumerConfig {
    @Override
    public String host() {
        String host = System.getProperty("provider.host");
        if (host != null && !host.isEmpty()) {
            return host;
        }
        return NetUtil.getLocalAddress().getHostAddress();
    }

    @Override
    public int port() {
        return NetUtil.DEFAULT_PORT;
    }

    @Override
    public Class<? extends ProxyFactory> proxy() {
        return SimpleProxyFactory.class;
    }

    @Override
    public Boolean async(Method method) {
        if (method.getName().equals("findAll")) {
            return true;
        }
        return super.async(method);
    }

    @Override
    public Boolean getConfig(Method method, String key, boolean dft) {
        if (method.getName().equals("aLongTimeMethod") && key.equals(Config.RETURN)) {
            return false;
        }
        return super.getConfig(method, key, dft);
    }
}
