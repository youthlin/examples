package com.youthlin.example.compiler.linscript.semantic;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 基本类型
 *
 * @author : youthlin.chen @ 2019-08-31 23:02
 */
@Data
@AllArgsConstructor
public class PrimitiveType implements IType {
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean");
    public static final PrimitiveType INT = new PrimitiveType("int");
    public static final PrimitiveType FLOAT = new PrimitiveType("float");
    public static final PrimitiveType STRING = new PrimitiveType("string");

    private String typeName;

    @Override
    public String toString() {
        return typeName;
    }

    public static PrimitiveType of(String name) {
        switch (name) {
            case "int":
                return PrimitiveType.INT;
            case "float":
                return PrimitiveType.FLOAT;
            case "boolean":
                return PrimitiveType.BOOLEAN;
            case "string":
                return PrimitiveType.STRING;
            default:
        }
        return null;
    }
}
