package org.vertx.mods.redis;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.NetSocket;
import org.vertx.mods.redis.netty.*;

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
            // connection commands
            case "auth":
                redisAuth(message);
                break;
            case "echo":
                redisEcho(message);
                break;
            case "ping":
                redisPing(message);
                break;
            case "quit":
                redisQuit(message);
                break;
            case "select":
                redisSelect(message);
                break;
            // strings
            case "append":
                redisAppend(message);
                break;
            case "bitcount":
                redisBitCount(message);
                break;
            case "bitop":
                redisBitOp(message);
                break;
            case "decr":
                redisDecr(message);
                break;
            case "decrby":
                redisDecrBy(message);
                break;
            case "get":
                redisGet(message);
                break;
            case "getbit":
                redisGetBit(message);
                break;
//            case "getrange":
//                redisGetRange(message);
//                break;
//            case "getset":
//                redisGetSet(message);
//                break;
//            case "incr":
//                redisIncr(message);
//                break;
//            case "incrby":
//                redisIncrBy(message);
//                break;
//            case "incrbyfloat":
//                redisIncrByFloat(message);
//                break;
//            case "mget":
//                redisMGet(message);
//                break;
//            case "mset":
//                redisMSet(message);
//                break;
//            case "msetnx":
//                redisMSetNX(message);
//                break;
//            case "psetnx":
//                redisPSetNX(message);
//                break;
            case "set":
                redisSet(message);
                break;
//            case "setbit":
//                redisSetBit(message);
//                break;
//            case "setex":
//                redisSetEX(message);
//                break;
//            case "setnx":
//                redisSetNX(message);
//                break;
//            case "setrange":
//                redisSetRange(message);
//                break;
//            case "strlen":
//                redisStrLen(message);
//                break;
            default:
                sendError(message, "Invalid command: " + command);
        }
    }

    /**
     * @param message {key: String, offset: Number}
     */
    private void redisGetBit(final Message<JsonObject> message) {
        String key = message.body.getString("key");

        if (key == null) {
            sendError(message, "key cannot be null");
        } else {
            Number offset = message.body.getNumber("offset");
            if (offset == null) {
                sendError(message, "offset cannot be null");
            } else {
                redisClient.send(new Command("GETBIT", key, offset), new Handler<Reply>() {
                    @Override
                    public void handle(Reply reply) {
                        if (reply.getType() == ReplyType.Error) {
                            sendError(message, ((ErrorReply) reply).data());
                        } else {
                            JsonObject replyMessage = new JsonObject();
                            replyMessage.putNumber("value", ((IntegerReply) reply).data());
                            sendOK(message, replyMessage);
                        }
                    }
                });
            }
        }
    }

    /**
     * @param message {key: String, decrement: Number}
     */
    private void redisDecrBy(final Message<JsonObject> message) {
        String key = message.body.getString("key");

        if (key == null) {
            sendError(message, "key cannot be null");
        } else {
            Number decrement = message.body.getNumber("decrement");
            if (decrement == null) {
                sendError(message, "decrement cannot be null");
            } else {
                redisClient.send(new Command("DECRBY", key, decrement), new Handler<Reply>() {
                    @Override
                    public void handle(Reply reply) {
                        if (reply.getType() == ReplyType.Error) {
                            sendError(message, ((ErrorReply) reply).data());
                        } else {
                            JsonObject replyMessage = new JsonObject();
                            replyMessage.putNumber("value", ((IntegerReply) reply).data());
                            sendOK(message, replyMessage);
                        }
                    }
                });
            }
        }
    }

    /**
     * @param message {key: String}
     */
    private void redisDecr(final Message<JsonObject> message) {
        String key = message.body.getString("key");

        if (key == null) {
            sendError(message, "key cannot be null");
        } else {
            redisClient.send(new Command("DECR", key), new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    if (reply.getType() == ReplyType.Error) {
                        sendError(message, ((ErrorReply) reply).data());
                    } else {
                        JsonObject replyMessage = new JsonObject();
                        replyMessage.putNumber("value", ((IntegerReply) reply).data());
                        sendOK(message, replyMessage);
                    }
                }
            });
        }
    }

    /**
     * @param message {operation: String, destkey: String, key: String, key... String[]}
     * @todo: [key...] is not implemented
     */
    private void redisBitOp(final Message<JsonObject> message) {
        String operation = message.body.getString("operation");

        if (operation == null) {
            sendError(message, "operation cannot be null");
        } else {
            if (operation.equals("AND") || operation.equals("OR") || operation.equals("XOR") || operation.equals("NOT")) {
                String destkey = message.body.getString("destkey");

                if (destkey == null) {
                    sendError(message, "destkey cannot be null");
                } else {
                    String key = message.body.getString("key");

                    if (key == null) {
                        sendError(message, "key cannot be null");
                    } else {
                        redisClient.send(new Command("BITOP", operation, destkey, key), new Handler<Reply>() {
                            @Override
                            public void handle(Reply reply) {
                                if (reply.getType() == ReplyType.Error) {
                                    sendError(message, ((ErrorReply) reply).data());
                                } else {
                                    JsonObject replyMessage = new JsonObject();
                                    replyMessage.putNumber("value", ((IntegerReply) reply).data());
                                    sendOK(message, replyMessage);
                                }
                            }
                        });

                    }
                }
            } else {
                sendError(message, "invalid operation: " + operation);
            }
        }
    }

    /**
     * @param message {key: String, [start]: Number, [end]: Number}
     */
    private void redisBitCount(final Message<JsonObject> message) {
        String key = message.body.getString("key");

        if (key == null) {
            sendError(message, "key cannot be null");
        } else {
            Integer start = message.body.getInteger("start");
            Integer end = message.body.getInteger("end");

            Command command;

            if (start == null && end == null) {
                command = new Command("BITCOUNT");
            } else {
                command = new Command("BITCOUNT", start, end);
            }

            redisClient.send(command, new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    if (reply.getType() == ReplyType.Error) {
                        sendError(message, ((ErrorReply) reply).data());
                    } else {
                        JsonObject replyMessage = new JsonObject();
                        replyMessage.putNumber("value", ((IntegerReply) reply).data());
                        sendOK(message, replyMessage);
                    }
                }
            });
        }
    }

    /**
     * @param message {key: String, value: String}
     */
    private void redisAppend(final Message<JsonObject> message) {
        String key = message.body.getString("key");

        if (key == null) {
            sendError(message, "key cannot be null");
        } else {
            String value = message.body.getString("value");
            if (value == null) {
                sendError(message, "value cannot be null");
            } else {
                redisClient.send(new Command("APPEND", key, value), new Handler<Reply>() {
                    @Override
                    public void handle(Reply reply) {
                        if (reply.getType() == ReplyType.Error) {
                            sendError(message, ((ErrorReply) reply).data());
                        } else {
                            JsonObject replyMessage = new JsonObject();
                            replyMessage.putNumber("value", ((IntegerReply) reply).data());
                            sendOK(message, replyMessage);
                        }
                    }
                });
            }
        }
    }

    /**
     * @param message {index: Number}
     */
    private void redisSelect(final Message<JsonObject> message) {
        Integer index = message.body.getInteger("index");

        if (index == null) {
            sendError(message, "index cannot be null");
        } else {
            redisClient.send(new Command("SELECT", index), new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    if (reply.getType() == ReplyType.Error) {
                        sendError(message, ((ErrorReply) reply).data());
                    } else {
                        JsonObject replyMessage = new JsonObject();
                        replyMessage.putString("value", ((StatusReply) reply).data());
                        sendOK(message, replyMessage);
                    }
                }
            });
        }
    }

    /**
     * @param message {password: String}
     */
    private void redisAuth(final Message<JsonObject> message) {
        String password = message.body.getString("password");

        if (password == null) {
            sendError(message, "password cannot be null");
        } else {
            redisClient.send(new Command("AUTH", password), new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    if (reply.getType() == ReplyType.Error) {
                        sendError(message, ((ErrorReply) reply).data());
                    } else {
                        JsonObject replyMessage = new JsonObject();
                        replyMessage.putString("value", ((StatusReply) reply).data());
                        sendOK(message, replyMessage);
                    }
                }
            });
        }
    }

    /**
     * @param message {message: String}
     */
    private void redisEcho(final Message<JsonObject> message) {
        String _message = message.body.getString("message");

        if (_message == null) {
            sendError(message, "message cannot be null");
        } else {
            redisClient.send(new Command("ECHO", _message), new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    if (reply.getType() == ReplyType.Error) {
                        sendError(message, ((ErrorReply) reply).data());
                    } else {
                        JsonObject replyMessage = new JsonObject();
                        replyMessage.putString("value", ((BulkReply) reply).asUTF8String());
                        sendOK(message, replyMessage);
                    }
                }
            });
        }
    }

    /**
     * @param message {}
     */
    private void redisPing(final Message<JsonObject> message) {
        redisClient.send(new Command("PING"), new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                if (reply.getType() == ReplyType.Error) {
                    sendError(message, ((ErrorReply) reply).data());
                } else {
                    JsonObject replyMessage = new JsonObject();
                    replyMessage.putString("value", ((StatusReply) reply).data());
                    sendOK(message, replyMessage);
                }
            }
        });
    }

    /**
     * @param message {}
     */
    private void redisQuit(final Message<JsonObject> message) {
        redisClient.send(new Command("QUIT"), new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                if (reply.getType() == ReplyType.Error) {
                    sendError(message, ((ErrorReply) reply).data());
                } else {
                    JsonObject replyMessage = new JsonObject();
                    replyMessage.putString("value", ((StatusReply) reply).data());
                    sendOK(message, replyMessage);
                }
            }
        });
    }

    /**
     * @param message {key: String, value: String}
     */
    private void redisSet(final Message<JsonObject> message) {
        String key = message.body.getString("key");

        if (key == null) {
            sendError(message, "key cannot be null");
        } else {
            String value = message.body.getString("value");
            if (value == null) {
                sendError(message, "value cannot be null");
            } else {
                redisClient.send(new Command("SET", key, value), new Handler<Reply>() {
                    @Override
                    public void handle(Reply reply) {
                        if (reply.getType() == ReplyType.Error) {
                            sendError(message, ((ErrorReply) reply).data());
                        } else {
                            JsonObject replyMessage = new JsonObject();
                            replyMessage.putString("value", ((StatusReply) reply).data());
                            sendOK(message, replyMessage);
                        }
                    }
                });
            }
        }
    }

    /**
     * @param message {key: String}
     */
    private void redisGet(final Message<JsonObject> message) {
        String key = message.body.getString("key");

        if (key == null) {
            sendError(message, "key cannot be null");
        } else {
            redisClient.send(new Command("GET", key), new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    if (reply.getType() == ReplyType.Error) {
                        sendError(message, ((ErrorReply) reply).data());
                    } else {
                        JsonObject replyMessage = new JsonObject();
                        replyMessage.putString("value", ((BulkReply) reply).asUTF8String());
                        sendOK(message, replyMessage);
                    }
                }
            });
        }
    }
}
