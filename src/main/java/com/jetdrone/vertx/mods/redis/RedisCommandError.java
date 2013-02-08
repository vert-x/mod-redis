package com.jetdrone.vertx.mods.redis;

public class RedisCommandError extends Exception {

    public RedisCommandError(String message) {
        super(message);
    }
}
