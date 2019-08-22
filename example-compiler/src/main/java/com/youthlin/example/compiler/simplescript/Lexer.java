package com.youthlin.example.compiler.simplescript;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * 词法解析器
 *
 * @author youthlin.chen
 * @date 2019-08-22 16:15
 */
@Slf4j
public class Lexer {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        String script = "int age = 42;";
        LexerResult result = lexer.tokenize(script);
        result.dump();

        script = "inta age = 45;";
        result = lexer.tokenize(script);
        result.dump();

        script = "in age = 45;";
        result = lexer.tokenize(script);
        result.dump();

        script = "age >= 45;";
        result = lexer.tokenize(script);
        result.dump();

        script = "age > 45;";
        result = lexer.tokenize(script);
        result.dump();
    }

    private enum State {
        //自动机状态  正常 标识符 数字 赋值 错误
        Normal, InId, InNum, InCompare, Error,
    }

    private static final int CR = '\r';
    private static final int LF = '\n';
    private static final int BL = ' ';
    private int getMeFirst = -1;
    private Reader reader;
    private int line = 1;
    private int column = 0;
    private List<String> errors = Lists.newArrayList();

    public LexerResult tokenize(String code) {
        List<Token> tokens = Lists.newArrayList();
        LexerResult result = new LexerResult().tokenList(tokens).errorList(errors);
        if (code == null) {
            errors.add("source code is null");
            return result;
        }
        reader = new CharArrayReader(code.toCharArray());
        try {
            Token token = getToken();
            while (token != null) {
                log.debug("已识别:{}", token);
                tokens.add(token);
                token = getToken();
            }
        } catch (IOException e) {
            errors.add("Unexpected IOException: " + e.getLocalizedMessage());
        }
        return result;
    }

    private Token getToken() throws IOException {
        State state = State.Normal;
        StringBuilder sb = new StringBuilder();
        int ch = getChar();
        while (ch != -1) {
            sb.append((char) ch);
            Token token;
            switch (state) {
                case Normal:
                    //region 开始
                    if (isAlpha(ch)) {
                        state = State.InId;
                    } else if (isDigit(ch)) {
                        state = State.InNum;
                    } else if (isBlank(ch)) {
                        deleteLast(sb);
                        state = State.Normal;
                    } else if (ch == '+') {
                        token = new Token(line, column, TokenType.PLUS, sb.toString());

                        return token;
                    } else if (ch == '-') {
                        token = new Token(line, column, TokenType.MINUS, sb.toString());

                        return token;
                    } else if (ch == '*') {
                        token = new Token(line, column, TokenType.TIMES, sb.toString());

                        return token;
                    } else if (ch == '/') {
                        token = new Token(line, column, TokenType.OVER, sb.toString());

                        return token;
                    } else if (ch == '(') {
                        token = new Token(line, column, TokenType.LPAREN, sb.toString());

                        return token;
                    } else if (ch == ')') {
                        token = new Token(line, column, TokenType.RPAREN, sb.toString());

                        return token;
                    } else if (ch == ';') {
                        token = new Token(line, column, TokenType.SEMI, sb.toString());

                        return token;
                    } else if (ch == '>' || ch == '<' || ch == '=') {
                        state = State.InCompare;
                    } else {
                        log.debug("Unexpected char:{}. already read:{}", showChar(ch), sb.toString());
                        errors.add(error("Unexpected char:" + showChar(ch)));
                        state = State.Error;
                    }
                    //endregion 开始
                    break;
                case InId:
                    //region 标识符
                    if (!isAlpha(ch) && !isDigit(ch)) {
                        //已经不是标识符
                        unGetChar(ch);
                        String value = toStringWithoutLast(sb);
                        TokenType type = TokenType.checkIdToKeywords(value);
                        return new Token(line, column, type, value);
                    }
                    //endregion 标识符
                    break;
                case InNum:
                    if (!isDigit(ch)) {
                        unGetChar(ch);
                        return new Token(line, column, TokenType.INTC, toStringWithoutLast(sb));
                    }
                    break;
                case InCompare:
                    //region 比较操作符或赋值
                    String value = toStringWithoutLast(sb);
                    switch (value) {
                        case ">":
                            if (ch == '=') {
                                return new Token(line, column, TokenType.GE, sb.toString());
                            } else {
                                unGetChar(ch);
                                return new Token(line, column, TokenType.GT, value);
                            }
                        case "<":
                            if (ch == '=') {
                                return new Token(line, column, TokenType.LE, sb.toString());
                            } else {
                                unGetChar(ch);
                                return new Token(line, column, TokenType.LT, value);
                            }
                        case "=":
                            if (ch == '=') {
                                return new Token(line, column, TokenType.EQ, sb.toString());
                            } else {
                                unGetChar(ch);
                                return new Token(line, column, TokenType.ASSIGNMENT, value);
                            }
                        default:
                            if (isBlank(ch)) {
                                deleteLast(sb);
                                break;
                            }
                            errors.add(error("This case should not happen: InCompare"));
                            state = State.Error;
                    }
                    //endregion 比较操作符或赋值
                    break;
                default:
            }
            ch = getChar();
        }
        return null;
    }

    private String error(String msg) {
        return "[" + line + " 行:" + column + " 列]" + msg;
    }

    private void deleteLast(StringBuilder sb) {
        sb.deleteCharAt(sb.length() - 1);
    }

    private String toStringWithoutLast(StringBuilder sb) {
        return sb.substring(0, sb.length() - 1);
    }

    private int getChar() throws IOException {
        int ch;
        if (getMeFirst != -1 && getMeFirst != BL && getMeFirst != CR && getMeFirst != LF) {
            ch = getMeFirst;
            getMeFirst = -1;
        } else {
            ch = reader.read();
        }
        if (ch == LF) {
            column = 0;
            line++;
        } else if (ch != -1) {
            column++;
        }

        if (ch == CR) {
            column--;
        }
        return ch;
    }

    private void unGetChar(int ch) {
        getMeFirst = ch;
        column--;
    }

    private String showChar(int ch) {
        if (ch == LF) {
            return "\\n";
        } else if (ch == CR) {
            return "\\r";
        } else {
            return "" + (char) ch;
        }
    }

    private static boolean isAlpha(int ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isDigit(int ch) {
        return (ch >= '0' && ch <= '9');
    }

    private static boolean isBlank(int ch) {
        return ((char) ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r');
    }

}
