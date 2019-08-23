package com.youthlin.example.compiler.simplescript;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <pre>
 *      [script]         -> [stmt]+
 *      [stmt]           -> [intDeclare]
 *      [stmt]           -> [expressionStmt]
 *      [stmt]           -> [assignStmt]
 *      [intDeclare]     -> INT ID [intDecRight]
 *      [intDecRight]    -> '=' [additive] ';'
 *      [intDecRight]    -> ';'
 *      [additive]       -> [multiplicative] ( '+'|'-' [multiplicative] )*
 *      [multiplicative] -> [primary] ( '*'|'/' [primary] )*
 *      [primary]        -> IntLiteral | ID | '(' additive ')'
 *      [expressionStmt] -> [additive] ';'
 *      [assignStmt]     -> ID '=' [additive] ';'
 * </pre>
 *
 * @author youthlin.chen
 * @date 2019-08-22 20:55
 */
@Slf4j
public class Parser {
    private List<Token> tokenList;
    private List<ErrorMessage> errorList;
    private int size;
    private Token lastRead;
    private int pos;


    public static void main(String[] args) {
        test("int a;");
        test("int a = 1;");
        test("2+3*4;");
        test("int a; a = 1; a + b;");
    }

    private static void test(String code) {
        Parser parser = new Parser();
        Lexer lexer = new Lexer();
        LexerResult tokenize = lexer.tokenize(code);
        tokenize.dump();
        if (tokenize.success()) {
            ParseResult parseResult = parser.parse(tokenize.tokenList());
            parseResult.dump();
        }
    }

    public ParseResult parse(String code) {
        Lexer lexer = new Lexer();
        LexerResult tokenize = lexer.tokenize(code);
        if (tokenize.success()) {
            return parse(tokenize.tokenList());
        }
        return new ParseResult().setErrorList(tokenize.errorList());
    }

    public ParseResult parse(List<Token> tokens) {
        reset(tokens);
        TreeNode root = script();
        return new ParseResult().setRoot(root).setErrorList(errorList);
    }

    private void reset(List<Token> source) {
        tokenList = source;
        errorList = Lists.newArrayList();
        size = source.size();
        lastRead = null;
        pos = 0;
    }


    /**
     * [script]         -> [stmt]+
     */
    private TreeNode script() {
        Token peekToken = peekToken();
        if (peekToken == null) {
            addError("Empty tokens");
            return null;
        }
        TreeNode stmt = stmt();
        TreeNode root = new TreeNode(TreeNodeType.script).addChild(stmt);
        while (peekToken() != null && errorList.isEmpty()) {
            root.addChild(stmt());
        }
        return root;
    }

    /**
     * [stmt]           -> [intDeclare]            INT
     * [stmt]           -> [expressionStmt]        IntLiteral ID (
     * [stmt]           -> [assignStmt]            ID
     */
    private TreeNode stmt() {
        Token peekToken = peekToken();
        if (peekToken == null) {
            addError(UNEXPECTED_EOF);
            return null;
        }
        TreeNode stmt = new TreeNode(TreeNodeType.stmt);
        switch (peekToken.getTokenType()) {
            case INT:
                stmt.addChild(intDeclare());
                break;
            case LPAREN:
            case INTLITERAL:
                stmt.addChild(expressionStmt());
                break;
            case ID:
                int position = pos;
                TreeNode child = expressionStmt();
                if (hasErrorSince(position)) {
                    List<ErrorMessage> lastErrors = clearErrorSince(position);
                    log.debug("expressionStmt() error:{}", lastErrors);
                    pos = position;
                    child = assignStmt();
                    if (hasErrorSince(position)) {
                        errorList.addAll(lastErrors);
                    }
                    stmt.addChild(child);
                    break;
                } else {
                    stmt.addChild(child);
                    break;
                }
            default:
                addError("", TokenType.INT, TokenType.LPAREN, TokenType.INTLITERAL, TokenType.ID);
        }
        return stmt;
    }

    /**
     * [intDeclare]     -> INT ID [intDecRight]    INT
     */
    private TreeNode intDeclare() {
        TreeNode intDeclare = new TreeNode(TreeNodeType.intDeclare);
        intDeclare.addChild(match(TokenType.INT), match(TokenType.ID), intDeclareRight());
        return intDeclare;
    }

    /**
     * [intDecRight]    -> '=' [exp] ';'           =
     * [intDecRight]    -> ';'                     ;
     */
    private TreeNode intDeclareRight() {
        Token peekToken = peekToken();
        if (peekToken == null) {
            addError(UNEXPECTED_EOF);
            return null;
        }
        TreeNode node = new TreeNode(TreeNodeType.intDecRight);
        switch (peekToken.getTokenType()) {
            case ASSIGNMENT:
                node.addChild(match(TokenType.ASSIGNMENT), additive(), match(TokenType.SEMI));
                break;
            case SEMI:
                node.addChild(match(TokenType.SEMI));
                break;
            default:
                addError("", TokenType.EQ, TokenType.SEMI);
        }
        return node;
    }

    /**
     * [additive]       -> [multiplicative] ( '+'|'-' [multiplicative] )*
     */
    private TreeNode additive() {
        TreeNode node = new TreeNode(TreeNodeType.additive);
        TreeNode child = multiplicative();
        node.addChild(child);
        Token peekToken = peekToken();
        if (peekToken == null) {
            addError(UNEXPECTED_EOF);
            return null;
        }
        if (peekToken.getTokenType().equals(TokenType.PLUS)
                || peekToken.getTokenType().equals(TokenType.MINUS)) {
            node.addChild(match(peekToken.getTokenType()),
                    multiplicative());
        }
        return node;
    }

    /**
     * [multiplicative] -> [primary] ( '*'|'/' [primary] )*
     */
    private TreeNode multiplicative() {
        TreeNode node = new TreeNode(TreeNodeType.multiplicative);
        node.addChild(primary());
        Token peekToken = peekToken();
        if (peekToken == null) {
            addError(UNEXPECTED_EOF);
            return null;
        }
        if (peekToken.getTokenType().equals(TokenType.TIMES)
                || peekToken.getTokenType().equals(TokenType.OVER)) {
            node.addChild(match(peekToken.getTokenType()), primary());
        }
        return node;
    }

    /**
     * [primary]        -> IntLiteral | ID | '(' additive ')'
     */
    private TreeNode primary() {
        Token peekToken = peekToken();
        if (peekToken == null) {
            addError(UNEXPECTED_EOF);
            return null;
        }
        TreeNode node = new TreeNode(TreeNodeType.primary);
        TokenType tokenType = peekToken.getTokenType();
        switch (tokenType) {
            case INTLITERAL:
            case ID:
                node.addChild(match(tokenType));
                break;
            case LPAREN:
                node.addChild(match(TokenType.LPAREN), additive(), match(TokenType.RPAREN));
                break;
            default:
                addError("", TokenType.INTLITERAL, TokenType.ID, TokenType.LPAREN);
        }
        return node;
    }

    /**
     * [expressionStmt] -> [exp] ';'               IntLiteral ID (
     */
    private TreeNode expressionStmt() {
        return new TreeNode(TreeNodeType.expressionStmt)
                .addChild(additive(), match(TokenType.SEMI));
    }

    /**
     * *      [assignStmt]     -> ID '=' [exp] ';'        ID
     */
    private TreeNode assignStmt() {
        return new TreeNode(TreeNodeType.assignStmt)
                .addChild(
                        match(TokenType.ID),
                        match(TokenType.ASSIGNMENT),
                        additive(),
                        match(TokenType.SEMI)
                );
    }

    private TreeNode match(TokenType expected) {
        Token token = getToken();
        if (token == null) {
            addError(UNEXPECTED_EOF);
            return null;
        }
        TokenType type = token.getTokenType();
        if (type.equals(expected)) {
            return new TreeNode(TreeNodeType.Terminal, token.getValue()).setTokenType(type);
        }
        addError("", expected);
        return null;
    }

    private static final String UNEXPECTED_EOF = "Unexpected EOF";

    private void addError(String msg, TokenType... expected) {
        if (Objects.equals(msg, "")) {
            msg = "Unexpected token";
        }
        StringBuilder sb = new StringBuilder(msg);
        Token lastRead = getLastRead();
        if (lastRead != null) {
            sb.append(" after ")
                    .append(lastRead.getLine()).append(" 行 ")
                    .append(lastRead.getColumn()).append(" 列(")
                    .append(lastRead.getValue()).append(") ")
            ;
        }
        if (expected != null) {
            sb.append(" expected: ").append(Arrays.stream(expected)
                    .map(TokenType::getName).collect(Collectors.joining(",")));
        }
        errorList.add(new ErrorMessage(pos, sb.toString()));
    }

    private boolean hasErrorSince(int position) {
        if (errorList.isEmpty()) {
            return false;
        }
        return errorList.get(errorList.size() - 1).getPos() > position;
    }

    private List<ErrorMessage> clearErrorSince(int position) {
        if (!errorList.isEmpty()) {
            int index = errorList.size() - 1;
            List<ErrorMessage> remove = Lists.newArrayList();
            for (int i = index; i >= 0; i--) {
                ErrorMessage error = errorList.get(i);
                if (error.getPos() > position) {
                    index = i;
                    remove.add(error);
                }
            }
            errorList = Lists.newArrayList(errorList.subList(0, index));
            return remove;
        }
        return Collections.emptyList();
    }

    private Token getToken() {
        if (pos < size) {
            return lastRead = tokenList.get(pos++);
        }
        return null;
    }

    private Token peekToken() {
        if (pos < size) {
            return tokenList.get(pos);
        }
        return null;
    }

    private Token getLastRead() {
        return lastRead;
    }

}
