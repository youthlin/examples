package com.youthlin.example.rpc.api;

import java.io.Serializable;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-28 15:33
 */
public interface CallBack<T> extends Serializable {
    void onDone(T result);

    void onException(Throwable e);
}
