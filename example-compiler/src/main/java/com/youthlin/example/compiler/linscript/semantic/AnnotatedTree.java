package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Maps;
import lombok.Data;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 带有语义的语法树
 *
 * @author : youthlin.chen @ 2019-08-31 23:15
 */
@Data
public class AnnotatedTree {
    private ParseTree tree;
    private GlobalScope globalScope = new GlobalScope();
    private Map<ParserRuleContext, IType> typeMap = Maps.newHashMap();
    private Map<ParserRuleContext, IScope> scopeMap = Maps.newHashMap();
    private Map<ParserRuleContext, ISymbol> symbolMap = Maps.newHashMap();
    private Map<ParserRuleContext, String> errorMap = Maps.newHashMap();

    public AnnotatedTree(ParseTree parseTree) {
        this.tree = parseTree;
    }

    public String showError() {
        return errorMap.entrySet().stream()
                .sorted((o1, o2) -> Comparator.nullsFirst(Comparator.comparingInt(Token::getLine)
                        .thenComparing(Token::getCharPositionInLine))
                        .compare(o1.getKey().start, o2.getKey().start))
                .map(entry -> {
                    Token start = entry.getKey().start;
                    Token end = entry.getKey().stop;
                    StringBuilder sb = new StringBuilder();
                    if (start != null) {
                        sb.append("start:[").append(start.getLine()).append(":")
                                .append(start.getCharPositionInLine() + 1)
                                .append("]");
                    }
                    if (end != null) {
                        sb.append("stop:[").append(end.getLine()).append(":")
                                .append(end.getCharPositionInLine() + 1).append("]");
                    }
                    sb.append(entry.getValue());
                    return sb.toString();
                }).collect(Collectors.joining("\n"));
    }

}
