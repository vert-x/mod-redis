package io.vertx.redis.reply;

public class IntegerReply implements Reply<Long> {
    private final long integer;

    public IntegerReply(long integer) {
        this.integer = integer;
    }

    @Override
    public byte getType() {
        return ':';
    }

    @Override
    public Long data() {
        return integer;
    }
}
