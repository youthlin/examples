package com.youthlin.example.compiler.linscript.semantic;

/**
 * @author youthlin.chen
 * @date 2019-09-03 11:46
 */
public class NullType implements IType {
    public static final NullType INSTANCE = new NullType();

    private NullType() {
    }

    @Override
    public String getTypeName() {
        return "null";
    }

    @Override
    public String toString() {
        return "NullType";
    }
}
