package com.youthlin.example.compiler.linscript.semantic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youthlin.example.compiler.Jsons;
import lombok.Getter;
import lombok.Setter;

/**
 * 符号的基类、普通变量
 *
 * @author : youthlin.chen @ 2019-08-31 22:45
 */
@Getter
@Setter
public class Symbol implements ISymbol {
    private String symbolName;
    @JsonIgnore
    private IScope scope;

    public Symbol(String symbolName, IScope parent) {
        this.symbolName = symbolName;
        this.scope = parent;
        parent.getSymbols().add(this);
    }

    @Override
    public String toString() {
        return Jsons.toJson(this);
    }

}
