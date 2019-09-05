package com.youthlin.example.compiler.linscript.runtime.lib.std;

import com.youthlin.example.compiler.linscript.runtime.Env;
import com.youthlin.example.compiler.linscript.runtime.Obj;

import java.util.Scanner;

/**
 * @author youthlin.chen
 * @date 2019-09-05 16:57
 */
public class Reader {
    private Scanner scanner = new Scanner(System.in);

    public void readLine(Env env, Obj obj) {
        String s = scanner.nextLine();

    }

}
