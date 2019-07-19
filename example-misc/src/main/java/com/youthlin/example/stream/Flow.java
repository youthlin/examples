package com.youthlin.example.stream;

import com.youthlin.example.stream.impl.BaseFlow;
import com.youthlin.example.stream.impl.VisitorImpl;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author youthlin.chen
 * @date 2019-07-18 20:39
 */
public interface Flow<T> {

    Flow<T> filter(Predicate<? super T> predicate);

    void forEach(Consumer<? super T> action);

    static <E> Flow<E> of(Collection<E> collection) {
        return new BaseFlow.Head<>(VisitorImpl.of(collection));
    }

}
