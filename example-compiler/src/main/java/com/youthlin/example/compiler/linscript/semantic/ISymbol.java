package com.youthlin.example.compiler.linscript.semantic;

/**
 * 符号
 * 如变量标识符、结构体名、方法名等
 *
 * @author : youthlin.chen @ 2019-08-31 22:32
 */
public interface ISymbol {
    enum Kind {
        //结构体标识符 结构体名称
        Struct,
        //接口标识符
        Interface,
        //结构体或接口的方法
        Method,
        //变量
        Variable,
        //接口常量
        Constant,
        //语句标签
        Label,
    }

    Kind getKind();

    /**
     * 返回符号名称
     */
    String getSymbolName();

    /**
     * 该符号所在作用域
     */
    IScope getScope();

    IType getType();

}
