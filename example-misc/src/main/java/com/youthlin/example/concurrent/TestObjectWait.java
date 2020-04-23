package com.youthlin.example.concurrent;

import com.google.common.collect.Lists;

import java.util.Queue;

/**
 * @author youthlin.chen
 * @date 2020-03-26 14:40
 */
public class TestObjectWait {
    private static final Queue<Integer> QUEUE = Lists.newLinkedList();
    private static final Object LOCK = new Object();
    private static final int MAX = 10;

    public static void main(String[] args) {
        ThreadUtil.newThread(() -> {
            Integer n;
            while (true) {
                synchronized (LOCK) {
                    while ((n = QUEUE.poll()) != null) {
                        System.out.println(n);
                    }
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "producer").start();
        ThreadUtil.newThread(() -> {
            int i = 0;
            synchronized (LOCK) {
                QUEUE.offer(++i);
            }
        }, "consumer").start();
    }
}
