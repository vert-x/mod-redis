package io.vertx.redis;

import io.vertx.redis.impl.RedisSubscriptions;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import io.vertx.redis.impl.MessageHandler;

import java.nio.charset.Charset;

@SuppressWarnings("unused")
public class RedisMod extends BusModBase implements Handler<Message<JsonObject>> {

    private RedisConnection redisClient;
    private RedisSubscriptions subscriptions = new RedisSubscriptions();

    private String encoding;
    private Charset charset;
    private String baseAddress;

    private enum ResponseTransform {
        NONE,
        ARRAY_TO_OBJECT,
        INFO
    }

    @Override
    public void start() {
        super.start();

        final String host = getOptionalStringConfig("host", "localhost");
        final int port = getOptionalIntConfig("port", 6379);
        final String encoding = getOptionalStringConfig("encoding", null);
        final boolean binary = getOptionalBooleanConfig("binary", false);
        // extra options that are nice to have
        final String auth = getOptionalStringConfig("auth", null);
        final int select = getOptionalIntConfig("select", 0);

        if (binary) {
            logger.warn("Binary mode is not implemented yet!!!");
        }

        if (encoding != null) {
            this.encoding = encoding;
        } else {
            this.encoding = "UTF-8";
        }

        charset = Charset.forName(this.encoding);

        redisClient = new RedisConnection(vertx, logger, host, port, auth, select, subscriptions);
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

        String command = message.body().getString("command");
        final Object o = message.body().getField("args");
        final JsonArray args;

        if (o != null) {
            if (o instanceof JsonArray) {
                args = (JsonArray) o;
            } else {
                args = new JsonArray().add(o);
            }
        } else {
            args = null;
        }

        if (command == null) {
            sendError(message, "command must be specified");
            return;
        } else {
            command = command.toLowerCase();
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
                                message.putString("channel", replyData[2].toString(encoding));
                                message.putString("message", replyData[3].toString(encoding));
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
                                message.putString("message", replyData[2].toString(encoding));
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

        redisClient.send(new Command(message.body(), charset).setExpectedReplies(expectedReplies).setHandler(new Handler<Reply>() {
            @Override
            public void handle(Reply reply) {
                processReply(message, reply, transform);
            }
        }));
    }

    private void processReply(Message<JsonObject> message, Reply reply, ResponseTransform transform) {
        JsonObject replyMessage;
        switch (reply.type()) {
            case '-': // Error
                sendError(message, reply.toString());
                return;
            case '+':   // Status
                replyMessage = new JsonObject();
                replyMessage.putString("value", reply.toString());
                sendOK(message, replyMessage);
                return;
            case '$':  // Bulk
                replyMessage = new JsonObject();
                if (transform == ResponseTransform.INFO) {
                    String info = reply.toString(encoding);
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
                    replyMessage.putString("value", reply.toString(encoding));
                }
                sendOK(message, replyMessage);
                return;
            case '*': // Multi
                replyMessage = new JsonObject();
                if (transform == ResponseTransform.ARRAY_TO_OBJECT) {
                    replyMessage.putObject("value", reply.toJsonObject(encoding));
                } else {
                    replyMessage.putArray("value", reply.toJsonArray(encoding));
                }
                sendOK(message, replyMessage);
                return;
            case ':':   // Integer
                replyMessage = new JsonObject();
                replyMessage.putNumber("value", reply.toNumber());
                sendOK(message, replyMessage);
                return;
            default:
                sendError(message, "Unknown message type");
        }
    }
}
