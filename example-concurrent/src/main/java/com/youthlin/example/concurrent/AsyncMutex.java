package com.youthlin.example.concurrent;


import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright (C) Qunar.com - All Rights Reserved.
 *
 * @author Mingxin Wang
 * @date 2018-04-17
 */
public final class AsyncMutex {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncMutex.class);
    private static final FutureCallback<Object> DEFAULT_CALLBACK = new FutureCallback<Object>() {
        @Override
        public void onSuccess(Object result) {
        }

        @Override
        public void onFailure(Throwable t) {
            LOGGER.error("Unexpected exception was caught while executing task.", t);
        }
    };

    private final Executor executor;
    private final MutexQueue<Invocation> queue;
    private final AtomicInteger pendingCount;

    private AsyncMutex(Executor executor) {
        this.executor = executor;
        this.queue = new MutexQueue<>();
        this.pendingCount = new AtomicInteger();
    }

    private static final class Invocation<T> {
        private final Callable<T> callable;
        private final FutureCallback<? super T> callback;

        private Invocation(Callable<T> callable, FutureCallback<? super T> callback) {
            this.callable = callable;
            this.callback = callback;
        }

        private void invoke(AsyncMutex mutex) {
            try {
                mutex.executor.execute(() -> {
                    T result = null;
                    Throwable throwable = null;
                    try {
                        result = callable.call();
                    } catch (Throwable t) {
                        throwable = t;
                    }
                    mutex.release();
                    try {
                        if (throwable == null) {
                            callback.onSuccess(result);
                        } else {
                            callback.onFailure(throwable);
                        }
                    } catch (Throwable t) {
                        LOGGER.error("Unexpected exception was caught while executing callback, with result={}, callback={}", result, callback, t);
                    }
                });
            } catch (Throwable t) {
                mutex.release();
                LOGGER.error("Unexpected exception was caught while submitting the task to the executor, with executor={}", mutex.executor, t);
            }
        }
    }


    public static AsyncMutex on(Executor executor) {
        Preconditions.checkNotNull(executor);
        return new AsyncMutex(executor);
    }

    public <T> void attach(Callable<T> callable) {
        attach(callable, DEFAULT_CALLBACK);
    }

    public <T> void attach(Callable<T> callable, FutureCallback<? super T> callback) {
        Preconditions.checkNotNull(callable);
        Preconditions.checkNotNull(callback);
        queue.offer(new Invocation<>(callable, callback));
        if (pendingCount.getAndIncrement() == 0) {
            queue.remove().invoke(this);
        }
    }

    private void release() {
        if (pendingCount.decrementAndGet() != 0) {
            queue.remove().invoke(this);
        }
    }

}
