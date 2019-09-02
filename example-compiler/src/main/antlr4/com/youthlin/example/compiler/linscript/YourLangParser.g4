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
typeType    :   ( qualifiedName | primitiveType | funType ) arrTypeSuffix* ;
arrTypeSuffix
            :   '[' ']' ;
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
            :   (structDeclaration | interfaceDeclaration) ;


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
            :   typeType    constantDeclarator (',' constantDeclarator)* ';'
            |   VAR constantDeclarator (',' constantDeclarator)* ';';
constantDeclarator
            :   IDENTIFIER arrTypeSuffix* '=' variableInitializer ;

methodDeclaration
            :   returnType IDENTIFIER formalParameters  (THROWS qualifiedNameList)?  methodBody;
returnType
            :   typeType | VOID ;
formalParameters
            :   '(' formalParameterList? ')' ;

methodBody  :   ';' | block ;

expression  :   primary
            |   leftExp=expression bop='.'  IDENTIFIER ( call='(' expressionList? ')' )?
            |   leftExp=expression index='[' rightExp=expression ']'
            |   leftExp=expression call='(' expressionList? ')'
            |   cast='(' typeType ')' rightExp=expression
            |   leftExp=expression postfix=('++' | '--')
            |   prefix=('+'|'-'|'++'|'--') rightExp=expression
            |   prefix=('~'|'!') rightExp=expression
            |   leftExp=expression bop=('*'|'/'|'%') rightExp=expression
            |   leftExp=expression bop=('+'|'-') rightExp=expression
            |   leftExp=expression ('<' '<' | '>' '>' '>' | '>' '>')rightExp= expression
            |   leftExp=expression bop=('<=' | '>=' | '>' | '<') rightExp=expression
            |   leftExp=expression bop=INSTANCEOF typeType
            |   leftExp=expression bop=('==' | '!=') rightExp=expression
            |   leftExp=expression bop='&' rightExp=expression
            |   leftExp=expression bop='^' rightExp=expression
            |   leftExp=expression bop='|' rightExp=expression
            |   leftExp=expression bop='&&' rightExp=expression
            |   leftExp=expression bop='||' rightExp=expression
            |   leftExp=expression bop='?' midExp=expression ':' rightExp=expression
            |   <assoc=right> leftExp=expression
                   bop=('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=')
                rightExp=expression
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
expressionList
            :   expression (',' expression )* ;
lambdaExpression
            :   returnType?  lambdaParameters '->' lambdaBody ;
lambdaParameters
            :   IDENTIFIER                              #withOneId
            |   '(' formalParameterList? ')'            #withType
            |   '(' IDENTIFIER (',' IDENTIFIER)* ')'    #withIds
            ;
formalParameterList
            :   formalParameter (',' formalParameter)* (',' lastFormalParameter)?
            |   lastFormalParameter ;
formalParameter
            :   typeType IDENTIFIER ;
lastFormalParameter
            :   typeType '...' IDENTIFIER ;
lambdaBody  :   expression | block ;
