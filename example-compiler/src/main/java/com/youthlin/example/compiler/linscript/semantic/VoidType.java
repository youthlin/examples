package com.youthlin.example.compiler.linscript.semantic;

/**
 * void 类型
 *
 * @author : youthlin.chen @ 2019-08-31 23:10
 */
public class VoidType implements IType {
    public static final VoidType INSTANCE = new VoidType();

    private VoidType() {
    }

    @Override
    public String getTypeName() {
        return "void";
    }

    @Override
    public String toString() {
        return getTypeName();
    }

}
