package com.youthlin.example.compiler.simplescript;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author youthlin.chen
 * @date 2019-08-23 16:35
 */
@Data
@AllArgsConstructor
public class ErrorMessage {
    public enum ErrorType {
        Lexer, Parser
    }

    private final ErrorType type;
    private final int pos;
    private final int line;
    private final int column;
    private final String message;

    public String toString() {
        if (type == ErrorType.Lexer) {
            return String.format("[词法错误]%d 行 %d 列：%s", line, column, message);
        }
        return String.format("[语法错误](TokenIndex=%d) %d 行 %d 列: %s", pos, line, column, message);
    }
}
