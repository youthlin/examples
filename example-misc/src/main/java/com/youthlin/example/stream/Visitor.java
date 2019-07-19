package com.youthlin.example.stream;

import java.util.Iterator;

/**
 * @author youthlin.chen
 * @date 2019-07-18 20:32
 */
public interface Visitor<T> extends Iterator<T> {

    default long getSizeIfKnown() {
        return -1;
    }

}
