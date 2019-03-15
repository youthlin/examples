package com.youthlin.example.bplus;

import java.util.Comparator;
import java.util.function.Function;

/**
 * @author youthlin.chen
 * @date 2019-03-15 20:30
 */
public interface BPlusData<K extends Comparable<K>> {
    Comparator<BPlusData> COMPARATOR = Comparator.comparing((Function<BPlusData, Comparable>) BPlusData::getKey);

    K getKey();

    static <T extends Comparable<T>> BPlusData<T> of(T data) {
        return new BPlusData<T>() {
            @Override
            public T getKey() {
                return data;
            }

            @Override
            public String toString() {
                return data.toString();
            }
        };
    }
}

