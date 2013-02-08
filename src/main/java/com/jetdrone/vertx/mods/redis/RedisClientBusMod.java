package com.jetdrone.vertx.mods.redis;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.NetSocket;
import com.jetdrone.vertx.mods.redis.netty.*;

import java.util.ArrayList;
import java.util.List;

public class RedisClientBusMod extends BusModBase implements Handler<Message<JsonObject>> {

    private static final class KeyValue {

        final String keyName;
        final String valueName;
        final String pairName;

        KeyValue(String keyName, String valueName, String pairName) {
            this.keyName = keyName;
            this.valueName = valueName;
            this.pairName = pairName;
        }
    }

    private static final class Option {
        final String name;

        Option(String name) {
            this.name = name;
        }
    }

    private NetSocket socket;
    private RedisClientBase redisClient;

    private static final KeyValue KV = new KeyValue("key", "value", "keyvalues");
    private static final KeyValue FV = new KeyValue("field", "value", "fieldvalues");
    private static final KeyValue SM = new KeyValue("score", "member", "scoremembers");

    private static final Option WITHSCORES = new Option("withscores");
    private static final Option ASC = new Option("asc");
    private static final Option DESC = new Option("desc");
    private static final Option ALPHA = new Option("alpha");

    @Override
    public void start() {
        super.start();

        String host = getOptionalStringConfig("host", "localhost");
        int port = getOptionalIntConfig("port", 6379);

        vertx.createNetClient().connect(port, host, new Handler<NetSocket>() {
            @Override
            public void handle(NetSocket netSocket) {
                socket = netSocket;
                redisClient = new RedisClientBase(netSocket);
            }
        });

        String address = getOptionalStringConfig("address", "redis-client");
        eb.registerHandler(address, this);
    }

    @Override
    public void stop() throws Exception {
        socket.close();
        super.stop();
    }

    @Override
    public void handle(Message<JsonObject> message) {

        String command = message.body.getString("command");

        if (command == null) {
            sendError(message, "command must be specified");
            return;
        }

        try {
            switch (command) {
                // no argument
                case "randomkey":
                case "discard":
                case "exec":
                case "multi":
                case "unwatch":
                case "script flush":
                case "script kill":
                case "ping":
                case "quit":
                case "bgrewriteaof":
                case "bgsave":
                case "client list":
                case "client getname":
                case "config resetstat":
                case "dbsize":
                case "debug segfault":
                case "flushall":
                case "flushdb":
                case "lastsave":
                case "monitor":
                case "save":
                case "sync":
                case "time":
                    redisExec(command, message);
                    break;
                // argument "key"
                case "dump":
                case "exists":
                case "persist":
                case "pttl":
                case "ttl":
                case "type":
                case "decr":
                case "get":
                case "incr":
                case "strlen":
                case "hgetall":
                case "hkeys":
                case "hlen":
                case "hvals":
                case "llen":
                case "lpop":
                case "rpop":
                case "scard":
                case "smembers":
                case "spop":
                case "zcard":
                case "debug object":
                    // arguments "key" ["key"...]
                case "del":
                case "mget":
                case "sdiff":
                case "sinter":
                case "sunion":
                case "watch":
                    redisExec(command, "key", message);
                    break;
                // argument "pattern"
                case "keys":
                    // argument "pattern" ["pattern"...]
                case "psubscribe":
                    redisExec(command, "pattern", message);
                    break;
                // argument "password"
                case "auth":
                    redisExec(command, "password", message);
                    break;
                // argument "message"
                case "echo":
                    redisExec(command, "message", message);
                    break;
                // argument "index"
                case "select":
                    redisExec(command, "index", message);
                    break;
                // argument "connection-name"
                case "client setname":
                    redisExec(command, "connection-name", message);
                    break;
                // argument "parameter"
                case "config get":
                    redisExec(command, "parameter", message);
                    break;
                // argument "script"
                case "script load":
                    // argument "script" ["script"...]
                case "script exists":
                    redisExec(command, "script", message);
                    break;
                // argument "channel" ["channel"...]
                case "subscribe":
                    redisExec(command, "channel", message);
                    break;
                // arguments "key" "value"
                case "append":
                case "getset":
                case "set":
                case "setnx":
                case "lpushx":
                case "rpushx":
                    // arguments "key" "value" ["value"...]
                case "lpush":
                case "rpush":
                    redisExec(command, "key", "value", message);
                    break;
                // argumens "key" "seconds"
                case "expire":
                    redisExec(command, "key", "seconds", message);
                    break;
                // argumens "key" "timestamp"
                case "expireat":
                    redisExec(command, "key", "timestamp", message);
                    break;
                // argumens "key" "db"
                case "move":
                    redisExec(command, "key", "db", message);
                    break;
                // argumens "key" "milliseconds"
                case "pexpire":
                    redisExec(command, "key", "milliseconds", message);
                    break;
                // argumens "key" "milliseconds-timestamp"
                case "pexpireat":
                    redisExec(command, "key", "milliseconds-timestamp", message);
                    break;
                // argumens "key" "newkey"
                case "rename":
                case "renamenx":
                    redisExec(command, "key", "newkey", message);
                    break;
                // arguments "key" "decrement"
                case "decrby":
                    redisExec(command, "key", "decrement", message);
                    break;
                // arguments "key" "offset"
                case "getbit":
                    redisExec(command, "key", "offset", message);
                    break;
                // arguments "key" "increment"
                case "incrby":
                case "incrbyfloat":
                    redisExec(command, "key", "increment", message);
                    break;
                // arguments "key" "field"
                case "hexists":
                case "hget":
                    // arguments "key" "field" ["field"...]
                case "hdel":
                case "hmget":
                    redisExec(command, "key", "field", message);
                    break;
                // arguments "key" "index"
                case "lindex":
                    redisExec(command, "key", "index", message);
                    break;
                // arguments "key" "member"
                case "sismember":
                case "zrank":
                case "zrevrank":
                case "zscore":
                    // arguments "key" "member" ["member"...]
                case "sadd":
                case "srem":
                case "zrem":
                    redisExec(command, "key", "member", message);
                    break;
                // arguments "source" "destination"
                case "rpoplpush":
                    redisExec(command, "source", "destination", message);
                    break;
                // arguments "channel" "message"
                case "publish":
                    redisExec(command, "channel", "message", message);
                    break;
                // arguments "host" "port"
                case "slaveof":
                    redisExec(command, "host", "port", message);
                    break;
                // arguments "ip" "port"
                case "client kill":
                    redisExec(command, "ip", "port", message);
                    break;
                // arguments "parameter" "value"
                case "config set":
                    redisExec(command, "parameter", "value", message);
                    break;
                // arguments "destination" "key" ["key"...]
                case "sdiffstore":
                case "sinterstore":
                case "sunionstore":
                    redisExec(command, "destination", "key", message);
                    break;
                // arguments "key" ["key"...] timeout
                case "blpop":
                case "brpop":
                    redisExec(command, "key", "timeout", message);
                    break;
                // arguments "key" "ttl" "serialized-value"
                case "restore":
                    redisExec(command, "key", "ttl", "serialized-value", message);
                    break;
                // arguments "key" "start" "end"
                case "getrange":
                    redisExec(command, "key", "start", "end", message);
                    break;
                // arguments "key" "milliseconds" "value"
                case "psetex":
                    redisExec(command, "key", "milliseconds", "value", message);
                    break;
                // arguments "key" "offset" "value"
                case "setbit":
                case "setrange":
                    redisExec(command, "key", "offset", "value", message);
                    break;
                // arguments "key" "seconds" "value"
                case "setex":
                    redisExec(command, "key", "seconds", "value", message);
                    break;
                // arguments "key" "field" "increment"
                case "hincrby":
                case "hincrbyfloat":
                    redisExec(command, "key", "field", "increment", message);
                    break;
                // arguments "key" "field" "value"
                case "hset":
                case "hsetnx":
                    redisExec(command, "key", "field", "value", message);
                    break;
                // arguments "source" "destination" "timeout"
                case "brpoplpush":
                    redisExec(command, "source", "destination", "timeout", message);
                    break;
                // arguments "key" "start" "stop"
                case "lrange":
                case "ltrim":
                case "zremrangebyrank":
                    redisExec(command, "key", "start", "stop", message);
                    break;
                // arguments "key" "count" "value"
                case "lrem":
                    redisExec(command, "key", "count", "value", message);
                    break;
                // arguments "key" "index" "value"
                case "lset":
                    redisExec(command, "key", "index", "value", message);
                    break;
                // arguments "source" "destination" "member"
                case "smove":
                    redisExec(command, "source", "destination", "member", message);
                    break;
                // arguments "key" "min" "max"
                case "zcount":
                case "zremrangebyscore":
                    redisExec(command, "key", "min", "max", message);
                    break;
                // arguments "key" "increment" "member"
                case "zincrby":
                    redisExec(command, "key", "increment", "member", message);
                    break;
                // arguments "operation" "destkey" "key" ["key"...]
                case "bitop":
                    redisExec(command, "operation", "destkey", "key", message);
                    break;
                // arguments "host" "port" "key" "destination-db" "timeout"
                case "migrate":
                    redisExec(command, "host", "port", "key", "destination-db", "timeout", message);
                    break;
                // argument ["section"]
                case "info":
                    redisExecLastOptional(command, "section", message);
                    break;
                // argument ["pattern" ["pattern"...]]
                case "punsubscribe":
                    redisExecLastOptional(command, "pattern", message);
                    break;
                // argument ["channel" ["channel"...]]
                case "unsubscribe":
                    redisExecLastOptional(command, "channel", message);
                    break;
                // arguments "subcommand" ["argument"]
                case "slowlog":
                    redisExecLastOptional(command, "subcommand", "argument", message);
                    break;
                // arguments "subcommand" ["arguments"]
                case "object":
                    redisExecLastOptional(command, "subcommand", "arguments", message);
                    break;
                // arguments "key" ["count"]
                case "srandmember":
                    redisExecLastOptional(command, "key", "count", message);
                    break;
                // arguments "key" "start" "stop" ["withscores"]
                case "zrange":
                case "zrevrange":
                    redisExecLastOptional(command, "key", "start", "stop", WITHSCORES, message);
                    break;
                // arguments KV: "key" "value" ["key" "value"...]
                case "mset":
                case "msetnx":
                    redisExecKV(command, KV, message);
                    break;
                // arguments "key" FV: "field" "value" ["field" "value"...]
                case "hmset":
                    redisExecKV(command, "key", FV, message);
                    break;
                // arguments "key" SM: "score" "member" ["score" "member"...]
                case "zadd":
                    redisExecKV(command, "key", SM, message);
                    break;
                // arguments "key" ["start"] ["end"]
                case "bitcount":
                    redisExecLast2Optional(command, "key", "start", "end", message);
                    break;
                // arguments ["nosave"] ["save"]
                case "shutdown":
                    redisExecLast2Optional(command, "nosave", "save", message);
                    break;
                // complex non generic: key [BY pattern] [LIMIT offset count] [GET pattern [GET pattern ...]] [ASC|DESC] [ALPHA] [STORE destination]
                case "sort":
                    redisExecSort(command, message);
                    break;
                // keys
                // lists
                case "linsert":
                // sorted sets
                case "zinterstore":
                case "zrangebyscore":
                case "zrevrangebyscore":
                case "zunionstore":
                    // scripting
                case "eval":
                case "evalsha":
                    sendError(message, "Not Implemented: " + command);
                    break;
                default:
                    sendError(message, "Invalid command: " + command);
            }
        } catch (RedisCommandError rce) {
            sendError(message, rce.getMessage());
        }
    }

    private Object getMandatoryField(final JsonObject message, final String argName) throws RedisCommandError {
        final Object arg = message.getField(argName);
        if (arg == null) {
            throw new RedisCommandError(argName + " cannot be null");
        } else {
            if (arg instanceof JsonArray) {
                return ((JsonArray) arg).toArray();
            } else {
                return arg;
            }
        }
    }

    private Object getMandatoryField(final Message<JsonObject> message, final String argName) throws RedisCommandError {
        final Object arg = message.body.getField(argName);
        if (arg == null) {
            throw new RedisCommandError(argName + " cannot be null");
        } else {
            if (arg instanceof JsonArray) {
                return ((JsonArray) arg).toArray();
            } else {
                return arg;
            }
        }
    }

    private Object[] getMandatoryKVField(final JsonArray array, final KeyValue keyValue) throws RedisCommandError {
        // flatten the json array
        Object[] args = new Object[array.size() * 2];
        for (int i = 0; i < array.size(); i++) {
            Object entry = array.get(i);
            if (entry instanceof JsonObject) {
                JsonObject jsonEntry = (JsonObject) entry;
                Object key = jsonEntry.getField(keyValue.keyName);
                Object value = jsonEntry.getField(keyValue.valueName);
                // kv cannot be null
                if (key == null || value == null) {
                    throw new RedisCommandError(keyValue.keyName + " or " + keyValue.valueName + " cannot be null");
                }

                args[2*i] = key;
                args[2*i + 1] = value;
            } else {
                throw new RedisCommandError(keyValue.pairName + " expected to have JsonObjects");
            }
        }

        return args;
    }

    private Object getOptionalField(final Message<JsonObject> message, final String argName) {
        final Object arg = message.body.getField(argName);
        if (arg == null) {
            return null;
        } else {
            if (arg instanceof JsonArray) {
                return ((JsonArray) arg).toArray();
            } else {
                return arg;
            }
        }
    }

    private void processReply(Message<JsonObject> message, Reply reply) {
        JsonObject replyMessage;

        switch (reply.getType()) {
            case Error:
                sendError(message, ((ErrorReply) reply).data());
                return;
            case Status:
                replyMessage = new JsonObject();
                replyMessage.putString("value", ((StatusReply) reply).data());
                sendOK(message, replyMessage);
                return;
            case Bulk:
                replyMessage = new JsonObject();
                replyMessage.putBinary("value", ((BulkReply) reply).data().array());
                sendOK(message, replyMessage);
                return;
            case MultiBulk:
                replyMessage = new JsonObject();
                // TODO: process array of bytes
                replyMessage.putString("value", ((MultiBulkReply) reply).toString());
                sendOK(message, replyMessage);
                return;
            case Integer:
                replyMessage = new JsonObject();
                replyMessage.putNumber("value", ((IntegerReply) reply).data());
                sendOK(message, replyMessage);
                return;
            default:
                sendError(message, "Unknown message type");
        }
    }

    /**
     * @param command Redis Command
     * @param message {}
     */
    private void redisExec(final String command, final Message<JsonObject> message) {
        redisClient.send(new Command(command), new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param argName0 first argument name
     * @param message  {argName0: value}
     */
    private void redisExec(final String command, final String argName0, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getMandatoryField(message, argName0);
        redisClient.send(new Command(command, arg0), new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param keyValue key value config
     * @param message  {argName0: value}
     */
    private void redisExecKV(final String command, final KeyValue keyValue, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg = message.body.getField(keyValue.pairName);
        if (arg == null) {
            // process single pair
            final Object arg0 = getMandatoryField(message, keyValue.keyName);
            final Object arg1 = getMandatoryField(message, keyValue.valueName);
            redisClient.send(new Command(command, arg0, arg1), new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    processReply(message, reply);
                }
            });
        } else {
            if (arg instanceof JsonArray) {
                JsonArray array = (JsonArray) arg;
                Object[] args = getMandatoryKVField(array, keyValue);

                redisClient.send(new Command(command, args), new Handler<Reply>() {
                    @Override
                    public void handle(Reply reply) {
                        processReply(message, reply);
                    }
                });
            } else {
                // error expected array of objects
                sendError(message, keyValue.pairName + " must be an array");
            }
        }
    }

    /**
     * @param command  Redis Command
     * @param keyValue key value config
     * @param message  {argName0: value}
     */
    private void redisExecKV(final String command, final String argName0, final KeyValue keyValue, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getMandatoryField(message, argName0);
        final Object arg = message.body.getField(keyValue.pairName);
        if (arg == null) {
            // process single pair
            final Object arg1 = getMandatoryField(message, keyValue.keyName);
            final Object arg2 = getMandatoryField(message, keyValue.valueName);
            redisClient.send(new Command(command, arg0, arg1, arg2), new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    processReply(message, reply);
                }
            });
        } else {
            if (arg instanceof JsonArray) {
                JsonArray array = (JsonArray) arg;
                Object[] args = getMandatoryKVField(array, keyValue);

                redisClient.send(new Command(command, arg0,  args), new Handler<Reply>() {
                    @Override
                    public void handle(Reply reply) {
                        processReply(message, reply);
                    }
                });
            } else {
                // error expected array of objects
                sendError(message, keyValue.pairName + " must be an array");
            }
        }
    }

    /**
     * @param command  Redis Command
     * @param argName0 first argument name
     * @param message  {argName0: value} or {}
     */
    private void redisExecLastOptional(final String command, final String argName0, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getOptionalField(message, argName0);
        final Command cmd;

        if (arg0 == null) {
            cmd = new Command(command);
        } else {
            cmd = new Command(command, arg0);
        }
        redisClient.send(cmd, new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param message  {argName0: value, argName1: value}
     */
    private void redisExec(final String command, final String argName0, final String argName1, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getMandatoryField(message, argName0);
        final Object arg1 = getMandatoryField(message, argName1);
        redisClient.send(new Command(command, arg0, arg1), new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param message  {argName0: value, argName1: value} or {}
     */
    private void redisExecLastOptional(final String command, final String argName0, final String argName1, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getMandatoryField(message, argName0);
        final Object arg1 = getOptionalField(message, argName1);
        final Command cmd;

        if (arg1 == null) {
            cmd = new Command(command, arg0);
        } else {
            cmd = new Command(command, arg0, arg1);
        }
        redisClient.send(cmd, new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param argName2 second argument name
     * @param message  {argName0: value, argName1: value, argName2: value}
     */
    private void redisExec(final String command, final String argName0, final String argName1, final String argName2, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getMandatoryField(message, argName0);
        final Object arg1 = getMandatoryField(message, argName1);
        final Object arg2 = getMandatoryField(message, argName2);
        redisClient.send(new Command(command, arg0, arg1, arg2), new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param message  {argName0: value, argName1: value} or {}
     */
    private void redisExecLastOptional(final String command, final String argName0, final String argName1, final String argName2, final Option option, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getMandatoryField(message, argName0);
        final Object arg1 = getMandatoryField(message, argName1);
        final Object arg2 = getMandatoryField(message, argName2);
        final Object arg3 = getOptionalField(message, option.name);

        final Command cmd;

        if (arg3 == null) {
            cmd = new Command(command, arg0, arg1, arg2);
        } else {
            cmd = new Command(command, arg0, arg1, arg2, option.name);
        }
        redisClient.send(cmd, new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param argName2 third argument name
     * @param argName3 forth argument name
     * @param argName4 fifth argument name
     * @param message  {argName0: value, argName1: value, argName2: value, argName3: value, argName4: value}
     */
    private void redisExec(final String command, final String argName0, final String argName1, final String argName2, final String argName3, final String argName4, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getMandatoryField(message, argName0);
        final Object arg1 = getMandatoryField(message, argName1);
        final Object arg2 = getMandatoryField(message, argName2);
        final Object arg3 = getMandatoryField(message, argName3);
        final Object arg4 = getMandatoryField(message, argName4);
        redisClient.send(new Command(command, arg0, arg1, arg2, arg3, arg4), new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param message  {argName0: value, argName1: value} or {}
     */
    private void redisExecLast2Optional(final String command, final String argName0, final String argName1, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getOptionalField(message, argName0);
        final Object arg1 = getOptionalField(message, argName1);
        final Command cmd;

        if (arg0 == null) {
            cmd = new Command(command);
        } else {
            if (arg1 == null) {
                cmd = new Command(command, arg0);
            } else {
                cmd = new Command(command, arg0, arg1);
            }
        }
        redisClient.send(cmd, new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param argName2 third argument name
     * @param message  {argName0: value, argName1: value} or {}
     */
    private void redisExecLast2Optional(final String command, final String argName0, final String argName1, final String argName2, final Message<JsonObject> message) throws RedisCommandError {
        final Object arg0 = getMandatoryField(message, argName0);
        final Object arg1 = getOptionalField(message, argName1);
        final Object arg2 = getOptionalField(message, argName2);
        final Command cmd;

        if (arg1 == null) {
            cmd = new Command(command, arg0);
        } else {
            if (arg2 == null) {
                cmd = new Command(command, arg0, arg1);
            } else {
                cmd = new Command(command, arg0, arg1, arg2);
            }
        }
        redisClient.send(cmd, new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });
    }

    /**
     * @param command  Redis Command
     * @param message  key [BY pattern] [LIMIT offset count] [GET pattern [GET pattern ...]] [ASC|DESC] [ALPHA] [STORE destination]
     * @throws RedisCommandError
     */
    private void redisExecSort(final String command, final Message<JsonObject> message) throws RedisCommandError {
        final Object key = getMandatoryField(message, "key");
        List<Object> args = new ArrayList<>();
        args.add(key);

        final Object by = getOptionalField(message, "by");
        if (by != null) {
            args.add("by");
            args.add(by);
        }

        final Object limit = getOptionalField(message, "limit");
        if (limit != null) {
            if (limit instanceof JsonObject) {
                args.add("limit");
                args.add(getMandatoryField((JsonObject) limit, "offset"));
                args.add(getMandatoryField((JsonObject) limit, "count"));
            } else {
                throw new RedisCommandError("limit must be a JsonObject");
            }
        }

        final Object get = getOptionalField(message, "get");
        if (get != null) {
            if (get instanceof JsonArray) {
                JsonArray array = (JsonArray) get;
                for (int i = 0; i < array.size(); i++) {
                    args.add("get");
                    args.add(array.get(i));
                }
            } else {
                args.add("get");
                args.add(get);
            }
        }

        final Object asc = getOptionalField(message, ASC.name);
        if (asc != null) {
            args.add(ASC.name);
        }

        final Object desc = getOptionalField(message, DESC.name);
        if (desc != null) {
            args.add(DESC.name);
        }

        final Object alpha = getOptionalField(message, ALPHA.name);
        if (alpha != null) {
            args.add(ALPHA.name);
        }

        final Object store = getOptionalField(message, "store");
        if (store != null) {
            args.add("store");
            args.add(store);
        }

        redisClient.send(new Command(command, args.toArray()), new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply);
            }
        });

    }
}
