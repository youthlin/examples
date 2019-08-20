package com.youthlin.example.tree;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;

/**
 * @author : youthlin.chen @ 2019-06-15 20:27
 */
public class TreePrinter {

    public static <T, N extends BinTreeNode<T, N>> String printTree(BinTreeNode<T, N> root) {
        return printTree(root, 0);
    }

    public static <T, N extends BinTreeNode<T, N>> String printTree(BinTreeNode<T, N> root, int offset) {
        return printTree(root, offset, " ", "_", "|");
    }

    public static <T, N extends BinTreeNode<T, N>> String printTree(BinTreeNode<T, N> root, int offset,
            String blankChar, String underLineChar, String verticalChar) {
        if (root == null) {
            return "";
        }
        int leftBlankCount = offset;

        // 中序遍历 准备工作 计算每个结点的偏移量
        Stack<BinTreeNode<T, N>> stack = new Stack<>();
        //辅助变量 用于判断偏移量是否需要调整
        BinTreeNode<T, N> pre = root;
        BinTreeNode<T, N> current = root;
        do {
            //往最左
            while (current != null) {
                stack.push(current);
                current = current.getLeft();
            }
            if (!stack.isEmpty()) {
                current = stack.pop();
                if (current.getLeft() == pre) {
                    /*
                     * 这种直接左子树宽度只有 1 的需要调整 加一个偏移量 因为要至少有一个下划线
                     *  _0
                     * |
                     * 9
                     * 这种就不需要：
                     *  _0
                     * |
                     * 90
                     * */
                    if (pre.dataWidth() < 2) {
                        offset++;
                    }
                }
                if (pre.getRight() == current) {
                    /*
                     * 中序下一个是直接右子树的 需要调整
                     * 2_
                     *   |
                     *   9
                     * 这种中序 2 的下一个不是直接右子树的 就不需要
                     * 2__
                     *    |
                     *   _9
                     *  |
                     *  88
                     * */
                    offset++;
                }
                current.setOffset(offset);
                offset += current.dataWidth();
                pre = current;
                current = current.getRight();
            }
        } while (current != null || !stack.isEmpty());

        // 层次遍历 真正输出
        Queue<BinTreeNode<T, N>> q = new LinkedList<>();
        q.offer(root);
        // 标志结点 用于标识每层的结尾
        q.offer(getEndFlag());
        StringBuilder sb = new StringBuilder();
        int currentOffset = 0;
        String leftBlankStr = repeatChar(blankChar, leftBlankCount);
        sb.append(leftBlankStr);
        while (true) {
            if (q.peek() == getEndFlag()) {
                sb.append('\n');
                break;
            }
            // 每一层的下划线和数据
            while (true) {
                current = q.poll();
                // 想不到吧 取出来又放进去了 为了等下输出竖线用的
                q.offer(current);
                if (current == getEndFlag()) {
                    // 这层结束了 该换行了
                    sb.append("\n").append(leftBlankStr);
                    currentOffset = 0;
                    break;
                }
                int thisOffset = Objects.requireNonNull(current).getOffset();
                // 有左子树 先输出左边的空格和下划线
                if (current.getLeft() != null) {
                    /*
                     *
                     * 01234567890123
                     *    ____13__
                     *   |        |
                     *  _11_     _15
                     * |    |   |
                     * 10   12  14
                     *
                     * 对于 11 13 来说 是队列的第一个 先是从头开始的空格 然后是下划线
                     * 对于 15 来说 先是从当前光标开始的空格 然后是下划线
                     * */
                    int leftOffset = current.getLeft().getOffset();
                    // 左子树那个点是空格 所以+1 下划线是从左子树那个点再往右一格开始
                    sb.append(repeatChar(blankChar, leftOffset - currentOffset + 1));
                    currentOffset = leftOffset + 1;
                    sb.append(repeatChar(underLineChar, thisOffset - currentOffset));
                    currentOffset = thisOffset;
                }
                // 输出本身
                String str = current.printData();
                sb.append(repeatChar(blankChar, thisOffset - currentOffset)).append(str);
                currentOffset = thisOffset + str.length();

                // 有右子树 输出右边的下划线
                if (current.getRight() != null) {
                    int rightOffset = current.getRight().getOffset();
                    sb.append(repeatChar(underLineChar, rightOffset - currentOffset));
                    currentOffset = rightOffset;
                }
            }

            //每一层的竖线
            while (true) {
                current = q.poll();
                if (current == END_FLAG) {
                    sb.append("\n").append(leftBlankStr);
                    currentOffset = 0;
                    q.offer(getEndFlag());
                    break;
                }
                BinTreeNode<T, N> left = Objects.requireNonNull(current).getLeft();
                if (left != null) {
                    int leftOffset = left.getOffset();
                    sb.append(repeatChar(blankChar, leftOffset - currentOffset)).append(verticalChar);
                    currentOffset = leftOffset + 1;
                    q.offer(left);
                }
                BinTreeNode<T, N> right = current.getRight();
                if (right != null) {
                    int rightOffset = right.getOffset();
                    sb.append(repeatChar(blankChar, rightOffset - currentOffset)).append(verticalChar);
                    currentOffset = rightOffset + 1;
                    q.offer(right);
                }
            }
        }
        return sb.toString();
    }

    private static String repeatChar(String ch, int count) {
        if (count == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    //region static flag

    private static final BinTreeNode END_FLAG = new BinTreeNode() {

        @Override
        public Object getData() {
            return null;
        }

        @Override
        public BinTreeNode getLeft() {
            return null;
        }

        @Override
        public BinTreeNode getRight() {
            return null;
        }

        @Override
        public String printData() {
            return null;
        }

        @Override
        public void setOffset(int offset) {
        }

        @Override
        public int getOffset() {
            return 0;
        }
    };

    @SuppressWarnings("unchecked")
    private static <T, N extends BinTreeNode<T, N>> BinTreeNode<T, N> getEndFlag() {
        return END_FLAG;
    }
    //endregion static flag

}
