package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 带有作用域的符号
 * 如结构体、方法
 *
 * @author : youthlin.chen @ 2019-09-01 10:05
 */
@Getter
@Setter
public abstract class ScopedSymbol extends Symbol implements IScope {
    private IScope parent;
    private List<ISymbol> symbols = Lists.newArrayList();
    private List<IScope> childScopes = Lists.newArrayList();

    public ScopedSymbol(String name, IScope parent) {
        super(name, parent);
        this.parent = parent;
        parent.getChildScopes().add(this);
    }

    @Override
    public String getScopeName() {
        return getSymbolName();
    }

}
