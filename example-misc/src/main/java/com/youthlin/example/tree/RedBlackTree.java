package com.youthlin.example.tree;

/**
 * @author : youthlin.chen @ 2019-08-20 21:33
 */
public class RedBlackTree<K, V> {
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> parent;
        Entry<K, V> left;
        Entry<K, V> right;
    }

    private Entry<K, V> root;
    private int size;
    private int modCount;

    public V put(K key, V value) {
        return value;
    }

    public V get(K key) {
        return null;
    }

}
