package com.youthlin.example.compiler.linscript.semantic;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * 结构体的方法
 * 是符号、作用域
 *
 * @author : youthlin.chen @ 2019-09-01 10:04
 */
@Data
public class Method extends ScopedSymbol {
    private IType returnType;
    private List<IType> parameterType;
    @Getter(AccessLevel.NONE)
    private FunctionType functionType;

    public Method(String name, IScope parent) {
        super(name, parent);
    }

    public FunctionType toFunctionType() {
        if (functionType == null) {
            functionType = new FunctionType();
            functionType.setReturnType(Objects.requireNonNull(returnType));
            functionType.setParameterType(Objects.requireNonNull(parameterType));
        }
        return functionType;

    }

}
