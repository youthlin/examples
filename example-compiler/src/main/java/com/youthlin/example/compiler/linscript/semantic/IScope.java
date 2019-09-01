package com.youthlin.example.compiler.linscript.semantic;

import java.util.List;

/**
 * 作用域
 * 全局作用域、结构体作用域、方法作用域、块作用域等
 *
 * @author : youthlin.chen @ 2019-08-31 22:32
 */
public interface IScope {
    /**
     * 返回该作用域下的符号
     */
    List<ISymbol> getSymbols();

    /**
     * 返回父作用域，全局作用域的父作用域返回 null
     */
    IScope getParent();

    /**
     * 返回子作用域，如全局作用域中可以有结构体、块作用域
     */
    List<IScope> getChildScopes();

    String getScopeName();
}
