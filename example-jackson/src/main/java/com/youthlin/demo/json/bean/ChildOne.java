package com.youthlin.demo.json.bean;

import com.youthlin.demo.json.util.Json;

/**
 * 创建: youthlin.chen
 * 时间: 2017-12-19 20:45
 */

public class ChildOne extends Parent {
    private Integer age;

    public Integer getAge() {
        return age;
    }

    public ChildOne setAge(Integer age) {
        this.age = age;
        return this;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
