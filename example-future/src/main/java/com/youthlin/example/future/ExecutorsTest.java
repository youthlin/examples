package com.youthlin.example.future;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-28 20:34
 */
public class ExecutorsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorsTest.class);
    private static final ListeningExecutorService EXECUTOR = MoreExecutors.listeningDecorator(
            new ThreadPoolExecutor(
                    4,
                    10,
                    60,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(10),
                    (r, e) -> {
                        LOGGER.error("Rejected!!");
                        String status = String
                                .format("Thread pool is EXHAUSTED!"
                                                + " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
                                                + " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)",
                                        Thread.currentThread().getName(), e.getPoolSize(),
                                        e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(),
                                        e.getLargestPoolSize(),
                                        e.getTaskCount(), e.getCompletedTaskCount(),
                                        e.isShutdown(), e.isTerminated(), e.isTerminating());
                        throw new RejectedExecutionException(status);
                    }
            ));

    public static void main(String[] args) {
        int n = 100;
        for (int i = 0; i < n; i++) {
            EXECUTOR.submit(() -> {
                Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
            });
        }
    }
}
