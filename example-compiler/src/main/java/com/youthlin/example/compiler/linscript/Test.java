package com.youthlin.example.compiler.linscript;

import com.youthlin.example.compiler.linscript.semantic.AnnotatedTree;
import com.youthlin.example.compiler.linscript.semantic.SemanticValidator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.IOException;

/**
 * @author youthlin.chen
 * @date 2019-08-27 21:38
 */
public class Test {
    public static void main(String[] args) throws IOException {
        File file = new File(".", "example-compiler/src/main/yourscript/test.y");
        System.out.println(file.getAbsolutePath());
        YourLangLexer lexer = new YourLangLexer(CharStreams.fromPath(file.toPath()));
        YourLangParser parser = new YourLangParser(new CommonTokenStream(lexer));
        YourLangParser.YourLangContext context = parser.yourLang();
        SemanticValidator semanticValidator = new SemanticValidator();
        AnnotatedTree at = semanticValidator.validate(context, file);
        semanticValidator.validate(at);
    }

}
