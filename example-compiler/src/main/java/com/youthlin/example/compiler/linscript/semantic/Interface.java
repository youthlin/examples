package com.youthlin.example.compiler.linscript.semantic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : youthlin.chen @ 2019-09-01 13:36
 */
@Getter
@Setter
public class Interface extends ScopedSymbol implements IType {
    public Interface(String name, IScope parent) {
        super(name, parent);
        setType(this);
    }

    @Override
    public String getTypeName() {
        return "interface " + getSymbolName();
    }

    @Override
    public String getScopeName() {
        return getTypeName();
    }

}
