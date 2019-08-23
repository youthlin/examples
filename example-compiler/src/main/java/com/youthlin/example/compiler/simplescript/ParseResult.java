package com.youthlin.example.compiler.simplescript;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author youthlin.chen
 * @date 2019-08-23 14:07
 */
@Data
@Accessors(chain = true)
public class ParseResult {
    private TreeNode root;
    private List<Error> errorList;

    @Data
    @AllArgsConstructor
    public static class Error {
        private int pos;
        private String message;
    }

    public void dump() {
        if (errorList != null && !errorList.isEmpty()) {
            errorList.forEach(System.err::println);
        } else {
            print("", root);
        }
    }

    private void print(String prefix, TreeNode node) {
        if (node == null) {
            System.out.println(prefix + "/null");
            return;
        }
        List<TreeNode> children = node.getChildren();
        String text = prefix + "/" + node.getValue();
        if (children != null && !children.isEmpty()) {
            children.forEach(child -> print(text, child));
        } else {
            System.out.println(text);
        }
    }
}
