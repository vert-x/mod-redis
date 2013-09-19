package io.vertx.redis.reply;

import org.vertx.java.core.buffer.Buffer;

public class BulkReply implements Reply<Buffer> {
    private final Buffer buffer;

    public BulkReply(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public Buffer data() {
        return buffer;
    }

    @Override
    public byte getType() {
        return '$';
    }

    public String asString(String encoding) {
        if (buffer == null) return null;
        return buffer.toString(encoding);
    }
}
