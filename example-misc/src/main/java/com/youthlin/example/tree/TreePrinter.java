package com.youthlin.example.tree;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author : youthlin.chen @ 2019-06-15 20:27
 */
public class TreePrinter {
    @Data
    @Accessors(chain = true, fluent = true)
    public static class Option {
        private static final String NEW_LINE;

        static {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            NEW_LINE = sw.toString();
        }

        public static final Option DEFAULT = new Option() {
            @Override
            public Option offset(int offset) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Option newLine(String newLine) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Option horizontal(char horizontal) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Option verticalToChild(char vertical) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Option verticalLastChild(char verticalLastChild) {
                throw new UnsupportedOperationException();
            }
        };

        private int offset = 0;
        @NonNull
        private String newLine = NEW_LINE;
        // 搜一下制表符 复制的
        private char verticalLineHead = '│';
        private char verticalToChild = '├';
        private char verticalLastChild = '└';
        private char horizontal = '─';
        private char leftCorner = '┌';
        private char rightCorner = '┐';
        private char leftVertical = '│';
        private char rightVertical = '│';
        private Supplier<String> nullNodeToString = () -> "";
    }

    private static class BinWrap<N> {
        private static final BinWrap END_FLAG = new BinWrap();
        final N node;
        final BinWrap<N> left;
        final BinWrap<N> right;
        int offset;

        BinWrap() {
            node = null;
            left = null;
            right = null;
        }

        BinWrap(N node, Function<N, N> getLeft, Function<N, N> getRight) {
            this.node = Objects.requireNonNull(node);
            N left = getLeft.apply(node);
            if (left != null) {
                this.left = new BinWrap<>(left, getLeft, getRight);
            } else {
                this.left = null;
            }
            N right = getRight.apply(node);
            if (right != null) {
                this.right = new BinWrap<>(right, getLeft, getRight);
            } else {
                this.right = null;
            }
        }

        @SuppressWarnings("unchecked")
        private static <N> BinWrap<N> getEndFlag() {
            return END_FLAG;
        }
    }

    public static <N> String printTree(N root, Function<N, N> getLeft, Function<N, N> getRight,
            Function<N, String> print, Option option) {
        if (root == null) {
            return "";
        }
        int offset = option.offset;
        int leftBlankCount = offset;

        // 中序遍历 准备工作 计算每个结点的偏移量
        Stack<BinWrap<N>> stack = new Stack<>();
        //辅助变量 用于判断偏移量是否需要调整
        BinWrap<N> wrapRoot = new BinWrap<>(root, getLeft, getRight);
        BinWrap<N> pre = wrapRoot, current = wrapRoot;
        do {
            //往最左
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            if (!stack.isEmpty()) {
                current = stack.pop();
                if (current.left == pre) {
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
                    if (print.apply(pre.node).length() < 2) {
                        offset++;
                    }
                }
                if (pre.right == current) {
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
                current.offset = offset;
                offset += print.apply(current.node).length();
                pre = current;
                current = current.right;
            }
        } while (current != null || !stack.isEmpty());

        // 层次遍历 真正输出
        Queue<BinWrap<N>> q = new LinkedList<>();
        q.offer(wrapRoot);
        // 标志结点 用于标识每层的结尾
        q.offer(BinWrap.getEndFlag());
        StringBuilder sb = new StringBuilder();
        int currentOffset = 0;
        String leftBlankStr = repeatChar(' ', leftBlankCount);
        sb.append(leftBlankStr);
        while (true) {
            if (q.peek() == BinWrap.getEndFlag()) {
                sb.append(option.newLine);
                break;
            }
            // 每一层的下划线和数据
            while (true) {
                current = q.poll();
                // 想不到吧 取出来又放进去了 为了等下输出竖线用的
                q.offer(current);
                if (current == BinWrap.getEndFlag()) {
                    // 这层结束了 该换行了
                    sb.append(option.newLine).append(leftBlankStr);
                    currentOffset = 0;
                    break;
                }
                int thisOffset = Objects.requireNonNull(current).offset;
                // 有左子树 先输出左边的空格和下划线
                if (current.left != null) {
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
                    int leftOffset = current.left.offset;
                    // 左子树那个点是空格 所以+1 下划线是从左子树那个点再往右一格开始
                    sb.append(repeatChar(' ', leftOffset - currentOffset));
                    sb.append(option.leftCorner);
                    currentOffset = leftOffset + 1;
                    sb.append(repeatChar(option.horizontal, thisOffset - currentOffset));
                    currentOffset = thisOffset;
                }
                // 输出本身
                String str = print.apply(current.node);
                sb.append(repeatChar(' ', thisOffset - currentOffset)).append(str);
                currentOffset = thisOffset + str.length();

                // 有右子树 输出右边的下划线
                if (current.right != null) {
                    int rightOffset = current.right.offset;
                    sb.append(repeatChar(option.horizontal, rightOffset - currentOffset));
                    sb.append(option.rightCorner);
                    currentOffset = rightOffset + 1;
                }
            }

            //每一层的竖线
            while (true) {
                current = q.poll();
                if (current == BinWrap.getEndFlag()) {
                    sb.append(option.newLine).append(leftBlankStr);
                    currentOffset = 0;
                    q.offer(BinWrap.getEndFlag());
                    break;
                }
                BinWrap<N> left = Objects.requireNonNull(current).left;
                if (left != null) {
                    int leftOffset = left.offset;
                    sb.append(repeatChar(' ', leftOffset - currentOffset)).append(option.leftVertical);
                    currentOffset = leftOffset + 1;
                    q.offer(left);
                }
                BinWrap<N> right = current.right;
                if (right != null) {
                    int rightOffset = right.offset;
                    sb.append(repeatChar(' ', rightOffset - currentOffset)).append(option.rightVertical);
                    currentOffset = rightOffset + 1;
                    q.offer(right);
                }
            }
        }
        return sb.toString();
    }

    private static String repeatChar(char ch, int count) {
        if (count == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * 包装结点 有一些 tostring 需要用的临时状态变量和方法，为了不侵入应用 所以包装一下
     */
    private static class Wrap<N> {
        private boolean isNull;
        private boolean printed;
        private final N node;
        private final List<Wrap<N>> children;

        private static <N> Wrap<N> newNullNode() {
            return new Wrap<>();
        }

        private Wrap() {
            isNull = true;
            this.node = null;
            this.children = null;
        }

        private Wrap(N node, Function<N, List<N>> getChildren) {
            this.node = node;
            List<N> children = getChildren.apply(node);
            ArrayList<Wrap<N>> list = null;
            if (children != null) {
                list = Lists.newArrayList();
                boolean childAllNull = true;
                boolean hasNullChild = false;
                for (N child : children) {
                    if (child == null) {
                        hasNullChild = true;
                        list.add(null);
                    } else {
                        childAllNull = false;
                        list.add(new Wrap<>(child, getChildren));
                    }
                }
                if (childAllNull) {
                    list = null;
                } else {
                    if (hasNullChild) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i) == null) {
                                list.set(i, newNullNode());
                            }
                        }
                    }
                }
            }
            this.children = list;
        }

        private boolean hasChildNotPrinted() {
            if (children == null) {
                return false;
            }
            for (Wrap child : children) {
                if (!child.printed) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasChild() {
            return children != null && !children.isEmpty();
        }

        private static void appendOffset(StringBuilder sb, int offset) {
            for (int i = 0; i < offset; i++) {
                sb.append(" ");
            }
        }

        private static <N> void pushChild(Stack<Wrap<N>> stack, Wrap<N> node) {
            if (node.hasChild()) {
                int size = node.children.size();
                Wrap<N> tmp;
                // 先 push 最右边的，这样 pop 时先拿到最左边的
                for (int i = size - 1; i >= 0; i--) {
                    tmp = node.children.get(i);
                    //if (tmp != null) {
                    stack.push(tmp);
                    //}
                }
            }
        }

        private static <N> void insert(ArrayList<Wrap<N>> list, Wrap<N> node, int index) {
            if (list.size() <= index) {
                list.add(node);
            } else {
                list.set(index, node);
            }
        }

        private static <N> boolean isChildOf(Wrap<N> node, Wrap<N> parent) {
            if (parent == null || !parent.hasChild()) {
                return false;
            }
            for (Wrap child : parent.children) {
                if (child == node) {
                    return true;
                }
            }
            return false;
        }

        private static <N> void appendBlank(StringBuilder sb, Wrap<N> preLineNode, int nodeIndex,
                Function<N, String> toString) {
            for (int j = 0; j < toString.apply(preLineNode.node).length() - 1; j++) {
                sb.append(" ");
            }
            //abc_xy
            //  |  |_vw
            //  |_xyz
            //index 是 0 时 只需要输出【 pre 宽度 - 1 个】空格即可。如上 abc 之下的空格都是2个
            //但不是 0 时，需要输出【 pre 宽度个】空格。如上 第二行两个竖线之间就是【 xy 的宽度个=2个】空格
            if (nodeIndex > 0) {
                sb.append(" ");
            }
        }

        private static boolean isLastChildOf(Wrap node, Wrap parent) {
            if (parent == null || !parent.hasChild()) {
                return false;
            }
            return node == parent.children.get(parent.children.size() - 1);
        }

    }

    public static <N> String toString(N root, Function<N, List<N>> getChildren,
            Function<N, String> dataToString, Option option) {
        Wrap<N> prevLineNode, current = new Wrap<>(root, getChildren);
        StringBuilder sb = new StringBuilder();
        Wrap.appendOffset(sb, option.offset);
        sb.append(dataToString.apply(current.node));

        // 用来先序遍历
        Stack<Wrap<N>> stack = new Stack<>();
        Wrap.pushChild(stack, current);
        // list 用来保存横向的最新的已输出结点
        // 如 abc_xy_xyz 处理完这行 list=[abc, xy, xyz]
        //      |_XY     处理完这行 list=[abc, XY, xyz](其中 xyz 没有用了，但还在 list 里也无妨)
        //         |_vw  处理这里时，需要遍历 list 看当前 vw 是否是 list 某一项的子结点。处理完这行 list=[abc, XY, vw]
        ArrayList<Wrap<N>> list = Lists.newArrayList(current);
        // 下标，输出后从左往右从 0 开始记
        int index = 0;
        // 即将输出的结点是否是行首结点 初始因为 root 已输出 所以不是行首
        boolean isLineHead = false;
        while (!stack.isEmpty()) {
            current = stack.pop();
            if (!isLineHead) {
                // 即将输出的结点不是行首 直接输出下划线接着连接就可以
                if (current.isNull) {
                    sb.append(option.horizontal).append(option.nullNodeToString.get());
                } else {
                    sb.append(option.horizontal).append(dataToString.apply(current.node));
                }
                current.printed = true;
                if (current.hasChild()) {
                    Wrap.insert(list, current, ++index);
                    Wrap.pushChild(stack, current);
                    isLineHead = false;
                } else {
                    //没有子结点了 下次就要另起一行 是行首了
                    isLineHead = true;
                    index = 0;
                    sb.append(option.newLine);
                    Wrap.appendOffset(sb, option.offset);
                }
            } else {
                // 即将输出的结点是行首 要看输出空格还是竖线
                for (int i = 0; i < list.size(); i++) {
                    prevLineNode = list.get(i);
                    index = i;
                    if (Wrap.isChildOf(current, prevLineNode)) {
                        // pre
                        // ↓
                        // abc_xx
                        //   |_yy
                        //     ↑
                        //     current
                        Wrap.appendBlank(sb, prevLineNode, i, dataToString);
                        if (Wrap.isLastChildOf(current, prevLineNode)) {
                            sb.append(option.verticalLastChild);
                        } else {
                            sb.append(option.verticalToChild);
                        }
                        if (current.isNull) {
                            sb.append(option.horizontal).append(option.nullNodeToString.get());
                        } else {
                            sb.append(option.horizontal).append(dataToString.apply(current.node));
                        }
                        current.printed = true;
                        if (current.hasChild()) {
                            Wrap.insert(list, current, ++index);
                            Wrap.pushChild(stack, current);
                            isLineHead = false;
                        } else {
                            sb.append(option.newLine);
                            Wrap.appendOffset(sb, option.offset);
                            index = 0;
                            isLineHead = true;
                        }
                        //跳出 for
                        break;
                    } else {
                        Wrap.appendBlank(sb, prevLineNode, i, dataToString);
                        if (prevLineNode.hasChildNotPrinted()) {
                            //abc_xx
                            //  |  |_yyy 这行的第一个竖线 遍历上一行的 abc 时 当前结点 yyy 不是 abc 的子结点，而且 abc 还有子结点没有输出
                            //  |_xyz
                            sb.append(option.verticalLineHead);
                        } else {
                            // abc_xx
                            //       |_yyy
                            //    ↑这里不是竖线，因为 abc 的子结点都输出了
                            sb.append(" ");
                        }
                    }
                }

            }
        }
        return sb.toString();
    }

}
