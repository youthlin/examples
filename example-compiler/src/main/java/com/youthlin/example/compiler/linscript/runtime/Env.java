package com.youthlin.example.compiler.linscript.runtime;

import com.youthlin.example.compiler.linscript.semantic.IType;

/**
 * 运行环境
 *
 * @author youthlin.chen
 * @date 2019-09-05 17:11
 */
public class Env {

    public IType getType(Obj obj) {
        return obj.getType();
    }


}
