package com.youthlin.example.compiler.linscript.semantic;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * 语义检查 semantics
 *
 * @author : youthlin.chen @ 2019-08-31 23:32
 */
public class SemanticValidator {
    public boolean validate(ParseTree tree) {
        //语义树
        AnnotatedTree at = new AnnotatedTree(tree);
        //第一趟 构造作用域、类型
        SymbolTypeScopeScanner symbolTypeScopeScanner = new SymbolTypeScopeScanner(at);
        ParseTreeWalker walker = new ParseTreeWalker();
        //遍历语法树 执行第一趟遍历 识别作用域、符号、类型
        walker.walk(symbolTypeScopeScanner, at.getTree());
        System.err.println(at.showError());
        System.out.println(at.getGlobalScope().print());
        //第二趟 为符号设置类型
        TypeResolver typeResolver = new TypeResolver(at);
        walker.walk(typeResolver, at.getTree());

        return true;
    }

}
