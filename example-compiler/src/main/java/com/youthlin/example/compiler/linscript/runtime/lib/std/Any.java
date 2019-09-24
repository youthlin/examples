package com.youthlin.example.compiler.linscript.runtime.lib.std;

import com.youthlin.example.compiler.linscript.runtime.Env;
import com.youthlin.example.compiler.linscript.runtime.Obj;
import com.youthlin.example.compiler.linscript.semantic.IType;

/**
 * @author youthlin.chen
 * @date 2019-09-05 17:30
 */
public class Any {
    public String toString(Env env, Obj obj) {
        IType type = obj.getType();
        int hashCode = System.identityHashCode(obj);
        if (type != null) {
            return type.getTypeName() + "@" + hashCode;
        }
        return "Unknown Type@" + hashCode;
    }

}
