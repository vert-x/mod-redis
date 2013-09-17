package io.vertx.redis.reply;

/**
 * Nested replies.
 */
public class MultiBulkReply implements Reply<Reply[]> {
    private final Reply[] replies;

    public MultiBulkReply(int total) {
        replies = new Reply[total];
    }

    void set(int pos, Reply reply) {
        replies[pos] = reply;
    }

    @Override
    public Reply[] data() {
        return replies;
    }

    @Override
    public byte getType() {
        return '*';
    }
}
