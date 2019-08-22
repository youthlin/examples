package com.youthlin.example.compiler.simplescript;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * programm              -> intDeclare
 *                          | expressionStatement
 *                          | assignmentStatement
 * intDeclare            -> 'int' Id ( = additive) ';'
 * expressionStatement   -> addtive ';'
 * addtive               -> multiplicative ( (+ | -) multiplicative)*
 * multiplicative        -> primary ( (* | /) primary)*
 * primary               -> IntLiteral
 *                          | Id
 *                          | (additive)
 * </pre>
 *
 * @author youthlin.chen
 * @date 2019-08-22 20:55
 */
@Slf4j
public class Parser {

}
