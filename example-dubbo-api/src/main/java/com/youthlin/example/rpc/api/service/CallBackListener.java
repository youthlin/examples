package com.youthlin.example.rpc.api.service;

/**
 * 创建: youthlin.chen
 * 时间: 2017-10-30 23:48
 */
public interface CallBackListener {
    String finished(String data);

    default void foo() {
        // 客户端调用服务端，传入本接口的实现类作为回调，finished 和 foo 都会在服务端回调客户端时，在客户端执行
        // methodThread[DubboClientHandler-192.168.56.1:20880-thread-1,5,main]
        System.out.println("Foo is default method" + Thread.currentThread());
    }

}
