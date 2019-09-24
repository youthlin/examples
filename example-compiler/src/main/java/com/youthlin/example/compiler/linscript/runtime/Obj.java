package com.youthlin.example.compiler.linscript.runtime;

import com.youthlin.example.compiler.linscript.semantic.IType;
import lombok.Data;
import lombok.NonNull;

/**
 * 对象
 *
 * @author youthlin.chen
 * @date 2019-09-05 17:12
 */
@Data
public class Obj {
    @NonNull
    private IType type;
    private Object value;

    public Obj(IType type) {
        this.type = type;
    }
}
