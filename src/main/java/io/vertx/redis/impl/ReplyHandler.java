package io.vertx.redis.impl;

public interface ReplyHandler {

    void handleReply(Reply reply);
}
