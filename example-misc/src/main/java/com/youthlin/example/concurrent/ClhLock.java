package com.youthlin.example.concurrent;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author youthlin.chen
 * @link https://coderbee.net/?p=577
 * @link https://russxia.com/2018/06/01/%E5%9F%BA%E4%BA%8E%E8%87%AA%E6%97%8B%E7%9A%84CLH%E9%94%81/
 * @date 2020-03-24 10:52
 */
public class ClhLock implements Lock {
    @Data
    private static class Node {
        private volatile boolean lockOrWait = true;
        // 打印 debug msg 看 可以没有这个字段
        private final Thread thread = Thread.currentThread();
    }

    /**
     * 原子地设置这个字段 getAndSet 会返回原先的值 形成隐式的链
     */
    private AtomicReference<Node> tail = new AtomicReference<>();
    /**
     * unlock 需要
     */
    private ThreadLocal<Node> currentThreadNode = ThreadLocal.withInitial(Node::new);

    @Override
    public void lock() {
        Node node = currentThreadNode.get();
        Node pre = tail.getAndSet(node);
        if (pre != null) {
            debug("等待" + pre.thread.getName() + "释放锁");
            //noinspection StatementWithEmptyBody
            while (pre.lockOrWait) {
            }
        }
        debug("获取到锁");
    }

    @Override
    public void unlock() {
        Node node = currentThreadNode.get();
        node.lockOrWait = false;
        debug("释放锁");
        tail.compareAndSet(node, null);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    private static void debug(String msg) {
        System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + "> " + msg);
    }
}
