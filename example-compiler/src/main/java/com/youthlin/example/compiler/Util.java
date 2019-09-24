package com.youthlin.example.compiler;

import com.google.common.collect.Lists;
import com.youthlin.example.tree.TreePrinter;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author : youthlin.chen @ 2019-08-28 22:02
 */
public class Util {
    public static String treeToString(Parser parser, ParseTree root) {
        Function<ParseTree, List<ParseTree>> getChildren = node -> {
            int count = node.getChildCount();
            ArrayList<ParseTree> list = Lists.newArrayListWithExpectedSize(count);
            for (int i = 0; i < count; i++) {
                list.add(node.getChild(i));
            }
            return list;
        };
        Function<ParseTree, String> toString = node -> {
            Object payload = node.getPayload();
            if (payload instanceof RuleContext) {
                int ruleIndex = ((RuleContext) payload).getRuleIndex();
                return parser.getRuleNames()[ruleIndex];
            }
            return node.getText();
        };
        return TreePrinter.toString(root, getChildren, toString, TreePrinter.Option.DEFAULT);
    }

}
