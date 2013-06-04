package com.jetdrone.vertx.mods.redis;

import static com.jetdrone.vertx.mods.redis.util.Encoding.numToBytes;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import com.jetdrone.vertx.mods.redis.reply.*;
import com.jetdrone.vertx.mods.redis.util.MessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetClient;
import org.vertx.java.core.net.NetSocket;

/**
 * Base class for Redis Vertx client. Generated client would use the facilties
 * in this class to implement typed commands.
 */
class RedisClientBase {
    public static final byte[] ARGS_PREFIX = "*".getBytes();
    public static final byte[] CRLF = "\r\n".getBytes();
    public static final byte[] BYTES_PREFIX = "$".getBytes();
    private static final Charset CHARSET = Charset.defaultCharset();

    private final Vertx vertx;
    private final Logger logger;
    private final String auth;
    private final Queue<Handler<Reply>> replies = new LinkedList<>();
    private final RedisSubscriptions channelSubscriptions;
    private final RedisSubscriptions patternSubscriptions;
    private NetSocket netSocket;
    private String host;
    private int port;

    private static enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    private State state = State.DISCONNECTED;

    public RedisClientBase(Vertx vertx, final Logger logger, String host, int port, String auth, RedisSubscriptions channelSubscriptions, RedisSubscriptions patternSubscriptions) {
        this.vertx = vertx;
        this.logger = logger;
        this.host = host;
        this.port = port;
        this.auth = auth;
        this.channelSubscriptions = channelSubscriptions;
        this.patternSubscriptions = patternSubscriptions;
    }

    void connect(final AsyncResultHandler<Void> resultHandler) {
        if (state == State.DISCONNECTED) {
            state = State.CONNECTING;
            NetClient client = vertx.createNetClient();
            client.connect(port, host, new AsyncResultHandler<NetSocket>() {
                @Override
                public void handle(final AsyncResult<NetSocket> asyncResult) {
                    if (asyncResult.failed()) {
                        logger.error("Net client error", asyncResult.cause());
                        if (resultHandler != null) {
                            resultHandler.handle(new AsyncResult<Void>() {
                                @Override
                                public Void result() {
                                    return null;
                                }

                                @Override
                                public Throwable cause() {
                                    return asyncResult.cause();
                                }

                                @Override
                                public boolean succeeded() {
                                    return false;
                                }

                                @Override
                                public boolean failed() {
                                    return true;
                                }
                            });
                        }
                        disconnect();
                    } else {
                        state = State.CONNECTED;
                        netSocket = asyncResult.result();
                        init(netSocket);
                        netSocket.exceptionHandler(new Handler<Throwable>() {
                            public void handle(Throwable e) {
                                logger.error("Socket client error", e);
                                disconnect();
                            }
                        });
                        netSocket.closeHandler(new Handler<Void>() {
                            public void handle(Void arg0) {
                                logger.info("Socket closed");
                                disconnect();
                            }
                        });
                        if (auth != null) {
                            // authenticate
                            List<byte[]> cmd = new ArrayList<>();
                            cmd.add("auth".getBytes());
                            cmd.add(auth.getBytes(CHARSET));
                            send(cmd, 1, new Handler<Reply>() {
                                @Override
                                public void handle(Reply reply) {
                                    final ErrorReply error;
                                    switch (reply.getType()) {
                                        case Error:
                                            error = (ErrorReply) reply;
                                            disconnect();
                                            break;
                                        default:
                                            error = null;
                                            break;
                                    }
                                    if (resultHandler != null) {
                                        resultHandler.handle(new AsyncResult<Void>() {
                                            final Throwable ex = error == null ? null : new RedisCommandError(error.data());
                                            @Override
                                            public Void result() {
                                                return null;
                                            }

                                            @Override
                                            public Throwable cause() {
                                                return ex;
                                            }

                                            @Override
                                            public boolean succeeded() {
                                                return ex == null;
                                            }

                                            @Override
                                            public boolean failed() {
                                                return ex != null;
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            if (resultHandler != null) {
                                resultHandler.handle(new AsyncResult<Void>() {
                                    @Override
                                    public Void result() {
                                        return null;
                                    }

                                    @Override
                                    public Throwable cause() {
                                        return null;
                                    }

                                    @Override
                                    public boolean succeeded() {
                                        return true;
                                    }

                                    @Override
                                    public boolean failed() {
                                        return false;
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    private void init(NetSocket netSocket) {
        this.netSocket = netSocket;
        final RedisDecoder redisDecoder = new RedisDecoder();
        netSocket.dataHandler(new Handler<Buffer>() {

            private ByteBuf read = null;

            @Override
            public void handle(Buffer buffer) {
                // Should only get one callback at a time, no sychronization necessary
                ByteBuf byteBuf = buffer.getByteBuf();
                if (read != null) {
                    // Merge the new buffer with the previous buffer
                    byteBuf = Unpooled.copiedBuffer(read, byteBuf);
                    read = null;
                }
                try {
                    // Attempt to decode a full reply from the channelbuffer
                    Reply receive = redisDecoder.receive(byteBuf);
                    // If successful, grab the matching handler
                    handleReply(receive);
                    // May be more to read
                    if (byteBuf.isReadable()) {
                        // More than one message in the buffer, need to be careful
                        handle(new Buffer(Unpooled.copiedBuffer(byteBuf)));
                    }
                } catch (IOException e) {
                    logger.error("Error receiving data from redis", e);
                    disconnect();
                } catch (IndexOutOfBoundsException th) {
                    // Got to catch decoding fails and try it again
                    byteBuf.resetReaderIndex();
                    read = Unpooled.copiedBuffer(byteBuf);
                }
            }
        });
    }

    // Redis 'subscribe', 'unsubscribe', 'psubscribe' and 'punsubscribe' commands can have multiple (including zero) replies
    // See http://redis.io/topics/pubsub
    // In all cases we want to have a handler to report errors
    void send(final List<byte[]> command, final int expectedReplies, final Handler<Reply> replyHandler) {
        switch (state) {
            case CONNECTED:
                // Serialize the buffer before writing it
                Buffer buffer = new Buffer();

                buffer.appendBytes(ARGS_PREFIX);
                buffer.appendBytes(numToBytes(command.size(), true));

                for (byte[] arg : command) {
                    buffer.appendBytes(BYTES_PREFIX);
                    buffer.appendBytes(numToBytes(arg.length, true));
                    buffer.appendBytes(arg);
                    buffer.appendBytes(CRLF);
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
                            send(command, expectedReplies, replyHandler);
                        } else {
                            replyHandler.handle(new ErrorReply("Unable to connect"));
                        }
                    }
                });
                break;
            case CONNECTING:
                logger.debug("Got send request while connecting. Will try again in a while.");
                vertx.setTimer(100, new Handler<Long>() {
                    public void handle(Long event) {
                        send(command, expectedReplies, replyHandler);
                    }
                });
        }
    }

    void handleReply(Reply reply) throws IOException {

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

        throw new IOException("Received a non pub/sub message without reply handler waiting:"+reply.toString());
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
                    if (data[0] instanceof BulkReply && "message".equals(((BulkReply) data[0]).asString(CHARSET))) {
                        String channel = ((BulkReply) data[1]).asString(CHARSET);
                        MessageHandler handler = channelSubscriptions.getHandler(channel);
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
                    if (data[0] instanceof BulkReply && "pmessage".equals(((BulkReply) data[0]).asString(CHARSET))) {
                        String pattern = ((BulkReply) data[1]).asString(CHARSET);
                        MessageHandler handler = patternSubscriptions.getHandler(pattern);
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

    void disconnect() {
        state = State.DISCONNECTED;
        while (!replies.isEmpty()) {
            replies.poll().handle(new ErrorReply("Connection closed"));
        }
        // make sure the socket is closed
        if (netSocket != null) {
            netSocket.close();
        }
    }
}
