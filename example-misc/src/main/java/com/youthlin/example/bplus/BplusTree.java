package com.youthlin.example.bplus;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author youthlin.chen
 * @date 2019-03-15 20:33
 * @link https://www.cnblogs.com/nullzx/p/8729425.html
 */
public class BplusTree<K, V> extends AbstractMap<K, V> {
    /**
     * 结点
     */
    private class Node {
        /**
         * 为 null 说明是 root 结点
         */
        private Node parent;
        /**
         * 结点的关键字
         */
        private List<Entry<K, V>> data = Lists.newLinkedList();
        /**
         * n个关键字则有n+1个子孩子
         * 为 null 说明是叶结点
         */
        private List<Node> children;
        /**
         * 同层次的下一个结点
         */
        private Node next;
    }

    /**
     * 根节点
     */
    private Node root;
    /**
     * 最小的结点
     */
    private Node min;
    /**
     * 阶数
     * m阶B+树内个节点最多存放m-1项数据
     */
    private final int m;
    private Comparator<K> comparator;
    private int size;
    private Set<Map.Entry<K, V>> entrySet;

    public BplusTree() {
        this.m = 5;
    }

    public BplusTree(int maxElementPerNode) {
        this.m = maxElementPerNode;
    }

    public BplusTree(Comparator<K> comparator) {
        this.m = 5;
        this.comparator = comparator;
    }

    public BplusTree(int maxElementPerNode, Comparator<K> comparator) {
        this.m = maxElementPerNode;
        this.comparator = comparator;
    }

    @Override
    public V get(Object key) {
        @SuppressWarnings("unchecked") K k = (K) key;
        Node leaf = findLeafNode(k);
        for (Entry<K, V> entry : leaf.data) {
            int compare = compare(entry.getKey(), k);
            if (compare == 0) {
                return entry.getValue();
            }
            if (compare > 0) {
                break;
            }
        }
        return null;
    }

    /**
     * 插入
     * 1. 若为空树 创建一个叶子结点 插入 此时root,min也是该叶子结点 结束
     * 2. 定位到要插入的叶子结点
     * 3. 针对叶子结点
     * 3.1 插入记录，若当前叶子结点记录数小于等于 m-1 结束
     * 3.2 否则将该叶子结点分裂为左右两个叶子结点，左边包含前 m/2 个，右边包含剩下的
     * 3.3 将分裂后右边的第一个记录进位到父结点中 该关键字的左右子孩子分别是刚分裂的左右叶子结点
     * 3.4 将当前结点指向父结点
     * 4. 针对内结点
     * 4.1 若当前结点记录数小于等于 m-1 结束
     * 4.2 否则 将该内结点分裂为两个内结点 左结点包含前 (m-1)/2 个记录 右结点包含 m/2 个记录
     * 中间的记录进位到父结点中 进位的该关键字左右子孩子分别是刚分裂的左右内结点
     * 4.3 将当前结点指向父结点 重复第 4 步
     *
     * @return the put value always
     */
    @Override
    public V put(K key, V value) {
        Node leaf = findLeafNode(key);
        insertToLeafNode(leaf, key, value);
        size++;
        if (leaf.data.size() <= m - 1) {
            return value;
        }
        Node current = splitLeaf(leaf);
        while (current.data.size() > m - 1) {
            current = splitInnerNode(current);
        }
        return value;
    }

    @Override
    @NotNull
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    final class EntrySet extends AbstractSet<Entry<K, V>> {
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Map.Entry<K, V>>() {
                Node currentNode = min;
                int index = 0;

                @Override
                public void remove() {
                    BplusTree.this.remove(currentNode, index);
                }

                @Override
                public boolean hasNext() {
                    return currentNode != null && (index < currentNode.data.size() || currentNode.next != null);
                }

                @Override
                public Map.Entry<K, V> next() {
                    Entry<K, V> entry = currentNode.data.get(index++);
                    if (index >= currentNode.data.size()) {
                        index = 0;
                        currentNode = currentNode.next;
                    }
                    return entry;
                }
            };
        }

        @Override
        public int size() {
            return size;
        }
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    private V remove(Node node, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return innerToString();
    }

    private Node findLeafNode(K key) {
        if (root == null) {
            min = root = new Node();
            return root;
        } else {
            Node current = root;
            while (current.children != null) {
                int index = index(current, key);
                current = current.children.get(index);
            }
            return current;
        }
    }

    /**
     * 在一个结点中定位要插入的元素应该插在哪个下标
     */
    private int index(Node node, Entry<K, V> entry) {
        return index(node, entry.getKey());
    }

    private int index(Node node, K key) {
        int index = 0;
        for (Entry<K, V> inNode : node.data) {
            if (compare(inNode.getKey(), key) <= 0) {
                index++;
            } else {
                break;
            }
        }
        return index;
    }

    @SuppressWarnings("unchecked")
    private int compare(K left, K right) {
        if (comparator != null) {
            return comparator.compare(left, right);
        }
        if (left instanceof Comparable && right instanceof Comparable) {
            return ((Comparable) left).compareTo(right);
        }
        throw new ClassCastException("key should be Comparable when comparator not specified.");
    }

    private void insertToLeafNode(Node leaf, K key, V value) {
        int index = index(leaf, key);
        leaf.data.add(index, new SimpleEntry<>(key, value));
    }

    private Node splitLeaf(Node leaf) {
        //分裂
        Node right = new Node();
        for (int i = m / 2; i < leaf.data.size(); ) {
            right.data.add(leaf.data.remove(i));
        }
        right.next = leaf.next;
        leaf.next = right;

        //进位
        Entry<K, V> up = right.data.get(0);
        return insertToUp(leaf, right, up);
    }

    private Node insertToUp(Node left, Node right, Entry<K, V> up) {
        Node parent = left.parent;
        if (parent == null) {
            parent = new Node();
            parent.children = Lists.newArrayList();
            root = parent;
        }
        int index = index(parent, up);
        parent.data.add(index, up);
        //noinspection StatementWithEmptyBody
        if (index < parent.children.size() && parent.children.get(index) == left) {
            //已经在不用再添加
        } else {
            parent.children.add(index, left);
        }
        parent.children.add(index + 1, right);
        left.parent = parent;
        right.parent = parent;
        return parent;
    }

    private Node splitInnerNode(Node node) {
        //分裂
        Node right = new Node();
        right.children = Lists.newLinkedList();
        int center = m / 2;
        for (int i = center + 1; i < node.data.size(); ) {
            right.data.add(node.data.remove(i));
            right.children.add(node.children.remove(i));
        }
        right.children.add(node.children.remove(center + 1));

        right.next = node.next;
        node.next = right;
        //进位
        Entry<K, V> up = node.data.remove(center);
        return insertToUp(node, right, up);
    }

    private String innerToString() {
        if (root == null) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        Node currentLevel = root;
        Node current;
        int level = 0;
        while (currentLevel != null) {
            current = currentLevel;
            sb.append(level++).append("=[");
            while (current != null) {
                sb.append('(').append(current.data).append(')');
                current = current.next;
                if (current != null) {
                    sb.append(',');
                }
            }
            sb.append("]");
            if (currentLevel.children != null) {
                sb.append(',');
                currentLevel = currentLevel.children.get(0);
            } else {
                currentLevel = null;
            }
        }
        sb.append('}');
        return sb.toString();

    }

    public static void main(String[] args) {
        BplusTree<Integer, Integer> tree = new BplusTree<>();
        tree.put(5, 5);
        tree.put(8, 8);
        tree.put(10, 10);
        tree.put(15, 15);
        System.out.println(tree);
        tree.put(16, 16);
        System.out.println(tree);
        tree.put(17, 17);
        System.out.println(tree);
        tree.put(18, 18);
        System.out.println("插入18后 " + tree);
        tree.put(19, 19);
        tree.put(20, 20);
        tree.put(21, 21);
        tree.put(22, 22);
        tree.put(6, 6);
        tree.put(9, 9);
        System.out.println("插入9后 " + tree);
        tree.put(7, 7);
        System.out.println("插入7后 " + tree);
        for (Entry<Integer, Integer> entry : tree.entrySet()) {
            System.out.println(entry);
        }
        System.out.println("get------");
        System.out.println(tree.get(5));
        System.out.println(tree.get(15));
        System.out.println(tree.get(16));
        System.out.println(tree.get(21));
        System.out.println(tree.get(22));
        System.out.println(tree.get(4));
        System.out.println(tree.get(11));
        System.out.println(tree.get(99));
        System.out.println("-----size empty");
        System.out.println(tree.size() + " " + tree.isEmpty() + " " + tree.keySet() + " " + tree.values());
    }

}
