package com.youthlin.example.compiler.linscript.semantic;

import lombok.Data;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.Map;

/**
 * 带有语义的语法树
 *
 * @author : youthlin.chen @ 2019-08-31 23:15
 */
@Data
public class AnnotatedTree {
    private ParseTree tree;
    private List<IType> types;
    private GlobalScope globalScope;
    private Map<ParserRuleContext, IType> typeMap;
    private Map<ParserRuleContext, IScope> scopeMap;
    private Map<ParserRuleContext, ISymbol> symbolMap;

    public AnnotatedTree(ParseTree parseTree) {
        this.tree = parseTree;
    }

}
