package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.youthlin.example.compiler.linscript.YourLangParser;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * 语义检查 semantics
 *
 * @author : youthlin.chen @ 2019-08-31 23:32
 */
@Slf4j
public class SemanticValidator {
    private Map<File, AnnotatedTree> fileAnnotatedTreeMap = Maps.newHashMap();
    private Set<AnnotatedTree> validated = Sets.newHashSet();

    public AnnotatedTree validate(YourLangParser.YourLangContext context, File file) {
        AnnotatedTree processed = fileAnnotatedTreeMap.get(file);
        if (processed != null) {
            return processed;
        }
        Util.setCurrentFile(file);
        AnnotatedTree at = new AnnotatedTree(context, file);
        fileAnnotatedTreeMap.put(file, at);
        ParseTreeWalker walker = new ParseTreeWalker();
        //第一趟 构造作用域、类型
        SymbolTypeScopeScanner symbolTypeScopeScanner = new SymbolTypeScopeScanner(at, this);
        walker.walk(symbolTypeScopeScanner, at.getTree());
        System.out.println(at.getGlobalScope().print());

        //第二趟 为符号设置类型
        TypeResolver typeResolver = new TypeResolver(at);
        at.setTypeResolved(true);
        walker.walk(typeResolver, at.getTree());

        return at;
    }

    public void validate2(AnnotatedTree at) {
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
        System.err.println(at.showError());
    }

}
