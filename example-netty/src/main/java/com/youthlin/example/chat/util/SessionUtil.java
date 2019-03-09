package com.youthlin.example.chat.util;

import com.youthlin.example.chat.attr.Attributes;
import com.youthlin.example.chat.model.User;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-13 16:37
 */
public class SessionUtil {
    private static final Map<Long, Channel> USER_ID_CHANNEL_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, ChannelGroup> GROUP_ID_CHANNEL_GROUP_MAP = new ConcurrentHashMap<>();

    public static void bindUser(User user, Channel channel) {
        USER_ID_CHANNEL_MAP.put(user.getId(), channel);
        channel.attr(Attributes.SESSION).set(user);
    }

    public static void unBindUser(Channel channel) {
        User user = getUser(channel);
        if (user != null) {
            long id = user.getId();
            USER_ID_CHANNEL_MAP.remove(id);
        }
        channel.attr(Attributes.SESSION).set(null);
    }

    public static User getUser(Channel channel) {
        return channel.attr(Attributes.SESSION).get();
    }

    public static Channel getChannel(long userId) {
        return USER_ID_CHANNEL_MAP.get(userId);
    }

    public static void bindChannelGroup(long groupId, ChannelGroup group) {
        GROUP_ID_CHANNEL_GROUP_MAP.put(groupId, group);
    }

    public static void clearGroup(long groupId) {
        GROUP_ID_CHANNEL_GROUP_MAP.remove(groupId);
    }

    public static ChannelGroup getChannelGroup(long groupId) {
        return GROUP_ID_CHANNEL_GROUP_MAP.get(groupId);
    }
}
