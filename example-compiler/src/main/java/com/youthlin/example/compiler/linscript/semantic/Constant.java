package com.youthlin.example.compiler.linscript.semantic;

/**
 * @author : youthlin.chen @ 2019-09-02 13:46
 */
public class Constant extends Symbol {
    public Constant(String symbolName, IScope scope) {
        super(symbolName, scope);
    }

    @Override
    public Kind getKind() {
        return Kind.Constant;
    }

    @Override
    public String toString() {
        return "Constant " + getSymbolName();
    }
}
