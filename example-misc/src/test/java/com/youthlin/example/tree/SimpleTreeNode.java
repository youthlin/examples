package com.youthlin.example.tree;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author youthlin.chen
 * @date 2019-08-24 19:52
 */
@Data
public class SimpleTreeNode<T> implements TreeNode<T, SimpleTreeNode<T>> {
    private T data;
    private List<SimpleTreeNode<T>> children;

    public SimpleTreeNode(T data) {
        this.data = data;
    }

    public void addChild(SimpleTreeNode<T> child) {
        if (children == null) {
            children = Lists.newArrayList();
        }
        children.add(child);
    }

    public static void main(String[] args) {
        //  __abc________
        // /   |          \
        // A   AB    ____ ABCD____
        // |  / \   /      /  \    \
        // a  A  B  aBCD AbCD ABcD ABCd
        SimpleTreeNode<String> root = new SimpleTreeNode<>("abc");
        SimpleTreeNode<String> a = new SimpleTreeNode<>("A");
        a.addChild(new SimpleTreeNode<>("a"));
        root.addChild(a);
        a = new SimpleTreeNode<>("AB");
        a.addChild(new SimpleTreeNode<>("A"));
        a.addChild(new SimpleTreeNode<>("B"));
        root.addChild(a);
        a = new SimpleTreeNode<>("ABCD");
        a.addChild(new SimpleTreeNode<>("aBCD"));
        a.addChild(new SimpleTreeNode<>("AbCD"));
        a.addChild(new SimpleTreeNode<>("ABcD"));
        a.addChild(new SimpleTreeNode<>("ABCd"));
        root.addChild(a);
        System.out.println(TreeNode.toString(root));
        System.out.println(TreePrinter.toString(root, SimpleTreeNode::getChildren, SimpleTreeNode::printData,
                new TreePrinter.Option().offset(10)));
        System.out.println(TreePrinter.toString(root, SimpleTreeNode::getChildren, SimpleTreeNode::printData,
                new TreePrinter.Option().horizontal('━').verticalLineHead('┃').verticalToChild('┣')
                        .verticalLastChild('┗')));
        System.out.println(TreePrinter.toString(root, SimpleTreeNode::getChildren, SimpleTreeNode::printData,
                TreePrinter.Option.DEFAULT));
    }

}
