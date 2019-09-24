package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 基本类型
 * boolean byte char int float
 *
 * @author : youthlin.chen @ 2019-08-31 23:02
 */
@AllArgsConstructor
public enum PrimitiveType implements IType {
    //
    BOOLEAN("boolean"),
    BYTE("byte"),
    CHAR("char"),
    INT("int"),
    FLOAT("float"),
    ;
    @Getter
    private String typeName;

    @Override
    public String toString() {
        return typeName;
    }

    private static Map<String, PrimitiveType> map = Maps.newHashMap();

    static {
        for (PrimitiveType primitiveType : PrimitiveType.values()) {
            map.put(primitiveType.getTypeName(), primitiveType);
        }
    }

    public static PrimitiveType of(String name) {
        return map.get(name);
    }

}
