package com.youthlin.demo.ioc.po;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-11 15:15.
 */
public class User {
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
