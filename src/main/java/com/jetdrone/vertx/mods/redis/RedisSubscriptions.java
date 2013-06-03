package com.jetdrone.vertx.mods.redis;

import com.jetdrone.vertx.mods.redis.util.MessageHandler;

import java.util.HashMap;
import java.util.Map;

public class RedisSubscriptions {

    private final Map<String, MessageHandler> subscribers = new HashMap<String, MessageHandler>();

    public void registerSubscribeHandler(String channel, MessageHandler messageHandler) {
        subscribers.put(channel, messageHandler);
    }

    public void unregisterSubscribeHandler(String channelOrPattern) {
        if (channelOrPattern == null) {
            subscribers.clear();
        } else {
            subscribers.remove(channelOrPattern);
        }
    }

    public MessageHandler getHandler(String channelOrPattern) {
        return subscribers.get(channelOrPattern);
    }
}
