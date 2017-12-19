package com.youthlin.demo.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youthlin.demo.json.bean.ChildOne;
import com.youthlin.demo.json.bean.ChildTwo;
import com.youthlin.demo.json.bean.Parent;
import com.youthlin.demo.json.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建: youthlin.chen
 * 时间: 2017-12-19 20:50
 */
public class Test {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        Parent parent = new Parent()
                .setId(0L)
                .setName("parent");
        LOGGER.info("parent = {}", parent);
        ChildOne childOne = new ChildOne();
        childOne.setAge(18).setId(1L).setName("child1");
        LOGGER.info("child1 = {}", childOne);
        String childOneJson = Json.toJson(childOne);
        LOGGER.info("child1 json: {}", childOneJson);
        ChildOne childOneFromJson = Json.fromJson(childOneJson, ChildOne.class);
        LOGGER.info("child1 from json: {}", childOneFromJson);

        ChildTwo childTwo = new ChildTwo();
        childTwo.setWeight(60).setId(2L).setName("child2");
        LOGGER.info("child2 = {}", childTwo);
        String childTwoJson = Json.toJson(childTwo);
        LOGGER.info("child2 json: {}", childTwoJson);
        ChildTwo childTwoFromJson = Json.fromJson(childTwoJson, ChildTwo.class);
        LOGGER.info("child2 from json: {}", childTwoFromJson);

    }
}
