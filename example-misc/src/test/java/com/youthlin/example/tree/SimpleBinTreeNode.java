package com.youthlin.example.tree;

import com.google.common.collect.Lists;
import lombok.Data;

/**
 * @author : youthlin.chen @ 2019-08-25 13:07
 */
@Data
public class SimpleBinTreeNode<T> implements BinTreeNode<T, SimpleBinTreeNode<T>> {
    T data;
    SimpleBinTreeNode<T> left;
    SimpleBinTreeNode<T> right;

    public SimpleBinTreeNode(T data) {
        this.data = data;
    }

    public static void main(String[] args) {
        //  abc
        //  /  \
        // a    ab
        //  \   /\
        //   b  A BB
        SimpleBinTreeNode<String> root = new SimpleBinTreeNode<>("abc");
        SimpleBinTreeNode<String> a = new SimpleBinTreeNode<>("a");
        a.right = new SimpleBinTreeNode<>("b");
        root.left = a;
        a = new SimpleBinTreeNode<>("ab");
        root.right = a;
        a.left = new SimpleBinTreeNode<>("A");
        a.right = new SimpleBinTreeNode<>("BB");
        SimpleBinTreeNode<String> nullNode = new SimpleBinTreeNode<>(null);
        System.out.println(BinTreeNode.toString(root));
        System.out.println(TreePrinter
                .printTree(root, SimpleBinTreeNode::getLeft, SimpleBinTreeNode::getRight, SimpleBinTreeNode::printData,
                        new TreePrinter.Option().offset(2).leftCorner(' ').horizontal('_').rightCorner(' ')
                                .leftVertical('|').rightVertical('|')));
        System.out.println(TreePrinter.toString(root,
                n -> Lists.newArrayList(n.getLeft(), n.getRight()),
                BinTreeNode::printData, TreePrinter.Option.DEFAULT));
        System.out.println("--------------------------------------");
        //   root
        //    /
        //   a
        //  /
        // b
        root = new SimpleBinTreeNode<>("root");
        a = new SimpleBinTreeNode<>("a");
        root.left = a;
        a.left = new SimpleBinTreeNode<>("b");
        System.out.println(BinTreeNode.toString(root));
        System.out.println(TreePrinter.toString(root,
                n -> Lists.newArrayList(n.getLeft(), n.getRight()),
                BinTreeNode::printData, TreePrinter.Option.DEFAULT));
        System.out.println(TreePrinter.toString(root,
                n -> Lists.newArrayList(n.getRight(), n.getLeft()),
                BinTreeNode::printData, new TreePrinter.Option().nullNodeToString(() -> "空")));
        // root
        //   \
        //    a
        //     \
        //      b
        root = new SimpleBinTreeNode<>("root");
        a = new SimpleBinTreeNode<>("a");
        root.right = a;
        a.right = new SimpleBinTreeNode<>("b");
        System.out.println("--------------------------------------");
        System.out.println(BinTreeNode.toString(root));
        System.out.println(TreePrinter.toString(root,
                n -> Lists.newArrayList(n.getLeft(), n.getRight()),
                BinTreeNode::printData, TreePrinter.Option.DEFAULT));

    }

}
