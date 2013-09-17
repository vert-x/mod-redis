package io.vertx.redis.reply;

import java.nio.charset.Charset;

public class BulkReply implements Reply<byte[]> {
    private final byte[] bytes;

    public BulkReply(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] data() {
        return bytes;
    }

    @Override
    public byte getType() {
        return '$';
    }

    public String asString(Charset charset) {
        if (bytes == null) return null;
        return new String(bytes, charset);
    }
}
