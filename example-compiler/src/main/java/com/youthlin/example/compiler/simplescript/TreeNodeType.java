package com.youthlin.example.compiler.simplescript;

/**
 * @author youthlin.chen
 * @date 2019-08-23 11:40
 */
public enum TreeNodeType {
    //
    script,
    stmt,
    intDeclare,
    expressionStmt,
    assignStmt,
    intDecRight,
    exp,
    term,
    otherTerm,
    factor,
    otherFactor,

    Terminal,

    Empty,

}
