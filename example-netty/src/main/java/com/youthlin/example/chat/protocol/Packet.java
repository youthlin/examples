package com.youthlin.example.chat.protocol;

import java.io.Serializable;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 15:12
 */
public abstract class Packet implements Serializable {
    private static final long serialVersionUID = 3324536774944494971L;

    public abstract byte command();

}
