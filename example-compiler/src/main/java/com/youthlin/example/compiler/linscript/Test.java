package com.youthlin.example.compiler.linscript;

import com.youthlin.example.compiler.linscript.semantic.SemanticValidator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author youthlin.chen
 * @date 2019-08-27 21:38
 */
public class Test {
    public static void main(String[] args) throws IOException {
        InputStream in = Test.class.getResourceAsStream("/test.y");
        YourLangLexer lexer = new YourLangLexer(CharStreams.fromStream(in));
        YourLangParser parser = new YourLangParser(new CommonTokenStream(lexer));
        YourLangParser.YourLangContext context = parser.yourLang();
        SemanticValidator semanticValidator = new SemanticValidator();
        semanticValidator.validate(context);
    }

}
