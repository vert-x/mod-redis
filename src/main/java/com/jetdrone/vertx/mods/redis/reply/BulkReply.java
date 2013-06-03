package com.jetdrone.vertx.mods.redis.reply;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public class BulkReply implements Reply<ByteBuf> {
    public static final char MARKER = '$';
    private final ByteBuf bytes;

    public BulkReply(ByteBuf bytes) {
        this.bytes = bytes;
    }

    @Override
    public ByteBuf data() {
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

    public byte[] asByteArray() {
        if (bytes == null) return null;
        int size = bytes.readableBytes();
        byte[] buffer = null;
        if (size > 0) {
            buffer = new byte[size];
            bytes.getBytes(bytes.readerIndex(), buffer);
        }

        return buffer;
    }
}
