package com.youthlin.example.concurrent;

import java.util.concurrent.locks.LockSupport;

/**
 * @author youthlin.chen
 * @date 2020-03-23 15:33
 */
public class TestLockSupport {
    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "将被阻塞");
            LockSupport.park(TestLockSupport.class);
/*
"Thread-0" prio=5 Id=14 WAITING on java.lang.Class@5b6f7412
	at java.base@13/jdk.internal.misc.Unsafe.park(Native Method)
	-  waiting on java.lang.Class@5b6f7412
	at java.base@13/java.util.concurrent.locks.LockSupport.park(LockSupport.java:194)
	at app//com.youthlin.example.concurrent.LockSupportTest.lambda$main$0(LockSupportTest.java:17)
	at app//com.youthlin.example.concurrent.LockSupportTest$$Lambda$14/0x0000000800ba4840.run(Unknown Source)
	at java.base@13/java.lang.Thread.run(Thread.java:830)
*/
            System.out.println(Thread.currentThread().getName() + "继续执行");
        });
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ThreadUtil.dump();

        LockSupport.unpark(thread);

    }
}
