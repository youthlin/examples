package com.youthlin.example.tree;

import java.util.Comparator;
import java.util.Map;
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
    private static class Entry<K, V> implements Map.Entry<K, V>, BinTreeNode<Entry<K, V>, Entry<K, V>> {
        private static final boolean RED = true;
        private static final boolean BLACK = true;
        K key;
        V value;
        Entry<K, V> parent;
        Entry<K, V> left;
        Entry<K, V> right;
        boolean color = RED;
        int offset;

        Entry(K k, V v) {
            key = k;
            value = v;
        }

        Entry(K k, V v, Entry<K, V> parent) {
            this(k, v);
            this.parent = parent;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = value;
            this.value = value;
            return oldValue;
        }

        @Override
        public Entry<K, V> getData() {
            return this;
        }

        @Override
        public Entry<K, V> getLeft() {
            return left;
        }

        @Override
        public Entry<K, V> getRight() {
            return right;
        }

        @Override
        public void setOffset(int offset) {
            this.offset = offset;
        }

        @Override
        public int getOffset() {
            return offset;
        }

        @Override public String toString() {
            return key + "=" + value;
        }
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
        Entry<K, V> current = root;
        while (current != null) {
            int compare = compare(current.key, key);
            if (compare > 0) {
                current = current.left;
            } else if (compare < 0) {
                current = current.right;
            } else {
                return current;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private int compare(K left, K right) {
        if (comparator != null) {
            return comparator.compare(left, right);
        }
        return ((Comparable<? super K>) Objects.requireNonNull(left)).compareTo(Objects.requireNonNull(right));
    }

    /**
     * 红黑树插入
     * 插入操作包括两部分工作：一查找插入的位置；二插入后自平衡。查找插入的父结点很简单，跟查找操作区别不大：
     * <p>
     * 1 从根结点开始查找；
     * 2 若根结点为空，那么插入结点作为根结点，结束。
     * 3 若根结点不为空，那么把根结点作为当前结点；
     * 4 若当前结点为null，返回当前结点的父结点，结束。
     * 5 若当前结点key等于查找key，那么该key所在结点就是插入结点，更新结点的值，结束。
     * 6 若当前结点key大于查找key，把当前结点的左子结点设置为当前结点，重复步骤4；
     * 7 若当前结点key小于查找key，把当前结点的右子结点设置为当前结点，重复步骤4；
     * <p>
     * 作者：安卓大叔
     * 链接：https://www.jianshu.com/p/e136ec79235c
     * 来源：简书
     * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
     */
    public V put(K key, V value) {
        //插入情景1：红黑树为空树
        if (root == null) {
            Entry<K, V> entry = new Entry<>(key, value);
            entry.color = Entry.BLACK;
            root = entry;
            size = 1;
            modCount++;
            return null;
        }

        Entry<K, V> parent = root;
        Entry<K, V> current = root;
        int compare = 0;
        while (current != null) {
            parent = current;
            compare = compare(current.key, key);
            if (compare > 0) {
                current = current.left;
            } else if (compare < 0) {
                current = current.right;
            } else {
                //插入情景2：插入结点的Key已存在
                return current.setValue(value);
            }
        }
        current = new Entry<>(key, value, parent);
        if (compare > 0) {
            parent.left = current;
        } else {
            parent.right = current;
        }

        //插入情景3：插入结点的父结点为黑结点
        if (parent.color == Entry.BLACK) {
            size++;
            modCount++;
            return null;
        }
        Entry<K, V> uncle = getUncle(current);
        if (uncle != null && uncle.color == Entry.RED) {
            //插入情景4：插入结点的父结点为红结点            \
            //插入情景4.1：叔叔结点存在并且为红结点           \__ 祖父是黑色
            // * 性质4：每个红色结点的两个子结点一定都是黑色。 /
            //   黑              红(current)          黑           红(current)
            //  红 红      =>  黑  黑               红  红     =>  黑 黑
            // 红             红                          红          红
            // current                                  current
            Entry<K, V> pp = current.parent.parent;
            pp.color = Entry.RED;
            pp.left.color = Entry.BLACK;
            pp.right.color = Entry.BLACK;
            current = pp;
        }



        return null;
    }

    private Entry<K, V> getUncle(Entry<K, V> entry) {
        Entry<K, V> parent = entry.parent;
        if (parent == null) {
            return null;
        }
        Entry<K, V> pp = parent.parent;
        if (pp == null) {
            return null;
        }
        if (pp.left == parent) {
            return pp.right;
        }
        return pp.left;
    }

    public static void main(String[] args) {
        RedBlackTree<Integer, String> tree = new RedBlackTree<>();
        System.out.println(TreePrinter.printTree(tree.root));
        for (int i = 0; i < 10; i++) {
            putAndPrint(tree, i, String.valueOf(i));
        }
    }

    private static <K, V> void putAndPrint(RedBlackTree<K, V> tree, K key, V value) {
        tree.put(key, value);
        System.out.println(TreePrinter.printTree(tree.root));
    }

}
