package com.youthlin.example.compiler.linscript.semantic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 块作用域
 *
 * @author : youthlin.chen @ 2019-08-31 22:51
 */
@Getter
@Setter
public class BlockScope implements IScope {
    private static int count = 0;
    @JsonIgnore
    private IScope parent;
    private String scopeName;
    private List<ISymbol> symbols = Lists.newArrayList();
    @JsonIgnore
    private List<IScope> childScopes = Lists.newArrayList();

    public BlockScope(IScope parent) {
        this(buildName(parent), parent);
    }

    public BlockScope(String name, IScope parent) {
        this.scopeName = name;
        this.parent = parent;
        if (parent != null) {
            parent.getChildScopes().add(this);
        }
    }

    private static String buildName(IScope parent) {
        if (parent != null) {
            if (parent instanceof Method) {
                return "MethodBody";
            }
            if (parent.getScopeName().equals("lambda")) {
                return "LambdaBody";
            }
            if (parent.getScopeName().equals("for")) {
                return "ForBody";
            }
        }
        return "BlockScope#" + count++;
    }

}
