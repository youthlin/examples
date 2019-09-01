package com.youthlin.example.compiler.linscript.semantic;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 块作用域
 *
 * @author : youthlin.chen @ 2019-08-31 22:51
 */
@Data
public class BlockScope implements IScope {
    private IScope parent;
    private List<ISymbol> symbols = Lists.newArrayList();
    private List<IScope> childScopes = Lists.newArrayList();

    public BlockScope(IScope parent) {
        this.parent = parent;
    }

}
