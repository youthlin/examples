package com.youthlin.example.stream;

import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * @author youthlin.chen
 * @date 2019-07-19 09:58
 */
public class StreamTest {
    public static void main(String[] args) {
        ArrayList<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        Flow.of(list)
                .filter(x -> x % 2 == 0)
                .forEach(System.out::println);
    }
}
