package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.linscript.YourLangParserBaseListener;

/**
 * 第一趟遍历语法树 识别出类型和作用域
 *
 * @author : youthlin.chen @ 2019-08-31 22:53
 */
public class TypeAndScopeScanner extends YourLangParserBaseListener {
    private AnnotatedTree annotatedTree;

    public TypeAndScopeScanner(AnnotatedTree tree) {
        annotatedTree = tree;
    }

}
