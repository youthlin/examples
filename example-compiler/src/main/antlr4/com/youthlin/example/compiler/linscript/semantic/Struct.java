package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * 结构体是类型、符号、作用域
 *
 * @author : youthlin.chen @ 2019-08-31 22:45
 */
@Data
public class Struct extends ScopedSymbol implements IType {
    @Getter(AccessLevel.NONE)
    private List<ISymbol> fields = Lists.newArrayList();
    @Getter(AccessLevel.NONE)
    private List<Method> methods = Lists.newArrayList();

    public Struct(String name, IScope parent) {
        super(name, parent);
    }

    public void addField(ISymbol field) {
        fields.add(field);
        getSymbols().add(field);
    }

    public void addMethod(Method method) {
        methods.add(method);
        getSymbols().add(method);
        getChildScopes().add(method);
    }

    @Override
    public String getTypeName() {
        return "struct " + getSymbolName();
    }

}
