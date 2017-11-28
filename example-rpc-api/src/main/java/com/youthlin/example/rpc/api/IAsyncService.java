package com.youthlin.example.rpc.api;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-28 15:33
 */
public interface IAsyncService<T> {
    void async(CallBack<T> callBack);

}
