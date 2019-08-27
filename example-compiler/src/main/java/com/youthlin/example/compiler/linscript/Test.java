package com.youthlin.example.compiler.linscript;

import com.google.common.collect.Lists;
import com.youthlin.example.tree.TreePrinter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author youthlin.chen
 * @date 2019-08-27 21:38
 */
public class Test {
    public static void main(String[] args) throws IOException {
        InputStream in = Test.class.getResourceAsStream("main.0");
        LinScriptLexer lexer = new LinScriptLexer(CharStreams.fromStream(in));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LinScriptParser parser = new LinScriptParser(tokens);
        ParseTree tree = parser.script();
        System.out.println(TreePrinter.toString(tree, node -> {
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
        }, TreePrinter.Option.DEFAULT));
    }
}
