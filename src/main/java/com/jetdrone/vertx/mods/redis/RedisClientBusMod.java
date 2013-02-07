package com.jetdrone.vertx.mods.redis;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.NetSocket;
import com.jetdrone.vertx.mods.redis.netty.*;

public class RedisClientBusMod extends BusModBase implements Handler<Message<JsonObject>> {

    private NetSocket socket;
    private RedisClientBase redisClient;

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
            // arguments "host" "port" "key" "destination-db" "timeout"
            case "migrate":
                redisExec(command, "host", "port", "key", "destination-db", "timeout", message);
                break;

            // keys
            case "object":
            case "sort":
            // strings
            case "bitcount":
            case "bitop":
            case "mset":
            case "msetnx":
            // hashes
            case "hdel":
            case "hmget":
            case "hmset":
            // lists
            case "blpop":
            case "brpop":
            case "linsert":
            case "lpush":
            case "rpush":
            // sets
            case "sadd":
            case "sdiffstore":
            case "sinterstore":
            case "srandmember":
            case "srem":
            case "sunionstore":
            // sorted sets
            case "zadd":
            case "zinterstore":
            case "zrange":
            case "zrangebyscore":
            case "zrem":
            case "zrevrange":
            case "zrevrangebyscore":
            case "zunionstore":
            // pub/sub
            case "punsubscribe":
            case "unsubscribe":
            // transactions
            // scripting
            case "eval":
            case "evalsha":
            // server
            case "info":
            case "shutdown":
            case "slowlog":
                sendError(message, "Not Implemented: " + command);
                break;
            default:
                sendError(message, "Invalid command: " + command);
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
                // TODO: process bytes
                replyMessage.putString("value", ((BulkReply) reply).asUTF8String());
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
     * @param command Redis Command
     * @param argName0 first argument name
     * @param message {argName0: value}
     */
    private void redisExec(final String command, final String argName0, final Message<JsonObject> message) {
        final Object arg0 = message.body.getField(argName0);

        if (arg0 == null) {
            sendError(message, arg0 + " cannot be null");
        } else {
            Command cmd;
            if (arg0 instanceof JsonArray) {
                cmd = new Command(command, ((JsonArray) arg0).toArray());
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
    }

    /**
     * @param command Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param message {argName0: value, argName1: value}
     */
    private void redisExec(final String command, final String argName0, final String argName1, final Message<JsonObject> message) {
        final Object arg0 = message.body.getField(argName0);

        if (arg0 == null) {
            sendError(message, arg0 + " cannot be null");
        } else {
            final Object arg1 = message.body.getField(argName1);

            if (arg1 == null) {
                sendError(message, arg1 + " cannot be null");
            } else {
                redisClient.send(new Command(command, arg0, arg1), new Handler<Reply>() {
                    @Override
                    public void handle(Reply reply) {
                        processReply(message, reply);
                    }
                });
            }
        }
    }

    /**
     * @param command Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param argName2 second argument name
     * @param message {argName0: value, argName1: value, argName2: value}
     */
    private void redisExec(final String command, final String argName0, final String argName1, final String argName2, final Message<JsonObject> message) {
        final Object arg0 = message.body.getField(argName0);

        if (arg0 == null) {
            sendError(message, arg0 + " cannot be null");
        } else {
            final Object arg1 = message.body.getField(argName1);

            if (arg1 == null) {
                sendError(message, arg1 + " cannot be null");
            } else {
                final Object arg2 = message.body.getField(argName2);

                if (arg2 == null) {
                    sendError(message, arg2 + " cannot be null");
                } else {
                    redisClient.send(new Command(command, arg0, arg1, arg2), new Handler<Reply>() {
                        @Override
                        public void handle(Reply reply) {
                            processReply(message, reply);
                        }
                    });
                }
            }
        }
    }

    /**
     * @param command Redis Command
     * @param argName0 first argument name
     * @param argName1 second argument name
     * @param argName2 third argument name
     * @param argName3 forth argument name
     * @param argName4 fifth argument name
     * @param message {argName0: value, argName1: value, argName2: value, argName3: value, argName4: value}
     */
    private void redisExec(final String command, final String argName0, final String argName1, final String argName2, final String argName3, final String argName4, final Message<JsonObject> message) {
        final Object arg0 = message.body.getField(argName0);

        if (arg0 == null) {
            sendError(message, arg0 + " cannot be null");
        } else {
            final Object arg1 = message.body.getField(argName1);

            if (arg1 == null) {
                sendError(message, arg1 + " cannot be null");
            } else {
                final Object arg2 = message.body.getField(argName2);

                if (arg2 == null) {
                    sendError(message, arg2 + " cannot be null");
                } else {
                    final Object arg3 = message.body.getField(argName3);

                    if (arg3 == null) {
                        sendError(message, arg3 + " cannot be null");
                    } else {
                        final Object arg4 = message.body.getField(argName4);

                        if (arg4 == null) {
                            sendError(message, arg4 + " cannot be null");
                        } else {
                            redisClient.send(new Command(command, arg0, arg1, arg2, arg3, arg4), new Handler<Reply>() {
                                @Override
                                public void handle(Reply reply) {
                                    processReply(message, reply);
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
