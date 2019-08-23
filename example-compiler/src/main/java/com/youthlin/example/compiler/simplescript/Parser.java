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
 * (00) [script]         -> [stmt]+
 * (01) [stmt]           -> [intDeclare]            INT
 * (02) [stmt]           -> [expressionStmt]        IntLiteral ID (
 * (03) [stmt]           -> [assignStmt]            ID
 * (04) [intDeclare]     -> INT ID [intDecRight]    INT
 * (05) [intDecRight]    -> '=' [exp] ';'           =
 * (06) [intDecRight]    -> ';'                     ;
 * (07) [exp]            -> [term] [otherTerm]      IntLiteral ID (
 * (08) [term]           -> [factor] [otherFactor]  IntLiteral ID (
 * (09) [factor]         -> IntLiteral              IntLiteral
 * (10) [factor]         -> ID                      ID
 * (11) [factor]         -> '(' [exp] ')'           (
 * (12) [otherTerm]      -> 空                       ; )
 * (13) [otherTerm]      -> '+' [term]              +
 * (14) [otherTerm]      -> '-' [term]              -
 * (15) [otherFactor]    -> 空                       + - ; )
 * (16) [otherFactor]    -> '*' [factor]            *
 * (17) [otherFactor]    -> '/' [factor]            /
 * (18) [expressionStmt] -> [exp] ';'               IntLiteral ID (
 * (19) [assignStmt]     -> ID '=' [exp] ';'        ID
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
     * (00) [script]         -> [stmt]+
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
     * (01) [stmt]           -> [intDeclare]            INT
     * (02) [stmt]           -> [expressionStmt]        IntLiteral ID (
     * (03) [stmt]           -> [assignStmt]            ID
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
     * (04) [intDeclare]     -> INT ID [intDecRight]    INT
     */
    private TreeNode intDeclare() {
        TreeNode intDeclare = new TreeNode(TreeNodeType.intDeclare);
        intDeclare.addChild(match(TokenType.INT), match(TokenType.ID), intDeclareRight());
        return intDeclare;
    }

    /**
     * (05) [intDecRight]    -> '=' [exp] ';'           =
     * (06) [intDecRight]    -> ';'                     ;
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
                node.addChild(match(TokenType.ASSIGNMENT), exp(), match(TokenType.SEMI));
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
     * (07) [exp]            -> [term] [otherTerm]      IntLiteral ID (
     */
    private TreeNode exp() {
        TreeNode node = new TreeNode(TreeNodeType.exp);
        return node.addChild(term(), otherTerm());
    }

    /**
     * (08) [term]           -> [factor] [otherFactor]  IntLiteral ID (
     */
    private TreeNode term() {
        TreeNode node = new TreeNode(TreeNodeType.term);
        return node.addChild(factor(), otherFactor());
    }

    /**
     * (09) [factor]         -> IntLiteral              IntLiteral
     * (10) [factor]         -> ID                      ID
     * (11) [factor]         -> '(' [exp] ')'           (
     */
    private TreeNode factor() {
        Token peekToken = peekToken();
        if (peekToken == null) {
            addError(UNEXPECTED_EOF);
            return null;
        }
        TreeNode node = new TreeNode(TreeNodeType.factor);
        switch (peekToken.getTokenType()) {
            case INTLITERAL:
                node.addChild(match(TokenType.INTLITERAL));
                break;
            case ID:
                node.addChild(match(TokenType.ID));
                break;
            case LPAREN:
                node.addChild(match(TokenType.LPAREN), exp(), match(TokenType.RPAREN));
                break;
            default:
                addError("", TokenType.INTLITERAL, TokenType.ID, TokenType.LPAREN);
        }
        return node;
    }

    /**
     * (15) [otherFactor]    -> 空                      + - ; )
     * (16) [otherFactor]    -> '*' [factor]            *
     * (17) [otherFactor]    -> '/' [factor]            /
     */
    private TreeNode otherFactor() {
        Token peekToken = peekToken();
        if (peekToken == null) {
            addError(UNEXPECTED_EOF);
            return null;
        }
        TreeNode node = new TreeNode(TreeNodeType.otherFactor);
        switch (peekToken.getTokenType()) {
            case TIMES:
                node.addChild(match(TokenType.TIMES), factor());
                break;
            case OVER:
                node.addChild(match(TokenType.OVER), factor());
                break;
            case SEMI:
            case RPAREN:
            case PLUS:
            case MINUS:
                node.addChild(new TreeNode());
                break;
            default:
                addError("", TokenType.TIMES, TokenType.OVER, TokenType.SEMI, TokenType.RPAREN, TokenType.PLUS, TokenType.MINUS);
        }
        return node;
    }

    /**
     * (12) [otherTerm]      -> 空                       ; )
     * (13) [otherTerm]      -> '+' [term]              +
     * (14) [otherTerm]      -> '-' [term]              -
     */
    private TreeNode otherTerm() {
        Token peekToken = peekToken();
        if (peekToken == null) {
            addError(UNEXPECTED_EOF);
            return null;
        }
        TreeNode node = new TreeNode(TreeNodeType.otherTerm);
        switch (peekToken.getTokenType()) {
            case PLUS:
                node.addChild(match(TokenType.PLUS), term());
                break;
            case MINUS:
                node.addChild(match(TokenType.MINUS), term());
                break;
            case SEMI:
            case RPAREN:
                node.addChild(new TreeNode());
                break;
            default:
                addError("", TokenType.PLUS, TokenType.MINUS, TokenType.SEMI, TokenType.RPAREN);
        }
        return node;
    }

    /**
     * (18) [expressionStmt] -> [exp] ';'               IntLiteral ID (
     */
    private TreeNode expressionStmt() {
        return new TreeNode(TreeNodeType.expressionStmt)
                .addChild(exp(), match(TokenType.SEMI));
    }

    /**
     * * (19) [assignStmt]     -> ID '=' [exp] ';'        ID
     */
    private TreeNode assignStmt() {
        return new TreeNode(TreeNodeType.assignStmt)
                .addChild(
                        match(TokenType.ID),
                        match(TokenType.ASSIGNMENT),
                        exp(),
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
