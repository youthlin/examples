package com.youthlin.example.compiler.linscript.semantic;

import java.util.List;
import java.util.Objects;

/**
 * @author : youthlin.chen @ 2019-09-01 12:56
 */
public class Util {

    public static <T> T findSymbolOnScope(IScope scope, String name, Class<T> type) {
        for (ISymbol symbol : scope.getSymbols()) {
            if (symbol.getClass().equals(type)) {
                if (Objects.equals(symbol.getSymbolName(), name)) {
                    return type.cast(symbol);
                }
            }
        }
        return null;
    }

    public static boolean checkDuplicateMethodOnScope(IScope scope, String name, List<String> parameterType) {
        for (ISymbol symbol : scope.getSymbols()) {
            if (symbol instanceof Method) {
                if (Objects.equals(symbol.getSymbolName(), name)
                        && Objects.equals(((Method) symbol).getParameterTypeName(), parameterType)
                ) {
                    return true;
                }
            }
        }
        return false;
    }

}
