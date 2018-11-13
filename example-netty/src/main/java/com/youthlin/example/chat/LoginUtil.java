package com.youthlin.example.chat;

import com.youthlin.example.chat.attr.Attributes;
import io.netty.channel.Channel;
import io.netty.util.Attribute;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 11:12
 */
public class LoginUtil {
    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel) {
        Attribute<Boolean> loginAttr = channel.attr(Attributes.LOGIN);
        return loginAttr != null && loginAttr.get() != null && loginAttr.get();
    }

}
