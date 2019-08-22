package com.youthlin.example.compiler.simplescript;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 单词
 *
 * @author youthlin.chen
 * @date 2019-08-22 16:02
 */
@Data
@AllArgsConstructor
public class Token {
    private final int line;
    private final int column;
    private final TokenType tokenType;
    private final String value;
}
