package com.youthlin.example.bplus;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;

/**
 * @author youthlin.chen
 * @date 2019-03-17 17:45
 */
public class BplusTreeTest {
    private BplusTree<Integer, String> tree;

    @Before
    public void before() {
        tree = new BplusTree<>(Comparator.reverseOrder());
        int n = 20;
        for (int i = 0; i < n; i++) {
            tree.put(i, String.valueOf(i));
        }
        System.out.println("before: " + tree);
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

}
