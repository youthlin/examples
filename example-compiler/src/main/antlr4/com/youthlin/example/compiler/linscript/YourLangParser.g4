parser grammar YourLangParser;

options { tokenVocab=YourLangLexer; }

yourLang    :   importPart*  body    exportPart? ;

importPart  :   IMPORT  qualifiedName '=' STRING_LITERAL ';' ;

exportPart  :   EXPORT  qualifiedNameList ';' ;
qualifiedName
            :   IDENTIFIER ('.' IDENTIFIER)* ;
qualifiedNameList
            :   qualifiedName (',' qualifiedName)* ;

body        :   bodyUnit+ ;
bodyUnit    :   statement
            |   structDeclaration
            |   interfaceDeclaration
            |   memberDeclaration;

statement   :   blockLabel=block
            |   IF parExpression statement (ELSE statement)?
            |   FOR '(' forControl ')' statement
            |   WHILE parExpression statement
            |   DO statement WHILE parExpression ';'
            |   TRY block (catchClause+ finallyBlock? | finallyBlock)
            |   SWITCH parExpression '{' switchLabel+ '}'
            |   RETURN expression? ';'
            |   THROW expression ';'
            |   BREAK IDENTIFIER? ';'
            |   CONTINUE IDENTIFIER? ';'
            |   SEMI
            |   statementExpression=expression ';'
            |   identifierLabel=IDENTIFIER ':' statement
            ;

block       :   '{' blockStatement* '}' ;
parExpression
            :   '(' expression ')' ;
forControl  :   forInit? ';' expression? ';' forUpdate=expressionList? ;
forInit     :   localVariableDeclaration | expressionList ;

catchClause :   CATCH '(' catchType IDENTIFIER ')' block;
catchType   :   qualifiedName ('|' qualifiedName)* ;

finallyBlock:   FINALLY block ;
switchLabel :   CASE ( constantExpression=expression | enumConstantName=IDENTIFIER ) ':' blockStatement*
            |   DEFAULT ':' blockStatement*
            ;

blockStatement
            :   localVariableDeclaration ';'
            |   statement
            |   localTypeDeclaration
            ;
localVariableDeclaration
            :   VAR variableDeclarators
            |   typeType variableDeclarators
            ;
typeType    :   ( qualifiedName | primitiveType | funType ) ('[' ']')* ;
primitiveType
            :   BOOLEAN | INT | STRING ;
funType     :   FUNCTION returnType '(' typeList? ')' ;
typeList    :   typeType (',' typeType)* ;

variableDeclarators
            :   variableDeclarator (',' variableDeclarator)* ;
variableDeclarator
            :   IDENTIFIER ('=' variableInitializer)? ;
variableInitializer
            :   arrayInitializer
            |   expression
            ;
arrayInitializer
            :   '{' (variableInitializer (',' variableInitializer)* ','? )? '}' ;

localTypeDeclaration
            :   (structDeclaration | interfaceDeclaration) ';' ;


structDeclaration
            :   STRUCT IDENTIFIER (EXTENDS typeType)? (IMPLEMENTS typeList)? structBody ;
interfaceDeclaration
            :   INTERFACE IDENTIFIER (EXTENDS typeList)? interfaceBody ;

structBody  :   '{' structBodyDeclaration* '}' ;
interfaceBody
            :   '{' interfaceBodyDeclaration* '}' ;

structBodyDeclaration
            :   ';'
            |   STATIC? block
            |   memberDeclaration
            ;
memberDeclaration
            :   fieldDeclaration
            |   methodDeclaration
            ;

interfaceBodyDeclaration
            :   interfaceMemberDeclaration
            |   ';'
            ;
interfaceMemberDeclaration
            :   constDeclaration
            |   methodDeclaration
            |   interfaceDeclaration
            |   structDeclaration
            ;

fieldDeclaration
            :   localVariableDeclaration ';' ;
constDeclaration
            :   typeType    constantDeclarator (',' constantDeclarator)* ';';
constantDeclarator
            :   IDENTIFIER ('[' ']')* '=' variableInitializer ;

methodDeclaration
            :   returnType IDENTIFIER formalParameters  ('[' ']')* (THROWS qualifiedNameList)?  methodBody;
returnType
            :   typeType | VOID ;
formalParameters
            :   '(' formalParameterList? ')' ;

methodBody  :   ';' | block ;


expression  :   primary
            |   expression bop='.' ( IDENTIFIER | methodCall )
            |   expression '[' expression ']'
            |   methodCall
            |   expression '(' expressionList? ')'
            |   '(' typeType ')' expression
            |   expression postfix=('++' | '--')
            |   prefix=('+'|'-'|'++'|'--') expression
            |   prefix=('~'|'!') expression
            |   expression bop=('*'|'/'|'%') expression
            |   expression bop=('+'|'-') expression
            |   expression ('<' '<' | '>' '>' '>' | '>' '>') expression
            |   expression bop=('<=' | '>=' | '>' | '<') expression
            |   expression bop=INSTANCEOF typeType
            |   expression bop=('==' | '!=') expression
            |   expression bop='&' expression
            |   expression bop='^' expression
            |   expression bop='|' expression
            |   expression bop='&&' expression
            |   expression bop='||' expression
            |   expression bop='?' expression ':' expression
            |   <assoc=right> expression
                   bop=('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=')
                expression
            |   lambdaExpression
            ;
primary     :   '(' expression ')'
            |   THIS
            |   literal
            |   IDENTIFIER
            ;
literal     :   integerLiteral | floatLiteral | BOOL_LITERAL | STRING_LITERAL | NULL_LITERAL ;
integerLiteral
            :   DECIMAL_LITERAL
            |   HEX_LITERAL
            |   OCT_LITERAL
            |   BINARY_LITERAL
            ;
floatLiteral
            :   FLOAT_LITERAL
            |   HEX_FLOAT_LITERAL
            ;
methodCall  :   IDENTIFIER '(' expressionList? ')' ;
expressionList
            :   expression (',' expression )* ;
lambdaExpression
            :   returnType?  lambdaParameters '->' lambdaBody ;
lambdaParameters
            :   IDENTIFIER
            |   '(' formalParameterList? ')'
            |   '(' IDENTIFIER (',' IDENTIFIER)* ')'
            ;
formalParameterList
            :   formalParameter (',' formalParameter)* (',' lastFormalParameter)?
            |   lastFormalParameter ;
formalParameter
            :   typeType IDENTIFIER ;
lastFormalParameter
            :   typeType '...' IDENTIFIER ;
lambdaBody  :   expression | block ;
