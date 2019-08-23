package com.youthlin.example.compiler.simplescript;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author youthlin.chen
 * @date 2019-08-23 16:35
 */
@Data
@AllArgsConstructor
public class ErrorMessage {
    private int pos;
    private String message;

}
