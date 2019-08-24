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
        return toString(root, 0);
    }

    static String toString(TreeNode root, int offset) {
        /*
         StringWriter sw = new StringWriter();
         PrintWriter out = new PrintWriter(sw);
         out.println();
         String newLine = sw.toString();
         */
        return toString(root, offset, "\r\n", '_', '|', '|');
    }

    static String toString(TreeNode root, int offset,
            String newLine, char horizontal, char vertical, char lastChild) {
        return TreePrinter.toString(root, TreeNode::getChildren, TreeNode::printData,
                offset, newLine, horizontal, vertical, lastChild);
    }

}
