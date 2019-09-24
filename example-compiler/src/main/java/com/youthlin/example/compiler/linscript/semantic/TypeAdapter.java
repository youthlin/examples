package com.youthlin.example.compiler.linscript.semantic;

/**
 * @author youthlin.chen
 * @date 2019-09-03 15:05
 */
public class TypeAdapter implements IType {
    private IType type = UnknownType.INSTANCE;

    @Override
    public String getTypeName() {
        return type.getTypeName();
    }

    @Override
    public String toString() {
        return "TypeAdapter(" + type + ")";
    }

}
