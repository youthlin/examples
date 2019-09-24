/** 参考 https://github.com/antlr/grammars-v4/blob/master/java/JavaParser.g4 */
parser grammar LinScriptParser;

options { tokenVocab=LinScriptLexer; }

// 该脚本由导入部分、主体部分、0或1个导出部分组成
script      :   importPart body  exportPart? ;

// 导入部分可以有任意个
importPart  :   importStmt* ;
importStmt  :   IMPORT IDENTIFIER '=' STRING_LITERAL ';' ;
// 导出部分
exportPart  :   EXPORT IDENTIFIER(',' IDENTIFIER)* ';' ;

// 主体部分是多条语句
body        :   statement+ ;

// 语句
statement   :   varDecStmt                              // 声明语句
            |   block                                   // 语句块
            |   IF parExp statement (ELSE statement)?   // 判断语句
            |   FOR '(' forControl ')' statement        // for 循环语句
            |   WHILE parExp statement                  // while 循环语句
            |   DO statement WHILE parExp ';'           // do-while 循环语句
            |   RETURN exp? ';'                         // 返回语句
            |   BREAK IDENTIFIER? ';'                   // break 跳转语句
            |   CONTINUE IDENTIFIER? ';'                // continue 跳转语句
            |   SEMI                                    // 空语句
            |   statementExpression=exp ';'             // 表达式语句
            |   identifierLabel=IDENTIFIER ':' statement// 带标签的语句
            ;

// region 声明语句
varDecStmt  :   varDeclare ';';
// 变量声明
varDeclare  :   argType varDecs                     // 确定类型声明
            |   VAR varDecs                         // 推导类型声明
            ;
varDecs     :   varDec (',' varDec)* ;
varDec      :   IDENTIFIER ('=' varInit)? ;
varInit     :   '{' expList? '}'                    // 用数组初始化
            |   exp                                 // 用表达式的值初始化
            ;


// 可以作为参数的类型 void 不可以作为参数。
argType     :   argType '[' exp? ']'
            |   primaryType
            |   funType
            ;
// 原生类型
primaryType :   INT
            |   STRING
            |   BOOLEAN
            |   ANY
            ;
// 函数类型
funType     :   FUN returnType '(' argTypeList* ')' ;
// 可以返回的类型
returnType  :   argType
            |   VOID
            ;
argTypeList :   argType (',' argType)* ;
// endregion 声明语句


// 语句块
block       :   '{' statement* '}';

parExp      :   '(' exp ')' ;
forControl  :   forInit? ';' exp? ';' forUpdate=expList? ;
forInit     :   varDeclare
            |   expList
            ;

// 表达式
exp         :   primary                                     // 原子表达式
            |   exp bop='.' IDENTIFIER                      // 取属性表达式
            |   funExp                                      // 函数表达式
            |   exp '[' exp ']'                             // 数组索引表达式
            |   exp '(' expList? ')'                        // 函数调用
            |   exp postfix=('++' | '--')                   // 自增自减
            |   prefix=('+'|'-'|'++'|'--') exp
            |   prefix=('~'|'!') exp
            |   exp bop=('*'|'/'|'%') exp
            |   exp bop=('+'|'-') exp
            |   exp ('<' '<' | '>' '>' '>' | '>' '>')  exp
            |   exp bop=('<=' | '>=' | '>' | '<') exp
            |   exp bop=INSTANCEOF exp
            |   exp bop=('==' | '!=') exp
            |   exp bop='&' exp
            |   exp bop='^' exp
            |   exp bop='|' exp
            |   exp bop='&&' exp
            |   exp bop='||' exp
            |   exp bop='?' exp ':' exp
            |   <assoc=right> 
                exp bop=('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=') exp
            ;
primary     :   '(' exp ')'
            |   literal
            |   IDENTIFIER
            ;
// 函数表达式
funExp      :   returnType? '(' argList? ')' block ;
argList     :   typedArg (',' typedArg)* ;
typedArg    :   type=argType? IDENTIFIER ;

expList     :   exp (',' exp)* ;

// 字面量
literal     :   integerLiteral
            |   STRING_LITERAL
            |   BOOL_LITERAL
            ;
// 整数字面量
integerLiteral
            :   DECIMAL_LITERAL
            |   HEX_LITERAL
            |   OCT_LITERAL
            |   BINARY_LITERAL
            ;
