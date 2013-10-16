var AbstractRedisClient = require("./abstractRedisClient");

// constructor
module.exports = function (redisAddress) {
  this.init(redisAddress);
};
// super class
module.exports.prototype = new AbstractRedisClient();
// define the constructor to be from this class
module.exports.prototype.constructor=module.exports;

/**
 * Set multiple hash fields to multiple values
 * @since 2.0.0
 */
module.exports.prototype.hmset = function() {this.send("HMSET", arguments);};

/**
 * Get the values of all the given hash fields
 * @since 2.0.0
 */
module.exports.prototype.hmget = function() {this.send("HMGET", arguments);};

/**
 * Determine the index of a member in a sorted set, with scores ordered from high to low
 * @since 2.0.0
 */
module.exports.prototype.zrevrank = function() {this.send("ZREVRANK", arguments);};

/**
 * Set the expiration for a key as a UNIX timestamp specified in milliseconds
 * @since 2.6.0
 */
module.exports.prototype.pexpireat = function() {this.send("PEXPIREAT", arguments);};

/**
 * Get the time to live for a key
 * @since 1.0.0
 */
module.exports.prototype.ttl = function() {this.send("TTL", arguments);};

/**
 * Check existence of scripts in the script cache.
 * @since 2.6.0
 */
module.exports.prototype.script_exists = function() {this.send("SCRIPT EXISTS", arguments);};

/**
 * Intersect multiple sets and store the resulting set in a key
 * @since 1.0.0
 */
module.exports.prototype.sinterstore = function() {this.send("SINTERSTORE", arguments);};

/**
 * Get the UNIX time stamp of the last successful save to disk
 * @since 1.0.0
 */
module.exports.prototype.lastsave = function() {this.send("LASTSAVE", arguments);};

/**
 * Get the length of the value stored in a key
 * @since 2.2.0
 */
module.exports.prototype.strlen = function() {this.send("STRLEN", arguments);};

/**
 * Move a member from one set to another
 * @since 1.0.0
 */
module.exports.prototype.smove = function() {this.send("SMOVE", arguments);};

/**
 * Forget about all watched keys
 * @since 2.2.0
 */
module.exports.prototype.unwatch = function() {this.send("UNWATCH", arguments);};

/**
 * Determine the type stored at key
 * @since 1.0.0
 */
module.exports.prototype.type = function() {this.send("TYPE", arguments);};

/**
 * Add one or more members to a set
 * @since 1.0.0
 */
module.exports.prototype.sadd = function() {this.send("SADD", arguments);};

/**
 * Increment the integer value of a key by one
 * @since 1.0.0
 */
module.exports.prototype.incr = function() {this.send("INCR", arguments);};

/**
 * Count set bits in a string
 * @since 2.6.0
 */
module.exports.prototype.bitcount = function() {this.send("BITCOUNT", arguments);};

/**
 * Set the value of an element in a list by its index
 * @since 1.0.0
 */
module.exports.prototype.lset = function() {this.send("LSET", arguments);};

/**
 * Listen for all requests received by the server in real time
 * @since 1.0.0
 */
module.exports.prototype.monitor = function() {this.send("MONITOR", arguments);};

/**
 * Get the value of a key
 * @since 1.0.0
 */
module.exports.prototype.get = function() {this.send("GET", arguments);};

/**
 * Make the server crash
 * @since 1.0.0
 */
module.exports.prototype.debug_segfault = function() {this.send("DEBUG SEGFAULT", arguments);};

/**
 * Get a range of elements from a list
 * @since 1.0.0
 */
module.exports.prototype.lrange = function() {this.send("LRANGE", arguments);};

/**
 * Append one or multiple values to a list
 * @since 1.0.0
 */
module.exports.prototype.rpush = function() {this.send("RPUSH", arguments);};

/**
 * Delete one or more hash fields
 * @since 2.0.0
 */
module.exports.prototype.hdel = function() {this.send("HDEL", arguments);};

/**
 * Change the selected database for the current connection
 * @since 1.0.0
 */
module.exports.prototype.select = function() {this.send("SELECT", arguments);};

/**
 * Inspect the state of the Pub/Sub subsystem
 * @since 2.8.0
 */
module.exports.prototype.pubsub = function() {this.send("PUBSUB", arguments);};

/**
 * Insert an element before or after another element in a list
 * @since 2.2.0
 */
module.exports.prototype.linsert = function() {this.send("LINSERT", arguments);};

/**
 * Pop a value from a list, push it to another list and return it; or block until one is available
 * @since 2.2.0
 */
module.exports.prototype.brpoplpush = function() {this.send("BRPOPLPUSH", arguments);};

/**
 * Trim a list to the specified range
 * @since 1.0.0
 */
module.exports.prototype.ltrim = function() {this.send("LTRIM", arguments);};

/**
 * Set multiple keys to multiple values
 * @since 1.0.1
 */
module.exports.prototype.mset = function() {this.send("MSET", arguments);};

/**
 * Create a key using the provided serialized value, previously obtained using DUMP.
 * @since 2.6.0
 */
module.exports.prototype.restore = function() {this.send("RESTORE", arguments);};

/**
 * Intersect multiple sets
 * @since 1.0.0
 */
module.exports.prototype.sinter = function() {this.send("SINTER", arguments);};

/**
 * Remove and get the last element in a list, or block until one is available
 * @since 2.0.0
 */
module.exports.prototype.brpop = function() {this.send("BRPOP", arguments);};

/**
 * Watch the given keys to determine execution of the MULTI/EXEC block
 * @since 2.2.0
 */
module.exports.prototype.watch = function() {this.send("WATCH", arguments);};

/**
 * Get the current connection name
 * @since 2.6.9
 */
module.exports.prototype.client_getname = function() {this.send("CLIENT GETNAME", arguments);};

/**
 * Get the value of a configuration parameter
 * @since 2.0.0
 */
module.exports.prototype.config_get = function() {this.send("CONFIG GET", arguments);};

/**
 * Get all the members in a set
 * @since 1.0.0
 */
module.exports.prototype.smembers = function() {this.send("SMEMBERS", arguments);};

/**
 * Set the value of a key, only if the key does not exist
 * @since 1.0.0
 */
module.exports.prototype.setnx = function() {this.send("SETNX", arguments);};

/**
 * Return a range of members in a sorted set, by score, with scores ordered from high to low
 * @since 2.2.0
 */
module.exports.prototype.zrevrangebyscore = function() {this.send("ZREVRANGEBYSCORE", arguments);};

/**
 * Increment the float value of a key by the given amount
 * @since 2.6.0
 */
module.exports.prototype.incrbyfloat = function() {this.send("INCRBYFLOAT", arguments);};

/**
 * Remove and return a random member from a set
 * @since 1.0.0
 */
module.exports.prototype.spop = function() {this.send("SPOP", arguments);};

/**
 * Get the number of fields in a hash
 * @since 2.0.0
 */
module.exports.prototype.hlen = function() {this.send("HLEN", arguments);};

/**
 * Make the server a slave of another instance, or promote it as master
 * @since 1.0.0
 */
module.exports.prototype.slaveof = function() {this.send("SLAVEOF", arguments);};

/**
 * Set the value and expiration in milliseconds of a key
 * @since 2.6.0
 */
module.exports.prototype.psetex = function() {this.send("PSETEX", arguments);};

/**
 * Set a key's time to live in seconds
 * @since 1.0.0
 */
module.exports.prototype.expire = function() {this.send("EXPIRE", arguments);};

/**
 * Decrement the integer value of a key by the given number
 * @since 1.0.0
 */
module.exports.prototype.decrby = function() {this.send("DECRBY", arguments);};

/**
 * Set multiple keys to multiple values, only if none of the keys exist
 * @since 1.0.1
 */
module.exports.prototype.msetnx = function() {this.send("MSETNX", arguments);};

/**
 * Ping the server
 * @since 1.0.0
 */
module.exports.prototype.ping = function() {this.send("PING", arguments);};

/**
 * Discard all commands issued after MULTI
 * @since 2.0.0
 */
module.exports.prototype.discard = function() {this.send("DISCARD", arguments);};

/**
 * Add multiple sorted sets and store the resulting sorted set in a new key
 * @since 2.0.0
 */
module.exports.prototype.zunionstore = function() {this.send("ZUNIONSTORE", arguments);};

/**
 * Execute a Lua script server side
 * @since 2.6.0
 */
module.exports.prototype.eval = function() {this.send("EVAL", arguments);};

/**
 * Atomically transfer a key from a Redis instance to another one.
 * @since 2.6.0
 */
module.exports.prototype.migrate = function() {this.send("MIGRATE", arguments);};

/**
 * Remove all keys from the current database
 * @since 1.0.0
 */
module.exports.prototype.flushdb = function() {this.send("FLUSHDB", arguments);};

/**
 * Get the length of a list
 * @since 1.0.0
 */
module.exports.prototype.llen = function() {this.send("LLEN", arguments);};

/**
 * Synchronously save the dataset to disk and then shut down the server
 * @since 1.0.0
 */
module.exports.prototype.shutdown = function() {this.send("SHUTDOWN", arguments);};

/**
 * Get the time to live for a key in milliseconds
 * @since 2.6.0
 */
module.exports.prototype.pttl = function() {this.send("PTTL", arguments);};

/**
 * Get all the fields and values in a hash
 * @since 2.0.0
 */
module.exports.prototype.hgetall = function() {this.send("HGETALL", arguments);};

/**
 * Rewrite the configuration file with the in memory configuration
 * @since 2.8.0
 */
module.exports.prototype.config_rewrite = function() {this.send("CONFIG REWRITE", arguments);};

/**
 * Remove one or more members from a sorted set
 * @since 1.2.0
 */
module.exports.prototype.zrem = function() {this.send("ZREM", arguments);};

/**
 * Perform bitwise operations between strings
 * @since 2.6.0
 */
module.exports.prototype.bitop = function() {this.send("BITOP", arguments);};

/**
 * Kill the script currently in execution.
 * @since 2.6.0
 */
module.exports.prototype.script_kill = function() {this.send("SCRIPT KILL", arguments);};

/**
 * Add multiple sets
 * @since 1.0.0
 */
module.exports.prototype.sunion = function() {this.send("SUNION", arguments);};

/**
 * Rename a key
 * @since 1.0.0
 */
module.exports.prototype.rename = function() {this.send("RENAME", arguments);};

/**
 * Return the number of keys in the selected database
 * @since 1.0.0
 */
module.exports.prototype.dbsize = function() {this.send("DBSIZE", arguments);};

/**
 * Remove the last element in a list, append it to another list and return it
 * @since 1.2.0
 */
module.exports.prototype.rpoplpush = function() {this.send("RPOPLPUSH", arguments);};

/**
 * Append a value to a key
 * @since 2.0.0
 */
module.exports.prototype.append = function() {this.send("APPEND", arguments);};

/**
 * Delete a key
 * @since 1.0.0
 */
module.exports.prototype.del = function() {this.send("DEL", arguments);};

/**
 * Return a random key from the keyspace
 * @since 1.0.0
 */
module.exports.prototype.randomkey = function() {this.send("RANDOMKEY", arguments);};

/**
 * Kill the connection of a client
 * @since 2.4.0
 */
module.exports.prototype.client_kill = function() {this.send("CLIENT KILL", arguments);};

/**
 * Get the value of a hash field
 * @since 2.0.0
 */
module.exports.prototype.hget = function() {this.send("HGET", arguments);};

/**
 * Remove one or more members from a set
 * @since 1.0.0
 */
module.exports.prototype.srem = function() {this.send("SREM", arguments);};

/**
 * Listen for messages published to the given channels
 * @since 2.0.0
 */
module.exports.prototype.subscribe = function() {this.send("SUBSCRIBE", arguments);};

/**
 * Mark the start of a transaction block
 * @since 1.2.0
 */
module.exports.prototype.multi = function() {this.send("MULTI", arguments);};

/**
 * Set the string value of a hash field
 * @since 2.0.0
 */
module.exports.prototype.hset = function() {this.send("HSET", arguments);};

/**
 * Append a value to a list, only if the list exists
 * @since 2.2.0
 */
module.exports.prototype.rpushx = function() {this.send("RPUSHX", arguments);};

/**
 * Determine if a given value is a member of a set
 * @since 1.0.0
 */
module.exports.prototype.sismember = function() {this.send("SISMEMBER", arguments);};

/**
 * Sets or clears the bit at offset in the string value stored at key
 * @since 2.2.0
 */
module.exports.prototype.setbit = function() {this.send("SETBIT", arguments);};

/**
 * Close the connection
 * @since 1.0.0
 */
module.exports.prototype.quit = function() {this.send("QUIT", arguments);};

/**
 * Return the current server time
 * @since 2.6.0
 */
module.exports.prototype.time = function() {this.send("TIME", arguments);};

/**
 * Stop listening for messages posted to channels matching the given patterns
 * @since 2.0.0
 */
module.exports.prototype.punsubscribe = function() {this.send("PUNSUBSCRIBE", arguments);};

/**
 * Manages the Redis slow queries log
 * @since 2.2.12
 */
module.exports.prototype.slowlog = function() {this.send("SLOWLOG", arguments);};

/**
 * Increment the score of a member in a sorted set
 * @since 1.2.0
 */
module.exports.prototype.zincrby = function() {this.send("ZINCRBY", arguments);};

/**
 * Get the number of members in a set
 * @since 1.0.0
 */
module.exports.prototype.scard = function() {this.send("SCARD", arguments);};

/**
 * Authenticate to the server
 * @since 1.0.0
 */
module.exports.prototype.auth = function() {this.send("AUTH", arguments);};

/**
 * Inspect the internals of Redis objects
 * @since 2.2.3
 */
module.exports.prototype.object = function() {this.send("OBJECT", arguments);};

/**
 * Subtract multiple sets and store the resulting set in a key
 * @since 1.0.0
 */
module.exports.prototype.sdiffstore = function() {this.send("SDIFFSTORE", arguments);};

/**
 * Remove elements from a list
 * @since 1.0.0
 */
module.exports.prototype.lrem = function() {this.send("LREM", arguments);};

/**
 * Load the specified Lua script into the script cache.
 * @since 2.6.0
 */
module.exports.prototype.script_load = function() {this.send("SCRIPT LOAD", arguments);};

/**
 * Return a serialized version of the value stored at the specified key.
 * @since 2.6.0
 */
module.exports.prototype.dump = function() {this.send("DUMP", arguments);};

/**
 * Stop listening for messages posted to the given channels
 * @since 2.0.0
 */
module.exports.prototype.unsubscribe = function() {this.send("UNSUBSCRIBE", arguments);};

/**
 * Intersect multiple sorted sets and store the resulting sorted set in a new key
 * @since 2.0.0
 */
module.exports.prototype.zinterstore = function() {this.send("ZINTERSTORE", arguments);};

/**
 * Return a range of members in a sorted set, by score
 * @since 1.0.5
 */
module.exports.prototype.zrangebyscore = function() {this.send("ZRANGEBYSCORE", arguments);};

/**
 * Get the values of all the given keys
 * @since 1.0.0
 */
module.exports.prototype.mget = function() {this.send("MGET", arguments);};

/**
 * Set the expiration for a key as a UNIX timestamp
 * @since 1.2.0
 */
module.exports.prototype.expireat = function() {this.send("EXPIREAT", arguments);};

/**
 * Remove all members in a sorted set within the given scores
 * @since 1.2.0
 */
module.exports.prototype.zremrangebyscore = function() {this.send("ZREMRANGEBYSCORE", arguments);};

/**
 * Determine the index of a member in a sorted set
 * @since 2.0.0
 */
module.exports.prototype.zrank = function() {this.send("ZRANK", arguments);};

/**
 * Determine if a key exists
 * @since 1.0.0
 */
module.exports.prototype.exists = function() {this.send("EXISTS", arguments);};

/**
 * Return a range of members in a sorted set, by index
 * @since 1.2.0
 */
module.exports.prototype.zrange = function() {this.send("ZRANGE", arguments);};

/**
 * Get a substring of the string stored at a key
 * @since 2.4.0
 */
module.exports.prototype.getrange = function() {this.send("GETRANGE", arguments);};

/**
 * Set the value and expiration of a key
 * @since 2.0.0
 */
module.exports.prototype.setex = function() {this.send("SETEX", arguments);};

/**
 * Get the number of members in a sorted set
 * @since 1.2.0
 */
module.exports.prototype.zcard = function() {this.send("ZCARD", arguments);};

/**
 * Reset the stats returned by INFO
 * @since 2.0.0
 */
module.exports.prototype.config_resetstat = function() {this.send("CONFIG RESETSTAT", arguments);};

/**
 * Return a range of members in a sorted set, by index, with scores ordered from high to low
 * @since 1.2.0
 */
module.exports.prototype.zrevrange = function() {this.send("ZREVRANGE", arguments);};

/**
 * Remove and get the first element in a list, or block until one is available
 * @since 2.0.0
 */
module.exports.prototype.blpop = function() {this.send("BLPOP", arguments);};

/**
 * Add one or more members to a sorted set, or update its score if it already exists
 * @since 1.2.0
 */
module.exports.prototype.zadd = function() {this.send("ZADD", arguments);};

/**
 * Subtract multiple sets
 * @since 1.0.0
 */
module.exports.prototype.sdiff = function() {this.send("SDIFF", arguments);};

/**
 * Increment the float value of a hash field by the given amount
 * @since 2.6.0
 */
module.exports.prototype.hincrbyfloat = function() {this.send("HINCRBYFLOAT", arguments);};

/**
 * Determine if a hash field exists
 * @since 2.0.0
 */
module.exports.prototype.hexists = function() {this.send("HEXISTS", arguments);};

/**
 * Asynchronously rewrite the append-only file
 * @since 1.0.0
 */
module.exports.prototype.bgrewriteaof = function() {this.send("BGREWRITEAOF", arguments);};

/**
 * Internal command used for replication
 * @since 1.0.0
 */
module.exports.prototype.sync = function() {this.send("SYNC", arguments);};

/**
 * Set the string value of a key and return its old value
 * @since 1.0.0
 */
module.exports.prototype.getset = function() {this.send("GETSET", arguments);};

/**
 * Remove all keys from all databases
 * @since 1.0.0
 */
module.exports.prototype.flushall = function() {this.send("FLUSHALL", arguments);};

/**
 * Rename a key, only if the new key does not exist
 * @since 1.0.0
 */
module.exports.prototype.renamenx = function() {this.send("RENAMENX", arguments);};

/**
 * Add multiple sets and store the resulting set in a key
 * @since 1.0.0
 */
module.exports.prototype.sunionstore = function() {this.send("SUNIONSTORE", arguments);};

/**
 * Sort the elements in a list, set or sorted set
 * @since 1.0.0
 */
module.exports.prototype.sort = function() {this.send("SORT", arguments);};

/**
 * Get the score associated with the given member in a sorted set
 * @since 1.2.0
 */
module.exports.prototype.zscore = function() {this.send("ZSCORE", arguments);};

/**
 * Set the string value of a key
 * @since 1.0.0
 */
module.exports.prototype.set = function() {this.send("SET", arguments);};

/**
 * Synchronously save the dataset to disk
 * @since 1.0.0
 */
module.exports.prototype.save = function() {this.send("SAVE", arguments);};

/**
 * Execute all commands issued after MULTI
 * @since 1.2.0
 */
module.exports.prototype.exec = function() {this.send("EXEC", arguments);};

/**
 * Increment the integer value of a hash field by the given number
 * @since 2.0.0
 */
module.exports.prototype.hincrby = function() {this.send("HINCRBY", arguments);};

/**
 * Get all the fields in a hash
 * @since 2.0.0
 */
module.exports.prototype.hkeys = function() {this.send("HKEYS", arguments);};

/**
 * Get one or multiple random members from a set
 * @since 1.0.0
 */
module.exports.prototype.srandmember = function() {this.send("SRANDMEMBER", arguments);};

/**
 * Get an element from a list by its index
 * @since 1.0.0
 */
module.exports.prototype.lindex = function() {this.send("LINDEX", arguments);};

/**
 * Increment the integer value of a key by the given amount
 * @since 1.0.0
 */
module.exports.prototype.incrby = function() {this.send("INCRBY", arguments);};

/**
 * Get information and statistics about the server
 * @since 1.0.0
 */
module.exports.prototype.info = function() {this.send("INFO", arguments);};

/**
 * Remove all members in a sorted set within the given indexes
 * @since 2.0.0
 */
module.exports.prototype.zremrangebyrank = function() {this.send("ZREMRANGEBYRANK", arguments);};

/**
 * Count the members in a sorted set with scores within the given values
 * @since 2.0.0
 */
module.exports.prototype.zcount = function() {this.send("ZCOUNT", arguments);};

/**
 * Overwrite part of a string at key starting at the specified offset
 * @since 2.2.0
 */
module.exports.prototype.setrange = function() {this.send("SETRANGE", arguments);};

/**
 * Remove and get the first element in a list
 * @since 1.0.0
 */
module.exports.prototype.lpop = function() {this.send("LPOP", arguments);};

/**
 * Listen for messages published to channels matching the given patterns
 * @since 2.0.0
 */
module.exports.prototype.psubscribe = function() {this.send("PSUBSCRIBE", arguments);};

/**
 * Echo the given string
 * @since 1.0.0
 */
module.exports.prototype.echo = function() {this.send("ECHO", arguments);};

/**
 * Asynchronously save the dataset to disk
 * @since 1.0.0
 */
module.exports.prototype.bgsave = function() {this.send("BGSAVE", arguments);};

/**
 * Execute a Lua script server side
 * @since 2.6.0
 */
module.exports.prototype.evalsha = function() {this.send("EVALSHA", arguments);};

/**
 * Prepend one or multiple values to a list
 * @since 1.0.0
 */
module.exports.prototype.lpush = function() {this.send("LPUSH", arguments);};

/**
 * Remove the expiration from a key
 * @since 2.2.0
 */
module.exports.prototype.persist = function() {this.send("PERSIST", arguments);};

/**
 * Set a key's time to live in milliseconds
 * @since 2.6.0
 */
module.exports.prototype.pexpire = function() {this.send("PEXPIRE", arguments);};

/**
 * Remove and get the last element in a list
 * @since 1.0.0
 */
module.exports.prototype.rpop = function() {this.send("RPOP", arguments);};

/**
 * Move a key to another database
 * @since 1.0.0
 */
module.exports.prototype.move = function() {this.send("MOVE", arguments);};

/**
 * Prepend a value to a list, only if the list exists
 * @since 2.2.0
 */
module.exports.prototype.lpushx = function() {this.send("LPUSHX", arguments);};

/**
 * Decrement the integer value of a key by one
 * @since 1.0.0
 */
module.exports.prototype.decr = function() {this.send("DECR", arguments);};

/**
 * Get all the values in a hash
 * @since 2.0.0
 */
module.exports.prototype.hvals = function() {this.send("HVALS", arguments);};

/**
 * Set a configuration parameter to the given value
 * @since 2.0.0
 */
module.exports.prototype.config_set = function() {this.send("CONFIG SET", arguments);};

/**
 * Find all keys matching the given pattern
 * @since 1.0.0
 */
module.exports.prototype.keys = function() {this.send("KEYS", arguments);};

/**
 * Set the value of a hash field, only if the field does not exist
 * @since 2.0.0
 */
module.exports.prototype.hsetnx = function() {this.send("HSETNX", arguments);};

/**
 * Returns the bit value at offset in the string value stored at key
 * @since 2.2.0
 */
module.exports.prototype.getbit = function() {this.send("GETBIT", arguments);};

/**
 * Get the list of client connections
 * @since 2.4.0
 */
module.exports.prototype.client_list = function() {this.send("CLIENT LIST", arguments);};

/**
 * Set the current connection name
 * @since 2.6.9
 */
module.exports.prototype.client_setname = function() {this.send("CLIENT SETNAME", arguments);};

/**
 * Post a message to a channel
 * @since 2.0.0
 */
module.exports.prototype.publish = function() {this.send("PUBLISH", arguments);};

/**
 * Remove all the scripts from the script cache.
 * @since 2.6.0
 */
module.exports.prototype.script_flush = function() {this.send("SCRIPT FLUSH", arguments);};

/**
 * Get debugging information about a key
 * @since 1.0.0
 */
module.exports.prototype.debug_object = function() {this.send("DEBUG OBJECT", arguments);};


