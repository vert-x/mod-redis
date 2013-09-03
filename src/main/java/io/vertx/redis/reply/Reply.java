package io.vertx.redis.reply;

public interface Reply<T> {
    T data();

    /**
     * Return the type of instance of this Reply. Useful to avoid checks against instanceof
     * @return enum
     */
    ReplyType getType();
}
