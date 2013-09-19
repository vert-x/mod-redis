package io.vertx.redis;

import org.vertx.java.core.eventbus.EventBus;

public class RedisClient extends AbstractRedisClient {

    public RedisClient(EventBus eventBus, String redisAddress) {
        super(eventBus, redisAddress);
    }

    /**
     * Set multiple hash fields to multiple values
     * @since 2.0.0
     */
    public void hmset(Object... args) {send("hmset", args);}

    /**
     * Get the values of all the given hash fields
     * @since 2.0.0
     */
    public void hmget(Object... args) {send("hmget", args);}

    /**
     * Determine the index of a member in a sorted set, with scores ordered from high to low
     * @since 2.0.0
     */
    public void zrevrank(Object... args) {send("zrevrank", args);}

    /**
     * Set the expiration for a key as a UNIX timestamp specified in milliseconds
     * @since 2.6.0
     */
    public void pexpireat(Object... args) {send("pexpireat", args);}

    /**
     * Get the time to live for a key
     * @since 1.0.0
     */
    public void ttl(Object... args) {send("ttl", args);}

    /**
     * Check existence of scripts in the script cache.
     * @since 2.6.0
     */
    public void script_exists(Object... args) {send("script_exists", args);}

    /**
     * Intersect multiple sets and store the resulting set in a key
     * @since 1.0.0
     */
    public void sinterstore(Object... args) {send("sinterstore", args);}

    /**
     * Get the UNIX time stamp of the last successful save to disk
     * @since 1.0.0
     */
    public void lastsave(Object... args) {send("lastsave", args);}

    /**
     * Get the length of the value stored in a key
     * @since 2.2.0
     */
    public void strlen(Object... args) {send("strlen", args);}

    /**
     * Move a member from one set to another
     * @since 1.0.0
     */
    public void smove(Object... args) {send("smove", args);}

    /**
     * Forget about all watched keys
     * @since 2.2.0
     */
    public void unwatch(Object... args) {send("unwatch", args);}

    /**
     * Determine the type stored at key
     * @since 1.0.0
     */
    public void type(Object... args) {send("type", args);}

    /**
     * Add one or more members to a set
     * @since 1.0.0
     */
    public void sadd(Object... args) {send("sadd", args);}

    /**
     * Increment the integer value of a key by one
     * @since 1.0.0
     */
    public void incr(Object... args) {send("incr", args);}

    /**
     * Count set bits in a string
     * @since 2.6.0
     */
    public void bitcount(Object... args) {send("bitcount", args);}

    /**
     * Set the value of an element in a list by its index
     * @since 1.0.0
     */
    public void lset(Object... args) {send("lset", args);}

    /**
     * Listen for all requests received by the server in real time
     * @since 1.0.0
     */
    public void monitor(Object... args) {send("monitor", args);}

    /**
     * Get the value of a key
     * @since 1.0.0
     */
    public void get(Object... args) {send("get", args);}

    /**
     * Make the server crash
     * @since 1.0.0
     */
    public void debug_segfault(Object... args) {send("debug_segfault", args);}

    /**
     * Get a range of elements from a list
     * @since 1.0.0
     */
    public void lrange(Object... args) {send("lrange", args);}

    /**
     * Append one or multiple values to a list
     * @since 1.0.0
     */
    public void rpush(Object... args) {send("rpush", args);}

    /**
     * Delete one or more hash fields
     * @since 2.0.0
     */
    public void hdel(Object... args) {send("hdel", args);}

    /**
     * Change the selected database for the current connection
     * @since 1.0.0
     */
    public void select(Object... args) {send("select", args);}

    /**
     * Inspect the state of the Pub/Sub subsystem
     * @since 2.8.0
     */
    public void pubsub(Object... args) {send("pubsub", args);}

    /**
     * Insert an element before or after another element in a list
     * @since 2.2.0
     */
    public void linsert(Object... args) {send("linsert", args);}

    /**
     * Pop a value from a list, push it to another list and return it; or block until one is available
     * @since 2.2.0
     */
    public void brpoplpush(Object... args) {send("brpoplpush", args);}

    /**
     * Trim a list to the specified range
     * @since 1.0.0
     */
    public void ltrim(Object... args) {send("ltrim", args);}

    /**
     * Set multiple keys to multiple values
     * @since 1.0.1
     */
    public void mset(Object... args) {send("mset", args);}

    /**
     * Create a key using the provided serialized value, previously obtained using DUMP.
     * @since 2.6.0
     */
    public void restore(Object... args) {send("restore", args);}

    /**
     * Intersect multiple sets
     * @since 1.0.0
     */
    public void sinter(Object... args) {send("sinter", args);}

    /**
     * Remove and get the last element in a list, or block until one is available
     * @since 2.0.0
     */
    public void brpop(Object... args) {send("brpop", args);}

    /**
     * Watch the given keys to determine execution of the MULTI/EXEC block
     * @since 2.2.0
     */
    public void watch(Object... args) {send("watch", args);}

    /**
     * Get the current connection name
     * @since 2.6.9
     */
    public void client_getname(Object... args) {send("client_getname", args);}

    /**
     * Get the value of a configuration parameter
     * @since 2.0.0
     */
    public void config_get(Object... args) {send("config_get", args);}

    /**
     * Get all the members in a set
     * @since 1.0.0
     */
    public void smembers(Object... args) {send("smembers", args);}

    /**
     * Set the value of a key, only if the key does not exist
     * @since 1.0.0
     */
    public void setnx(Object... args) {send("setnx", args);}

    /**
     * Return a range of members in a sorted set, by score, with scores ordered from high to low
     * @since 2.2.0
     */
    public void zrevrangebyscore(Object... args) {send("zrevrangebyscore", args);}

    /**
     * Increment the float value of a key by the given amount
     * @since 2.6.0
     */
    public void incrbyfloat(Object... args) {send("incrbyfloat", args);}

    /**
     * Remove and return a random member from a set
     * @since 1.0.0
     */
    public void spop(Object... args) {send("spop", args);}

    /**
     * Get the number of fields in a hash
     * @since 2.0.0
     */
    public void hlen(Object... args) {send("hlen", args);}

    /**
     * Make the server a slave of another instance, or promote it as master
     * @since 1.0.0
     */
    public void slaveof(Object... args) {send("slaveof", args);}

    /**
     * Set the value and expiration in milliseconds of a key
     * @since 2.6.0
     */
    public void psetex(Object... args) {send("psetex", args);}

    /**
     * Set a key's time to live in seconds
     * @since 1.0.0
     */
    public void expire(Object... args) {send("expire", args);}

    /**
     * Decrement the integer value of a key by the given number
     * @since 1.0.0
     */
    public void decrby(Object... args) {send("decrby", args);}

    /**
     * Set multiple keys to multiple values, only if none of the keys exist
     * @since 1.0.1
     */
    public void msetnx(Object... args) {send("msetnx", args);}

    /**
     * Ping the server
     * @since 1.0.0
     */
    public void ping(Object... args) {send("ping", args);}

    /**
     * Discard all commands issued after MULTI
     * @since 2.0.0
     */
    public void discard(Object... args) {send("discard", args);}

    /**
     * Add multiple sorted sets and store the resulting sorted set in a new key
     * @since 2.0.0
     */
    public void zunionstore(Object... args) {send("zunionstore", args);}

    /**
     * Execute a Lua script server side
     * @since 2.6.0
     */
    public void eval(Object... args) {send("eval", args);}

    /**
     * Atomically transfer a key from a Redis instance to another one.
     * @since 2.6.0
     */
    public void migrate(Object... args) {send("migrate", args);}

    /**
     * Remove all keys from the current database
     * @since 1.0.0
     */
    public void flushdb(Object... args) {send("flushdb", args);}

    /**
     * Get the length of a list
     * @since 1.0.0
     */
    public void llen(Object... args) {send("llen", args);}

    /**
     * Synchronously save the dataset to disk and then shut down the server
     * @since 1.0.0
     */
    public void shutdown(Object... args) {send("shutdown", args);}

    /**
     * Get the time to live for a key in milliseconds
     * @since 2.6.0
     */
    public void pttl(Object... args) {send("pttl", args);}

    /**
     * Get all the fields and values in a hash
     * @since 2.0.0
     */
    public void hgetall(Object... args) {send("hgetall", args);}

    /**
     * Rewrite the configuration file with the in memory configuration
     * @since 2.8.0
     */
    public void config_rewrite(Object... args) {send("config_rewrite", args);}

    /**
     * Remove one or more members from a sorted set
     * @since 1.2.0
     */
    public void zrem(Object... args) {send("zrem", args);}

    /**
     * Perform bitwise operations between strings
     * @since 2.6.0
     */
    public void bitop(Object... args) {send("bitop", args);}

    /**
     * Kill the script currently in execution.
     * @since 2.6.0
     */
    public void script_kill(Object... args) {send("script_kill", args);}

    /**
     * Add multiple sets
     * @since 1.0.0
     */
    public void sunion(Object... args) {send("sunion", args);}

    /**
     * Rename a key
     * @since 1.0.0
     */
    public void rename(Object... args) {send("rename", args);}

    /**
     * Return the number of keys in the selected database
     * @since 1.0.0
     */
    public void dbsize(Object... args) {send("dbsize", args);}

    /**
     * Remove the last element in a list, append it to another list and return it
     * @since 1.2.0
     */
    public void rpoplpush(Object... args) {send("rpoplpush", args);}

    /**
     * Append a value to a key
     * @since 2.0.0
     */
    public void append(Object... args) {send("append", args);}

    /**
     * Delete a key
     * @since 1.0.0
     */
    public void del(Object... args) {send("del", args);}

    /**
     * Return a random key from the keyspace
     * @since 1.0.0
     */
    public void randomkey(Object... args) {send("randomkey", args);}

    /**
     * Kill the connection of a client
     * @since 2.4.0
     */
    public void client_kill(Object... args) {send("client_kill", args);}

    /**
     * Get the value of a hash field
     * @since 2.0.0
     */
    public void hget(Object... args) {send("hget", args);}

    /**
     * Remove one or more members from a set
     * @since 1.0.0
     */
    public void srem(Object... args) {send("srem", args);}

    /**
     * Listen for messages published to the given channels
     * @since 2.0.0
     */
    public void subscribe(Object... args) {send("subscribe", args);}

    /**
     * Mark the start of a transaction block
     * @since 1.2.0
     */
    public void multi(Object... args) {send("multi", args);}

    /**
     * Set the string value of a hash field
     * @since 2.0.0
     */
    public void hset(Object... args) {send("hset", args);}

    /**
     * Append a value to a list, only if the list exists
     * @since 2.2.0
     */
    public void rpushx(Object... args) {send("rpushx", args);}

    /**
     * Determine if a given value is a member of a set
     * @since 1.0.0
     */
    public void sismember(Object... args) {send("sismember", args);}

    /**
     * Sets or clears the bit at offset in the string value stored at key
     * @since 2.2.0
     */
    public void setbit(Object... args) {send("setbit", args);}

    /**
     * Close the connection
     * @since 1.0.0
     */
    public void quit(Object... args) {send("quit", args);}

    /**
     * Return the current server time
     * @since 2.6.0
     */
    public void time(Object... args) {send("time", args);}

    /**
     * Stop listening for messages posted to channels matching the given patterns
     * @since 2.0.0
     */
    public void punsubscribe(Object... args) {send("punsubscribe", args);}

    /**
     * Manages the Redis slow queries log
     * @since 2.2.12
     */
    public void slowlog(Object... args) {send("slowlog", args);}

    /**
     * Increment the score of a member in a sorted set
     * @since 1.2.0
     */
    public void zincrby(Object... args) {send("zincrby", args);}

    /**
     * Get the number of members in a set
     * @since 1.0.0
     */
    public void scard(Object... args) {send("scard", args);}

    /**
     * Authenticate to the server
     * @since 1.0.0
     */
    public void auth(Object... args) {send("auth", args);}

    /**
     * Inspect the internals of Redis objects
     * @since 2.2.3
     */
    public void object(Object... args) {send("object", args);}

    /**
     * Subtract multiple sets and store the resulting set in a key
     * @since 1.0.0
     */
    public void sdiffstore(Object... args) {send("sdiffstore", args);}

    /**
     * Remove elements from a list
     * @since 1.0.0
     */
    public void lrem(Object... args) {send("lrem", args);}

    /**
     * Load the specified Lua script into the script cache.
     * @since 2.6.0
     */
    public void script_load(Object... args) {send("script_load", args);}

    /**
     * Return a serialized version of the value stored at the specified key.
     * @since 2.6.0
     */
    public void dump(Object... args) {send("dump", args);}

    /**
     * Stop listening for messages posted to the given channels
     * @since 2.0.0
     */
    public void unsubscribe(Object... args) {send("unsubscribe", args);}

    /**
     * Intersect multiple sorted sets and store the resulting sorted set in a new key
     * @since 2.0.0
     */
    public void zinterstore(Object... args) {send("zinterstore", args);}

    /**
     * Return a range of members in a sorted set, by score
     * @since 1.0.5
     */
    public void zrangebyscore(Object... args) {send("zrangebyscore", args);}

    /**
     * Get the values of all the given keys
     * @since 1.0.0
     */
    public void mget(Object... args) {send("mget", args);}

    /**
     * Set the expiration for a key as a UNIX timestamp
     * @since 1.2.0
     */
    public void expireat(Object... args) {send("expireat", args);}

    /**
     * Remove all members in a sorted set within the given scores
     * @since 1.2.0
     */
    public void zremrangebyscore(Object... args) {send("zremrangebyscore", args);}

    /**
     * Determine the index of a member in a sorted set
     * @since 2.0.0
     */
    public void zrank(Object... args) {send("zrank", args);}

    /**
     * Determine if a key exists
     * @since 1.0.0
     */
    public void exists(Object... args) {send("exists", args);}

    /**
     * Return a range of members in a sorted set, by index
     * @since 1.2.0
     */
    public void zrange(Object... args) {send("zrange", args);}

    /**
     * Get a substring of the string stored at a key
     * @since 2.4.0
     */
    public void getrange(Object... args) {send("getrange", args);}

    /**
     * Set the value and expiration of a key
     * @since 2.0.0
     */
    public void setex(Object... args) {send("setex", args);}

    /**
     * Get the number of members in a sorted set
     * @since 1.2.0
     */
    public void zcard(Object... args) {send("zcard", args);}

    /**
     * Reset the stats returned by INFO
     * @since 2.0.0
     */
    public void config_resetstat(Object... args) {send("config_resetstat", args);}

    /**
     * Return a range of members in a sorted set, by index, with scores ordered from high to low
     * @since 1.2.0
     */
    public void zrevrange(Object... args) {send("zrevrange", args);}

    /**
     * Remove and get the first element in a list, or block until one is available
     * @since 2.0.0
     */
    public void blpop(Object... args) {send("blpop", args);}

    /**
     * Add one or more members to a sorted set, or update its score if it already exists
     * @since 1.2.0
     */
    public void zadd(Object... args) {send("zadd", args);}

    /**
     * Subtract multiple sets
     * @since 1.0.0
     */
    public void sdiff(Object... args) {send("sdiff", args);}

    /**
     * Increment the float value of a hash field by the given amount
     * @since 2.6.0
     */
    public void hincrbyfloat(Object... args) {send("hincrbyfloat", args);}

    /**
     * Determine if a hash field exists
     * @since 2.0.0
     */
    public void hexists(Object... args) {send("hexists", args);}

    /**
     * Asynchronously rewrite the append-only file
     * @since 1.0.0
     */
    public void bgrewriteaof(Object... args) {send("bgrewriteaof", args);}

    /**
     * Internal command used for replication
     * @since 1.0.0
     */
    public void sync(Object... args) {send("sync", args);}

    /**
     * Set the string value of a key and return its old value
     * @since 1.0.0
     */
    public void getset(Object... args) {send("getset", args);}

    /**
     * Remove all keys from all databases
     * @since 1.0.0
     */
    public void flushall(Object... args) {send("flushall", args);}

    /**
     * Rename a key, only if the new key does not exist
     * @since 1.0.0
     */
    public void renamenx(Object... args) {send("renamenx", args);}

    /**
     * Add multiple sets and store the resulting set in a key
     * @since 1.0.0
     */
    public void sunionstore(Object... args) {send("sunionstore", args);}

    /**
     * Sort the elements in a list, set or sorted set
     * @since 1.0.0
     */
    public void sort(Object... args) {send("sort", args);}

    /**
     * Get the score associated with the given member in a sorted set
     * @since 1.2.0
     */
    public void zscore(Object... args) {send("zscore", args);}

    /**
     * Set the string value of a key
     * @since 1.0.0
     */
    public void set(Object... args) {send("set", args);}

    /**
     * Synchronously save the dataset to disk
     * @since 1.0.0
     */
    public void save(Object... args) {send("save", args);}

    /**
     * Execute all commands issued after MULTI
     * @since 1.2.0
     */
    public void exec(Object... args) {send("exec", args);}

    /**
     * Increment the integer value of a hash field by the given number
     * @since 2.0.0
     */
    public void hincrby(Object... args) {send("hincrby", args);}

    /**
     * Get all the fields in a hash
     * @since 2.0.0
     */
    public void hkeys(Object... args) {send("hkeys", args);}

    /**
     * Get one or multiple random members from a set
     * @since 1.0.0
     */
    public void srandmember(Object... args) {send("srandmember", args);}

    /**
     * Get an element from a list by its index
     * @since 1.0.0
     */
    public void lindex(Object... args) {send("lindex", args);}

    /**
     * Increment the integer value of a key by the given amount
     * @since 1.0.0
     */
    public void incrby(Object... args) {send("incrby", args);}

    /**
     * Get information and statistics about the server
     * @since 1.0.0
     */
    public void info(Object... args) {send("info", args);}

    /**
     * Remove all members in a sorted set within the given indexes
     * @since 2.0.0
     */
    public void zremrangebyrank(Object... args) {send("zremrangebyrank", args);}

    /**
     * Count the members in a sorted set with scores within the given values
     * @since 2.0.0
     */
    public void zcount(Object... args) {send("zcount", args);}

    /**
     * Overwrite part of a string at key starting at the specified offset
     * @since 2.2.0
     */
    public void setrange(Object... args) {send("setrange", args);}

    /**
     * Remove and get the first element in a list
     * @since 1.0.0
     */
    public void lpop(Object... args) {send("lpop", args);}

    /**
     * Listen for messages published to channels matching the given patterns
     * @since 2.0.0
     */
    public void psubscribe(Object... args) {send("psubscribe", args);}

    /**
     * Echo the given string
     * @since 1.0.0
     */
    public void echo(Object... args) {send("echo", args);}

    /**
     * Asynchronously save the dataset to disk
     * @since 1.0.0
     */
    public void bgsave(Object... args) {send("bgsave", args);}

    /**
     * Execute a Lua script server side
     * @since 2.6.0
     */
    public void evalsha(Object... args) {send("evalsha", args);}

    /**
     * Prepend one or multiple values to a list
     * @since 1.0.0
     */
    public void lpush(Object... args) {send("lpush", args);}

    /**
     * Remove the expiration from a key
     * @since 2.2.0
     */
    public void persist(Object... args) {send("persist", args);}

    /**
     * Set a key's time to live in milliseconds
     * @since 2.6.0
     */
    public void pexpire(Object... args) {send("pexpire", args);}

    /**
     * Remove and get the last element in a list
     * @since 1.0.0
     */
    public void rpop(Object... args) {send("rpop", args);}

    /**
     * Move a key to another database
     * @since 1.0.0
     */
    public void move(Object... args) {send("move", args);}

    /**
     * Prepend a value to a list, only if the list exists
     * @since 2.2.0
     */
    public void lpushx(Object... args) {send("lpushx", args);}

    /**
     * Decrement the integer value of a key by one
     * @since 1.0.0
     */
    public void decr(Object... args) {send("decr", args);}

    /**
     * Get all the values in a hash
     * @since 2.0.0
     */
    public void hvals(Object... args) {send("hvals", args);}

    /**
     * Set a configuration parameter to the given value
     * @since 2.0.0
     */
    public void config_set(Object... args) {send("config_set", args);}

    /**
     * Find all keys matching the given pattern
     * @since 1.0.0
     */
    public void keys(Object... args) {send("keys", args);}

    /**
     * Set the value of a hash field, only if the field does not exist
     * @since 2.0.0
     */
    public void hsetnx(Object... args) {send("hsetnx", args);}

    /**
     * Returns the bit value at offset in the string value stored at key
     * @since 2.2.0
     */
    public void getbit(Object... args) {send("getbit", args);}

    /**
     * Get the list of client connections
     * @since 2.4.0
     */
    public void client_list(Object... args) {send("client_list", args);}

    /**
     * Set the current connection name
     * @since 2.6.9
     */
    public void client_setname(Object... args) {send("client_setname", args);}

    /**
     * Post a message to a channel
     * @since 2.0.0
     */
    public void publish(Object... args) {send("publish", args);}

    /**
     * Remove all the scripts from the script cache.
     * @since 2.6.0
     */
    public void script_flush(Object... args) {send("script_flush", args);}

    /**
     * Get debugging information about a key
     * @since 1.0.0
     */
    public void debug_object(Object... args) {send("debug_object", args);}


}
