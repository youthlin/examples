package com.youthlin.example.compiler.linscript.semantic;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * @author youthlin.chen
 * @date 2019-09-03 15:08
 */
@Getter
@Setter
public class ImportSymbol extends Symbol implements IType {
    private String original;
    private File file;
    private AnnotatedTree importAt;

    public ImportSymbol(IScope scope, String symbolName, String originalName, File file, AnnotatedTree importAt) {
        super(symbolName, scope);
        this.original = originalName;
        this.file = file;
        this.importAt = importAt;
        //初始未知类型
        setType(UnknownType.INSTANCE);
    }

    @Override
    public Kind getKind() {
        return Kind.Import;
    }

    @Override
    public String toString() {
        return "ImportSymbol " + getSymbolName() + "(" + getType() + ")";
    }

    @Override
    public String getTypeName() {
        return getType().getTypeName();
    }

}
