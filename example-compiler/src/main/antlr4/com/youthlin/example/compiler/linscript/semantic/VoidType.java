package com.youthlin.example.compiler.linscript.semantic;

import lombok.Data;

/**
 * void 类型
 *
 * @author : youthlin.chen @ 2019-08-31 23:10
 */
@Data
public class VoidType implements IType {
    public static final VoidType INSTANCE = new VoidType();

    private VoidType() {
    }

    @Override
    public String getTypeName() {
        return "void";
    }

}
