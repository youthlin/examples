package com.youthlin.example.compiler.linscript.runtime;

import com.google.common.collect.Maps;
import com.youthlin.example.compiler.linscript.YourLangParser;
import com.youthlin.example.compiler.linscript.YourLangParserBaseListener;
import com.youthlin.example.compiler.linscript.semantic.AnnotatedTree;
import com.youthlin.example.compiler.linscript.semantic.ArrayType;
import com.youthlin.example.compiler.linscript.semantic.Constant;
import com.youthlin.example.compiler.linscript.semantic.IType;
import com.youthlin.example.compiler.linscript.semantic.Interface;
import com.youthlin.example.compiler.linscript.semantic.Method;
import com.youthlin.example.compiler.linscript.semantic.Struct;
import com.youthlin.example.compiler.linscript.semantic.Symbol;
import com.youthlin.example.compiler.linscript.semantic.Util;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * @author youthlin.chen
 * @date 2019-09-05 18:54
 */
public class Runner extends YourLangParserBaseListener {
    private AnnotatedTree at;
    private Stack<Obj> stack = new Stack<>();
    private Map<Long, Obj> heap = Maps.newHashMap();
    private long nextAddr = 0L;

    private long getNextAddr() {
        return nextAddr++;
    }

    public Runner(AnnotatedTree at) {
        this.at = at;
    }

    @Override
    public void enterExpression(YourLangParser.ExpressionContext ctx) {
        if (ctx.call != null) {
            IType retType = at.getTypeMap().get(ctx);
            IType leftType = at.getTypeMap().get(ctx.leftExp);
            if (leftType instanceof Struct) {
                Obj obj = new Obj(leftType);
                stack.push(obj);
            }
        }
        if (ctx.bop != null) {
            if (ctx.IDENTIFIER() != null) {
                String id = ctx.IDENTIFIER().getText();
                IType leftType = at.getTypeMap().get(ctx.leftExp);
                if (ctx.call == null) {
                    //取字段
                    if (leftType instanceof Interface) {
                        Symbol field = Util.findFieldSince((Interface) leftType, id);
                        Objects.requireNonNull(field);
                        Obj obj = heap.get(field.getAddr());
                        stack.push(obj);
                    }
                } else {
                    //方法调用
                    Method method = Util.findMethodSince((Struct) leftType, id);
                    Objects.requireNonNull(method);
                    pushArgs(ctx.expressionList());
                    if (method.isNative()) {
                        //todo
                    }
                }
            }
        }
    }

    private void pushArgs(YourLangParser.ExpressionListContext ctx) {
        if (ctx == null) {
            return;
        }
        for (YourLangParser.ExpressionContext expressionContext : ctx.expression()) {
            pushArg(expressionContext);
        }
    }

    private void pushArg(YourLangParser.ExpressionContext ctx) {
        YourLangParser.PrimaryContext primary = ctx.primary();
        if (primary != null) {
            YourLangParser.LiteralContext literal = primary.literal();
            if (literal != null) {
                TerminalNode stringLiteral = literal.STRING_LITERAL();
                if (stringLiteral != null) {
                    Obj obj = new Obj(ArrayType.CHAR_ARRAY);
                    String text = stringLiteral.getText();
                    text = text.substring(1, text.length() - 1);
                    obj.setValue(text.toCharArray());
                    stack.push(obj);
                }
            }
        }
    }

    @Override
    public void exitConstantDeclarator(YourLangParser.ConstantDeclaratorContext ctx) {
        Constant constant = (Constant) at.getSymbolMap().get(ctx);
        Obj obj = stack.pop();
        long addr = getNextAddr();
        constant.setAddr(addr);
        heap.put(addr, obj);
    }

}
