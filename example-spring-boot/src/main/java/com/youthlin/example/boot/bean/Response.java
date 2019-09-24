package com.youthlin.example.boot.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : youthlin.chen @ 2019-09-18 22:12
 */
@Data
@Accessors(chain = true)
public class Response<T> {
    private int code;
    private String msg;
    private T data;
}
