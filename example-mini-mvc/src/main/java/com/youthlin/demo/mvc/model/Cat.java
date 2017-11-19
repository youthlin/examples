package com.youthlin.demo.mvc.model;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-19 00:42
 */
public class Cat {
    private double weight;

    @Override public String toString() {
        return "Cat{" +
                "weight=" + weight +
                '}';
    }

    public double getWeight() {
        return weight;
    }

    public Cat setWeight(double weight) {
        this.weight = weight;
        return this;
    }
}
