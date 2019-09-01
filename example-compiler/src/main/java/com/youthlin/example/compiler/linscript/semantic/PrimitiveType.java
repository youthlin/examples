package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.Jsons;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 基本类型
 *
 * @author : youthlin.chen @ 2019-08-31 23:02
 */
@Getter
@Setter
@AllArgsConstructor
public class PrimitiveType implements IType {
    public static final PrimitiveType INT = new PrimitiveType("int");
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean");
    public static final PrimitiveType STRING = new PrimitiveType("string");

    private String typeName;

    @Override
    public String toString() {
        return Jsons.toJson(this);
    }

}
