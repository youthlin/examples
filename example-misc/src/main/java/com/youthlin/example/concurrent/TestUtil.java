package com.youthlin.example.concurrent;

import com.google.common.collect.Maps;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author youthlin.chen
 * @date 2020-03-24 10:46
 */
public class TestUtil {
    private static final ConcurrentMap<String, ThreadPoolExecutor> MAP = Maps.newConcurrentMap();
    private static final AtomicInteger NEXT_INT = new AtomicInteger();

    public static ThreadPoolExecutor getExecutor(String name, int core, int max, int qSize) {
        ThreadPoolExecutor executor = MAP.computeIfAbsent(name, n ->
                new ThreadPoolExecutor(core, max, 60, TimeUnit.SECONDS,
                        qSize > 0
                                ? new LimitSizeBlockingQueue<>(new LinkedBlockingQueue<>(), qSize)
                                : new SynchronousQueue<>(),
                        r -> new Thread(r, name + "-Thread-" + NEXT_INT.getAndIncrement())) {{
                    allowCoreThreadTimeOut(true);
                }}
        );
        executor.setCorePoolSize(core);
        executor.setMaximumPoolSize(max);
        BlockingQueue<Runnable> queue = executor.getQueue();
        if (queue instanceof LimitSizeBlockingQueue) {
            ((LimitSizeBlockingQueue<Runnable>) queue).setMaxSize(qSize);
            if (qSize > 0 && ((LimitSizeBlockingQueue<Runnable>) queue).getDelegate() instanceof SynchronousQueue) {
                ((LimitSizeBlockingQueue<Runnable>) queue).setDelegate(new LinkedBlockingQueue<>());
            }
        }
        return executor;
    }

    public static void dump() {
        ThreadInfo[] threadInfos = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        Arrays.stream(threadInfos)
                .forEach(System.out::println);
    }

}
