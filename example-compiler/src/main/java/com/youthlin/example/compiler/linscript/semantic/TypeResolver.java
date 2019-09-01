package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.linscript.YourLangParserBaseListener;

/**
 * 为每个符号赋上正确的类型
 *
 * @author : youthlin.chen @ 2019-09-01 17:17
 */
public class TypeResolver extends YourLangParserBaseListener {
    private AnnotatedTree at;

    public TypeResolver(AnnotatedTree tree) {
        this.at = tree;
    }

}
