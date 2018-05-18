grammar Calc;

prog
    : stat+
    ;

stat
    : expr                       # printExpr
    | ID '=' expr                # assign
    ;

expr
    : expr op=(MUL|DIV) expr        # MulDiv
    | expr op=(ADD|SUB) expr        # AddSub
    | INT                        # int
    | ID                         # id
    | '(' expr ')'               # parens
    ;
MUL : '*' ;
DIV : '/' ;
ADD : '+' ;
SUB : '-' ;
ID  : [a-zA-Z]+ ;

INT : [0-9]+ ;

WS  : [ \t\r\n]+ -> skip ;    // toss out whitespace
