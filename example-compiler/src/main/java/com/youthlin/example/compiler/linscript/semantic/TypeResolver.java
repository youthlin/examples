package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Lists;
import com.youthlin.example.compiler.linscript.YourLangParser;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Objects;

/**
 * 类型设置
 *
 * @author : youthlin.chen @ 2019-09-01 17:17
 */
@Slf4j
public class TypeResolver extends BaseListener {

    public TypeResolver(AnnotatedTree tree) {
        super(tree);
    }

    @Override
    public void exitImportPart(YourLangParser.ImportPartContext ctx) {
        ImportSymbol importSymbol = (ImportSymbol) at.getSymbolMap().get(ctx);
        for (Symbol exportSymbol : importSymbol.getImportAt().getExportSymbols()) {
            if (Objects.equals(exportSymbol.getSymbolName(), importSymbol.getOriginal())) {
                importSymbol.setType(exportSymbol.getType());
                log.debug("导入的符号是{} 从文件{}导出 真实类型是 {}", importSymbol,
                        importSymbol.getImportAt().getFile(), exportSymbol.getType());
            }
        }
    }


    /**
     * funType 的形参有递归使用 typeType 所以要在 exit 时处理。
     * 这样处理 funType 时，它的形参部分就已经处理完成了
     */
    @Override
    public void exitTypeType(YourLangParser.TypeTypeContext ctx) {
        IScope currentScope = at.getScopeMap().get(ctx);
        YourLangParser.QualifiedNameContext customType = ctx.qualifiedName();
        YourLangParser.PrimitiveTypeContext primitiveType = ctx.primitiveType();
        YourLangParser.FunTypeContext funType = ctx.funType();
        int arr = ctx.arrTypeSuffix().size();
        IType type = null;
        if (primitiveType != null) {
            type = PrimitiveType.of(primitiveType.getText());
        }
        if (customType != null) {
            type = at.findTypeSinceScope(customType.getText(), currentScope);
        }
        if (funType != null) {
            IType rtType = null;
            YourLangParser.ReturnTypeContext rtCtx = funType.returnType();
            if (rtCtx.typeType() != null) {
                rtType = at.getTypeMap().get(rtCtx.typeType());
            }
            if (rtCtx.VOID() != null) {
                rtType = VoidType.INSTANCE;
            }
            List<IType> parTypes = Lists.newArrayList();
            if (funType.typeList() != null) {
                for (YourLangParser.TypeTypeContext parTyCtx : funType.typeList().typeType()) {
                    parTypes.add(at.getTypeMap().get(parTyCtx));
                }
            }
            FunctionType functionType = new FunctionType();
            functionType.setReturnType(rtType);
            functionType.setParameterType(parTypes);
            type = functionType;
        }
        while (arr-- > 0) {
            type = ArrayType.buildFromElementType(type);
        }
        at.getTypeMap().put(ctx, type);
        log.info("识别 {} 为类型 {}", ctx.getText(), type);
    }

    @Override
    public void exitReturnType(YourLangParser.ReturnTypeContext ctx) {
        if (ctx.VOID() != null) {
            at.getTypeMap().put(ctx, VoidType.INSTANCE);
        } else {
            at.getTypeMap().put(ctx, at.getTypeMap().get(ctx.typeType()));
        }
    }

    @Override
    public void exitStructDeclaration(YourLangParser.StructDeclarationContext ctx) {
        Struct struct = (Struct) at.getScopeMap().get(ctx);
        YourLangParser.TypeTypeContext superStruct = ctx.typeType();
        YourLangParser.TypeListContext interfaces = ctx.typeList();
        if (superStruct != null) {
            IType superType = at.getTypeMap().get(superStruct);
            if (superType instanceof Struct) {
                struct.setSuperStruct((Struct) superType);
            } else {
                at.getErrorMap().put(ctx, "should extends a struct");
            }
        }
        if (interfaces != null) {
            List<Interface> interfaceList = toList(interfaces);
            struct.setSuperInterfaces(interfaceList);
        }
    }

    private List<Interface> toList(YourLangParser.TypeListContext ctx) {
        List<Interface> interfaceList = Lists.newArrayList();
        for (YourLangParser.TypeTypeContext typeCtx : ctx.typeType()) {
            IType itfsType = at.getTypeMap().get(typeCtx);
            if (itfsType instanceof Interface) {
                interfaceList.add((Interface) itfsType);
            } else {
                at.getErrorMap().put(ctx, "should implements a interface");
            }
        }
        return interfaceList;
    }

    @Override
    public void exitInterfaceDeclaration(YourLangParser.InterfaceDeclarationContext ctx) {
        YourLangParser.TypeListContext typeListContext = ctx.typeList();
        if (typeListContext != null) {
            Interface anInterface = (Interface) at.getScopeMap().get(ctx);
            List<Interface> interfaceList = toList(typeListContext);
            anInterface.setSuperInterfaces(interfaceList);
        }
    }

    /**
     * exit 时 typeType 已经识别过了
     */
    @Override
    public void exitLocalVariableDeclaration(YourLangParser.LocalVariableDeclarationContext ctx) {
        YourLangParser.TypeTypeContext typeType = ctx.typeType();
        if (typeType != null) {
            IType type = at.getTypeMap().get(typeType);

            YourLangParser.VariableDeclaratorsContext varDecCtx = ctx.variableDeclarators();
            for (YourLangParser.VariableDeclaratorContext varDec : varDecCtx.variableDeclarator()) {
                ISymbol symbol = at.getSymbolMap().get(varDec);
                ((Variable) symbol).setType(type);
                log.info("设置 变量标识符 {} 的类型 {}", symbol, type);
            }
        }
    }

    @Override
    public void exitConstDeclaration(YourLangParser.ConstDeclarationContext ctx) {
        YourLangParser.TypeTypeContext typeTypeContext = ctx.typeType();
        if (typeTypeContext != null) {
            IType type = at.getTypeMap().get(typeTypeContext);
            for (YourLangParser.ConstantDeclaratorContext dec : ctx.constantDeclarator()) {
                ISymbol symbol = at.getSymbolMap().get(dec);
                ((Constant) symbol).setType(type);
                log.info("设置 常量标识符 {} 的类型 {}", symbol, type);
            }
        }
    }

    @Override
    public void exitFormalParameter(YourLangParser.FormalParameterContext ctx) {
        IType type = at.getTypeMap().get(ctx.typeType());
        ((Variable) at.getSymbolMap().get(ctx)).setType(type);
        log.info("设置 形参 {} 的类型 {}", ctx.IDENTIFIER().getText(), type);
        at.getTypeMap().put(ctx, type);
    }

    @Override
    public void exitLastFormalParameter(YourLangParser.LastFormalParameterContext ctx) {
        IType type = at.getTypeMap().get(ctx.typeType());
        at.getTypeMap().put(ctx, type);
    }

    @Override
    public void exitMethodDeclaration(YourLangParser.MethodDeclarationContext ctx) {
        Method method = (Method) at.getScopeMap().get(ctx);
        YourLangParser.ReturnTypeContext returnType = ctx.returnType();
        method.setReturnType(at.getTypeMap().get(returnType));
        YourLangParser.FormalParametersContext parameters = ctx.formalParameters();
        List<IType> parameterType = toType(parameters.formalParameterList());
        method.setParameterType(parameterType);
        method.done();
        log.info("设置 方法的返回和形参类型 方法 {} 全类型= {}", method, method.getType());
    }

    private List<IType> toType(YourLangParser.FormalParameterListContext parameterListContext) {
        List<IType> parameterType = Lists.newArrayList();
        if (parameterListContext == null) {
            return parameterType;
        }
        for (YourLangParser.FormalParameterContext paramCtx : parameterListContext.formalParameter()) {
            parameterType.add(at.getTypeMap().get(paramCtx));
        }
        if (parameterListContext.lastFormalParameter() != null) {
            parameterType.add(at.getTypeMap().get(parameterListContext.lastFormalParameter()));
        }
        return parameterType;
    }

    @Override
    public void exitLambdaExpression(YourLangParser.LambdaExpressionContext ctx) {
        YourLangParser.LambdaParametersContext parameters = ctx.lambdaParameters();
        if (!(parameters instanceof YourLangParser.WithTypeContext)) {
            return;
        }
        YourLangParser.ReturnTypeContext returnType = ctx.returnType();
        IType rtType;
        if (returnType == null) {
            rtType = VoidType.INSTANCE;
        } else {
            rtType = at.getTypeMap().get(returnType);
        }
        FunctionType functionType = new FunctionType();
        functionType.setReturnType(rtType);
        YourLangParser.FormalParameterListContext list =
                ((YourLangParser.WithTypeContext) parameters).formalParameterList();
        List<IType> parameterType = toType(list);
        functionType.setParameterType(parameterType);
        at.getTypeMap().put(ctx, functionType);
        log.debug("识别 lambda 类型为 {}", functionType);
    }

    @Override
    public void exitExportPart(YourLangParser.ExportPartContext ctx) {
        IScope scope = at.getScope(ctx);
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String exportName = node.getText();
            List<ISymbol> list = Util.findSymbolOnScope(scope, exportName, Struct.class, Interface.class);
            //可以导出结构、接口
            if (list.isEmpty()) {
                error(log, ctx, "Unknown symbol '" + exportName + "' on export, only Struct, Interface, Method can export");
            } else if (list.size() > 1) {
                error(log, ctx, "Find more than one symbol named " + exportName + ", export which one is confused: " + list);
            } else {
                ISymbol symbol = list.get(0);
                IType type = symbol.getType();
                log.info("导出符号 {} 的类型 {}", exportName, type);
                at.getExportSymbols().add((Symbol) symbol);
                for (AnnotatedTree exportTo : at.getExportTo()) {
                    for (ImportSymbol importSymbol : exportTo.getImportSymbols()) {
                        if (Objects.equals(importSymbol.getFile(), at.getFile())
                                && Objects.equals(importSymbol.getOriginal(), exportName)) {
                            importSymbol.setType(type);
                            log.info("将引用本符号的符号 {} 设置为真实类型 {}", importSymbol, type);
                        }
                    }
                }

            }
        }

    }


}
