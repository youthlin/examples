package com.youthlin.example.generic;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : youthlin.chen @ 2019-11-02 15:01
 */
public class MyMap<K, V> {
    private Map<K, V> map = new HashMap<>();

    public V get(K key) {
        return map.get(key);
    }

    public void put(K key, V value) {
        map.put(key, value);
    }
}
