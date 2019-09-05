package com.youthlin.example.compiler.linscript.semantic;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * 结构体的方法
 * 是符号、作用域
 *
 * @author : youthlin.chen @ 2019-09-01 10:04
 */
@Getter
@Setter
public class Method extends AbstractScopedSymbol {
    private boolean isNative;
    private IType returnType;
    private List<IType> parameterType;

    public Method(String name, IScope parent) {
        super(name, parent);
    }

    public void done() {
        FunctionType functionType = new FunctionType();
        functionType.setReturnType(Objects.requireNonNull(returnType));
        functionType.setParameterType(Objects.requireNonNull(parameterType));
        setType(functionType);
    }

    @Override
    public Kind getKind() {
        return Kind.Method;
    }

    @Override
    public String toString() {
        return "Method " + getSymbolName();
    }
}
