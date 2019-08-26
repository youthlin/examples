package com.youthlin.example.compiler.simplescript;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.youthlin.example.tree.TreePrinter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    private static final String PRINT_TREE = "-tree";
    private static final String EXIT = "exit();";
    private Map<String, Integer> variables = Maps.newHashMap();
    private boolean verbose;
    private boolean printTree;

    {
        variables.put(LAST_ANSWER, 0);
    }

    public static void main(String[] args) {
        IntScript script = new IntScript();
        Set<String> argSet = Sets.newHashSet(args);
        script.verbose = argSet.contains(VERBOSE_FLAG);
        script.printTree = argSet.contains(PRINT_TREE);
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
                        TreeNode root = result.getRoot();
                        script.evaluate(root, "");
                        if (script.printTree) {
                            System.out.println(TreePrinter.toString(root, TreeNode::getChildren, TreeNode::getValue,
                                    TreePrinter.Option.DEFAULT));
                        }
                    } else {
                        System.err.println(REPEAT + " Script: " + REPEAT);
                        System.err.println(scriptText);
                        result.getErrorList().forEach(System.err::println);
                    }
                    System.out.print("\n>");
                    scriptText = "";
                }
            } catch (Exception e) {
                // log.error("", e);
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
        List<TreeNode> children = node.getChildren();
        switch (type) {
            case script:
                // *  [script]         -> [stmt]+
                // 最后一条语句的值作为返回值
                // fallthrough
            case stmt:
                // *  [stmt]           -> [intDeclare]            INT
                // *  [stmt]           -> [expressionStmt]        IntLiteral ID (
                // *  [stmt]           -> [assignStmt]            ID
                // children 应该就是 1 个
                for (TreeNode child : children) {
                    result = evaluate(child, prefix);
                }
                break;
            case intDeclare:
                // *  [intDeclare]     -> INT ID [intDecRight]    INT
                // 返回 ID 的值
                String varName = children.get(1).getValue();
                if (variables.containsKey(varName)) {
                    throw new IllegalStateException("variable already declared: " + varName);
                }
                TreeNode intDecRight = children.get(2);
                result = evaluate(intDecRight, prefix);
                variables.put(varName, result);
                break;
            case intDecRight:
                // *  [intDecRight]    -> '=' [exp] ';'           =
                // *  [intDecRight]    -> ';'                     ;
                if (children.size() == 1) {
                    result = null;
                    break;
                }
                TreeNode exp = children.get(1);
                result = evaluate(exp, prefix);
                break;
            case additive:
                //  [additive]       -> [multiplicative] ( '+'|'-' [multiplicative] )*
                TreeNode add1 = children.get(0);
                Integer add1Value = evaluate(add1, prefix);
                for (int i = 1; i < children.size(); i++) {
                    TreeNode op = children.get(i);
                    TreeNode add2 = children.get(++i);
                    Integer add2Value = evaluate(add2, prefix);
                    if (op.getTokenType().equals(TokenType.PLUS)) {
                        add1Value = add1Value + add2Value;
                    } else {
                        add1Value = add1Value - add2Value;
                    }
                }
                result = add1Value;
                break;
            case multiplicative:
                // [multiplicative] -> [primary] ( '*'|'/' [primary] )*
                TreeNode pri = children.get(0);
                Integer mul1 = evaluate(pri, prefix);
                for (int i = 1; i < children.size(); i++) {
                    TreeNode op = children.get(i);
                    TreeNode pri2 = children.get(++i);
                    Integer mul2 = evaluate(pri2, prefix);
                    if (op.getTokenType().equals(TokenType.TIMES)) {
                        mul1 = mul1 * mul2;
                    } else {
                        mul1 = mul1 / mul2;
                    }
                }
                result = mul1;
                break;
            case primary:
                //     * [primary]        -> IntLiteral | ID | '(' additive ')'
                TreeNode child1 = children.get(0);
                if (child1.getTokenType().equals(TokenType.INTLITERAL)) {
                    result = Integer.parseInt(child1.getValue());
                    break;
                }
                if (child1.getTokenType().equals(TokenType.ID)) {
                    String name = child1.getValue();
                    if (variables.containsKey(name)) {
                        result = variables.get(name);
                        if (result == null) {
                            throw new IllegalStateException("variable has not been set any value: " + name);
                        }
                    } else {
                        throw new Exception("unknown variable: " + name);
                    }
                    break;
                }
                TreeNode additive = children.get(1);
                result = evaluate(additive, prefix);
                break;
            case expressionStmt:
                // *  [expressionStmt] -> [exp] ';'               IntLiteral ID (
                TreeNode expStmtChild = children.get(0);
                result = evaluate(expStmtChild, prefix);
                break;
            case assignStmt:
                // *  [assignStmt]     -> ID '=' [exp] ';'        ID
                String assName = children.get(0).getValue();
                if (!variables.containsKey(assName)) {
                    throw new IllegalStateException("Can not assign value to unknown variable: " + assName);
                }
                TreeNode assStmtExp = children.get(2);
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
