package com.youthlin.example.concurrent;

import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Copyright (C) Qunar.com - All Rights Reserved.
 *
 * @author Mingxin Wang
 * @date 2018-05-30
 */
public class MutexQueue<T> extends AbstractQueue<T> implements Queue<T> {
    private static final AtomicReferenceFieldUpdater<Node, Node> QUEUE_TAIL_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");
    private static final AtomicReferenceFieldUpdater<Node, Node> QUEUE_HEAD_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "prev");

    private static final AtomicIntegerFieldUpdater<MutexQueue> COUNT_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(MutexQueue.class, "count");

    private static final class Node<T> {
        private T data;
        volatile Node<T> prev;
        volatile Node<T> next;

        private Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head = new Node<>(null);
    private volatile Node<T> tail = head;
    private volatile int count = 0;
    private int modCount = 0;


    @Override
    public boolean offer(T data) {
        Node<T> current = new Node<>(data);
        for (; ; ) {
            if (QUEUE_TAIL_UPDATER.weakCompareAndSet(tail, null, current)) {
                current.prev = tail;
                tail = current;
                COUNT_UPDATER.incrementAndGet(this);
                modCount++;
                return true;
            }
        }
    }

    @Override
    public T poll() {
        for (; ; ) {
            Node<T> next = head.next;
            if (next == null) {
                return null;
            }
            if (QUEUE_HEAD_UPDATER.weakCompareAndSet(next, head, null)) {
                T data = next.data;
                head = next;
                head.data = null;
                COUNT_UPDATER.decrementAndGet(MutexQueue.this);
                modCount++;
                return data;
            }
        }
    }

    @Override
    public T peek() {
        Node<T> next = head.next;
        if (next == null) {
            return null;
        }
        return next.data;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> current = head;
            int expectModCount = modCount;

            @Override
            public boolean hasNext() {
                checkModCount();
                return current.next != null;
            }

            @Override
            public T next() {
                checkModCount();
                return (current = current.next).data;
            }

            @Override
            public void remove() {
                expectModCount = ++modCount;
                Node<T> prev = current.prev;
                Node<T> next = current.next;
                prev.next = next;
                if (next != null) {
                    next.prev = prev;
                }
                current.data = null;
                COUNT_UPDATER.decrementAndGet(MutexQueue.this);
            }

            private void checkModCount() {
                if (expectModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
            }
        };
    }

}
