package com.youthlin.example.bplus;

/**
 * @author youthlin.chen
 * @date 2019-03-15 20:33
 */
public interface IBplusTree<K, V> {
    void put(K key, V value);

    V get(K key);

    V remove(K key);

}
