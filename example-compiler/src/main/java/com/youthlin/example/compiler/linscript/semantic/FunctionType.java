package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 函数类型
 *
 * @author : youthlin.chen @ 2019-08-31 23:09
 */
@Getter
@Setter
@ToString
public class FunctionType implements IType {
    private IType returnType;
    private List<IType> parameterType = Lists.newArrayList();

    public FunctionType() {
    }

    @Override
    public String getTypeName() {
        StringBuilder sb = new StringBuilder("fun ")
                .append(returnType == null ? "<returnType>" : returnType.getTypeName())
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
