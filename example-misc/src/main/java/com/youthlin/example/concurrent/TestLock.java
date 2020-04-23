package com.youthlin.example.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author youthlin.chen
 * @date 2020-03-24 10:09
 */
public class TestLock {

    private static final int THREAD_COUNT = 10;
    private static final int LOOP_SIZE = 10000;
    private static int count = 0;

    public static void main(String[] args) {
        testLock(new FakeLock());
        testLock(new ReentrantLock());
        testLock(new ClhLock());
    }

    private static void testLock(Lock lock) {
        count = 0;
        System.out.println("count=" + count + "-----Test lock: " + lock);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            ThreadUtil.getExecutor("Test", 10, 10, 0).execute(() -> {
                incrementWithLock(lock);
                System.out.println(Thread.currentThread().getName() + " end");
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("count=" + count);
    }

    private static void incrementWithLock(Lock lock) {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " started");
            for (int j = 0; j < LOOP_SIZE; j++) {
                ++count;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {
            }
        } finally {
            lock.unlock();
        }
    }

}
