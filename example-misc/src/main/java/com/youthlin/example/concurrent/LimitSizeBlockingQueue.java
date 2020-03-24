package com.youthlin.example.concurrent;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author youthlin.chen
 * @date 2020-03-24 15:01
 */
@Data
public class LimitSizeBlockingQueue<T> implements BlockingQueue<T> {
    private BlockingQueue<T> delegate;
    private volatile long maxSize;

    public LimitSizeBlockingQueue(BlockingQueue<T> delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    public LimitSizeBlockingQueue(BlockingQueue<T> delegate, long maxSize) {
        Preconditions.checkNotNull(delegate);
        Preconditions.checkArgument(maxSize > 0);
        this.delegate = delegate;
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(@NotNull T t) {
        if (delegate.size() >= maxSize) {
            throw new IllegalStateException();
        }
        return delegate.add(t);
    }

    @Override
    public boolean offer(@NotNull T t) {
        if (delegate.size() >= maxSize) {
            return false;
        }
        return delegate.offer(t);
    }

    @Override
    public T remove() {
        return delegate.remove();
    }

    @Override
    public T poll() {
        return delegate.poll();
    }

    @Override
    public T element() {
        return delegate.element();
    }

    @Override
    public T peek() {
        return delegate.peek();
    }

    @Override
    public void put(@NotNull T t) throws InterruptedException {
        while (delegate.size() >= maxSize) {
            Thread.sleep(100);
        }
        delegate.put(t);
    }

    @Override
    public boolean offer(T t, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        if (delegate.size() < maxSize) {
            return delegate.offer(t, timeout, unit);
        }
        long end = System.currentTimeMillis() + unit.toMillis(timeout);
        while (delegate.size() >= maxSize && System.currentTimeMillis() < end) {
            Thread.sleep(100);
        }
        if (System.currentTimeMillis() < end) {
            return delegate.offer(t, end - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
        return false;
    }

    @NotNull
    @Override
    public T take() throws InterruptedException {
        return delegate.take();
    }

    @Nullable
    @Override
    public T poll(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return delegate.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return (int) Math.min(maxSize - delegate.size(), 0);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        if (delegate.size() + c.size() > maxSize) {
            throw new IllegalStateException();
        }
        return delegate.addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return delegate.toArray(a);
    }

    @Override
    public int drainTo(@NotNull Collection<? super T> c) {
        return delegate.drainTo(c);
    }

    @Override
    public int drainTo(@NotNull Collection<? super T> c, int maxElements) {
        return delegate.drainTo(c, maxElements);
    }
}
