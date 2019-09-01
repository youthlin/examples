package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Lists;
import com.youthlin.example.compiler.linscript.YourLangParser;
import com.youthlin.example.compiler.linscript.YourLangParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;
import java.util.Stack;

/**
 * 第一趟遍历语法树 识别出符号、类型和作用域
 * 符号       类型         作用域
 * variable   primitive   global
 * constant   void        block
 * struct     fun         struct
 * method     struct      method
 *
 * @author : youthlin.chen @ 2019-08-31 22:53
 */
public class SymbolTypeScopeScanner extends YourLangParserBaseListener {
    private AnnotatedTree annotatedTree;
    private Stack<IScope> scopeStack = new Stack<>();

    public SymbolTypeScopeScanner(AnnotatedTree tree) {
        annotatedTree = tree;
    }

    private void pushScope(ParserRuleContext context, IScope scope) {
        scopeStack.push(scope);
        annotatedTree.getScopeMap().put(context, scope);
    }

    private IScope popScope() {
        return scopeStack.pop();
    }

    private IScope currentScope() {
        return scopeStack.peek();
    }

    @Override
    public void enterYourLang(YourLangParser.YourLangContext ctx) {
        pushScope(ctx, annotatedTree.getGlobalScope());
    }

    @Override
    public void enterBlock(YourLangParser.BlockContext ctx) {
        BlockScope scope = new BlockScope(currentScope());
        pushScope(ctx, scope);
    }

    @Override
    public void exitBlock(YourLangParser.BlockContext ctx) {
        popScope();
    }

    @Override
    public void enterStatement(YourLangParser.StatementContext ctx) {
        if (ctx.FOR() != null) {
            pushScope(ctx, new BlockScope("for", currentScope()));
        }
    }

    @Override
    public void exitStatement(YourLangParser.StatementContext ctx) {
        if (ctx.FOR() != null) {
            // 为了可以让 for 初始块中的 i 在 for 结束后失效
            // for(int i=0;;){} //for 中的 i
            // int i=1;         //新的 i
            popScope();
        }
    }

    @Override
    public void enterInterfaceDeclaration(YourLangParser.InterfaceDeclarationContext ctx) {
        String interfaceName = ctx.IDENTIFIER().getText();
        IScope currentScope = currentScope();
        if (Util.findSymbolOnScope(currentScope, interfaceName, Interface.class) != null) {
            annotatedTree.getErrorMap().put(ctx, "Duplicated interface name '" + interfaceName
                    + "' on scope: " + currentScope.getScopeName());
        } else if (Util.findSymbolOnScope(currentScope, interfaceName, Struct.class) != null) {
            annotatedTree.getErrorMap()
                    .put(ctx, "There is already a struct named '" + interfaceName
                            + "' on scope: " + currentScope.getScopeName());
        }
        Interface anInterface = new Interface(interfaceName, currentScope);
        pushScope(ctx, anInterface);
        annotatedTree.getTypeMap().put(ctx, anInterface);
        annotatedTree.getSymbolMap().put(ctx, anInterface);
    }

    @Override
    public void exitInterfaceDeclaration(YourLangParser.InterfaceDeclarationContext ctx) {
        popScope();
    }

    @Override
    public void enterStructDeclaration(YourLangParser.StructDeclarationContext ctx) {
        String structName = ctx.IDENTIFIER().getText();
        IScope currentScope = currentScope();
        if (Util.findSymbolOnScope(currentScope, structName, Struct.class) != null) {
            annotatedTree.getErrorMap().put(ctx, "Duplicated struct name '" + structName
                    + "' on scope: " + currentScope.getScopeName());
        }
        Struct struct = new Struct(structName, currentScope);
        pushScope(ctx, struct);
        annotatedTree.getTypeMap().put(ctx, struct);
        annotatedTree.getSymbolMap().put(ctx, struct);
    }

    @Override
    public void exitStructDeclaration(YourLangParser.StructDeclarationContext ctx) {
        popScope();
    }

    @Override
    public void enterMethodDeclaration(YourLangParser.MethodDeclarationContext ctx) {
        String methodName = ctx.IDENTIFIER().getText();
        YourLangParser.FormalParametersContext formalParametersCtx = ctx.formalParameters();
        YourLangParser.FormalParameterListContext formalParameterListCtx = formalParametersCtx.formalParameterList();
        List<String> parameterTypeNameList = null;
        if (formalParameterListCtx != null) {
            parameterTypeNameList = Lists.newArrayList();
            for (YourLangParser.FormalParameterContext parameterCtx : formalParameterListCtx.formalParameter()) {
                parameterTypeNameList.add(parameterCtx.typeType().getText());
            }
            YourLangParser.LastFormalParameterContext lastCtx = formalParameterListCtx.lastFormalParameter();
            if (lastCtx != null) {
                parameterTypeNameList.add(lastCtx.typeType().getText() + "[]");
            }
        }
        IScope parentScope = currentScope();
        if (Util.checkDuplicateMethodOnScope(parentScope, methodName, parameterTypeNameList)) {
            annotatedTree.getErrorMap().put(ctx, "Duplicated method name '" + methodName + "("
                    + (parameterTypeNameList != null
                    ? String.join(",", parameterTypeNameList)
                    : "")
                    + ")' on scope: " + parentScope.getScopeName());
        }
        Method method = new Method(methodName, parentScope);
        method.setParameterTypeName(parameterTypeNameList);
        pushScope(ctx, method);
        annotatedTree.getSymbolMap().put(ctx, method);
    }

    @Override
    public void exitMethodDeclaration(YourLangParser.MethodDeclarationContext ctx) {
        popScope();
    }

    @Override
    public void enterLambdaExpression(YourLangParser.LambdaExpressionContext ctx) {
        BlockScope lambda = new BlockScope("lambda", currentScope());
        pushScope(ctx, lambda);
    }

    @Override
    public void exitLambdaExpression(YourLangParser.LambdaExpressionContext ctx) {
        popScope();
    }

    @Override
    public void enterFormalParameter(YourLangParser.FormalParameterContext ctx) {
        String parameterName = ctx.IDENTIFIER().getText();
        IScope currentScope = currentScope();
        if (Util.findSymbolOnScope(currentScope, parameterName, Symbol.class) != null) {
            annotatedTree.getErrorMap().put(ctx, "Duplicated parameter name '" + parameterName
                    + "' on scope: " + currentScope.getScopeName());
        }
        Symbol symbol = new Symbol(parameterName, currentScope);
        annotatedTree.getSymbolMap().put(ctx, symbol);
    }

    @Override
    public void enterFunType(YourLangParser.FunTypeContext ctx) {
        FunctionType functionType = new FunctionType(currentScope());
        annotatedTree.getTypeMap().put(ctx, functionType);
    }

    @Override
    public void enterVariableDeclarator(YourLangParser.VariableDeclaratorContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        IScope currentScope = currentScope();
        if (Util.findSymbolOnScope(currentScope, varName, Symbol.class) != null) {
            annotatedTree.getErrorMap().put(ctx, "Duplicated var name '" + varName
                    + "' on scope: " + currentScope.getScopeName());
        }
        Symbol symbol = new Symbol(varName, currentScope);
        annotatedTree.getSymbolMap().put(ctx, symbol);
    }

}
