package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.linscript.YourLangParser;
import com.youthlin.example.compiler.linscript.YourLangParserBaseListener;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Map;
import java.util.Objects;

/**
 * 类型推断
 *
 * @author : youthlin.chen @ 2019-09-02 19:39
 */
@Slf4j
public class TypeInfer extends YourLangParserBaseListener {
    private AnnotatedTree at;

    public TypeInfer(AnnotatedTree at) {
        this.at = at;
    }

    @Override
    public void exitPrimary(YourLangParser.PrimaryContext primary) {
        if (primary.expression() != null) {
            at.getTypeMap().put(primary, at.getTypeMap().get(primary.expression()));
            return;
        }

        IScope scope = at.getScopeMap().get(primary);
        if (primary.THIS() != null) {
            while (scope != null && !(scope instanceof Struct)) {
                scope = scope.getParent();
            }
            if (scope != null) {
                at.getTypeMap().put(primary, (Struct) scope);
                log.info("识别出 this 是结构类型{}", scope);
            } else {
                at.getErrorMap().put(primary, "'this' can not use here");
            }
            return;
        }

        YourLangParser.LiteralContext literal = primary.literal();
        if (literal != null) {
            IType type = null;
            if (literal.BOOL_LITERAL() != null) {
                type = PrimitiveType.BOOLEAN;
            }
            if (literal.integerLiteral() != null) {
                type = PrimitiveType.INT;
            }
            if (literal.floatLiteral() != null) {
                type = PrimitiveType.FLOAT;
            }
            if (literal.STRING_LITERAL() != null) {
                type = PrimitiveType.STRING;
            }
            if (type == null) {
                at.getErrorMap().put(primary, "暂不支持的字面量类型: " + literal.getText());
            }
            at.getTypeMap().put(primary, type);
            log.info("识别出字面量 {} 的类型: {}", literal.getText(), type);
            return;
        }

        if (primary.IDENTIFIER() != null) {
            String id = primary.IDENTIFIER().getText();
            IType type = null;
            label:
            while (scope != null) {
                for (ISymbol symbol : scope.getSymbols()) {
                    if (Objects.equals(symbol.getSymbolName(), id)) {
                        type = symbol.getType();
                        break label;
                    }
                }
                scope = scope.getParent();
            }
            if (type == null) {
                at.getErrorMap().put(primary, "Unknown symbol: " + id);
                log.warn("Unknown symbol: {}", id);
            } else {
                at.getTypeMap().put(primary, type);
                log.info("识别出标识符 {} 的类型 {}", id, type);
            }
        }

    }

    @Override
    public void exitExpression(YourLangParser.ExpressionContext ctx) {
        Map<ParserRuleContext, IType> typeMap = at.getTypeMap();
        if (ctx.primary() != null) {
            typeMap.put(ctx, typeMap.get(ctx.primary()));
            return;
        }
        YourLangParser.ExpressionContext leftExp = ctx.leftExp;
        YourLangParser.ExpressionContext rightExp = ctx.rightExp;
        YourLangParser.ExpressionContext midExp = ctx.midExp;
        //二元操作
        if (ctx.bop != null) {
            IType leftType = typeMap.get(leftExp);
            // 取字段或方法调用
            if (ctx.IDENTIFIER() != null) {
                String id = ctx.IDENTIFIER().getText();
                if (ctx.call != null) {
                    //方法调用
                    if (leftType instanceof Struct) {
                        Method method = Util.findMethodSince((Struct) leftType, id);
                        if (method != null) {
                            log.debug("推断调用结构体方法后 {} 类型为 {}", ctx.getText(), method.getReturnType());
                            at.getTypeMap().put(ctx, method.getReturnType());
                        } else {
                            at.getErrorMap().put(ctx, "Unknown method " + id + " on " + leftType.getTypeName());
                        }
                        return;
                    }
                    if (leftType instanceof Interface) {
                        Method method = Util.findMethodSince((Interface) leftType, id);
                        if (method != null) {
                            log.debug("推断调用结构体方法后 {} 类型为 {}", ctx.getText(), method.getReturnType());
                            at.getTypeMap().put(ctx, method.getReturnType());
                        } else {
                            at.getErrorMap().put(ctx, "Unknown method " + id + " on " + leftType.getTypeName());
                        }
                        return;
                    }
                } else {
                    //取字段
                    if (leftType instanceof Struct) {
                        Symbol field = Util.findFieldSince((Struct) leftType, id);
                        if (field != null) {
                            log.debug("推断取字段后 {} 类型为 {}", ctx.getText(), field.getType());
                            at.getTypeMap().put(ctx, field.getType());
                        } else {
                            at.getErrorMap().put(ctx, "Unknown field name: " + id + " on " + leftType.getTypeName());
                        }
                        return;
                    }
                    if (leftType instanceof Interface) {
                        Symbol field = Util.findFieldSince((Interface) leftType, id);
                        if (field != null) {
                            log.debug("推断取字段后 {} 类型为 {}", ctx.getText(), field.getType());
                            at.getTypeMap().put(ctx, field.getType());
                        } else {
                            at.getErrorMap().put(ctx, "Unknown field name: " + id + " on " + leftType.getTypeName());
                        }
                        return;
                    }
                }
                return;
            }
            // 赋值 todo 推断 var 变量类型
            if (Objects.equals(ctx.bop.getText(), "=")) {

            }
        }

        //数组取下标操作
        if (ctx.index != null) {
            IType arrType = typeMap.get(leftExp);
            if (!(arrType instanceof ArrayType)) {
                at.getErrorMap().put(ctx, leftExp.getText() + " is not a array");
            } else {
                IType rightType = typeMap.get(rightExp);
                if (rightType != PrimitiveType.INT) {
                    at.getErrorMap().put(ctx, "index is not int: " + rightExp.getText());
                } else {
                    IType elementType = ((ArrayType) arrType).getElementType();
                    log.debug("推断数组取下标后的 {} 类型为 {}", ctx.getText(), elementType);
                    typeMap.put(ctx, elementType);
                }
            }
            return;
        }
        //方法调用
        if (ctx.call != null) {
            //todo 推断 ((x)->x++)(1); 根据 rightExp 推断左部类型是 fun int(int)
            IType funType = typeMap.get(leftExp);
            if (!(funType instanceof FunctionType)) {
                at.getErrorMap().put(ctx, leftExp.getText() + " is not a method");
            } else {
                IType returnType = ((FunctionType) funType).getReturnType();
                log.debug("推断方法调用后 {} 类型为 {}", ctx.getText(), returnType);
                typeMap.put(ctx, returnType);
            }
            return;
        }
        //强制转换
        if (ctx.cast != null) {
            IType type = typeMap.get(ctx.typeType());
            log.debug("推断强制转换后 {} 类型为 {}", ctx.getText(), type);
            typeMap.put(ctx, type);
            return;
        }
        if (ctx.lambdaExpression() != null) {
            typeMap.put(ctx, typeMap.get(ctx.lambdaExpression()));
        }
    }
}
