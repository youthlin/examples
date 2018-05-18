package com.youthlin.demo.antlr.snl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 创建: youthlin.chen
 * 时间: 2018-05-08 19:29
 */
public class SnlTest {
    public static void main(String[] args) throws IOException {
        InputStream is = args.length > 0 ? new FileInputStream(args[0]) : System.in;

        SNLLexer lexer = new SNLLexer(CharStreams.fromStream(is));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SNLParser snlParser = new SNLParser(tokens);
        ParseTree tree = snlParser.program();
        System.out.println(tree.toStringTree(snlParser));
    }

}
