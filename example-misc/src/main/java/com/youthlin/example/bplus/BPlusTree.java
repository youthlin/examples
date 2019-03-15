package com.youthlin.example.bplus;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author youthlin.chen
 * @date 2019-03-15 20:33
 * @link https://www.cnblogs.com/nullzx/p/8729425.html
 */
public class BPlusTree<K extends Comparable<K>, T extends BPlusData<K>> implements IBPlusTree<K, T> {
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
        private List<T> data = Lists.newLinkedList();
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
    private int m;

    public BPlusTree(int maxElementPerNode) {
        this.m = maxElementPerNode;
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
     */
    @Override
    public void insert(T data) {
        Node leaf = findLeafNode(data);
        insertToLeafNode(leaf, data);
        if (leaf.data.size() <= m - 1) {
            return;
        }
        Node current = splitLeaf(leaf);
        while (current.data.size() > m - 1) {
            current = splitInnerNode(current);
        }
    }

    private Node findLeafNode(T data) {
        if (root == null) {
            min = root = new Node();
            return root;
        } else {
            Node current = root;
            while (current.children != null) {
                int index = index(current, data);
                current = current.children.get(index);
            }
            return current;
        }
    }

    private void insertToLeafNode(Node leaf, T data) {
        int index = index(leaf, data);
        leaf.data.add(index, data);
    }

    /**
     * 在一个结点中定位要插入的元素应该插在哪个下标
     */
    private int index(Node node, T data) {
        int index = 0;
        for (T inNode : node.data) {
            if (BPlusData.COMPARATOR.compare(inNode, data) <= 0) {
                index++;
            } else {
                break;
            }
        }
        return index;
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
        T up = right.data.get(0);
        return insertToUp(leaf, right, up);
    }

    private Node insertToUp(Node left, Node right, T up) {
        Node parent = left.parent;
        if (parent == null) {
            parent = new Node();
            parent.children = Lists.newArrayList();
            root = parent;
        }
        int index = index(parent, up);
        parent.data.add(index, up);
        parent.children.add(index, left);
        parent.children.add(index + 1, right);
        left.parent = parent;
        right.parent = parent;
        return parent;
    }

    private Node splitInnerNode(Node node) {
        //分裂
        Node right = new Node();
        int center = m / 2;
        for (int i = center + 1; i < node.data.size(); i++) {
            right.data.add(node.data.remove(i));
        }
        node.next = right;
        //进位
        T up = node.data.get(center);
        return insertToUp(node, right, up);
    }

    @Override
    public T find(K key) {
        return null;
    }

    @Override
    public T delete(K key) {
        return null;
    }

    private final Iterator<T> no_data_iterator = new Iterator<T>() {
        @Override
        public void remove() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            return null;
        }
    };

    public Iterator<T> iterator() {
        if (root == null) {
            return no_data_iterator;
        }
        return new Iterator<T>() {
            int index = 0;
            Node current = min;

            @Override
            public boolean hasNext() {
                return current != null && (index < current.data.size() || current.next != null);
            }

            @Override
            public T next() {
                T data = current.data.get(index);
                index++;
                if (index == current.data.size()) {
                    index = 0;
                    current = current.next;
                }
                return data;
            }
        };
    }

    @Override
    public String toString() {
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
            }
            if (currentLevel.children != null) {
                currentLevel = currentLevel.children.get(0);
            } else {
                currentLevel = null;
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        BPlusTree<Integer, BPlusData<Integer>> tree = new BPlusTree<>(5);
        tree.insert(BPlusData.of(5));
        tree.insert(BPlusData.of(8));
        tree.insert(BPlusData.of(10));
        tree.insert(BPlusData.of(15));
        System.out.println(tree);
        tree.insert(BPlusData.of(16));
        System.out.println(tree);
        tree.insert(BPlusData.of(17));
        System.out.println(tree);
        tree.insert(BPlusData.of(18));
        System.out.println("插入18后 " + tree);
        tree.insert(BPlusData.of(19));
        tree.insert(BPlusData.of(20));
        tree.insert(BPlusData.of(21));
        tree.insert(BPlusData.of(22));
        tree.insert(BPlusData.of(6));
        tree.insert(BPlusData.of(9));
        System.out.println("插入9后 " + tree);
        tree.insert(BPlusData.of(7));
        System.out.println("插入7后 " + tree);

    }

}
