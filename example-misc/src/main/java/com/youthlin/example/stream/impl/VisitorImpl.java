package com.youthlin.example.stream.impl;

import com.youthlin.example.stream.Visitor;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author youthlin.chen
 * @date 2019-07-18 20:36
 */
public class VisitorImpl<T> implements Visitor<T> {
    private Iterator<T> iterator;

    public VisitorImpl(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }

    public static <E> VisitorImpl<E> of(Collection<E> collection) {
        return new VisitorImpl<E>(collection.iterator()) {
            @Override
            public long getSizeIfKnown() {
                return collection.size();
            }
        };
    }

}
