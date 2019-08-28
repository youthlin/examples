package com.youthlin.example.compiler;

import com.google.common.collect.Lists;
import com.youthlin.example.compiler.linscript.LinScriptParser;
import com.youthlin.example.tree.TreePrinter;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

/**
 * @author : youthlin.chen @ 2019-08-28 22:02
 */
public class Util {
    public static String treeToString(ParseTree root) {
        return TreePrinter.toString(root, node -> {
            int count = node.getChildCount();
            ArrayList<ParseTree> list = Lists.newArrayListWithExpectedSize(count);
            for (int i = 0; i < count; i++) {
                list.add(node.getChild(i));
            }
            return list;
        }, node -> {
            Object payload = node.getPayload();
            if (payload instanceof RuleContext) {
                int ruleIndex = ((RuleContext) payload).getRuleIndex();
                int len = LinScriptParser.ruleNames.length;
                if (ruleIndex >= 0 && ruleIndex < len) {
                    return LinScriptParser.ruleNames[ruleIndex];
                }
            }
            return node.getText();
        }, TreePrinter.Option.DEFAULT);
    }
}
