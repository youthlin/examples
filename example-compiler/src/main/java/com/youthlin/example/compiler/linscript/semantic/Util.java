package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Lists;
import org.slf4j.MDC;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @author : youthlin.chen @ 2019-09-01 12:56
 */
public class Util {

    public static boolean hasSymbolOnScope(IScope scope, String name, Class<?>... types) {
        for (Class<?> type : types) {
            if (findSymbolOnScope(scope, name, type) != null) {
                return true;
            }
        }
        return false;
    }

    public static List<ISymbol> findSymbolOnScope(IScope scope, String name, Class<?>... types) {
        List<ISymbol> list = Lists.newArrayList();
        for (Class<?> type : types) {
            ISymbol find = findSymbolOnScope(scope, name, type);
            if (find != null) {
                list.add(find);
            }
        }
        return list;
    }

    private static ISymbol findSymbolOnScope(IScope scope, String name, Class<?> type) {
        for (ISymbol symbol : scope.getSymbols()) {
            if (Objects.equals(symbol.getSymbolName(), name) && symbol.getClass().equals(type)) {
                return symbol;
            }
        }
        return null;
    }

    public static Symbol findFieldSince(Struct struct, String fieldName) {
        if (struct == null) {
            return null;
        }
        for (ISymbol symbol : struct.getSymbols()) {
            if (Objects.equals(symbol.getSymbolName(), fieldName)
                    && (symbol instanceof Variable) || symbol instanceof Constant) {
                return (Symbol) symbol;
            }
        }
        Symbol find = findFieldSince(struct.getSuperStruct(), fieldName);
        if (find != null) {
            return find;
        }
        List<Interface> superInterfaces = struct.getSuperInterfaces();
        if (superInterfaces == null) {
            return null;
        }
        for (Interface superInterface : superInterfaces) {
            find = findFieldSince(superInterface, fieldName);
            if (find != null) {
                return find;
            }
        }
        return null;
    }

    public static Symbol findFieldSince(Interface itfs, String fieldName) {
        if (itfs == null) {
            return null;
        }
        for (ISymbol symbol : itfs.getSymbols()) {
            if (Objects.equals(symbol.getSymbolName(), fieldName)
                    && (symbol instanceof Variable) || symbol instanceof Constant) {
                return (Symbol) symbol;
            }
        }
        List<Interface> superInterfaces = itfs.getSuperInterfaces();
        if (superInterfaces == null) {
            return null;
        }
        for (Interface superInterface : superInterfaces) {
            Symbol find = findFieldSince(superInterface, fieldName);
            if (find != null) {
                return find;
            }
        }
        return null;
    }

    public static Method findMethodSince(Struct struct, String methodName) {
        if (struct == null) {
            return null;
        }
        for (ISymbol symbol : struct.getSymbols()) {
            if (Objects.equals(symbol.getSymbolName(), methodName)
                    && symbol instanceof Method) {
                return (Method) symbol;
            }
        }
        Method find = findMethodSince(struct.getSuperStruct(), methodName);
        if (find != null) {
            return find;
        }
        List<Interface> superInterfaces = struct.getSuperInterfaces();
        if (superInterfaces == null) {
            return null;
        }
        for (Interface superInterface : superInterfaces) {
            find = findMethodSince(superInterface, methodName);
            if (find != null) {
                return find;
            }
        }
        return null;
    }

    public static Method findMethodSince(Interface itfs, String methodName) {
        if (itfs == null) {
            return null;
        }
        for (ISymbol symbol : itfs.getSymbols()) {
            if (Objects.equals(symbol.getSymbolName(), methodName)
                    && symbol instanceof Method) {
                return (Method) symbol;
            }
        }
        List<Interface> superInterfaces = itfs.getSuperInterfaces();
        if (superInterfaces == null) {
            return null;
        }
        for (Interface superInterface : superInterfaces) {
            Method find = findMethodSince(superInterface, methodName);
            if (find != null) {
                return find;
            }
        }
        return null;
    }

    public static void setCurrentFile(File file) {
        MDC.put("CURRENT", file.getName());
    }
}
