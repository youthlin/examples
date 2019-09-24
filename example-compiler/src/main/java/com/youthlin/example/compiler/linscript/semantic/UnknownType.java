package com.youthlin.example.compiler.linscript.semantic;

/**
 * @author youthlin.chen
 * @date 2019-09-03 15:06
 */
public class UnknownType implements IType {
    public static final UnknownType INSTANCE = new UnknownType();

    private UnknownType() {
    }

    @Override
    public String getTypeName() {
        return "UnknownType";
    }

    @Override
    public String toString() {
        return getTypeName();
    }

}
