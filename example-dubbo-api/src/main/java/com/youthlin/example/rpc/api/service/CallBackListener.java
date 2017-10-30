package com.youthlin.example.rpc.api.service;

/**
 * 创建: youthlin.chen
 * 时间: 2017-10-30 23:48
 */
public interface CallBackListener<T> {
    T finished(T data);
}
