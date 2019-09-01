package com.youthlin.example.compiler.linscript.semantic;

import lombok.Data;

import java.util.List;

/**
 * 函数类型
 *
 * @author : youthlin.chen @ 2019-08-31 23:09
 */
@Data
public class FunctionType implements IType {
    private IType returnType;
    private List<IType> parameterType;

    public String getTypeName() {
        StringBuilder sb = new StringBuilder("fun ")
                .append(returnType.getTypeName())
                .append("(");
        if (parameterType != null) {
            int i = 0;
            for (IType parameterType : parameterType) {
                if (++i > 1) {
                    sb.append(", ");
                }
                sb.append(parameterType.getTypeName());
            }
        }
        return sb.append(")").toString();
    }

}
