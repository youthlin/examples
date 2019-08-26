package com.youthlin.demo.antlr.calc;

import com.google.common.collect.Lists;
import com.youthlin.example.tree.TreePrinter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * 创建: youthlin.chen
 * 时间: 2018-05-08 10:21
 */
public class CalcMain {
    public static void main(String[] args) throws Exception {
        InputStream is = args.length > 0 ? new FileInputStream(args[0]) : System.in;

        CalcLexer lexer = new CalcLexer(CharStreams.fromStream(is));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CalcParser parser = new CalcParser(tokens);
        ParseTree tree = parser.prog();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new DirectiveListener(), tree);

//        EvalVisitor eval = new EvalVisitor();
//        //开始遍历语法分析树
//        eval.visit(tree);

        System.out.println(tree.toStringTree(parser));

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
                int len = CalcParser.ruleNames.length;
                if (ruleIndex >= 0 && ruleIndex < len) {
                    return CalcParser.ruleNames[ruleIndex];
                }
            }
            return node.getText();
        }, TreePrinter.Option.DEFAULT));

    }
}
