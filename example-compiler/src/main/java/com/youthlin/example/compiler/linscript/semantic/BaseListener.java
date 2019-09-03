package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.linscript.YourLangParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;

/**
 * @author youthlin.chen
 * @date 2019-09-03 17:15
 */
public class BaseListener extends YourLangParserBaseListener {
    protected AnnotatedTree at;

    BaseListener(AnnotatedTree at) {
        this.at = at;
    }

    protected void error(Logger log, ParserRuleContext ctx, String msg) {
        at.getErrorMap().put(ctx, msg);
        log.warn(msg);
    }

}
