package com.youthlin.example.compiler.simplescript;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * Token 类型
 *
 * @author youthlin.chen
 * @date 2019-08-22 16:04
 */
@AllArgsConstructor
public enum TokenType {
    //标识符
    ID("id"), ERROR("error"), INTC("intNumber"),
    //保留字
    INT("int"), IF("if"), ELSE("else"),
    //特殊符号
    ASSIGNMENT("="),
    EQ("=="), LE("<="), GE(">="),
    LT("<"), GT(">"),
    PLUS("+"), MINUS("-"), TIMES("*"), OVER("/"),
    LPAREN("("), RPAREN(")"), SEMI(";"),
    //
    ;
    @Getter
    private String name;

    public static TokenType checkIdToKeywords(String token) {
        if (Objects.equals(INT.name, token)) {
            return INT;
        }
        if (Objects.equals(IF.name, token)) {
            return IF;
        }
        if (Objects.equals(ELSE.name, token)) {
            return ELSE;
        }
        return ID;
    }

}
