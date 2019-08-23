package com.youthlin.example.compiler.simplescript;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 仅支持整型的脚本
 *
 * @author youthlin.chen
 * @date 2019-08-23 16:25
 */
@Slf4j
public class IntScript {
    private static final String LAST_ANSWER = "_ans_";
    private static final String REPEAT = Strings.repeat("-", 20);
    private static final String VERBOSE_FLAG = "-v";
    private static final String EXIT = "exit();";
    private Map<String, Integer> variables = Maps.newHashMap();
    private boolean verbose;

    {
        variables.put(LAST_ANSWER, 0);
    }


    public static void main(String[] args) {
        IntScript script = new IntScript();
        if (args.length > 0 && args[0].equals(VERBOSE_FLAG)) {
            script.verbose = true;
        }
        script.printHello();
        Parser parser = new Parser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String scriptText = "";
        //提示符
        System.out.print("\n>");
        while (true) {
            try {
                String line = reader.readLine().trim();
                if (Objects.equals(line, EXIT)) {
                    System.out.println("Bye");
                    break;
                }
                scriptText += line + "\r\n";
                if (line.endsWith(";")) {
                    ParseResult result = parser.parse(scriptText);
                    if (script.verbose) {
                        result.dump();
                    }
                    if (result.success()) {
                        script.evaluate(result.getRoot(), "");
                    } else {
                        System.err.println(REPEAT + " Script: " + REPEAT);
                        System.err.println(scriptText);
                        result.getErrorList().forEach(System.err::println);
                    }
                    System.out.print("\n>");
                    scriptText = "";
                }
            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
                System.out.print("\n>");
                scriptText = "";
            }
        }

    }

    private void printHello() {
        System.out.println("Simple script language!");
        if (verbose) {
            System.out.println("verbose mode");
        }
        System.out.println("type " + EXIT + " to exit");
    }

    private Integer evaluate(TreeNode node, String indent) throws Exception {
        String prefix = indent + "/" + node.getType();
        if (verbose) {
            System.out.println("Calculating " + prefix + " " + node.getValue());
        }
        Integer result = null;
        TreeNodeType type = node.getType();
        switch (type) {
            case script:
                // * (00) [script]         -> [stmt]+
                // 最后一条语句的值作为返回值
                // fallthrough
            case stmt:
                // * (01) [stmt]           -> [intDeclare]            INT
                // * (02) [stmt]           -> [expressionStmt]        IntLiteral ID (
                // * (03) [stmt]           -> [assignStmt]            ID
                // children 应该就是 1 个
                for (TreeNode child : node.getChildren()) {
                    result = evaluate(child, prefix);
                }
                break;
            case intDeclare:
                // * (04) [intDeclare]     -> INT ID [intDecRight]    INT
                // 返回 ID 的值
                String varName = node.getChildren().get(1).getValue();
                TreeNode intDecRight = node.getChildren().get(2);
                result = evaluate(intDecRight, prefix);
                variables.put(varName, result);
                break;
            case intDecRight:
                // * (05) [intDecRight]    -> '=' [exp] ';'           =
                // * (06) [intDecRight]    -> ';'                     ;
                if (node.getChildren().size() == 1) {
                    result = null;
                    break;
                }
                TreeNode exp = node.getChildren().get(1);
                result = evaluate(exp, prefix);
                break;
            case exp:
                // * (07) [exp]            -> [term] [otherTerm]      IntLiteral ID (
                // * (08) [term]           -> [factor] [otherFactor]  IntLiteral ID (
                // * (12) [otherTerm]      -> 空                       ; )
                // * (13) [otherTerm]      -> '+' [term]              +
                // * (14) [otherTerm]      -> '-' [term]              -
                TreeNode term = node.getChildren().get(0);
                TreeNode otherTerm = node.getChildren().get(1);
                Integer termValue = evaluate(term, prefix);
                if (otherTerm.getChildren().size() > 1) {
                    TreeNode addOp = otherTerm.getChildren().get(0);
                    TreeNode addTerm = otherTerm.getChildren().get(1);
                    Integer addTermValue = evaluate(addTerm, prefix);
                    if (addTermValue == null) {
                        result = termValue;
                        break;
                    }
                    if (addOp.getValue().equals(TokenType.PLUS.getName())) {
                        result = termValue + addTermValue;
                    } else {
                        result = termValue - addTermValue;
                    }
                } else {
                    result = termValue;
                }
                break;
            case term:
                // * (08) [term]           -> [factor] [otherFactor]  IntLiteral ID (
                // * (09) [factor]         -> IntLiteral              IntLiteral
                // * (10) [factor]         -> ID                      ID
                // * (11) [factor]         -> '(' [exp] ')'           (
                // * (15) [otherFactor]    -> 空                       + - ; )
                // * (16) [otherFactor]    -> '*' [factor]            *
                // * (17) [otherFactor]    -> '/' [factor]            /
                TreeNode factor = node.getChildren().get(0);
                TreeNode otherFactor = node.getChildren().get(1);
                Integer factorValue = evaluate(factor, prefix);
                if (otherFactor.getChildren().size() > 1) {
                    TreeNode op = otherFactor.getChildren().get(0);
                    TreeNode multiFactor = otherFactor.getChildren().get(1);
                    Integer multiValue = evaluate(multiFactor, prefix);
                    if (multiValue == null) {
                        result = factorValue;
                        break;
                    }
                    if (op.getValue().equals(TokenType.TIMES.getName())) {
                        result = factorValue * multiValue;
                    } else {
                        result = factorValue / multiValue;
                    }
                } else {
                    result = factorValue;
                }
                break;
            case factor:
                // * (09) [factor]         -> IntLiteral              IntLiteral
                // * (10) [factor]         -> ID                      ID
                // * (11) [factor]         -> '(' [exp] ')'           (
                List<TreeNode> children = node.getChildren();
                if (children.size() == 1) {
                    TreeNode child = children.get(0);
                    result = evaluate(child, prefix);
                } else {
                    TreeNode factorExp = children.get(1);
                    result = evaluate(factorExp, prefix);
                }
                break;
            case expressionStmt:
                // * (18) [expressionStmt] -> [exp] ';'               IntLiteral ID (
                TreeNode expStmtChild = node.getChildren().get(0);
                result = evaluate(expStmtChild, prefix);
                break;
            case assignStmt:
                // * (19) [assignStmt]     -> ID '=' [exp] ';'        ID
                String assName = node.getChildren().get(0).getValue();
                if (!variables.containsKey(assName)) {
                    throw new IllegalStateException("Can not assign value to unknown variable: " + assName);
                }
                TreeNode assStmtExp = node.getChildren().get(2);
                result = evaluate(assStmtExp, prefix);
                variables.put(assName, result);
                break;
            case Terminal:
                TokenType tokenType = node.getTokenType();
                switch (tokenType) {
                    case INTLITERAL:
                        result = Integer.parseInt(node.getValue());
                        break;
                    case ID:
                        String name = node.getValue();
                        if (variables.containsKey(name)) {
                            result = variables.get(name);
                            if (result == null) {
                                throw new IllegalStateException("variable has not been set any value: " + name);
                            }
                        } else {
                            throw new Exception("unknown variable: " + name);
                        }

                        break;
                    default:
                        result = null;
                }
                break;
            default:
        }
        if ("".equals(indent)) {
            System.out.println(result);
            variables.put(LAST_ANSWER, result);
        }
        if (verbose) {
            System.out.println("Calculated " + prefix + " " + node.getValue() + " = " + result);
        }
        return result;
    }
}
