package com.youthlin.example.stream;

import java.util.Iterator;

/**
 * 自定义迭代器
 *
 * @author youthlin.chen
 * @date 2019-07-18 20:32
 */
public interface Visitor<T> extends Iterator<T> {
    long UNKNOWN_SIZE = -1;

    /**
     * 获取元素个数
     *
     * @return 获取元素个数 如果个数未知 返回{@link #UNKNOWN_SIZE}
     */
    default long getSizeIfKnown() {
        return UNKNOWN_SIZE;
    }

}
