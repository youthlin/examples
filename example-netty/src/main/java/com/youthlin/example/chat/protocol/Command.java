package com.youthlin.example.chat.protocol;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-12 15:13
 */
public interface Command {
    byte LOGIN_REQUEST = 1;
    byte LOGIN_RESPONSE = 2;
    byte LOGOUT_REQUEST = 3;
    byte MESSAGE_REQUEST = 4;
    byte CREATE_GROUP_REQUEST = 5;
    byte JOIN_GROUP_REQUEST = 6;
    byte QUIT_GROUP_REQUEST = 7;

}
