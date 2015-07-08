package io.vertx.java.redis;

import org.vertx.java.core.eventbus.EventBus;

public class RedisClient extends AbstractRedisClient {

    public RedisClient(EventBus eventBus, String redisAddress) {
        super(eventBus, redisAddress);
    }

  /**
   * Get the values of all the given hash fields
   * @since 2.0.0
   */
  public void hmget(Object... args) {send(RedisCommand.HMGET, args);}

  /**
   * Set multiple hash fields to multiple values
   * @since 2.0.0
   */
  public void hmset(Object... args) {send(RedisCommand.HMSET, args);}

  /**
   * Determine the index of a member in a sorted set, with scores ordered from high to low
   * @since 2.0.0
   */
  public void zrevrank(Object... args) {send(RedisCommand.ZREVRANK, args);}

  /**
   * Set the expiration for a key as a UNIX timestamp specified in milliseconds
   * @since 2.6.0
   */
  public void pexpireat(Object... args) {send(RedisCommand.PEXPIREAT, args);}

  /**
   * Get the time to live for a key
   * @since 1.0.0
   */
  public void ttl(Object... args) {send(RedisCommand.TTL, args);}

  /**
   * Check existence of scripts in the script cache.
   * @since 2.6.0
   */
  public void script_exists(Object... args) {send(RedisCommand.SCRIPT_EXISTS, args);}

  /**
   * Intersect multiple sets and store the resulting set in a key
   * @since 1.0.0
   */
  public void sinterstore(Object... args) {send(RedisCommand.SINTERSTORE, args);}

  /**
   * Get the UNIX time stamp of the last successful save to disk
   * @since 1.0.0
   */
  public void lastsave(Object... args) {send(RedisCommand.LASTSAVE, args);}

  /**
   * Get the length of the value stored in a key
   * @since 2.2.0
   */
  public void strlen(Object... args) {send(RedisCommand.STRLEN, args);}

  /**
   * Move a member from one set to another
   * @since 1.0.0
   */
  public void smove(Object... args) {send(RedisCommand.SMOVE, args);}

  /**
   * Forget about all watched keys
   * @since 2.2.0
   */
  public void unwatch(Object... args) {send(RedisCommand.UNWATCH, args);}

  /**
   * Determine the type stored at key
   * @since 1.0.0
   */
  public void type(Object... args) {send(RedisCommand.TYPE, args);}

  /**
   * Add one or more members to a set
   * @since 1.0.0
   */
  public void sadd(Object... args) {send(RedisCommand.SADD, args);}

  /**
   * Increment the integer value of a key by one
   * @since 1.0.0
   */
  public void incr(Object... args) {send(RedisCommand.INCR, args);}

  /**
   * Count set bits in a string
   * @since 2.6.0
   */
  public void bitcount(Object... args) {send(RedisCommand.BITCOUNT, args);}

  /**
   * Set the value of an element in a list by its index
   * @since 1.0.0
   */
  public void lset(Object... args) {send(RedisCommand.LSET, args);}

  /**
   * Listen for all requests received by the server in real time
   * @since 1.0.0
   */
  public void monitor(Object... args) {send(RedisCommand.MONITOR, args);}

  /**
   * Get the value of a key
   * @since 1.0.0
   */
  public void get(Object... args) {send(RedisCommand.GET, args);}

  /**
   * Make the server crash
   * @since 1.0.0
   */
  public void debug_segfault(Object... args) {send(RedisCommand.DEBUG_SEGFAULT, args);}

  /**
   * Append one or multiple values to a list
   * @since 1.0.0
   */
  public void rpush(Object... args) {send(RedisCommand.RPUSH, args);}

  /**
   * Get a range of elements from a list
   * @since 1.0.0
   */
  public void lrange(Object... args) {send(RedisCommand.LRANGE, args);}

  /**
   * Delete one or more hash fields
   * @since 2.0.0
   */
  public void hdel(Object... args) {send(RedisCommand.HDEL, args);}

  /**
   * Merge N different HyperLogLogs into a single one.
   * @since 2.8.9
   */
  public void pfmerge(Object... args) {send(RedisCommand.PFMERGE, args);}

  /**
   * Change the selected database for the current connection
   * @since 1.0.0
   */
  public void select(Object... args) {send(RedisCommand.SELECT, args);}

  /**
   * Find first bit set or clear in a string
   * @since 2.8.7
   */
  public void bitpos(Object... args) {send(RedisCommand.BITPOS, args);}

  /**
   * Inspect the state of the Pub/Sub subsystem
   * @since 2.8.0
   */
  public void pubsub(Object... args) {send(RedisCommand.PUBSUB, args);}

  /**
   * Insert an element before or after another element in a list
   * @since 2.2.0
   */
  public void linsert(Object... args) {send(RedisCommand.LINSERT, args);}

  /**
   * Adds the specified elements to the specified HyperLogLog.
   * @since 2.8.9
   */
  public void pfadd(Object... args) {send(RedisCommand.PFADD, args);}

  /**
   * Pop a value from a list, push it to another list and return it; or block until one is available
   * @since 2.2.0
   */
  public void brpoplpush(Object... args) {send(RedisCommand.BRPOPLPUSH, args);}

  /**
   * Trim a list to the specified range
   * @since 1.0.0
   */
  public void ltrim(Object... args) {send(RedisCommand.LTRIM, args);}

  /**
   * Set multiple keys to multiple values
   * @since 1.0.1
   */
  public void mset(Object... args) {send(RedisCommand.MSET, args);}

  /**
   * Create a key using the provided serialized value, previously obtained using DUMP.
   * @since 2.6.0
   */
  public void restore(Object... args) {send(RedisCommand.RESTORE, args);}

  /**
   * Intersect multiple sets
   * @since 1.0.0
   */
  public void sinter(Object... args) {send(RedisCommand.SINTER, args);}

  /**
   * Remove and get the last element in a list, or block until one is available
   * @since 2.0.0
   */
  public void brpop(Object... args) {send(RedisCommand.BRPOP, args);}

  /**
   * Watch the given keys to determine execution of the MULTI/EXEC block
   * @since 2.2.0
   */
  public void watch(Object... args) {send(RedisCommand.WATCH, args);}

  /**
   * Get the current connection name
   * @since 2.6.9
   */
  public void client_getname(Object... args) {send(RedisCommand.CLIENT_GETNAME, args);}

  /**
   * Get the value of a configuration parameter
   * @since 2.0.0
   */
  public void config_get(Object... args) {send(RedisCommand.CONFIG_GET, args);}

  /**
   * Get all the members in a set
   * @since 1.0.0
   */
  public void smembers(Object... args) {send(RedisCommand.SMEMBERS, args);}

  /**
   * Set the value of a key, only if the key does not exist
   * @since 1.0.0
   */
  public void setnx(Object... args) {send(RedisCommand.SETNX, args);}

  /**
   * Return a range of members in a sorted set, by score, with scores ordered from high to low
   * @since 2.2.0
   */
  public void zrevrangebyscore(Object... args) {send(RedisCommand.ZREVRANGEBYSCORE, args);}

  /**
   * Increment the float value of a key by the given amount
   * @since 2.6.0
   */
  public void incrbyfloat(Object... args) {send(RedisCommand.INCRBYFLOAT, args);}

  /**
   * Remove and return a random member from a set
   * @since 1.0.0
   */
  public void spop(Object... args) {send(RedisCommand.SPOP, args);}

  /**
   * Get the number of fields in a hash
   * @since 2.0.0
   */
  public void hlen(Object... args) {send(RedisCommand.HLEN, args);}

  /**
   * Make the server a slave of another instance, or promote it as master
   * @since 1.0.0
   */
  public void slaveof(Object... args) {send(RedisCommand.SLAVEOF, args);}

  /**
   * Set the value and expiration in milliseconds of a key
   * @since 2.6.0
   */
  public void psetex(Object... args) {send(RedisCommand.PSETEX, args);}

  /**
   * Set a key's time to live in seconds
   * @since 1.0.0
   */
  public void expire(Object... args) {send(RedisCommand.EXPIRE, args);}

  /**
   * Decrement the integer value of a key by the given number
   * @since 1.0.0
   */
  public void decrby(Object... args) {send(RedisCommand.DECRBY, args);}

  /**
   * Set multiple keys to multiple values, only if none of the keys exist
   * @since 1.0.1
   */
  public void msetnx(Object... args) {send(RedisCommand.MSETNX, args);}

  /**
   * Ping the server
   * @since 1.0.0
   */
  public void ping(Object... args) {send(RedisCommand.PING, args);}

  /**
   * Discard all commands issued after MULTI
   * @since 2.0.0
   */
  public void discard(Object... args) {send(RedisCommand.DISCARD, args);}

  /**
   * Add multiple sorted sets and store the resulting sorted set in a new key
   * @since 2.0.0
   */
  public void zunionstore(Object... args) {send(RedisCommand.ZUNIONSTORE, args);}

  /**
   * Execute a Lua script server side
   * @since 2.6.0
   */
  public void eval(Object... args) {send(RedisCommand.EVAL, args);}

  /**
   * Return a range of members in a sorted set, by lexicographical range
   * @since 2.8.9
   */
  public void zrangebylex(Object... args) {send(RedisCommand.ZRANGEBYLEX, args);}

  /**
   * Atomically transfer a key from a Redis instance to another one.
   * @since 2.6.0
   */
  public void migrate(Object... args) {send(RedisCommand.MIGRATE, args);}

  /**
   * Remove all keys from the current database
   * @since 1.0.0
   */
  public void flushdb(Object... args) {send(RedisCommand.FLUSHDB, args);}

  /**
   * Get the length of a list
   * @since 1.0.0
   */
  public void llen(Object... args) {send(RedisCommand.LLEN, args);}

  /**
   * Synchronously save the dataset to disk and then shut down the server
   * @since 1.0.0
   */
  public void shutdown(Object... args) {send(RedisCommand.SHUTDOWN, args);}

  /**
   * Get the time to live for a key in milliseconds
   * @since 2.6.0
   */
  public void pttl(Object... args) {send(RedisCommand.PTTL, args);}

  /**
   * Get all the fields and values in a hash
   * @since 2.0.0
   */
  public void hgetall(Object... args) {send(RedisCommand.HGETALL, args);}

  /**
   * Rewrite the configuration file with the in memory configuration
   * @since 2.8.0
   */
  public void config_rewrite(Object... args) {send(RedisCommand.CONFIG_REWRITE, args);}

  /**
   * Remove one or more members from a sorted set
   * @since 1.2.0
   */
  public void zrem(Object... args) {send(RedisCommand.ZREM, args);}

  /**
   * Perform bitwise operations between strings
   * @since 2.6.0
   */
  public void bitop(Object... args) {send(RedisCommand.BITOP, args);}

  /**
   * Kill the script currently in execution.
   * @since 2.6.0
   */
  public void script_kill(Object... args) {send(RedisCommand.SCRIPT_KILL, args);}

  /**
   * Add multiple sets
   * @since 1.0.0
   */
  public void sunion(Object... args) {send(RedisCommand.SUNION, args);}

  /**
   * Rename a key
   * @since 1.0.0
   */
  public void rename(Object... args) {send(RedisCommand.RENAME, args);}

  /**
   * Remove the last element in a list, append it to another list and return it
   * @since 1.2.0
   */
  public void rpoplpush(Object... args) {send(RedisCommand.RPOPLPUSH, args);}

  /**
   * Return the number of keys in the selected database
   * @since 1.0.0
   */
  public void dbsize(Object... args) {send(RedisCommand.DBSIZE, args);}

  /**
   * Append a value to a key
   * @since 2.0.0
   */
  public void append(Object... args) {send(RedisCommand.APPEND, args);}

  /**
   * Delete a key
   * @since 1.0.0
   */
  public void del(Object... args) {send(RedisCommand.DEL, args);}

  /**
   * Return a random key from the keyspace
   * @since 1.0.0
   */
  public void randomkey(Object... args) {send(RedisCommand.RANDOMKEY, args);}

  /**
   * Kill the connection of a client
   * @since 2.4.0
   */
  public void client_kill(Object... args) {send(RedisCommand.CLIENT_KILL, args);}

  /**
   * Get the value of a hash field
   * @since 2.0.0
   */
  public void hget(Object... args) {send(RedisCommand.HGET, args);}

  /**
   * Remove one or more members from a set
   * @since 1.0.0
   */
  public void srem(Object... args) {send(RedisCommand.SREM, args);}

  /**
   * Listen for messages published to the given channels
   * @since 2.0.0
   */
  public void subscribe(Object... args) {send(RedisCommand.SUBSCRIBE, args);}

  /**
   * Incrementally iterate sorted sets elements and associated scores
   * @since 2.8.0
   */
  public void zscan(Object... args) {send(RedisCommand.ZSCAN, args);}

  /**
   * Mark the start of a transaction block
   * @since 1.2.0
   */
  public void multi(Object... args) {send(RedisCommand.MULTI, args);}

  /**
   * Set the string value of a hash field
   * @since 2.0.0
   */
  public void hset(Object... args) {send(RedisCommand.HSET, args);}

  /**
   * Append a value to a list, only if the list exists
   * @since 2.2.0
   */
  public void rpushx(Object... args) {send(RedisCommand.RPUSHX, args);}

  /**
   * Determine if a given value is a member of a set
   * @since 1.0.0
   */
  public void sismember(Object... args) {send(RedisCommand.SISMEMBER, args);}

  /**
   * Sets or clears the bit at offset in the string value stored at key
   * @since 2.2.0
   */
  public void setbit(Object... args) {send(RedisCommand.SETBIT, args);}

  /**
   * Close the connection
   * @since 1.0.0
   */
  public void quit(Object... args) {send(RedisCommand.QUIT, args);}

  /**
   * Return the current server time
   * @since 2.6.0
   */
  public void time(Object... args) {send(RedisCommand.TIME, args);}

  /**
   * Stop listening for messages posted to channels matching the given patterns
   * @since 2.0.0
   */
  public void punsubscribe(Object... args) {send(RedisCommand.PUNSUBSCRIBE, args);}

  /**
   * Manages the Redis slow queries log
   * @since 2.2.12
   */
  public void slowlog(Object... args) {send(RedisCommand.SLOWLOG, args);}

  /**
   * Increment the score of a member in a sorted set
   * @since 1.2.0
   */
  public void zincrby(Object... args) {send(RedisCommand.ZINCRBY, args);}

  /**
   * Get the number of members in a set
   * @since 1.0.0
   */
  public void scard(Object... args) {send(RedisCommand.SCARD, args);}

  /**
   * Authenticate to the server
   * @since 1.0.0
   */
  public void auth(Object... args) {send(RedisCommand.AUTH, args);}

  /**
   * Inspect the internals of Redis objects
   * @since 2.2.3
   */
  public void object(Object... args) {send(RedisCommand.OBJECT, args);}

  /**
   * Subtract multiple sets and store the resulting set in a key
   * @since 1.0.0
   */
  public void sdiffstore(Object... args) {send(RedisCommand.SDIFFSTORE, args);}

  /**
   * Remove elements from a list
   * @since 1.0.0
   */
  public void lrem(Object... args) {send(RedisCommand.LREM, args);}

  /**
   * Incrementally iterate the keys space
   * @since 2.8.0
   */
  public void scan(Object... args) {send(RedisCommand.SCAN, args);}

  /**
   * Load the specified Lua script into the script cache.
   * @since 2.6.0
   */
  public void script_load(Object... args) {send(RedisCommand.SCRIPT_LOAD, args);}

  /**
   * Return a serialized version of the value stored at the specified key.
   * @since 2.6.0
   */
  public void dump(Object... args) {send(RedisCommand.DUMP, args);}

  /**
   * Stop listening for messages posted to the given channels
   * @since 2.0.0
   */
  public void unsubscribe(Object... args) {send(RedisCommand.UNSUBSCRIBE, args);}

  /**
   * Intersect multiple sorted sets and store the resulting sorted set in a new key
   * @since 2.0.0
   */
  public void zinterstore(Object... args) {send(RedisCommand.ZINTERSTORE, args);}

  /**
   * Return a range of members in a sorted set, by score
   * @since 1.0.5
   */
  public void zrangebyscore(Object... args) {send(RedisCommand.ZRANGEBYSCORE, args);}

  /**
   * Get the values of all the given keys
   * @since 1.0.0
   */
  public void mget(Object... args) {send(RedisCommand.MGET, args);}

  /**
   * Set the expiration for a key as a UNIX timestamp
   * @since 1.2.0
   */
  public void expireat(Object... args) {send(RedisCommand.EXPIREAT, args);}

  /**
   * Remove all members in a sorted set within the given scores
   * @since 1.2.0
   */
  public void zremrangebyscore(Object... args) {send(RedisCommand.ZREMRANGEBYSCORE, args);}

  /**
   * Determine the index of a member in a sorted set
   * @since 2.0.0
   */
  public void zrank(Object... args) {send(RedisCommand.ZRANK, args);}

  /**
   * Determine if a key exists
   * @since 1.0.0
   */
  public void exists(Object... args) {send(RedisCommand.EXISTS, args);}

  /**
   * Return a range of members in a sorted set, by index
   * @since 1.2.0
   */
  public void zrange(Object... args) {send(RedisCommand.ZRANGE, args);}

  /**
   * Get a substring of the string stored at a key
   * @since 2.4.0
   */
  public void getrange(Object... args) {send(RedisCommand.GETRANGE, args);}

  /**
   * Set the value and expiration of a key
   * @since 2.0.0
   */
  public void setex(Object... args) {send(RedisCommand.SETEX, args);}

  /**
   * Get the number of members in a sorted set
   * @since 1.2.0
   */
  public void zcard(Object... args) {send(RedisCommand.ZCARD, args);}

  /**
   * Reset the stats returned by INFO
   * @since 2.0.0
   */
  public void config_resetstat(Object... args) {send(RedisCommand.CONFIG_RESETSTAT, args);}

  /**
   * Return a range of members in a sorted set, by index, with scores ordered from high to low
   * @since 1.2.0
   */
  public void zrevrange(Object... args) {send(RedisCommand.ZREVRANGE, args);}

  /**
   * Remove and get the first element in a list, or block until one is available
   * @since 2.0.0
   */
  public void blpop(Object... args) {send(RedisCommand.BLPOP, args);}

  /**
   * Add one or more members to a sorted set, or update its score if it already exists
   * @since 1.2.0
   */
  public void zadd(Object... args) {send(RedisCommand.ZADD, args);}

  /**
   * Subtract multiple sets
   * @since 1.0.0
   */
  public void sdiff(Object... args) {send(RedisCommand.SDIFF, args);}

  /**
   * Increment the float value of a hash field by the given amount
   * @since 2.6.0
   */
  public void hincrbyfloat(Object... args) {send(RedisCommand.HINCRBYFLOAT, args);}

  /**
   * Determine if a hash field exists
   * @since 2.0.0
   */
  public void hexists(Object... args) {send(RedisCommand.HEXISTS, args);}

  /**
   * Asynchronously rewrite the append-only file
   * @since 1.0.0
   */
  public void bgrewriteaof(Object... args) {send(RedisCommand.BGREWRITEAOF, args);}

  /**
   * Internal command used for replication
   * @since 1.0.0
   */
  public void sync(Object... args) {send(RedisCommand.SYNC, args);}

  /**
   * Count the number of members in a sorted set between a given lexicographical range
   * @since 2.8.9
   */
  public void zlexcount(Object... args) {send(RedisCommand.ZLEXCOUNT, args);}

  /**
   * Set the string value of a key and return its old value
   * @since 1.0.0
   */
  public void getset(Object... args) {send(RedisCommand.GETSET, args);}

  /**
   * Remove all keys from all databases
   * @since 1.0.0
   */
  public void flushall(Object... args) {send(RedisCommand.FLUSHALL, args);}

  /**
   * Rename a key, only if the new key does not exist
   * @since 1.0.0
   */
  public void renamenx(Object... args) {send(RedisCommand.RENAMENX, args);}

  /**
   * Add multiple sets and store the resulting set in a key
   * @since 1.0.0
   */
  public void sunionstore(Object... args) {send(RedisCommand.SUNIONSTORE, args);}

  /**
   * Incrementally iterate hash fields and associated values
   * @since 2.8.0
   */
  public void hscan(Object... args) {send(RedisCommand.HSCAN, args);}

  /**
   * Sort the elements in a list, set or sorted set
   * @since 1.0.0
   */
  public void sort(Object... args) {send(RedisCommand.SORT, args);}

  /**
   * Get the score associated with the given member in a sorted set
   * @since 1.2.0
   */
  public void zscore(Object... args) {send(RedisCommand.ZSCORE, args);}

  /**
   * Set the string value of a key
   * @since 1.0.0
   */
  public void set(Object... args) {send(RedisCommand.SET, args);}

  /**
   * Synchronously save the dataset to disk
   * @since 1.0.0
   */
  public void save(Object... args) {send(RedisCommand.SAVE, args);}

  /**
   * Execute all commands issued after MULTI
   * @since 1.2.0
   */
  public void exec(Object... args) {send(RedisCommand.EXEC, args);}

  /**
   * Increment the integer value of a hash field by the given number
   * @since 2.0.0
   */
  public void hincrby(Object... args) {send(RedisCommand.HINCRBY, args);}

  /**
   * Get all the fields in a hash
   * @since 2.0.0
   */
  public void hkeys(Object... args) {send(RedisCommand.HKEYS, args);}

  /**
   * Get one or multiple random members from a set
   * @since 1.0.0
   */
  public void srandmember(Object... args) {send(RedisCommand.SRANDMEMBER, args);}

  /**
   * Incrementally iterate Set elements
   * @since 2.8.0
   */
  public void sscan(Object... args) {send(RedisCommand.SSCAN, args);}

  /**
   * Get an element from a list by its index
   * @since 1.0.0
   */
  public void lindex(Object... args) {send(RedisCommand.LINDEX, args);}

  /**
   * Increment the integer value of a key by the given amount
   * @since 1.0.0
   */
  public void incrby(Object... args) {send(RedisCommand.INCRBY, args);}

  /**
   * Get information and statistics about the server
   * @since 1.0.0
   */
  public void info(Object... args) {send(RedisCommand.INFO, args);}

  /**
   * Remove all members in a sorted set within the given indexes
   * @since 2.0.0
   */
  public void zremrangebyrank(Object... args) {send(RedisCommand.ZREMRANGEBYRANK, args);}

  /**
   * Count the members in a sorted set with scores within the given values
   * @since 2.0.0
   */
  public void zcount(Object... args) {send(RedisCommand.ZCOUNT, args);}

  /**
   * Overwrite part of a string at key starting at the specified offset
   * @since 2.2.0
   */
  public void setrange(Object... args) {send(RedisCommand.SETRANGE, args);}

  /**
   * Remove and get the first element in a list
   * @since 1.0.0
   */
  public void lpop(Object... args) {send(RedisCommand.LPOP, args);}

  /**
   * Return the approximated cardinality of the set(s) observed by the HyperLogLog at key(s).
   * @since 2.8.9
   */
  public void pfcount(Object... args) {send(RedisCommand.PFCOUNT, args);}

  /**
   * Listen for messages published to channels matching the given patterns
   * @since 2.0.0
   */
  public void psubscribe(Object... args) {send(RedisCommand.PSUBSCRIBE, args);}

  /**
   * Echo the given string
   * @since 1.0.0
   */
  public void echo(Object... args) {send(RedisCommand.ECHO, args);}

  /**
   * Asynchronously save the dataset to disk
   * @since 1.0.0
   */
  public void bgsave(Object... args) {send(RedisCommand.BGSAVE, args);}

  /**
   * Execute a Lua script server side
   * @since 2.6.0
   */
  public void evalsha(Object... args) {send(RedisCommand.EVALSHA, args);}

  /**
   * Prepend one or multiple values to a list
   * @since 1.0.0
   */
  public void lpush(Object... args) {send(RedisCommand.LPUSH, args);}

  /**
   * Remove the expiration from a key
   * @since 2.2.0
   */
  public void persist(Object... args) {send(RedisCommand.PERSIST, args);}

  /**
   * Set a key's time to live in milliseconds
   * @since 2.6.0
   */
  public void pexpire(Object... args) {send(RedisCommand.PEXPIRE, args);}

  /**
   * Stop processing commands from clients for some time
   * @since 2.9.50
   */
  public void client_pause(Object... args) {send(RedisCommand.CLIENT_PAUSE, args);}

  /**
   * Remove and get the last element in a list
   * @since 1.0.0
   */
  public void rpop(Object... args) {send(RedisCommand.RPOP, args);}

  /**
   * Move a key to another database
   * @since 1.0.0
   */
  public void move(Object... args) {send(RedisCommand.MOVE, args);}

  /**
   * Prepend a value to a list, only if the list exists
   * @since 2.2.0
   */
  public void lpushx(Object... args) {send(RedisCommand.LPUSHX, args);}

  /**
   * Decrement the integer value of a key by one
   * @since 1.0.0
   */
  public void decr(Object... args) {send(RedisCommand.DECR, args);}

  /**
   * Get all the values in a hash
   * @since 2.0.0
   */
  public void hvals(Object... args) {send(RedisCommand.HVALS, args);}

  /**
   * Set a configuration parameter to the given value
   * @since 2.0.0
   */
  public void config_set(Object... args) {send(RedisCommand.CONFIG_SET, args);}

  /**
   * Find all keys matching the given pattern
   * @since 1.0.0
   */
  public void keys(Object... args) {send(RedisCommand.KEYS, args);}

  /**
   * Set the value of a hash field, only if the field does not exist
   * @since 2.0.0
   */
  public void hsetnx(Object... args) {send(RedisCommand.HSETNX, args);}

  /**
   * Returns the bit value at offset in the string value stored at key
   * @since 2.2.0
   */
  public void getbit(Object... args) {send(RedisCommand.GETBIT, args);}

  /**
   * Get the list of client connections
   * @since 2.4.0
   */
  public void client_list(Object... args) {send(RedisCommand.CLIENT_LIST, args);}

  /**
   * Set the current connection name
   * @since 2.6.9
   */
  public void client_setname(Object... args) {send(RedisCommand.CLIENT_SETNAME, args);}

  /**
   * Post a message to a channel
   * @since 2.0.0
   */
  public void publish(Object... args) {send(RedisCommand.PUBLISH, args);}

  /**
   * Remove all members in a sorted set between the given lexicographical range
   * @since 2.8.9
   */
  public void zremrangebylex(Object... args) {send(RedisCommand.ZREMRANGEBYLEX, args);}

  /**
   * Remove all the scripts from the script cache.
   * @since 2.6.0
   */
  public void script_flush(Object... args) {send(RedisCommand.SCRIPT_FLUSH, args);}

  /**
   * Get debugging information about a key
   * @since 1.0.0
   */
  public void debug_object(Object... args) {send(RedisCommand.DEBUG_OBJECT, args);}


}