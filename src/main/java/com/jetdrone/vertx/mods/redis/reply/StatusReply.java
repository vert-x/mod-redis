package com.jetdrone.vertx.mods.redis.reply;

public class StatusReply implements Reply<String> {
    public static final char MARKER = '+';
    private final String status;

    public StatusReply(String status) {
        this.status = status;
    }

    @Override
    public String data() {
        return status;
    }

    @Override
    public ReplyType getType() {
        return ReplyType.Status;
    }
}
