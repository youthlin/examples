package com.youthlin.example.compiler.linscript;

import com.youthlin.example.compiler.Util;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;

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
        System.out.println(Util.treeToString(tree));
    }
}
