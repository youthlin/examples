package com.youthlin.example.tree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author youthlin.chen
 * @date 2019-03-17 17:45
 */
public class BplusTreeTest {
    private BplusTree<Integer, String> tree;

    @Before
    public void before() {
        tree = new BplusTree<>(5);
        int n = 13;
        for (int i = 0; i < n; i++) {
            tree.put(i, String.valueOf(i));
            System.out.println("put " + i + " " + tree);
        }
        System.out.println("before: " + tree.getMaxChildren() + " " + tree.getMinElementPerNode() + " " + tree);
    }

    @Test
    public void testClone() {
        @SuppressWarnings("unchecked")
        BplusTree<Integer, String> clone = (BplusTree<Integer, String>) tree.clone();
        System.out.println("clone: " + clone);
        System.out.println(tree.equals(clone));
        clone.putAll(tree);
        System.out.println("clone+putAll: " + clone);
    }

    @Test
    public void testSerialize() {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(tree);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
            @SuppressWarnings("unchecked")
            BplusTree<Integer, String> read = (BplusTree<Integer, String>) in.readObject();
            System.out.println("read: " + read);
            System.out.println(read.equals(tree));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        System.out.println("remove: " + tree.remove(0) + " size=" + tree.size() + " " + tree);
        System.out.println("remove: " + tree.remove(1) + " size=" + tree.size() + " " + tree);
        System.out.println("remove: " + tree.remove(8) + " size=" + tree.size() + " " + tree);
        System.out.println("after: " + " size=" + tree.size() + " " + tree);
    }
}
