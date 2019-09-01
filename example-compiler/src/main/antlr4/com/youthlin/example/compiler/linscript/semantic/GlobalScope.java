package com.youthlin.example.compiler.linscript.semantic;

import lombok.Data;

/**
 * 全局作用域 没有父作用域
 *
 * @author : youthlin.chen @ 2019-08-31 22:36
 */
@Data
public class GlobalScope extends BlockScope {
    public GlobalScope() {
        super(null);
    }

}
