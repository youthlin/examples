package com.youthlin.example.compiler.linscript.semantic;

/**
 * @author : youthlin.chen @ 2019-09-02 13:56
 */
public class Label extends Symbol {
    public Label(String symbolName, IScope scope) {
        super(symbolName, scope);
    }

    @Override
    public Kind getKind() {
        return Kind.Label;
    }
}
