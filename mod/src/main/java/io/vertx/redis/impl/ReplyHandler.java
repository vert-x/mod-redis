package io.vertx.redis.impl;

import io.vertx.redis.Reply;

public interface ReplyHandler {

    void handleReply(Reply reply);
}
