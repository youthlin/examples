package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.youthlin.example.compiler.linscript.YourLangLexer;
import com.youthlin.example.compiler.linscript.YourLangParser;
import com.youthlin.example.compiler.linscript.runtime.Runner;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 语义检查 semantics
 *
 * @author : youthlin.chen @ 2019-08-31 23:32
 */
@Slf4j
public class SemanticValidator {
    private boolean stdFileLoaded;
    private Map<File, AnnotatedTree> fileAnnotatedTreeMap = Maps.newHashMap();
    private Set<AnnotatedTree> validated = Sets.newHashSet();

    public void validate(File file, File stdLibDir) throws IOException {
        YourLangLexer lexer = new YourLangLexer(CharStreams.fromPath(file.toPath()));
        YourLangParser parser = new YourLangParser(new CommonTokenStream(lexer));
        YourLangParser.YourLangContext context = parser.yourLang();
        if (!stdFileLoaded) {
            log.info("========== load std lib ==============");
            stdFileLoaded = true;
            File[] stdFiles = stdLibDir.listFiles((dir, name) -> name.endsWith(".y"));
            if (stdFiles != null) {
                for (File stdFile : stdFiles) {
                    validate(stdFile, stdLibDir);
                }
            }
            log.info("========== loaded std lib ==============\n");
        }
        AnnotatedTree at = validate1(context, file, stdLibDir);
        validate2(at);
        Runner runner = new Runner(at);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(runner, at.getTree());
    }

    AnnotatedTree validate1(YourLangParser.YourLangContext context, File file, File stdLibDir) {
        AnnotatedTree processed = fileAnnotatedTreeMap.get(file);
        if (processed != null) {
            return processed;
        }
        Util.setCurrentFile(file);
        AnnotatedTree at = new AnnotatedTree(context, file);
        at.setStdLibDir(stdLibDir);
        fileAnnotatedTreeMap.put(file, at);
        ParseTreeWalker walker = new ParseTreeWalker();
        //第一趟 构造作用域、类型 因为遍历的过程中可能有的符号还没有声明 所以这一步还不能确定类型
        SymbolTypeScopeScanner symbolTypeScopeScanner = new SymbolTypeScopeScanner(at, this);
        walker.walk(symbolTypeScopeScanner, at.getTree());
        System.out.println(at.getFileScope().print());

        //第二趟 为符号设置类型
        TypeResolver typeResolver = new TypeResolver(at);
        at.setTypeResolved(true);
        walker.walk(typeResolver, at.getTree());

        return at;
    }

    private void validate2(AnnotatedTree at) {
        if (validated.contains(at)) {
            return;
        }
        validated.add(at);
        for (AnnotatedTree from : at.getImportFrom()) {
            validate2(from);
        }
        Util.setCurrentFile(at.getFile());
        log.info("========== 类型推断 {} ==========", at.getFile());
        ParseTreeWalker walker = new ParseTreeWalker();
        //第三趟 类型推断
        TypeInfer typeInfer = new TypeInfer(at);
        walker.walk(typeInfer, at.getTree());
        if (!at.getErrorMap().isEmpty()) {
            System.err.println(at.showError());
        }
    }

}
