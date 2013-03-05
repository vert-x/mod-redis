package com.jetdrone.vertx.mods.redis.reply;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;

import static com.jetdrone.vertx.mods.redis.util.Encoding.numToBytes;

public class IntegerReply implements Reply<Long> {
    public static final char MARKER = ':';
    private final long integer;

    public IntegerReply(long integer) {
        this.integer = integer;
    }

    @Override
    public ReplyType getType() {
        return ReplyType.Integer;
    }


    @Override
    public Long data() {
        return integer;
    }

    @Override
    public boolean isPubSubMessage() {
        return false;
    }
}
