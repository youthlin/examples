package com.youthlin.example.rpc.bean;

import java.io.Serializable;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-26 22:19
 */
public class User implements Serializable {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    @Override public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
