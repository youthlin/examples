package com.youthlin.example.compiler.linscript.semantic;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 符号的基类、普通变量
 *
 * @author : youthlin.chen @ 2019-08-31 22:45
 */
@Data @AllArgsConstructor
public class Symbol implements ISymbol {
    private String symbolName;
    private IScope scope;

}
