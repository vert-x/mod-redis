package com.jetdrone.vertx.mods.redis.reply;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;

public class BulkReply implements Reply<ChannelBuffer> {
    public static final char MARKER = '$';
    private final ChannelBuffer bytes;

    public BulkReply(ChannelBuffer bytes) {
        this.bytes = bytes;
    }

    @Override
    public ChannelBuffer data() {
        return bytes;
    }

    @Override
    public ReplyType getType() {
        return ReplyType.Bulk;
    }

    public String asString(Charset charset) {
        if (bytes == null) return null;
        return bytes.toString(charset);
    }
}
