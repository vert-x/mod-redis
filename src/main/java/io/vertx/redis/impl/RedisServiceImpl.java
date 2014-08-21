package io.vertx.redis.impl;

import io.vertx.core.*;
import io.vertx.core.json.*;

public final class RedisServiceImpl extends AbstractRedisService {

  RedisServiceImpl(Vertx vertx, JsonObject config) { super(vertx, config); }

  public void append(JsonArray args, Handler<AsyncResult<?>> handler) { send("APPEND", args, handler); }

  public void auth(JsonArray args, Handler<AsyncResult<?>> handler) { send("AUTH", args, handler); }

  public void bgrewriteaof(Handler<AsyncResult<?>> handler) { send("BGREWRITEAOF",null, handler); }

  public void bgsave(Handler<AsyncResult<?>> handler) { send("BGSAVE",null, handler); }

  public void bitcount(JsonArray args, Handler<AsyncResult<?>> handler) { send("BITCOUNT", args, handler); }

  public void bitop(JsonArray args, Handler<AsyncResult<?>> handler) { send("BITOP", args, handler); }

  public void bitpos(JsonArray args, Handler<AsyncResult<?>> handler) { send("BITPOS", args, handler); }

  public void blpop(JsonArray args, Handler<AsyncResult<?>> handler) { send("BLPOP", args, handler); }

  public void brpop(JsonArray args, Handler<AsyncResult<?>> handler) { send("BRPOP", args, handler); }

  public void brpoplpush(JsonArray args, Handler<AsyncResult<?>> handler) { send("BRPOPLPUSH", args, handler); }

  public void clientKill(JsonArray args, Handler<AsyncResult<?>> handler) { send("CLIENT KILL", args, handler); }

  public void clientList(Handler<AsyncResult<?>> handler) { send("CLIENT LIST",null, handler); }

  public void clientGetname(Handler<AsyncResult<?>> handler) { send("CLIENT GETNAME",null, handler); }

  public void clientPause(JsonArray args, Handler<AsyncResult<?>> handler) { send("CLIENT PAUSE", args, handler); }

  public void clientSetname(JsonArray args, Handler<AsyncResult<?>> handler) { send("CLIENT SETNAME", args, handler); }

  public void clusterSlots(Handler<AsyncResult<?>> handler) { send("CLUSTER SLOTS",null, handler); }

  public void command(Handler<AsyncResult<?>> handler) { send("COMMAND",null, handler); }

  public void commandCount(Handler<AsyncResult<?>> handler) { send("COMMAND COUNT",null, handler); }

  public void commandGetkeys(Handler<AsyncResult<?>> handler) { send("COMMAND GETKEYS",null, handler); }

  public void commandInfo(JsonArray args, Handler<AsyncResult<?>> handler) { send("COMMAND INFO", args, handler); }

  public void configGet(JsonArray args, Handler<AsyncResult<?>> handler) { send("CONFIG GET", args, handler); }

  public void configRewrite(Handler<AsyncResult<?>> handler) { send("CONFIG REWRITE",null, handler); }

  public void configSet(JsonArray args, Handler<AsyncResult<?>> handler) { send("CONFIG SET", args, handler); }

  public void configResetstat(Handler<AsyncResult<?>> handler) { send("CONFIG RESETSTAT",null, handler); }

  public void dbsize(Handler<AsyncResult<?>> handler) { send("DBSIZE",null, handler); }

  public void debugObject(JsonArray args, Handler<AsyncResult<?>> handler) { send("DEBUG OBJECT", args, handler); }

  public void debugSegfault(Handler<AsyncResult<?>> handler) { send("DEBUG SEGFAULT",null, handler); }

  public void decr(JsonArray args, Handler<AsyncResult<?>> handler) { send("DECR", args, handler); }

  public void decrby(JsonArray args, Handler<AsyncResult<?>> handler) { send("DECRBY", args, handler); }

  public void del(JsonArray args, Handler<AsyncResult<?>> handler) { send("DEL", args, handler); }

  public void discard(Handler<AsyncResult<?>> handler) { send("DISCARD",null, handler); }

  public void dump(JsonArray args, Handler<AsyncResult<?>> handler) { send("DUMP", args, handler); }

  public void echo(JsonArray args, Handler<AsyncResult<?>> handler) { send("ECHO", args, handler); }

  public void eval(JsonArray args, Handler<AsyncResult<?>> handler) { send("EVAL", args, handler); }

  public void evalsha(JsonArray args, Handler<AsyncResult<?>> handler) { send("EVALSHA", args, handler); }

  public void exec(Handler<AsyncResult<?>> handler) { send("EXEC",null, handler); }

  public void exists(JsonArray args, Handler<AsyncResult<?>> handler) { send("EXISTS", args, handler); }

  public void expire(JsonArray args, Handler<AsyncResult<?>> handler) { send("EXPIRE", args, handler); }

  public void expireat(JsonArray args, Handler<AsyncResult<?>> handler) { send("EXPIREAT", args, handler); }

  public void flushall(Handler<AsyncResult<?>> handler) { send("FLUSHALL",null, handler); }

  public void flushdb(Handler<AsyncResult<?>> handler) { send("FLUSHDB",null, handler); }

  public void get(JsonArray args, Handler<AsyncResult<?>> handler) { send("GET", args, handler); }

  public void getbit(JsonArray args, Handler<AsyncResult<?>> handler) { send("GETBIT", args, handler); }

  public void getrange(JsonArray args, Handler<AsyncResult<?>> handler) { send("GETRANGE", args, handler); }

  public void getset(JsonArray args, Handler<AsyncResult<?>> handler) { send("GETSET", args, handler); }

  public void hdel(JsonArray args, Handler<AsyncResult<?>> handler) { send("HDEL", args, handler); }

  public void hexists(JsonArray args, Handler<AsyncResult<?>> handler) { send("HEXISTS", args, handler); }

  public void hget(JsonArray args, Handler<AsyncResult<?>> handler) { send("HGET", args, handler); }

  public void hgetall(JsonArray args, Handler<AsyncResult<?>> handler) { send("HGETALL", args, handler); }

  public void hincrby(JsonArray args, Handler<AsyncResult<?>> handler) { send("HINCRBY", args, handler); }

  public void hincrbyfloat(JsonArray args, Handler<AsyncResult<?>> handler) { send("HINCRBYFLOAT", args, handler); }

  public void hkeys(JsonArray args, Handler<AsyncResult<?>> handler) { send("HKEYS", args, handler); }

  public void hlen(JsonArray args, Handler<AsyncResult<?>> handler) { send("HLEN", args, handler); }

  public void hmget(JsonArray args, Handler<AsyncResult<?>> handler) { send("HMGET", args, handler); }

  public void hmset(JsonArray args, Handler<AsyncResult<?>> handler) { send("HMSET", args, handler); }

  public void hset(JsonArray args, Handler<AsyncResult<?>> handler) { send("HSET", args, handler); }

  public void hsetnx(JsonArray args, Handler<AsyncResult<?>> handler) { send("HSETNX", args, handler); }

  public void hvals(JsonArray args, Handler<AsyncResult<?>> handler) { send("HVALS", args, handler); }

  public void incr(JsonArray args, Handler<AsyncResult<?>> handler) { send("INCR", args, handler); }

  public void incrby(JsonArray args, Handler<AsyncResult<?>> handler) { send("INCRBY", args, handler); }

  public void incrbyfloat(JsonArray args, Handler<AsyncResult<?>> handler) { send("INCRBYFLOAT", args, handler); }

  public void info(JsonArray args, Handler<AsyncResult<?>> handler) { send("INFO", args, handler); }

  public void keys(JsonArray args, Handler<AsyncResult<?>> handler) { send("KEYS", args, handler); }

  public void lastsave(Handler<AsyncResult<?>> handler) { send("LASTSAVE",null, handler); }

  public void lindex(JsonArray args, Handler<AsyncResult<?>> handler) { send("LINDEX", args, handler); }

  public void linsert(JsonArray args, Handler<AsyncResult<?>> handler) { send("LINSERT", args, handler); }

  public void llen(JsonArray args, Handler<AsyncResult<?>> handler) { send("LLEN", args, handler); }

  public void lpop(JsonArray args, Handler<AsyncResult<?>> handler) { send("LPOP", args, handler); }

  public void lpush(JsonArray args, Handler<AsyncResult<?>> handler) { send("LPUSH", args, handler); }

  public void lpushx(JsonArray args, Handler<AsyncResult<?>> handler) { send("LPUSHX", args, handler); }

  public void lrange(JsonArray args, Handler<AsyncResult<?>> handler) { send("LRANGE", args, handler); }

  public void lrem(JsonArray args, Handler<AsyncResult<?>> handler) { send("LREM", args, handler); }

  public void lset(JsonArray args, Handler<AsyncResult<?>> handler) { send("LSET", args, handler); }

  public void ltrim(JsonArray args, Handler<AsyncResult<?>> handler) { send("LTRIM", args, handler); }

  public void mget(JsonArray args, Handler<AsyncResult<?>> handler) { send("MGET", args, handler); }

  public void migrate(JsonArray args, Handler<AsyncResult<?>> handler) { send("MIGRATE", args, handler); }

  public void monitor(Handler<AsyncResult<?>> handler) { send("MONITOR",null, handler); }

  public void move(JsonArray args, Handler<AsyncResult<?>> handler) { send("MOVE", args, handler); }

  public void mset(JsonArray args, Handler<AsyncResult<?>> handler) { send("MSET", args, handler); }

  public void msetnx(JsonArray args, Handler<AsyncResult<?>> handler) { send("MSETNX", args, handler); }

  public void multi(Handler<AsyncResult<?>> handler) { send("MULTI",null, handler); }

  public void object(JsonArray args, Handler<AsyncResult<?>> handler) { send("OBJECT", args, handler); }

  public void persist(JsonArray args, Handler<AsyncResult<?>> handler) { send("PERSIST", args, handler); }

  public void pexpire(JsonArray args, Handler<AsyncResult<?>> handler) { send("PEXPIRE", args, handler); }

  public void pexpireat(JsonArray args, Handler<AsyncResult<?>> handler) { send("PEXPIREAT", args, handler); }

  public void pfadd(JsonArray args, Handler<AsyncResult<?>> handler) { send("PFADD", args, handler); }

  public void pfcount(JsonArray args, Handler<AsyncResult<?>> handler) { send("PFCOUNT", args, handler); }

  public void pfmerge(JsonArray args, Handler<AsyncResult<?>> handler) { send("PFMERGE", args, handler); }

  public void ping(Handler<AsyncResult<?>> handler) { send("PING",null, handler); }

  public void psetex(JsonArray args, Handler<AsyncResult<?>> handler) { send("PSETEX", args, handler); }

  public void psubscribe(JsonArray args, Handler<AsyncResult<?>> handler) { send("PSUBSCRIBE", args, handler); }

  public void pubsub(JsonArray args, Handler<AsyncResult<?>> handler) { send("PUBSUB", args, handler); }

  public void pttl(JsonArray args, Handler<AsyncResult<?>> handler) { send("PTTL", args, handler); }

  public void publish(JsonArray args, Handler<AsyncResult<?>> handler) { send("PUBLISH", args, handler); }

  public void punsubscribe(JsonArray args, Handler<AsyncResult<?>> handler) { send("PUNSUBSCRIBE", args, handler); }

  public void quit(Handler<AsyncResult<?>> handler) { send("QUIT",null, handler); }

  public void randomkey(Handler<AsyncResult<?>> handler) { send("RANDOMKEY",null, handler); }

  public void rename(JsonArray args, Handler<AsyncResult<?>> handler) { send("RENAME", args, handler); }

  public void renamenx(JsonArray args, Handler<AsyncResult<?>> handler) { send("RENAMENX", args, handler); }

  public void restore(JsonArray args, Handler<AsyncResult<?>> handler) { send("RESTORE", args, handler); }

  public void role(Handler<AsyncResult<?>> handler) { send("ROLE",null, handler); }

  public void rpop(JsonArray args, Handler<AsyncResult<?>> handler) { send("RPOP", args, handler); }

  public void rpoplpush(JsonArray args, Handler<AsyncResult<?>> handler) { send("RPOPLPUSH", args, handler); }

  public void rpush(JsonArray args, Handler<AsyncResult<?>> handler) { send("RPUSH", args, handler); }

  public void rpushx(JsonArray args, Handler<AsyncResult<?>> handler) { send("RPUSHX", args, handler); }

  public void sadd(JsonArray args, Handler<AsyncResult<?>> handler) { send("SADD", args, handler); }

  public void save(Handler<AsyncResult<?>> handler) { send("SAVE",null, handler); }

  public void scard(JsonArray args, Handler<AsyncResult<?>> handler) { send("SCARD", args, handler); }

  public void scriptExists(JsonArray args, Handler<AsyncResult<?>> handler) { send("SCRIPT EXISTS", args, handler); }

  public void scriptFlush(Handler<AsyncResult<?>> handler) { send("SCRIPT FLUSH",null, handler); }

  public void scriptKill(Handler<AsyncResult<?>> handler) { send("SCRIPT KILL",null, handler); }

  public void scriptLoad(JsonArray args, Handler<AsyncResult<?>> handler) { send("SCRIPT LOAD", args, handler); }

  public void sdiff(JsonArray args, Handler<AsyncResult<?>> handler) { send("SDIFF", args, handler); }

  public void sdiffstore(JsonArray args, Handler<AsyncResult<?>> handler) { send("SDIFFSTORE", args, handler); }

  public void select(JsonArray args, Handler<AsyncResult<?>> handler) { send("SELECT", args, handler); }

  public void set(JsonArray args, Handler<AsyncResult<?>> handler) { send("SET", args, handler); }

  public void setbit(JsonArray args, Handler<AsyncResult<?>> handler) { send("SETBIT", args, handler); }

  public void setex(JsonArray args, Handler<AsyncResult<?>> handler) { send("SETEX", args, handler); }

  public void setnx(JsonArray args, Handler<AsyncResult<?>> handler) { send("SETNX", args, handler); }

  public void setrange(JsonArray args, Handler<AsyncResult<?>> handler) { send("SETRANGE", args, handler); }

  public void shutdown(JsonArray args, Handler<AsyncResult<?>> handler) { send("SHUTDOWN", args, handler); }

  public void sinter(JsonArray args, Handler<AsyncResult<?>> handler) { send("SINTER", args, handler); }

  public void sinterstore(JsonArray args, Handler<AsyncResult<?>> handler) { send("SINTERSTORE", args, handler); }

  public void sismember(JsonArray args, Handler<AsyncResult<?>> handler) { send("SISMEMBER", args, handler); }

  public void slaveof(JsonArray args, Handler<AsyncResult<?>> handler) { send("SLAVEOF", args, handler); }

  public void slowlog(JsonArray args, Handler<AsyncResult<?>> handler) { send("SLOWLOG", args, handler); }

  public void smembers(JsonArray args, Handler<AsyncResult<?>> handler) { send("SMEMBERS", args, handler); }

  public void smove(JsonArray args, Handler<AsyncResult<?>> handler) { send("SMOVE", args, handler); }

  public void sort(JsonArray args, Handler<AsyncResult<?>> handler) { send("SORT", args, handler); }

  public void spop(JsonArray args, Handler<AsyncResult<?>> handler) { send("SPOP", args, handler); }

  public void srandmember(JsonArray args, Handler<AsyncResult<?>> handler) { send("SRANDMEMBER", args, handler); }

  public void srem(JsonArray args, Handler<AsyncResult<?>> handler) { send("SREM", args, handler); }

  public void strlen(JsonArray args, Handler<AsyncResult<?>> handler) { send("STRLEN", args, handler); }

  public void subscribe(JsonArray args, Handler<AsyncResult<?>> handler) { send("SUBSCRIBE", args, handler); }

  public void sunion(JsonArray args, Handler<AsyncResult<?>> handler) { send("SUNION", args, handler); }

  public void sunionstore(JsonArray args, Handler<AsyncResult<?>> handler) { send("SUNIONSTORE", args, handler); }

  public void sync(Handler<AsyncResult<?>> handler) { send("SYNC",null, handler); }

  public void time(Handler<AsyncResult<?>> handler) { send("TIME",null, handler); }

  public void ttl(JsonArray args, Handler<AsyncResult<?>> handler) { send("TTL", args, handler); }

  public void type(JsonArray args, Handler<AsyncResult<?>> handler) { send("TYPE", args, handler); }

  public void unsubscribe(JsonArray args, Handler<AsyncResult<?>> handler) { send("UNSUBSCRIBE", args, handler); }

  public void unwatch(Handler<AsyncResult<?>> handler) { send("UNWATCH",null, handler); }

  public void watch(JsonArray args, Handler<AsyncResult<?>> handler) { send("WATCH", args, handler); }

  public void zadd(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZADD", args, handler); }

  public void zcard(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZCARD", args, handler); }

  public void zcount(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZCOUNT", args, handler); }

  public void zincrby(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZINCRBY", args, handler); }

  public void zinterstore(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZINTERSTORE", args, handler); }

  public void zlexcount(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZLEXCOUNT", args, handler); }

  public void zrange(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZRANGE", args, handler); }

  public void zrangebylex(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZRANGEBYLEX", args, handler); }

  public void zrangebyscore(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZRANGEBYSCORE", args, handler); }

  public void zrank(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZRANK", args, handler); }

  public void zrem(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZREM", args, handler); }

  public void zremrangebylex(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZREMRANGEBYLEX", args, handler); }

  public void zremrangebyrank(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZREMRANGEBYRANK", args, handler); }

  public void zremrangebyscore(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZREMRANGEBYSCORE", args, handler); }

  public void zrevrange(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZREVRANGE", args, handler); }

  public void zrevrangebyscore(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZREVRANGEBYSCORE", args, handler); }

  public void zrevrank(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZREVRANK", args, handler); }

  public void zscore(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZSCORE", args, handler); }

  public void zunionstore(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZUNIONSTORE", args, handler); }

  public void scan(JsonArray args, Handler<AsyncResult<?>> handler) { send("SCAN", args, handler); }

  public void sscan(JsonArray args, Handler<AsyncResult<?>> handler) { send("SSCAN", args, handler); }

  public void hscan(JsonArray args, Handler<AsyncResult<?>> handler) { send("HSCAN", args, handler); }

  public void zscan(JsonArray args, Handler<AsyncResult<?>> handler) { send("ZSCAN", args, handler); }

}
