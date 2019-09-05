package com.youthlin.example.compiler.linscript.runtime.lib.std;

import com.youthlin.example.compiler.linscript.runtime.Env;
import com.youthlin.example.compiler.linscript.runtime.Obj;

/**
 * @author youthlin.chen
 * @date 2019-09-05 16:56
 */
public class Printer {
    public static void println(Env env, Obj obj, Obj any) {

        System.out.println(any.getValue());
    }

}
