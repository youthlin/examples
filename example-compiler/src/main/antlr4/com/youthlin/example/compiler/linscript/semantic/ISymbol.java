package com.youthlin.example.compiler.linscript.semantic;

/**
 * 符号
 * 如变量标识符、结构体名、方法名等
 *
 * @author : youthlin.chen @ 2019-08-31 22:32
 */
public interface ISymbol {
    /**
     * 返回符号名称
     */
    String getSymbolName();

    /**
     * 该符号所在作用域
     */
    IScope getScope();

}
