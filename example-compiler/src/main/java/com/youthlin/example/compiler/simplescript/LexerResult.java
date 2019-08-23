package com.youthlin.example.compiler.simplescript;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author youthlin.chen
 * @date 2019-08-22 16:18
 */
@Data
@Accessors(chain = true, fluent = true)
public class LexerResult {
    private List<Token> tokenList;
    private List<String> errorList;

    public boolean success() {
        return errorList == null || errorList.isEmpty();
    }

    public boolean fail() {
        return errorList != null && !errorList.isEmpty();
    }

    public void dump() {
        System.out.println(Strings.repeat("-", 50));
        if (success()) {
            tokenList.forEach(System.out::println);
        } else {
            errorList.forEach(System.err::println);
        }
        System.out.println(Strings.repeat("-", 50));
    }
}
