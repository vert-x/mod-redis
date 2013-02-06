class RedisHelper {

    def address
    def eventBus

    RedisHelper(eventBus, address = 'redis-client') {
        this.eventBus = eventBus
        this.address = address
    }

    // connection

    def select(int index, Closure closure = null) {
        eventBus.send(address, [command: 'select', index: index], closure)
    }

    // hashes

    def hdel(String key, fields, Closure closure = null) {
        eventBus.send(address, [command: 'hdel', key: key, fields: fields], closure)
    }

    def hexists(String key, field, Closure closure = null) {
        eventBus.send(address, [command:  'hexists', key: key, field: field], closure)
    }

    def hgetall(String key, Closure closure = null) {
        eventBus.send(address, [command: 'hgetall', key: key], closure)
    }

    def hget(String key, field, Closure closure = null) {
        eventBus.send(address, [command: 'hget', key: key, field: field], closure)
    }

    def hincrby(String key, field, increment, Closure closure = null) {
        eventBus.send(address, [command: 'hincrby', key: key, field: field, increment: increment], closure)
    }

    def hkeys(String key, Closure closure = null) {
        eventBus.send(address, [command: 'hkeys', key: key], closure)
    }

    def hlen(String key, Closure closure = null) {
        eventBus.send(address, [command: 'hlen', key: key], closure)
    }

    def hmget(String key, fields, Closure closure = null) {
        eventBus.send(address, [command: 'hmget', key: key, fields: fields], closure)
    }

    def hmset(String key, fields, Closure closure = null) {
        eventBus.send(address, [command: 'hmset', key: key, fields: fields], closure)
    }

    def hset(String key, field, value, Closure closure = null) {
        eventBus.send(address, [command: 'hset', key: key, field: field, value: value], closure)
    }

    def hsetnx(String key, field, value, Closure closure = null) {
        eventBus.send(address, [command: 'hsetnx', key:  key, field: field, value: value], closure)
    }

    def hvals(String key, Closure closure = null) {
        eventBus.send(address, [command: 'hvals', key: key], closure)
    }

    // keys

    def del(keys, Closure closure = null) {
        eventBus.send(address, [command: 'del', keys: keys], closure)
    }

    def exists(String key, Closure closure = null) {
        eventBus.send(address, [command: 'exists', key: key], closure)
    }

    def expireat(String key, timestamp, Closure closure = null) {
        eventBus.send(address, [command: 'expireat', key: key, timestamp: timestamp], closure)
    }

    def expire(String key, seconds, Closure closure = null) {
        eventBus.send(address, [command: 'expire', key: key, seconds: seconds], closure)
    }

    def keys(pattern, Closure closure = null) {
        eventBus.send(address, [command: 'keys', pattern: pattern], closure)
    }

    def move(String key, index, Closure closure = null) {
        eventBus.send(address, [command: 'move', key: key, index: index], closure)
    }

    def persist(String key, Closure closure = null) {
        eventBus.send(address, [command: 'persist', key: key], closure)
    }

    def randomkey(Closure closure = null) {
        eventBus.send(address, [command: 'randomkey'], closure)
    }

    def rename(String key, String newKey, Closure closure = null) {
        eventBus.send(address, [command: 'rename', key: key, newkey: newKey], closure)
    }

    def renamenx(String key, String newKey, Closure closure = null) {
        eventBus.send(address, [command: 'renamenx', key: key, newkey: newKey], closure)
    }

    def sort(String key, resultKey, Closure closure = null) {
        eventBus.send(address, [command: 'sort', key: key, resultkey: resultKey], closure)
    }

    def ttl(String key, Closure closure = null) {
        eventBus.send(address, [command: 'ttl', key: key], closure)
    }

    def type(String key, Closure closure = null) {
        eventBus.send(address, [command: 'type', key: key], closure)
    }

    // lists

    def blpop(keys, timeout, Closure closure = null) {
        eventBus.send(address, [command: 'blpop', keys: keys, timeout: timeout], closure)
    }

    def brpop(keys, timeout, Closure closure = null) {
        eventBus.send(address, [command: 'brpop', keys: keys, timeout: timeout], closure)
    }

    def brpoplpush(source, destination, timeout, Closure closure = null) {
        eventBus.send(address, [command: 'brpoplpush', source: source, destination: destination, timeout: timeout], closure)
    }

    def lindex(String key, index, Closure closure = null) {
        eventBus.send(address, [command: 'lindex', key: key, index: index], closure)
    }

    def linsert(String key, value, pivot, before, Closure closure = null) {
        eventBus.send(address, [command: 'linsert', key: key, value: value, pivot: pivot, before: before], closure)
    }

    def llen(String key, Closure closure = null) {
        eventBus.send(address, [command: 'llen', key: key], closure)
    }

    def lpop(String key, Closure closure = null) {
        eventBus.send(address, [command: 'lpop', key: key], closure)
    }

    def lpush(String key, values, Closure closure = null) {
        eventBus.send(address, [command: 'lpush', key: key, values: values], closure)
    }

    def lpushx(String key, value, Closure closure = null) {
        eventBus.send(address, [command: 'lpushx', key: key, value: value], closure)
    }

    def lrange(String key, start, end, Closure closure = null) {
        eventBus.send(address, [command: 'lrange', key: key, start: start, end: end], closure)
    }

    def lrem(String key, value, count, Closure closure = null) {
        eventBus.send(address, [command: 'lrem', key: key, value: value, count: count], closure)
    }

    def lset(String key, value, index, Closure closure = null) {
        eventBus.send(address, [command: 'lset', key: key, value: value, index: index], closure)
    }

    def ltrim(String key, start, end, Closure closure = null) {
        eventBus.send(address, [command: 'ltrim', key: key, start: start, end: end], closure)
    }

    def rpop(String key, Closure closure = null) {
        eventBus.send(address, [command: 'rpop', key: key], closure)
    }

    def rpoplpush(String source, destination, Closure closure = null) {
        eventBus.send(address, [command: 'rpoplpush', source: source, destination: destination], closure)
    }

    def rpush(String key, values, Closure closure = null) {
        eventBus.send(address, [command: 'rpush', key: key, values: values], closure)
    }

    def rpushx(String key, value, Closure closure = null) {
        eventBus.send(address, [command: 'rpushx', key: key, value: value], closure)
    }

    // sets

    def sadd(String key, members, Closure closure = null) {
        eventBus.send(address, [command: 'sadd', key: key, members: members], closure)
    }

    def scard(String key, Closure closure = null) {
        eventBus.send(address, [command: 'scard', key: key], closure)
    }

    def sdiff(keys, Closure closure = null) {
        eventBus.send(address, [command: 'sdiff', keys: keys], closure)
    }

    def sdiffstore(keys, destination, Closure closure = null) {
        eventBus.send(address, [command: 'sdiff', keys: keys, destination: destination], closure)
    }

    def sinter(keys, Closure closure = null) {
        eventBus.send(address, [command: 'sinter', keys: keys], closure)
    }

    def sinterstore(keys, destination, Closure closure = null) {
        eventBus.send(address, [command: 'sinterstore', keys: keys, destination: destination], closure)
    }

    def sismember(String key, member, Closure closure = null) {
        eventBus.send(address, [command: 'sismember', key: key, member: member], closure)
    }

    def smembers(String key, Closure closure = null) {
        eventBus.send(address, [command: 'smembers', key: key], closure)
    }

    def smove(source, destination, member, Closure closure = null) {
        eventBus.send(address, [command: 'smove', source: source, destination: destination, member: member], closure)
    }

    def spop(String key, Closure closure = null) {
        eventBus.send(address, [command: 'spop', key: key], closure)
    }

    def srandmember(String key, Closure closure = null) {
        eventBus.send(address, [command: 'srandmember', key: key], closure)
    }

    def srandmember(String key, members, Closure closure = null) {
        eventBus.send(address, [command: 'srandmember', key: key, members: members], closure)
    }

    def sunion(keys, Closure closure = null) {
        eventBus.send(address, [command: 'sunion', keys: keys], closure)
    }

    def sunionstore(keys, destination, Closure closure = null) {
        eventBus.send(address, [command: 'sunion', keys: keys, destination: destination], closure)
    }

    // sortedsets

    def zadd(String key, members, Closure closure = null) {
        eventBus.send(address, [command: 'zadd', key: key, members: members], closure)
    }

    def zcard(String key, Closure closure = null) {
        eventBus.send(address, [command: 'zcard', key: key], closure)
    }

    def zcount(String key, min, max, Closure closure = null) {
        eventBus.send(address, [command: 'zcount', key: key, min: min, max: max], closure)
    }

    def zincrby(String key, member, increment, Closure closure = null) {
        eventBus.send(address, [command: 'zincrby', key: key, member: member, increment: increment], closure)
    }

    def zinterstore(keys, destination, aggregate, Closure closure = null) {
        eventBus.send(address, [command: 'zinterstore', keys: keys, destination: destination, aggregate: aggregate], closure)
    }

    def zrangebyscore(String key, min, max, offset, count, Closure closure = null) {
        eventBus.send(address, [command: 'zrangebyscore', key: key, min: min, max: max, offset: offset, count: count], closure)
    }

    def zrange(String key, start, end, Closure closure = null) {
        eventBus.send(address, [command: 'zrange', key: key, start: start, end: end], closure)
    }

    def zrank(String key, member, Closure closure = null) {
        eventBus.send(address, [command: 'zrank', key: key, member: member], closure)
    }

    def zrem(String key, members, Closure closure = null) {
        eventBus.send(address, [command: 'zrem', key: key, members: members], closure)
    }

    def zremrangebyrank(String key, start, end, Closure closure = null) {
        eventBus.send(address, [command: 'zremrangebyrank', key: key, start: start, end: end], closure)
    }

    def zremrangebyscore(String key, min, max, Closure closure = null) {
        eventBus.send(address, [command: 'zremrangebyscore', key: key, min: min, max: max], closure)
    }

    def zrevrangebyscore(String key, min, max, offset, count, Closure closure = null) {
        eventBus.send(address, [command: 'zrevrangebyscore', key: key, min: min, max: max, offset: offset, count: count], closure)
    }

    def zrevrange(String key, start, end, Closure closure = null) {
        eventBus.send(address, [command: 'zrevrange', key: key, start: start, end: end], closure)
    }

    def zrevrank(String key, member, Closure closure = null) {
        eventBus.send(address, [command: 'zrevrank', key: key, member: member], closure)
    }

    def zscore(String key, member, Closure closure = null) {
        eventBus.send(address, [command: 'zscore', key: key, member: member], closure)
    }

    def zunionstore(keys, destination, aggregate, Closure closure = null) {
        eventBus.send(address, [command: 'zunionstore', keys: keys, destination: destination, aggregate: aggregate], closure)
    }

    // strings

    def append(String key, value, Closure closure = null) {
        eventBus.send(address, [command: 'append', key: key, value: value], closure)
    }

    def decrby(String key, decrement, Closure closure = null) {
        eventBus.send(address, [command: 'decrby', key: key, decrement: decrement], closure)
    }

    def decr(String key, Closure closure = null) {
        eventBus.send(address, [command: 'decrby', key: key], closure)
    }

    def getbit(String key, offset, Closure closure = null) {
        eventBus.send(address, [command: 'getbit', key: key, offset: offset], closure)
    }

    def get(String key, Closure closure = null) {
        eventBus.send(address, [command: 'get', key: key], closure)
    }

    def getrange(String key, start, end, Closure closure = null) {
        eventBus.send(address, [command: 'getrange', key: key, start: start, end: end], closure)
    }

    def getset(String key, value, Closure closure = null) {
        eventBus.send(address, [command: 'getset', key: key, value: value], closure)
    }

    def incrby(String key, increment, Closure closure = null) {
        eventBus.send(address, [command: 'incrby', key: key, increment: increment], closure)
    }

    def incr(String key, Closure closure = null) {
        eventBus.send(address, [command: 'incr', key: key], closure)
    }

    def mget(keys, Closure closure = null) {
        eventBus.send(address, [command: 'mget', keys: keys], closure)
    }

    def mset(keyvalues, Closure closure = null) {
        eventBus.send(address, [command: 'mset', keyvalues: keyvalues], closure)
    }

    def msetnx(keyvalues, Closure closure = null) {
        eventBus.send(address, [command: 'msetnx', keyvalues: keyvalues], closure)
    }

    def setbit(String key, offset, value, Closure closure = null) {
        eventBus.send(address, [command: 'setbit', key: key, offset: offset, value: value], closure)
    }

    def set(String key, value, Closure closure = null) {
        eventBus.send(address, [command: 'set', key: key, value: value], closure)
    }

    def setex(String key, value, seconds, Closure closure = null) {
        eventBus.send(address, [command: 'setex', key: key, value: value, seconds: seconds], closure)
    }

    def setnx(String key, value, Closure closure = null) {
        eventBus.send(address, [command: 'setex', key: key, value: value], closure)
    }

    def setrange(String key, value, offset, Closure closure = null) {
        eventBus.send(address, [command: 'setrange', key: key, value: value, offset: offset], closure)
    }

    def strlen(String key, Closure closure = null) {
        eventBus.send(address, [command: 'strlen', key: key], closure)
    }
}