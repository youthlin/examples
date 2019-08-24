package com.youthlin.example.tree;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

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

    static <T, N extends TreeNode<T, N>> String toString(TreeNode<T, N> root) {
        return toString(root, 0);
    }

    static <T, N extends TreeNode<T, N>> String toString(TreeNode<T, N> root, int offset) {
        /*
         StringWriter sw = new StringWriter();
         PrintWriter out = new PrintWriter(sw);
         out.println();
         String newLine = sw.toString();
         */
        return toString(root, offset, "\r\n", '_', '|', '|');
    }

    static <T, N extends TreeNode<T, N>> String toString(TreeNode<T, N> root, int offset,
            String newLine, char horizontal, char vertical, char lastChild) {
        class Wrap {
            private boolean printed;
            private final TreeNode<T, N> node;
            private final List<Wrap> children;

            private Wrap() {
                node = null;
                children = null;
            }

            private Wrap(TreeNode<T, N> node) {
                this.node = Objects.requireNonNull(node);
                List<N> children = node.getChildren();
                if (children != null) {
                    this.children = Lists.newArrayList();
                    for (N child : children) {
                        if (child == null) {
                            this.children.add(null);
                        } else {
                            this.children.add(new Wrap(child));
                        }
                    }
                } else {
                    this.children = null;
                }
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

            private /*static*/ void appendBlank(StringBuilder sb, Wrap preLineNode, int nodeIndex) {
                for (int j = 0; j < preLineNode.node.printData().length() - 1; j++) {
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

            private /*static*/ void printOffset(StringBuilder sb, int offset) {
                for (int i = 0; i < offset; i++) {
                    sb.append(" ");
                }
            }

            private /*static*/ void pushChild(Stack<Wrap> stack, Wrap node) {
                if (node.hasChild()) {
                    int size = node.children.size();
                    Wrap tmp;
                    // 先 push 最右边的，这样 pop 时先拿到最左边的
                    for (int i = size - 1; i >= 0; i--) {
                        tmp = node.children.get(i);
                        if (tmp != null) {
                            stack.push(tmp);
                        }
                    }
                }
            }

            private /*static*/ void insert(ArrayList<Wrap> list, Wrap node, int index) {
                if (list.size() <= index) {
                    list.add(node);
                } else {
                    list.set(index, node);
                }
            }

            private /*static*/ boolean isChildOf(Wrap node, Wrap parent) {
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

            private /*static*/ boolean isLastChildOf(Wrap node, Wrap parent) {
                if (parent == null || !parent.hasChild()) {
                    return false;
                }
                return node == parent.children.get(parent.children.size() - 1);
            }
        }
        // 用来模拟静态方法 因为内部类不能有 static 方法
        final Wrap util = new Wrap();

        Wrap current, previousLineNode;
        current = new Wrap(root);

        StringBuilder sb = new StringBuilder();
        util.printOffset(sb, offset);
        sb.append(current.node.printData());

        // 用来先序遍历
        Stack<Wrap> stack = new Stack<>();
        util.pushChild(stack, current);
        // list 用来保存横向的最新的已输出结点
        // 如 abc_xy_xyz 处理完这行 list=[abc, xy, xyz]
        //      |_XY     处理完这行 list=[abc, XY, xyz](其中 xyz 没有用了，但还在 list 里也无妨)
        //         |_vw  处理这里时，需要遍历 list 看当前 vw 是否是 list 某一项的子结点。处理完这行 list=[abc, XY, vw]
        ArrayList<Wrap> list = Lists.newArrayList(current);
        // 已输出的每个结点的下标
        int index = 0;
        // 即将输出的结点是否是行首结点 初始因为 root 已输出 所以不是行首
        boolean isLineHead = false;
        while (!stack.isEmpty()) {
            current = stack.pop();
            if (!isLineHead) {
                // 即将输出的结点不是行首 直接输出下划线接着连接就可以
                sb.append(horizontal).append(current.node.printData());
                current.printed = true;
                if (current.hasChild()) {
                    util.insert(list, current, ++index);
                    util.pushChild(stack, current);
                    isLineHead = false;
                } else {
                    //没有子结点了 下次就要另起一行 是行首了
                    isLineHead = true;
                    index = 0;
                    sb.append(newLine);
                    util.printOffset(sb, offset);
                }
            } else {
                // 即将输出的结点是行首 要看输出空格还是竖线
                for (int i = 0; i < list.size(); i++) {
                    previousLineNode = list.get(i);
                    index = i;
                    if (util.isChildOf(current, previousLineNode)) {
                        // pre
                        // ↓
                        // abc_xx
                        //   |_yy
                        //     ↑
                        //     current
                        util.appendBlank(sb, previousLineNode, i);
                        if (util.isLastChildOf(current, previousLineNode)) {
                            sb.append(lastChild);
                        } else {
                            sb.append(vertical);
                        }
                        sb.append(horizontal).append(current.node.printData());
                        current.printed = true;
                        if (current.hasChild()) {
                            util.insert(list, current, ++index);
                            util.pushChild(stack, current);
                            isLineHead = false;
                        } else {
                            sb.append(newLine);
                            util.printOffset(sb, offset);
                            index = 0;
                            isLineHead = true;
                        }
                        //跳出 for
                        break;
                    } else {
                        util.appendBlank(sb, previousLineNode, i);
                        if (previousLineNode.hasChildNotPrinted()) {
                            //abc_xx
                            //  |  |_yyy 这行的第一个竖线 遍历上一行的 abc 时 当前结点 yyy 不是 abc 的子结点，而且 abc 还有子结点没有输出
                            //  |_xyz
                            sb.append(vertical);
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
