package com.youthlin.example.compiler.linscript.semantic;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : youthlin.chen @ 2019-09-01 13:36
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Interface extends AbstractScopedSymbol implements IType {
    private List<Interface> superInterfaces;

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

    @Override
    public Kind getKind() {
        return Kind.Interface;
    }

    @Override
    public String toString() {
        return String.format("Interface %s", getSymbolName());
    }
}
