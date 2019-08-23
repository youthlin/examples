package com.youthlin.example.compiler.simplescript;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

/**
 * @author youthlin.chen
 * @date 2019-08-23 11:40
 */
@Data
@Accessors(chain = true)
public class TreeNode {
    private TreeNodeType type;
    private String value;
    private List<TreeNode> children;

    public TreeNode() {
        this(TreeNodeType.Empty);
    }

    public TreeNode(TreeNodeType type) {
        this(type, type.name());
    }

    public TreeNode(TreeNodeType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TreeNode addChild(TreeNode... child) {
        if (children == null) {
            children = Lists.newArrayList();
        }
        children.addAll(Arrays.asList(child));
        return this;
    }

}
