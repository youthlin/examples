package com.youthlin.example.cglib;

import com.youthlin.example.cglib.bean.FiledRecorder;
import com.youthlin.example.cglib.bean.User;

import java.math.BigDecimal;

/**
 * 创建: youthlin.chen
 * 时间: 2017-11-09 10:14
 */
public class Main {
    public static void main(String[] args) {
        FiledRecorder<User> userFiledRecorder = new FiledRecorder<>(User.class);
        User user = User.newUser(userFiledRecorder);
        System.out.println(userFiledRecorder.getFieldStatusMap());
        System.out.println(userFiledRecorder.getFieldValueMap());
        System.out.println(user);
        user.setAmount(BigDecimal.ONE);
        System.out.println(userFiledRecorder.getFieldStatusMap());
        System.out.println(userFiledRecorder.getFieldValueMap());
        System.out.println(user);
    }
}
