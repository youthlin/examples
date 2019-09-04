package com.youthlin.example.compiler.linscript;

import com.youthlin.example.compiler.linscript.semantic.SemanticValidator;

import java.io.File;
import java.io.IOException;

/**
 * @author youthlin.chen
 * @date 2019-08-27 21:38
 */
public class Test {
    public static void main(String[] args) throws IOException {
        File file = new File(".", "example-compiler/src/main/yourscript/PrimitiveTypeTest.y");
        File dir = new File(file.getParent(), "lib");
        System.out.println(file.getAbsolutePath());
        SemanticValidator semanticValidator = new SemanticValidator();
        semanticValidator.validate(file, dir);

    }

}
