package com.youthlin.example.compiler.linscript.semantic;

import lombok.Getter;
import lombok.Setter;

/**
 * 数组类型
 *
 * @author : youthlin.chen @ 2019-08-31 23:11
 */
@Getter
@Setter
public class ArrayType implements IType {
    private IType elementType;

    @Override
    public String getTypeName() {
        return elementType.getTypeName() + "[]";
    }

}
