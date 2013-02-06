package org.vertx.mods.redis.netty;

public enum ReplyType {
    Error,
    Status,
    Bulk,
    MultiBulk,
    Integer
}
