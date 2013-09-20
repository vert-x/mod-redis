package io.vertx.redis.impl;

import io.vertx.redis.reply.*;

/**
 * An interface mainly created to pass 2 parameters to pub/sub message handler (thus avoiding double casts)
 */
public interface MessageHandler {
    public void handle(String channelOrPattern, Reply[] replyData);
}
