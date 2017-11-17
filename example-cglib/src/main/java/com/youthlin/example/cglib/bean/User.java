package com.youthlin.example.cglib.bean;

import net.sf.cglib.proxy.Enhancer;

import java.math.BigDecimal;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-09 10:14
 */
public class User {
    private Long id;
    private String name;
    private BigDecimal amount;

    //private 不能被 cglib create
    protected User() {
    }

    public static User newUser(FiledRecorder filedRecorder) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(User.class);
        enhancer.setCallback(filedRecorder);
        return (User) enhancer.create();
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

    public BigDecimal getAmount() {
        return amount;
    }

    public User setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }
}
