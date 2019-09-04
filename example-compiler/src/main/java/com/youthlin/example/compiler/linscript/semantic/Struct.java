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
public class Struct extends AbstractScopedSymbol implements IType {
    static final Struct ANY = new Struct("Any", null);
    /**
     * 默认父类
     */
    private Struct superStruct = ANY;
    private List<Interface> superInterfaces;
    private List<Method> constructors;

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
