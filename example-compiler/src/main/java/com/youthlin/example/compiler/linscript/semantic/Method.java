package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.Jsons;
import lombok.AccessLevel;
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
public class Method extends ScopedSymbol {
    private IType returnType;
    private List<IType> parameterType;
    private List<String> parameterTypeName;
    @Getter(AccessLevel.NONE)
    private FunctionType functionType;

    public Method(String name, IScope parent) {
        super(name, parent);
    }

    public FunctionType toFunctionType() {
        if (functionType == null) {
            functionType = new FunctionType(this);
            functionType.setReturnType(Objects.requireNonNull(returnType));
            functionType.setParameterType(Objects.requireNonNull(parameterType));
        }
        return functionType;

    }

    @Override
    public String toString() {
        return Jsons.toJson(this);
    }

}
