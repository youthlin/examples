package com.youthlin.example.rpc.api.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建: youthlin.chen
 * 时间: 2017-10-28 00:07
 */
public class User implements Serializable {
    private Long id;
    private String name;
    private int age;
    private Map<String, Object> ext = new HashMap<>();

    //region getter and setter
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

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public User setExt(Map<String, Object> ext) {
        this.ext = ext;
        return this;
    }

    //endregion getter and setter

    @Override public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", ext=" + ext +
                '}';
    }
}
