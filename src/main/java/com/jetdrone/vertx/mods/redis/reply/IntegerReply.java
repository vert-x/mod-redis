package com.jetdrone.vertx.mods.redis.reply;

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
}
