package io.vertx.redis;

public class RedisCommandError extends Exception {

    public RedisCommandError(String message) {
        super(message);
    }
}
