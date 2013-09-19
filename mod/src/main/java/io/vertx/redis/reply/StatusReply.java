package io.vertx.redis.reply;

public class StatusReply implements Reply<String> {
    private final String status;

    public StatusReply(String status) {
        this.status = status;
    }

    @Override
    public String data() {
        return status;
    }

    @Override
    public byte getType() {
        return '+';
    }
}
