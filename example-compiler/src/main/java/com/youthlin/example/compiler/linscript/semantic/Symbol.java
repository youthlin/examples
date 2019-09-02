package com.youthlin.example.compiler.linscript.semantic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * 符号的基类、普通变量
 *
 * @author : youthlin.chen @ 2019-08-31 22:45
 */
@Getter
@Setter
public abstract class Symbol implements ISymbol {
    private String symbolName;
    @JsonIgnore
    private IScope scope;
    private IType type;

    public Symbol(String symbolName, IScope scope) {
        this.symbolName = symbolName;
        this.scope = scope;
        scope.getSymbols().add(this);
    }

}
