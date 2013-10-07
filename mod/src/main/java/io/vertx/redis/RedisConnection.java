package io.vertx.redis;

import java.nio.charset.Charset;
import java.util.*;

import io.vertx.redis.impl.RedisAsyncResult;
import io.vertx.redis.impl.RedisSubscriptions;
import io.vertx.redis.reply.ReplyParser;
import io.vertx.redis.reply.*;
import io.vertx.redis.impl.MessageHandler;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetClient;
import org.vertx.java.core.net.NetSocket;

/**
 * Base class for Redis Vertx client. Generated client would use the facilties
 * in this class to implement typed commands.
 */
public class RedisConnection {

    private static final byte ARGS_PREFIX = '*';
    private static final byte[] CRLF = "\r\n".getBytes();
    private static final byte BYTES_PREFIX = '$';

    private static final byte[] NEG_ONE = convert(-1);

    // Cache 256 number conversions. That should cover a huge
    // percentage of numbers passed over the wire.
    private static final int NUM_MAP_LENGTH = 256;
    private static final byte[][] numMap = new byte[NUM_MAP_LENGTH][];

    static {
        for (int i = 0; i < NUM_MAP_LENGTH; i++) {
            numMap[i] = convert(i);
        }
    }

    // Optimized for the direct to ASCII bytes case
    // About 5x faster than using Long.toString.getBytes
    private static byte[] numToBytes(long value) {
        if (value >= 0 && value < NUM_MAP_LENGTH) {
            int index = (int) value;
            return numMap[index];
        } else if (value == -1) {
            return NEG_ONE;
        }
        return convert(value);
    }

    private static byte[] convert(long value) {
        boolean negative = value < 0;
        // Checked javadoc: If the argument is equal to 10^n for integer n, then the result is n.
        // Also, if negative, leave another slot for the sign.
        long abs = Math.abs(value);
        int index = (value == 0 ? 0 : (int) Math.log10(abs)) + (negative ? 2 : 1);
        byte[] bytes = new byte[index];
        // Put the sign in the slot we saved
        if (negative) bytes[0] = '-';
        long next = abs;
        while ((next /= 10) > 0) {
            bytes[--index] = (byte) ('0' + (abs % 10));
            abs = next;
        }
        bytes[--index] = (byte) ('0' + abs);
        return bytes;
    }

    private final Vertx vertx;
    private final Logger logger;
    private final Queue<Handler<Reply>> replies = new LinkedList<>();
    private final RedisSubscriptions subscriptions;
    private NetSocket netSocket;
    private final String host;
    private final int port;
    private final Charset encoding;

    private static enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    private State state = State.DISCONNECTED;

    public RedisConnection(Vertx vertx, final Logger logger, String host, int port, RedisSubscriptions subscriptions, Charset encoding) {
        this.vertx = vertx;
        this.logger = logger;
        this.host = host;
        this.port = port;
        this.subscriptions = subscriptions;
        this.encoding = encoding;
    }

    private void appendToBuffer(final Object value, final Buffer buffer) {
        buffer.appendByte(BYTES_PREFIX);
        if (value == null) {
            buffer.appendByte((byte) '0');
            buffer.appendBytes(CRLF);
            buffer.appendBytes(CRLF);
        } else {
            byte[] bytes;
            // Possible types are: String, JsonObject, JsonArray, JsonElement, Number, Boolean, byte[]

            if (value instanceof byte[]) {
                bytes = (byte[]) value;
            } else if (value instanceof Buffer) {
                bytes = ((Buffer) value).getBytes();
            } else if (value instanceof String) {
                bytes = ((String) value).getBytes(encoding);
            } else if (value instanceof Byte) {
                bytes = numToBytes((Byte) value);
            } else if (value instanceof Short) {
                bytes = numToBytes((Short) value);
            } else if (value instanceof Integer) {
                bytes = numToBytes((Integer) value);
            } else if (value instanceof Long) {
                bytes = numToBytes((Long) value);
            } else {
                bytes = value.toString().getBytes(encoding);
            }

            buffer.appendBytes(numToBytes(bytes.length));

            buffer.appendBytes(CRLF);
            buffer.appendBytes(bytes);
            buffer.appendBytes(CRLF);
        }
    }

    void connect(final AsyncResultHandler<Void> resultHandler) {
        if (state == State.DISCONNECTED) {
            state = State.CONNECTING;
            // instantiate a parser for the connection
            final ReplyParser replyParser = new ReplyParser(this);

            NetClient client = vertx.createNetClient();
            client.connect(port, host, new AsyncResultHandler<NetSocket>() {
                @Override
                public void handle(final AsyncResult<NetSocket> asyncResult) {
                    if (asyncResult.failed()) {
                        logger.error("Net client error", asyncResult.cause());
                        if (resultHandler != null) {
                            resultHandler.handle(new RedisAsyncResult<Void>(asyncResult.cause()));
                        }
                        // make sure the socket is closed
                        if (netSocket != null) {
                            netSocket.close();
                        }
                        // update state
                        state = State.DISCONNECTED;
                    } else {
                        state = State.CONNECTED;
                        netSocket = asyncResult.result();
                        // set the data handler (the reply parser)
                        netSocket.dataHandler(replyParser);
                        // set the exception handler
                        netSocket.exceptionHandler(new Handler<Throwable>() {
                            public void handle(Throwable e) {
                                logger.error("Socket client error", e);
                                // make sure the socket is closed
                                if (netSocket != null) {
                                    netSocket.close();
                                }
                                // update state
                                state = State.DISCONNECTED;
                            }
                        });
                        // set the close handler
                        netSocket.closeHandler(new Handler<Void>() {
                            public void handle(Void arg0) {
                                logger.info("Socket closed");
                                // clean the reply pool
                                while (!replies.isEmpty()) {
                                    replies.poll().handle(new ErrorReply("Connection closed"));
                                }
                                // update state
                                state = State.DISCONNECTED;
                            }
                        });
                        if (resultHandler != null) {
                            resultHandler.handle(new RedisAsyncResult<Void>(null));
                        }

                        // TODO: process waiting queue (for messages that have been requested while the connection was not totally established
                    }
                }
            });
        }
    }

    // Redis 'subscribe', 'unsubscribe', 'psubscribe' and 'punsubscribe' commands can have multiple (including zero) replies
    // See http://redis.io/topics/pubsub
    // In all cases we want to have a handler to report errors
    void send(final JsonObject request, final int expectedReplies, final Handler<Reply> replyHandler) {
        switch (state) {
            case CONNECTED:

                String command = request.getString("command");
                JsonArray args = request.getArray("args");

                int totalArgs;
                if (args == null) {
                    totalArgs = 0;
                } else {
                    totalArgs = args.size();
                }

                int spc = command.indexOf(' '); // there are commands which are multi word
                String extraCommand = null;

                if (spc != -1) {
                    extraCommand = command.substring(spc + 1);
                    command = command.substring(0, spc);
                }

                // serialize the request
                Buffer buffer = new Buffer();
                buffer.appendByte(ARGS_PREFIX);
                if (extraCommand == null) {
                    buffer.appendBytes(numToBytes(totalArgs + 1));
                } else {
                    buffer.appendBytes(numToBytes(totalArgs + 2));
                }
                buffer.appendBytes(CRLF);
                // serialize the command
                appendToBuffer(command.getBytes(encoding), buffer);
                if (extraCommand != null) {
                    appendToBuffer(extraCommand.getBytes(encoding), buffer);
                }

                // serialize arguments
                for (int i = 0; i < totalArgs; i++) {
                    appendToBuffer(args.get(i), buffer);
                }

                // The order read must match the order written, vertx guarantees
                // that this is only called from a single thread.
                netSocket.write(buffer);
                for (int i = 0; i < expectedReplies; ++i) {
                    replies.offer(replyHandler);
                }
                break;
            case DISCONNECTED:
                logger.info("Got request when disconnected. Trying to connect.");
                connect(new AsyncResultHandler<Void>() {
                    public void handle(AsyncResult<Void> connection) {
                        if (connection.succeeded()) {
                            send(request, expectedReplies, replyHandler);
                        } else {
                            replyHandler.handle(new ErrorReply("Unable to connect"));
                        }
                    }
                });
                break;
            case CONNECTING:
                logger.debug("Got send request while connecting. Will try again in a while.");
                // TODO: queue this instead of adding timers
                vertx.setTimer(100, new Handler<Long>() {
                    public void handle(Long event) {
                        send(request, expectedReplies, replyHandler);
                    }
                });
        }
    }

    public void handleReply(Reply reply) {

        // Important to have this first - 'message' and 'pmessage' can be pushed at any moment, 
        // so they must be filtered out before checking replies queue
        if (handlePushedPubSubMessage(reply)) {
            return;
        }
        
        Handler<Reply> handler = replies.poll();
        if (handler != null) {
            // handler waits for this response
            handler.handle(reply);
            return;
        }

        throw new RuntimeException("Received a non pub/sub message without reply handler waiting:"+reply.toString());
    }

    // Handle 'message' and 'pmessage' messages; returns true if the message was handled
    // Appropriate number of handlers for 'subscribe', 'unsubscribe', 'psubscribe' and 'punsubscribe' is inserted when these commands are sent
    // See http://redis.io/topics/pubsub
    boolean handlePushedPubSubMessage(Reply reply) {
        // Pub/sub messages are always multi-bulk
        if (reply instanceof MultiBulkReply) {
            MultiBulkReply mbReply = (MultiBulkReply) reply;

            Reply[] data = mbReply.data();
            if (data != null) {
                // message
                if (data.length == 3) {
                    if (data[0] instanceof BulkReply && "message".equals(((BulkReply) data[0]).asString("UTF-8"))) {
                        String channel = ((BulkReply) data[1]).asString("UTF-8");
                        MessageHandler handler = subscriptions.getChannelHandler(channel);
                        if (handler != null)
                        {
                            handler.handle(channel, data);
                        }                       
                        // It is possible to get a message after removing subscription in the client but before Redis command executes,
                        // so ignoring message here (consumer already is not interested in it)
                        return true;
                    }
                } 
                // pmessage
                else if (data.length == 4) {
                    if (data[0] instanceof BulkReply && "pmessage".equals(((BulkReply) data[0]).asString("UTF-8"))) {
                        String pattern = ((BulkReply) data[1]).asString("UTF-8");
                        MessageHandler handler = subscriptions.getPatternHandler(pattern);
                        if (handler != null)
                        {
                            handler.handle(pattern, data);
                        }                       
                        // It is possible to get a message after removing subscription in the client but before Redis command executes,
                        // so ignoring message here (consumer already is not interested in it)
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
