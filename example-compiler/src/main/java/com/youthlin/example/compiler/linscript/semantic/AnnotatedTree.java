package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 带有语义的语法树
 *
 * @author : youthlin.chen @ 2019-08-31 23:15
 */
@Getter
@Setter
public class AnnotatedTree {
    private File file;
    private ParseTree tree;
    private boolean typeResolved;
    private GlobalScope globalScope = new GlobalScope();
    private Map<ParserRuleContext, IType> typeMap = Maps.newHashMap();
    private Map<ParserRuleContext, IScope> scopeMap = Maps.newHashMap();
    private Map<ParserRuleContext, ISymbol> symbolMap = Maps.newHashMap();
    private Map<ParserRuleContext, String> errorMap = Maps.newHashMap();
    private List<ImportSymbol> importSymbols = Lists.newArrayList();
    private List<Symbol> exportSymbols = Lists.newArrayList();
    private List<AnnotatedTree> exportTo = Lists.newArrayList();
    private List<AnnotatedTree> importFrom = Lists.newArrayList();

    public AnnotatedTree(ParseTree parseTree, File file) {
        this.file = file;
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

    public IType findTypeSinceScope(String name, IScope scope) {
        while (scope != null) {
            for (ISymbol symbol : scope.getSymbols()) {
                if (symbol instanceof Struct || symbol instanceof Interface || symbol instanceof ImportSymbol) {
                    if (Objects.equals(symbol.getSymbolName(), name)) {
                        return (IType) symbol;
                    }
                }
            }
            scope = scope.getParent();
        }
        return null;
    }

    public IScope getScope(ParserRuleContext ctx) {
        return scopeMap.get(ctx);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
