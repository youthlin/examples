package com.youthlin.demo.json.bean;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.youthlin.demo.json.util.Json;

/**
 * 创建: youthlin.chen
 * 时间: 2017-12-19 20:44
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS
        , include = JsonTypeInfo.As.PROPERTY
        , property = "@class"
        , defaultImpl = ChildOne.class
)
public class Parent {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public Parent setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Parent setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
