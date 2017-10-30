package com.youthlin.example.rpc.api.service;

/**
 * 创建: youthlin.chen
 * 时间: 2017-10-30 23:47
 */
public interface CallBackService<T> {
    boolean process(String param, CallBackListener<T> callback);
}
