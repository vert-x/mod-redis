package io.vertx.redis;

import io.vertx.redis.util.MessageHandler;

import java.util.HashMap;
import java.util.Map;

public class RedisSubscriptions {

    private final Map<String, MessageHandler> subscribers = new HashMap<>();

    public void registerSubscribeHandler(String channelOrPattern, MessageHandler messageHandler) {
        subscribers.put(channelOrPattern, messageHandler);
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

    public int size() {
        return subscribers.size();
    }
}
