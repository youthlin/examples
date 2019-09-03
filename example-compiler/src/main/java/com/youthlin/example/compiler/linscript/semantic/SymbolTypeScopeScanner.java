package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.linscript.YourLangLexer;
import com.youthlin.example.compiler.linscript.YourLangParser;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * 第一趟遍历语法树 识别出符号、类型和作用域
 *
 * @author : youthlin.chen @ 2019-08-31 22:53
 */
@Slf4j
public class SymbolTypeScopeScanner extends BaseListener {
    private Stack<IScope> scopeStack = new Stack<>();
    private SemanticValidator validator;

    public SymbolTypeScopeScanner(AnnotatedTree tree, SemanticValidator validator) {
        super(tree);
        this.validator = validator;
    }

    private void pushScope(ParserRuleContext context, IScope scope) {
        log.debug("进入新的作用域 {}", scope);
        scopeStack.push(scope);
        at.getScopeMap().put(context, scope);
    }

    private void popScope() {
        IScope pop = scopeStack.pop();
        log.debug("退出作用域 {}", pop);
    }

    private IScope currentScope() {
        return scopeStack.peek();
    }

    @Override
    public void enterYourLang(YourLangParser.YourLangContext ctx) {
        pushScope(ctx, at.getGlobalScope());
    }

    @Override
    public void enterImportPart(YourLangParser.ImportPartContext ctx) {
        String fileName = ctx.STRING_LITERAL().getText();
        fileName = fileName.substring(1, fileName.length() - 1);
        File currentFile = at.getFile();
        if (currentFile == null) {
            error(log, ctx, "Can not use import when not file mode");
        } else {
            File importFile = new File(currentFile.getParent(), fileName);
            String path = importFile.getAbsolutePath();
            if (importFile.exists()) {
                log.info("导入文件 {}", path);
                try {
                    YourLangLexer lexer = new YourLangLexer(CharStreams.fromPath(importFile.toPath()));
                    YourLangParser parser = new YourLangParser(new CommonTokenStream(lexer));
                    log.info(">>>开始处理文件 {}", path);
                    AnnotatedTree importAt = validator.validate(parser.yourLang(), importFile);
                    log.debug("导入文件 类型已处理? {}", importAt.isTypeResolved());
                    IScope scope = currentScope();
                    String original = ctx.original.getText();
                    String name = original;
                    if (ctx.rename != null) {
                        name = ctx.rename.getText();
                    }
                    at.getImportFrom().add(importAt);
                    importAt.getExportTo().add(at);
                    ImportSymbol symbol = new ImportSymbol(scope, name, original, importFile, importAt);
                    at.getImportSymbols().add(symbol);
                    at.getSymbolMap().put(ctx, symbol);
                    log.info("<<<处理完成 {}", path);
                    log.info("导入文件 {} 符号 {} as {}", fileName, original, name);
                } catch (IOException e) {
                    error(log, ctx, "IOException when process file {}" + path);
                }
            } else {
                error(log, ctx, "Import file not found: " + path);
            }
        }
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
        if (ctx.identifierLabel != null) {
            String labelName = ctx.IDENTIFIER().getText();
            log.debug("检查标识符 语句标签 {}", labelName);
            IScope scope = currentScope();
            if (Util.hasSymbolOnScope(scope, labelName, Label.class)) {
                at.getErrorMap().put(ctx, "Duplicated label name '" + labelName
                        + "' on scope: " + scope.getScopeName());
            }
            Label label = new Label(labelName, scope);
            at.getSymbolMap().put(ctx, label);
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
    public void enterCatchClause(YourLangParser.CatchClauseContext ctx) {
        String text = ctx.IDENTIFIER().getText();
        addVar(ctx, text, currentScope());
    }

    @Override
    public void enterInterfaceDeclaration(YourLangParser.InterfaceDeclarationContext ctx) {
        String interfaceName = ctx.IDENTIFIER().getText();
        log.debug("检查标识符 接口标识符 {}", interfaceName);
        IScope currentScope = currentScope();
        if (Util.hasSymbolOnScope(currentScope, interfaceName, Interface.class, Struct.class)) {
            at.getErrorMap().put(ctx, "Duplicated interface/struct name '" + interfaceName
                    + "' on scope: " + currentScope.getScopeName());
        }
        Interface anInterface = new Interface(interfaceName, currentScope);
        pushScope(ctx, anInterface);
        at.getTypeMap().put(ctx, anInterface);
        at.getSymbolMap().put(ctx, anInterface);
    }

    @Override
    public void exitInterfaceDeclaration(YourLangParser.InterfaceDeclarationContext ctx) {
        popScope();
    }

    @Override
    public void enterConstantDeclarator(YourLangParser.ConstantDeclaratorContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        log.debug("检查标识符 常量 {}", name);
        IScope scope = currentScope();
        if (Util.hasSymbolOnScope(scope, name, Variable.class, Constant.class)) {
            at.getErrorMap().put(ctx, "Duplicated parameter name '" + name
                    + "' on scope: " + scope.getScopeName());
        }
        Constant constant = new Constant(name, scope);
        at.getSymbolMap().put(ctx, constant);
    }

    @Override
    public void enterStructDeclaration(YourLangParser.StructDeclarationContext ctx) {
        String structName = ctx.IDENTIFIER().getText();
        log.debug("检查标识符 结构体 {}", structName);
        IScope currentScope = currentScope();
        if (Util.hasSymbolOnScope(currentScope, structName, Interface.class, Struct.class)) {
            at.getErrorMap().put(ctx, "Duplicated interface/struct name '" + structName
                    + "' on scope: " + currentScope.getScopeName());
        }
        Struct struct = new Struct(structName, currentScope);
        pushScope(ctx, struct);
        at.getTypeMap().put(ctx, struct);
        at.getSymbolMap().put(ctx, struct);
    }

    @Override
    public void exitStructDeclaration(YourLangParser.StructDeclarationContext ctx) {
        popScope();
    }

    @Override
    public void enterMethodDeclaration(YourLangParser.MethodDeclarationContext ctx) {
        String methodName = ctx.IDENTIFIER().getText();
        log.debug("检查标识符 方法 {}", methodName);
        IScope currentScope = currentScope();
        Method method = new Method(methodName, currentScope);
        pushScope(ctx, method);
        at.getSymbolMap().put(ctx, method);
    }

    @Override
    public void exitMethodDeclaration(YourLangParser.MethodDeclarationContext ctx) {
        at.getScopeMap().put(ctx, currentScope());
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
    public void enterWithOneId(YourLangParser.WithOneIdContext ctx) {
        TerminalNode identifier = ctx.IDENTIFIER();
        addVar(ctx, identifier.getText(), currentScope());
    }

    @Override
    public void enterWithIds(YourLangParser.WithIdsContext ctx) {
        List<TerminalNode> identifier = ctx.IDENTIFIER();
        IScope currentScope = currentScope();
        for (TerminalNode terminalNode : identifier) {
            addVar(ctx, terminalNode.getText(), currentScope);
        }
    }

    private void addVar(ParserRuleContext ctx, String name, IScope scope) {
        log.debug("检查标识符 变量 {}", name);
        if (Util.hasSymbolOnScope(scope, name, Variable.class, Constant.class)) {
            at.getErrorMap().put(ctx, "Duplicated parameter name '" + name
                    + "' on scope: " + scope.getScopeName());
        }
        Variable variable = new Variable(name, scope);
        at.getSymbolMap().put(ctx, variable);
    }

    @Override
    public void enterFormalParameter(YourLangParser.FormalParameterContext ctx) {
        String parameterName = ctx.IDENTIFIER().getText();
        IScope currentScope = currentScope();
        addVar(ctx, parameterName, currentScope);
    }

    @Override
    public void enterLastFormalParameter(YourLangParser.LastFormalParameterContext ctx) {
        String paraName = ctx.IDENTIFIER().getText();
        IScope currentScope = currentScope();
        addVar(ctx, paraName, currentScope);
    }

    @Override
    public void enterFunType(YourLangParser.FunTypeContext ctx) {
        FunctionType functionType = new FunctionType();
        at.getTypeMap().put(ctx, functionType);
    }

    @Override
    public void enterVariableDeclarator(YourLangParser.VariableDeclaratorContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        IScope currentScope = currentScope();
        addVar(ctx, varName, currentScope);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        if (ctx instanceof YourLangParser.YourLangContext) {
            return;
        }
        at.getScopeMap().put(ctx, currentScope());
    }

}
