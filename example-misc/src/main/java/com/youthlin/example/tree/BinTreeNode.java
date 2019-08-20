package com.youthlin.example.tree;

/**
 * @author : youthlin.chen @ 2019-08-20 22:39
 */
public interface BinTreeNode<T, N extends BinTreeNode<T, N>> {
    T getData();

    N getLeft();

    N getRight();

    void setOffset(int offset);

    int getOffset();

    default String printData() {
        return String.valueOf(getData());
    }

    default int dataWidth() {
        return printData().length();
    }
}
