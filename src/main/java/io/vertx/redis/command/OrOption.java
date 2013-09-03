package io.vertx.redis.command;

public final class OrOption {
    public final Option o1;
    public final Option o2;

    public OrOption(Option o1, Option o2) {
        this.o1 = o1;
        this.o2 = o2;
    }
}
