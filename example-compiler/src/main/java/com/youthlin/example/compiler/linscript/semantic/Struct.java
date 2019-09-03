package com.youthlin.example.compiler.linscript.semantic;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 结构体是类型、符号、作用域
 *
 * @author : youthlin.chen @ 2019-08-31 22:45
 */
@Getter
@Setter
public class Struct extends ScopedSymbol implements IType {
    public static final Struct ANY = new Struct("Any", new GlobalScope());
    private Struct superStruct = ANY;
    private List<Interface> superInterfaces;

    public Struct(String name, IScope parent) {
        super(name, parent);
        setType(this);
    }

    @Override
    public String getTypeName() {
        return "struct " + getSymbolName();
    }

    @Override
    public String getScopeName() {
        return getTypeName();
    }

    @Override
    public Kind getKind() {
        return Kind.Struct;
    }

    @Override
    public String toString() {
        return String.format("Struct %s", getSymbolName());
    }

}
