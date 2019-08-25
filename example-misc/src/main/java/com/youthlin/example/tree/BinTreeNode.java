package com.youthlin.example.tree;

/**
 * @author : youthlin.chen @ 2019-08-20 22:39
 */
public interface BinTreeNode<T, N extends BinTreeNode<T, N>> {
    T getData();

    N getLeft();

    N getRight();

    default String printData() {
        return String.valueOf(getData());
    }

    static <T, N extends BinTreeNode<T, N>> String toString(BinTreeNode<T, N> root) {
        return TreePrinter.toString(root, BinTreeNode::getLeft, BinTreeNode::getRight,
                BinTreeNode::printData, TreePrinter.Option.DEFAULT);
    }

}
