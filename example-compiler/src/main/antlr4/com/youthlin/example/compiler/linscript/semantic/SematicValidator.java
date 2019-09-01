package com.youthlin.example.compiler.linscript.semantic;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * 语义检查
 *
 * @author : youthlin.chen @ 2019-08-31 23:32
 */
public class SematicValidator {
    public boolean validate(ParseTree tree) {
        //语义树
        AnnotatedTree at = new AnnotatedTree(tree);
        //第一趟 构造作用域、类型
        TypeAndScopeScanner typeAndScopeScanner = new TypeAndScopeScanner(at);
        ParseTreeWalker walker = new ParseTreeWalker();
        //遍历语法树 执行第一趟遍历
        walker.walk(typeAndScopeScanner, at.getTree());

        return true;
    }

}
