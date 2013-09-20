package io.vertx.redis;

import io.vertx.redis.impl.RedisSubscriptions;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import io.vertx.redis.reply.*;
import io.vertx.redis.impl.MessageHandler;

import java.nio.charset.Charset;

@SuppressWarnings("unused")
public class RedisMod extends BusModBase implements Handler<Message<JsonObject>> {

    private RedisConnection redisClient;
    private RedisSubscriptions subscriptions = new RedisSubscriptions();

    private String encoding;
    private String baseAddress;

    private enum ResponseTransform {
        NONE,
        ARRAY_TO_OBJECT,
        INFO
    }

    @Override
    public void start() {
        super.start();

        String host = getOptionalStringConfig("host", "localhost");
        int port = getOptionalIntConfig("port", 6379);
        String encoding = getOptionalStringConfig("encoding", null);
        boolean binary = getOptionalBooleanConfig("binary", false);
        String auth = getOptionalStringConfig("auth", null);

        if (binary) {
            logger.warn("Binary mode is not implemented yet!!!");
        }

        if (encoding != null) {
            this.encoding = encoding;
        } else {
            this.encoding = "UTF-8";
        }

        redisClient = new RedisConnection(vertx, logger, host, port, auth, subscriptions, Charset.forName(this.encoding));
        redisClient.connect(null);
        
        baseAddress = getOptionalStringConfig("address", "io.vertx.mod-redis");
        eb.registerHandler(baseAddress, this);
    }

    private ResponseTransform getResponseTransformFor(String command) {
        if (command.equals("hgetall")) {
            return ResponseTransform.ARRAY_TO_OBJECT;
        }
        if (command.equals("info")) {
            return ResponseTransform.INFO;
        }

        return ResponseTransform.NONE;
    }
    
    @Override
    public void handle(final Message<JsonObject> message) {

        final String command = message.body().getString("command").toLowerCase();
        final JsonArray args = message.body().getArray("args");

        if (command == null) {
            sendError(message, "command must be specified");
            return;
        }

        final ResponseTransform transform = getResponseTransformFor(command);

        // subscribe/psubscribe and unsubscribe/punsubscribe commands can have multiple (including zero) replies
        int expectedReplies = 1;

        switch (command) {
            // argument "pattern" ["pattern"...]
            case "psubscribe":
                // in this case we need also to register handlers
                if (args == null) {
                    sendError(message, "at least one pattern is required!");
                    return;
                }
                expectedReplies = args.size();
                for (Object obj : args) {
                    String pattern = (String) obj;
                    // compose the listening address as base + . + pattern
                    final String vertxChannel = baseAddress + "." + pattern;
                    subscriptions.registerPatternSubscribeHandler(pattern, new MessageHandler() {
                        @Override
                        public void handle(String pattern, Reply[] replyData) {
                                JsonObject replyMessage = new JsonObject();
                                replyMessage.putString("status", "ok");
                                JsonObject message = new JsonObject();
                                message.putString("pattern", pattern);
                                message.putString("channel", ((BulkReply) replyData[2]).asString(encoding));
                                message.putString("message", ((BulkReply) replyData[3]).asString(encoding));
                                replyMessage.putObject("value", message);
                                eb.send(vertxChannel, replyMessage);
                            }
                        });
                }
                break;
            // argument "channel" ["channel"...]
            case "subscribe":
                if (args == null) {
                    sendError(message, "at least one pattern is required!");
                    return;
                }
                // in this case we need also to register handlers
                expectedReplies = args.size();
                for (Object obj : args) {
                    String channel = (String) obj;
                    // compose the listening address as base + . + channel
                    final String vertxChannel = baseAddress + "." + channel;
                    subscriptions.registerChannelSubscribeHandler(channel, new MessageHandler() {
                        @Override
                        public void handle(String channel, Reply[] replyData) {
                                JsonObject replyMessage = new JsonObject();
                                replyMessage.putString("status", "ok");
                                JsonObject message = new JsonObject();
                                message.putString("channel", channel);
                                message.putString("message", ((BulkReply) replyData[2]).asString(encoding));
                                replyMessage.putObject("value", message);
                                eb.send(vertxChannel, replyMessage);
                        }
                    });
                }
                break;
            // argument ["pattern" ["pattern"...]]
            case "punsubscribe":
                // unregister all channels
                if (args == null || args.size() == 0) {
                    // unsubscribe all
                    expectedReplies = subscriptions.patternSize();
                    subscriptions.unregisterPatternSubscribeHandler(null);
                } else {
                    expectedReplies = args.size();
                    for (Object obj : args) {
                        String pattern = (String) obj;
                        subscriptions.unregisterPatternSubscribeHandler(pattern);
                    }
                }
                break;
            // argument ["channel" ["channel"...]]
            case "unsubscribe":
                // unregister all channels
                if (args == null || args.size() == 0) {
                    // unsubscribe all
                    expectedReplies = subscriptions.channelSize();
                    subscriptions.unregisterChannelSubscribeHandler(null);
                } else {
                    expectedReplies = args.size();
                    for (Object obj : args) {
                        String channel = (String) obj;
                        subscriptions.unregisterChannelSubscribeHandler(channel);
                    }
                }
                break;
        }

        redisClient.send(message.body(), expectedReplies, new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply, transform);
            }
        });
    }

    private void processReply(Message<JsonObject> message, Reply reply, ResponseTransform transform) {
        JsonObject replyMessage;
        switch (reply.getType()) {
            case '-': // Error
                sendError(message, ((ErrorReply) reply).data());
                return;
            case '+':   // Status
                replyMessage = new JsonObject();
                replyMessage.putString("value", ((StatusReply) reply).data());
                sendOK(message, replyMessage);
                return;
            case '$':  // Bulk
                replyMessage = new JsonObject();
                if (transform == ResponseTransform.INFO) {
                    String info = ((BulkReply) reply).asString(encoding);
                    String lines[] = info.split("\\r?\\n");
                    JsonObject value = new JsonObject();
                    JsonObject section = null;
                    for (String line : lines) {
                        if (line.length() == 0) {
                            // end of section
                            section = null;
                            continue;
                        }

                        if (line.charAt(0) == '#') {
                            // begin section
                            section = new JsonObject();
                            // create a sub key with the section name
                            value.putObject(line.substring(2).toLowerCase(), section);
                        } else {
                            // entry in section
                            int split = line.indexOf(':');
                            if (section == null) {
                                value.putString(line.substring(0, split), line.substring(split + 1));
                            } else {
                                section.putString(line.substring(0, split), line.substring(split + 1));
                            }
                        }
                    }
                    replyMessage.putObject("value", value);
                } else {
                    replyMessage.putString("value", ((BulkReply) reply).asString(encoding));
                }
                sendOK(message, replyMessage);
                return;
            case '*': // MultiBulk
                replyMessage = new JsonObject();
                MultiBulkReply mbreply = (MultiBulkReply) reply;
                if (transform == ResponseTransform.ARRAY_TO_OBJECT) {
                    JsonObject bulk = new JsonObject();
                    Reply[] mbreplyData = mbreply.data();

                    for (int i = 0; i < mbreplyData.length; i+=2) {
                        if (mbreplyData[i].getType() != '$') {
                            sendError(message, "Expected String as key type in multibulk: " + mbreplyData[i].getType());
                            return;
                        }
                        BulkReply brKey = (BulkReply) mbreplyData[i];
                        Reply brValue = mbreplyData[i+1];
                        switch (brValue.getType()) {
                            case '$':   // Bulk
                                bulk.putString(brKey.asString(encoding), ((BulkReply) brValue).asString(encoding));
                                break;
                            case ':':   // Integer
                                bulk.putNumber(brKey.asString(encoding), ((IntegerReply) brValue).data());
                                break;
                            default:
                                sendError(message, "Unknown sub message type in multibulk: " + mbreplyData[i+1].getType());
                                return;
                        }

                    }
                    replyMessage.putObject("value", bulk);
                } else {
                    JsonArray bulk = new JsonArray();
                    for (Reply r : mbreply.data()) {
                        switch (r.getType()) {
                            case '$':   // Bulk
                                bulk.addString(((BulkReply) r).asString(encoding));
                                break;
                            case ':':   // Integer
                                bulk.addNumber(((IntegerReply) r).data());
                                break;
                            default:
                                sendError(message, "Unknown sub message type in multibulk: " + r.getType());
                                return;
                        }
                    }
                    replyMessage.putArray("value", bulk);
                }
                sendOK(message, replyMessage);
                return;
            case ':':   // Integer
                replyMessage = new JsonObject();
                replyMessage.putNumber("value", ((IntegerReply) reply).data());
                sendOK(message, replyMessage);
                return;
            default:
                sendError(message, "Unknown message type");
        }
    }
}
