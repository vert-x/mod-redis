package io.vertx.redis;

import static io.vertx.redis.util.Encoding.numToBytes;

import java.nio.charset.Charset;
import java.util.*;

import io.vertx.redis.impl.RedisAsyncResult;
import io.vertx.redis.reply.ReplyParser;
import io.vertx.redis.reply.*;
import io.vertx.redis.util.MessageHandler;
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
public class RedisClientBase {
    private static final byte[] ARGS_PREFIX = "*".getBytes();
    private static final byte[] CRLF = "\r\n".getBytes();
    private static final byte[] BYTES_PREFIX = "$".getBytes();
    private static final Charset CHARSET = Charset.defaultCharset();

    private final Vertx vertx;
    private final Logger logger;
    private final String auth;
    private final Queue<Handler<Reply>> replies = new LinkedList<>();
    // TODO: merge this 2 objects into a single one
    private final RedisSubscriptions channelSubscriptions;
    private final RedisSubscriptions patternSubscriptions;
    private NetSocket netSocket;
    private final String host;
    private final int port;

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
                        disconnect();
                    } else {
                        state = State.CONNECTED;
                        netSocket = asyncResult.result();
                        // set the data handler (the reply parser)
                        netSocket.dataHandler(replyParser);
                        // set the exception handler
                        netSocket.exceptionHandler(new Handler<Throwable>() {
                            public void handle(Throwable e) {
                                logger.error("Socket client error", e);
                                disconnect();
                            }
                        });
                        // set the close handler
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
                                        case '-':   // Error
                                            error = (ErrorReply) reply;
                                            disconnect();
                                            break;
                                        default:
                                            error = null;
                                            break;
                                    }
                                    if (resultHandler != null) {
                                        resultHandler.handle(new RedisAsyncResult<Void>(error == null ? null : new RedisCommandError(error.data())));
                                    }
                                }
                            });
                        } else {
                            if (resultHandler != null) {
                                resultHandler.handle(new RedisAsyncResult<Void>(null));
                            }
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
                // TODO: queue this instead of adding timers
                vertx.setTimer(100, new Handler<Long>() {
                    public void handle(Long event) {
                        send(command, expectedReplies, replyHandler);
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
