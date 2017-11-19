package com.youthlin.demo.mvc.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-14 16:40.
 */
public class User implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String note;
    private int age;
    private Cat[] cat;

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", note='" + note + '\'' +
                ", age=" + age +
                ", cat=" + Arrays.toString(cat) +
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

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getNote() {
        return note;
    }

    public User setNote(String note) {
        this.note = note;
        return this;
    }

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public Cat[] getCat() {
        return cat;
    }

    public User setCat(Cat[] cat) {
        this.cat = cat;
        return this;
    }
}
