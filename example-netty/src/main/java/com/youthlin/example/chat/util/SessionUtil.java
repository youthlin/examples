package com.youthlin.example.chat.util;

import com.youthlin.example.chat.attr.Attributes;
import com.youthlin.example.chat.model.User;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 16:37
 */
public class SessionUtil {
    private static final Map<Long, Channel> MAP = new ConcurrentHashMap<>();

    public static void bindUser(User user, Channel channel) {
        MAP.put(user.getId(), channel);
        channel.attr(Attributes.SESSION).set(user);
    }

    public static void unBindUser(Channel channel) {
        User user = getUser(channel);
        if (user != null) {
            long id = user.getId();
            MAP.remove(id);
        }
        channel.attr(Attributes.SESSION).set(null);
    }

    public static User getUser(Channel channel) {
        return channel.attr(Attributes.SESSION).get();
    }

    public static Channel getChannel(long userId) {
        return MAP.get(userId);
    }
}
