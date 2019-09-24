package com.youthlin.example.compiler.linscript.semantic;

/**
 * @author : youthlin.chen @ 2019-09-02 13:16
 */
public class Variable extends Symbol {
    public Variable(String symbolName, IScope scope) {
        super(symbolName, scope);
    }

    @Override
    public Kind getKind() {
        return Kind.Variable;
    }

    @Override
    public String toString() {
        return "Variable " + getSymbolName();
    }
}
