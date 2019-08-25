package com.youthlin.example.tree;

import java.util.List;

/**
 * @author youthlin.chen
 * @date 2019-08-24 17:47
 */
public interface TreeNode<T, N extends TreeNode<T, N>> {
    T getData();

    List<N> getChildren();

    default String printData() {
        return String.valueOf(getData());
    }

    static String toString(TreeNode root) {
        return TreePrinter.toString(root, TreeNode::getChildren, TreeNode::printData, TreePrinter.Option.DEFAULT);
    }

}
