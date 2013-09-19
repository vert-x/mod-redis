package io.vertx.redis.reply;

public class ErrorReply implements Reply<String> {
    private final String error;

    public ErrorReply(String error) {
        this.error = error;
    }

    @Override
    public String data() {
        return error;
    }

    @Override
    public byte getType() {
        return '-';
    }
}
