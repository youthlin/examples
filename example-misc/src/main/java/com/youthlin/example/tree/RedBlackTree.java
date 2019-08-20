package com.youthlin.example.tree;

import java.util.Comparator;
import java.util.Objects;

/**
 * https://www.jianshu.com/p/e136ec79235c
 * <p>
 * 性质1：每个节点要么是黑色，要么是红色。
 * 性质2：根节点是黑色。
 * 性质3：每个叶子节点（NIL）是黑色。
 * 性质4：每个红色结点的两个子结点一定都是黑色。
 * 性质5：任意一结点到每个叶子结点的路径都包含数量相同的黑结点。
 *
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

    private final Comparator<? super K> comparator;
    private Entry<K, V> root;
    private int size;
    private int modCount;

    public RedBlackTree() {
        comparator = null;
    }

    public RedBlackTree(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    public V put(K key, V value) {
        return value;
    }

    /**
     * 1 从根结点开始查找，把根结点设置为当前结点；
     * 2 若当前结点为空，返回null；
     * 3 若当前结点不为空，用当前结点的key跟查找key作比较；
     * 4 若当前结点key等于查找key，那么该key就是查找目标，返回当前结点；
     * 5 若当前结点key大于查找key，把当前结点的左子结点设置为当前结点，重复步骤2；
     * 6 若当前结点key小于查找key，把当前结点的右子结点设置为当前结点，重复步骤2；
     * <p>
     * 作者：安卓大叔
     * 链接：https://www.jianshu.com/p/e136ec79235c
     * 来源：简书
     * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
     */
    public V get(K key) {
        Entry<K, V> entry = getEntry(key);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }

    private Entry<K, V> getEntry(K key) {
        if (comparator != null) {
            return getEntryUseComparator(key);
        }
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) Objects.requireNonNull(key);
        Entry<K, V> current = root;
        while (current != null) {
            int compare = k.compareTo(current.key);
            if (compare > 0) {
                current = current.right;
            } else if (compare < 0) {
                current = current.left;
            } else {
                return current;
            }
        }
        return null;
    }

    private Entry<K, V> getEntryUseComparator(K key) {
        assert comparator != null;
        Entry<K, V> current = root;
        while (current != null) {
            int compare = comparator.compare(key, current.key);
            if (compare == 0) {
                return current;
            } else if (compare > 0) {
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return null;
    }

}
