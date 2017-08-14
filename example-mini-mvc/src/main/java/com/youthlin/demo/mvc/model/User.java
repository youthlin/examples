package com.youthlin.demo.mvc.model;

import java.io.Serializable;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-14 16:40.
 */
public class User implements Serializable {
    private Long id;
    private String name;

    @Override public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

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
}
