package com.jetdrone.vertx.mods.redis;

import com.jetdrone.vertx.mods.redis.reply.Reply;
import org.vertx.java.core.Handler;

import java.util.HashMap;
import java.util.Map;

public class RedisSubscriptions {

    private final Map<String, Handler<Reply>> subscribers = new HashMap<>();

    public void registerSubscribeHandler(String channel, Handler<Reply> replyHandler) {
        subscribers.put(channel, replyHandler);
    }

    public void unregisterSubscribeHandler(String channel) {
        if (channel == null) {
            subscribers.clear();
        } else {
            subscribers.remove(channel);
        }
    }

    public Handler<Reply> getHandler(String channel) {
        return subscribers.get(channel);
    }
}
