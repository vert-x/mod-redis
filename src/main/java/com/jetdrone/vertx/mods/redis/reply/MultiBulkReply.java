package com.jetdrone.vertx.mods.redis.reply;

import com.jetdrone.vertx.mods.redis.RedisDecoder;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Nested replies.
 */
public class MultiBulkReply implements Reply<Reply[]> {
    public static final char MARKER = '*';
    private final Reply[] replies;
    private final int size;
    private int index = 0;

    private static final Charset CHARSET = Charset.defaultCharset();

    public MultiBulkReply(RedisDecoder rd, ByteBuf is) throws IOException {
        long l = RedisDecoder.readLong(is);
        if (l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Java only supports arrays up to " + Integer.MAX_VALUE + " in size");
        }
        size = (int) l;
        if (size == -1) {
            replies = null;
        } else {
            if (size < 0) {
                throw new IllegalArgumentException("Invalid size: " + size);
            }
            replies = new Reply[size];
            read(rd, is);
        }
    }

    public void read(RedisDecoder rd, ByteBuf is) throws IOException {
        for (int i = index; i < size; i++) {
            replies[i] = rd.receive(is);
            index = i + 1;
        }
    }

    @Override
    public Reply[] data() {
        return replies;
    }

    @Override
    public ReplyType getType() {
        return ReplyType.MultiBulk;
    }
}
