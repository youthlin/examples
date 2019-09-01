package com.youthlin.example.compiler.linscript.semantic;

import com.youthlin.example.compiler.Jsons;
import lombok.Getter;
import lombok.Setter;

/**
 * 全局作用域 没有父作用域
 *
 * @author : youthlin.chen @ 2019-08-31 22:36
 */
@Getter
@Setter
public class GlobalScope extends BlockScope {
    public GlobalScope() {
        super(null);
    }

    @Override
    public String toString() {
        return Jsons.toJson(this);
    }

    @Override
    public String getScopeName() {
        return "Global";
    }

    public String print() {
        return print(0, this);
    }

    private static String print(int i, IScope scope) {
        StringBuilder sb = new StringBuilder(scope.getScopeName()).append("[");
        for (ISymbol symbol : scope.getSymbols()) {
            sb.append(symbol.getSymbolName()).append(" ");
        }
        sb.append("]");
        int index = ++i;
        for (IScope childScope : scope.getChildScopes()) {
            sb.append("\n");
            for (int j = 0; j < index; j++) {
                sb.append("\t");
            }
            sb.append(print(index, childScope));
        }
        return sb.toString();
    }
}
