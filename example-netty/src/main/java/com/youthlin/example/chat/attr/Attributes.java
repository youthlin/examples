package com.youthlin.example.chat.attr;

import io.netty.util.AttributeKey;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 11:11
 */
public interface Attributes {
    AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");

}
