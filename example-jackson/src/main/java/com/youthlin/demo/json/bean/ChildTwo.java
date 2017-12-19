package com.youthlin.demo.json.bean;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.youthlin.demo.json.util.Json;

/**
 * 创建: youthlin.chen
 * 时间: 2017-12-19 20:49
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class ChildTwo extends Parent {
    private double weight;

    public double getWeight() {
        return weight;
    }

    public ChildTwo setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
