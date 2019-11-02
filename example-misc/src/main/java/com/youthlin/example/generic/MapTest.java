package com.youthlin.example.generic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : youthlin.chen @ 2019-11-02 15:00
 */
public class MapTest {
    public static void main(String[] args) {
        Map<List<? extends Number>, String> listStringMap = new HashMap<>();
        listStringMap.put(Collections.singletonList(1), "1");
        listStringMap.put(Collections.singletonList(2), "2");
        List<Long> list2L = Collections.singletonList(2L);
        listStringMap.put(list2L, "2L");
        System.out.println("1: " + listStringMap.get(Collections.singletonList(1)));
        System.out.println("2: " + listStringMap.get(Collections.singletonList(2)));
        System.out.println("2L: " + listStringMap.get(list2L));
        MyMap<List<? extends Number>, String> myMap = new MyMap<>();
        myMap.put(Collections.singletonList(1), "1");
        myMap.put(Collections.singletonList(2), "2");
        myMap.put(list2L, "2L");
        System.out.println("1: " + myMap.get(Collections.singletonList(1)));
        System.out.println("2: " + myMap.get(Collections.singletonList(2)));
        System.out.println("2L: " + myMap.get(list2L));
        // MyMap<List<Number>, String> myMap2 = new MyMap<>();
        // Error:(30, 49) java: 不兼容的类型: java.util.List<java.lang.Long>无法转换为java.util.List<java.lang.Number>
        // System.out.println("2L: " + myMap2.get(list2L));

    }

}
