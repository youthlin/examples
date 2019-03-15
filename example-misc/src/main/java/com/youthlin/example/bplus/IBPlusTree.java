package com.youthlin.example.bplus;

/**
 * @author youthlin.chen
 * @date 2019-03-15 20:33
 */
public interface IBPlusTree<K extends Comparable<K>, T extends BPlusData<K>> {
    void insert(T data);

    T find(K key);

    T delete(K key);

}
