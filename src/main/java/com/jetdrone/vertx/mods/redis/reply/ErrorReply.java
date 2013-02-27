package com.jetdrone.vertx.mods.redis.reply;

public class ErrorReply implements Reply<String> {
    public static final char MARKER = '-';
    private final String error;

    public ErrorReply(String error) {
        this.error = error;
    }

    @Override
    public String data() {
        return error;
    }

    @Override
    public ReplyType getType() {
        return ReplyType.Error;
    }
}
