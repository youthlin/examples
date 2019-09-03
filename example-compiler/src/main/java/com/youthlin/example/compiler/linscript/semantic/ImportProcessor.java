package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.linscript.YourLangParserBaseListener;

/**
 * @author youthlin.chen
 * @date 2019-09-03 11:52
 */
public class ImportProcessor extends YourLangParserBaseListener {
    private AnnotatedTree at;

    public ImportProcessor(AnnotatedTree at) {
        this.at = at;
    }
}
