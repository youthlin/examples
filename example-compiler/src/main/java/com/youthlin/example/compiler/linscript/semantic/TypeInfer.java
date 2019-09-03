package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.linscript.YourLangParser;
import com.youthlin.example.compiler.linscript.YourLangParserBaseListener;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

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
        IScope currentScope = scope;
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
        if (primary.SUPER() != null) {
            while (scope != null && !(scope instanceof Struct)) {
                scope = scope.getParent();
            }
            if (scope != null) {
                Struct superStruct = ((Struct) scope).getSuperStruct();
                at.getTypeMap().put(primary, superStruct);
                log.info("识别出 super 是结构类型 {}", superStruct);
            } else {
                at.getErrorMap().put(primary, "'super' can not use here");
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
            if (literal.NULL_LITERAL() != null) {
                type = NullType.INSTANCE;
            }
            if (type == null) {
                at.getErrorMap().put(primary, "暂不支持的字面量类型: " + literal.getText());
            }
            at.getTypeMap().put(primary, type);
            log.info("识别出字面量 {} 的类型: {}", literal.getText(), type);
            return;
        }

        // 变量、字段、方法
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
                scope = currentScope;
                while (scope != null && !(scope instanceof Struct)) {
                    scope = scope.getParent();
                }
                // 字段和方法不能重名
                Symbol field = Util.findFieldSince((Struct) scope, id);
                if (field != null) {
                    type = field.getType();
                } else {
                    Method method = Util.findMethodSince((Struct) scope, id);
                    if (method != null) {
                        type = method.getType();
                    }
                }
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
            processBop(ctx, leftExp, ctx.bop, rightExp);
            return;
        }
        if (ctx.assign != null) {
            processAssign(ctx, leftExp, rightExp);
            return;
        }
        if (ctx.postfix != null) {
            //todo 是否可以自增自减
            switch (ctx.postfix.getText()) {
                case "++":
                case "--":
                default:
            }
            return;
        }
        if (ctx.prefix != null) {
            switch (ctx.prefix.getText()) {
                case "~":
                    typeMap.put(ctx, PrimitiveType.INT);
                    return;
                case "!":
                    typeMap.put(ctx, PrimitiveType.BOOLEAN);
                    return;
                case "+":
                case "-":
                case "++":
                case "--":
                default:
            }
            return;
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
            if (funType instanceof Struct) {
                //构造方法
                at.getTypeMap().put(ctx, funType);
                log.debug("推断构造方法 {} 返回类型为 {}", ctx.getText(), funType);
                return;
            }
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

    //todo
    private void processBop(YourLangParser.ExpressionContext ctx, YourLangParser.ExpressionContext leftExp,
            Token op, YourLangParser.ExpressionContext rightExp) {
        Map<ParserRuleContext, IType> typeMap = at.getTypeMap();
        switch (op.getText()) {
            case "+":
                //数字相加是数字 数字+string 是 string 其他报错(必须显示 toString)
            case "%":
            case "-":
            case "*":
            case "/":
            case ">>":
            case ">>>":
            case "<<":
            case "<=":
            case ">=":
            case "==":
            case "!=":
            case ">":
            case "<":
            case "&":
            case "|":
            case "^":
            case "&&":
            case "||":
            case "?":
            default:
        }

    }

    //赋值 todo 推断 var 变量类型
    private void processAssign(YourLangParser.ExpressionContext ctx, YourLangParser.ExpressionContext leftExp,
            YourLangParser.ExpressionContext rightExp) {
        IType leftType = at.getTypeMap().get(leftExp);
        //检查左边是否可以被赋值
        if (leftType instanceof VoidType) {
            at.getErrorMap().put(ctx, "Can not assign to void type");
        }

        //检查右边类型是否可以兼容给左边

    }

}
