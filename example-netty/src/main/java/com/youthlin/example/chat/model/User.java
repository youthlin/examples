package com.youthlin.example.chat.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 16:02
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -4182093678975864411L;
    private long id;
    private int age;
    private String name;
    private String picUrl;
    private String address;
}
