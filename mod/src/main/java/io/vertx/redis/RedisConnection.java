package io.vertx.redis;

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
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetClient;
import org.vertx.java.core.net.NetSocket;

/**
 * Base class for Redis Vertx client. Generated client would use the facilties
 * in this class to implement typed commands.
 */
public class RedisConnection {


    private final Vertx vertx;
    private final Logger logger;

    private final Queue<Handler<Reply>> repliesQueue = new LinkedList<>();
    private final Queue<Command> connectingQueue = new LinkedList<>();

    private final RedisSubscriptions subscriptions;
    private NetSocket netSocket;

    private final String host;
    private final int port;
    private final String auth;
    private final int select;

    private static enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    private State state = State.DISCONNECTED;

    public RedisConnection(Vertx vertx, final Logger logger, String host, int port, String auth, int select, RedisSubscriptions subscriptions) {
        this.vertx = vertx;
        this.logger = logger;
        this.host = host;
        this.port = port;
        this.auth = auth;
        this.select = select;
        this.subscriptions = subscriptions;
    }

    private void doAuth(final Handler<Void> next) {
        if (auth != null) {
            Command command = new Command("auth", auth).setHandler(new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    switch (reply.getType()) {
                        case '-':
                            logger.error(((ErrorReply) reply).data());
                            netSocket.close();
                            break;
                        case '+':
                            // OK
                            next.handle(null);
                            break;
                        default:
                            throw new RuntimeException("Unexpected reply: " + reply.getType() + ": " + reply.data());
                    }
                }
            });

            send(command);
        } else {
            next.handle(null);
        }
    }

    private void doSelect(final Handler<Void> next) {
        if (select != 0) {
            Command command = new Command("select", select).setHandler(new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    switch (reply.getType()) {
                        case '-':
                            logger.error(((ErrorReply) reply).data());
                            netSocket.close();
                            break;
                        case '+':
                            // OK
                            next.handle(null);
                            break;
                        default:
                            throw new RuntimeException("Unexpected reply: " + reply.getType() + ": " + reply.data());
                    }
                }
            });

            send(command);
        } else {
            next.handle(null);
        }
    }

    private void onConnect(final Handler<Void> next) {
        doAuth(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                doSelect(new Handler<Void>() {
                    @Override
                    public void handle(Void event) {
                        next.handle(null);
                    }
                });
            }
        });
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
                        // clean the reply queue
                        while (!repliesQueue.isEmpty()) {
                            repliesQueue.poll().handle(new ErrorReply("Connection closed"));
                        }
                        // clean waiting for connection queue
                        while (!connectingQueue.isEmpty()) {
                            connectingQueue.poll().getHandler().handle(new ErrorReply("Connection closed"));
                        }
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
                                // clean the reply queue
                                while (!repliesQueue.isEmpty()) {
                                    repliesQueue.poll().handle(new ErrorReply("Connection closed"));
                                }
                                // clean waiting for connection queue
                                while (!connectingQueue.isEmpty()) {
                                    connectingQueue.poll().getHandler().handle(new ErrorReply("Connection closed"));
                                }
                                // update state
                                state = State.DISCONNECTED;
                            }
                        });
                        // set the close handler
                        netSocket.closeHandler(new Handler<Void>() {
                            public void handle(Void arg0) {
                                logger.info("Socket closed");
                                // clean the reply queue
                                while (!repliesQueue.isEmpty()) {
                                    repliesQueue.poll().handle(new ErrorReply("Connection closed"));
                                }
                                // clean waiting for connection queue
                                while (!connectingQueue.isEmpty()) {
                                    connectingQueue.poll().getHandler().handle(new ErrorReply("Connection closed"));
                                }
                                // update state
                                state = State.DISCONNECTED;
                            }
                        });

                        onConnect(new Handler<Void>() {
                            @Override
                            public void handle(Void event) {
                                // process waiting queue (for messages that have been requested while the connection was not totally established)
                                while (!connectingQueue.isEmpty()) {
                                    send(connectingQueue.poll());
                                }
                                // emit ready!
                                if (resultHandler != null) {
                                    resultHandler.handle(new RedisAsyncResult<Void>(null));
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    // Redis 'subscribe', 'unsubscribe', 'psubscribe' and 'punsubscribe' commands can have multiple (including zero) repliesQueue
    // See http://redis.io/topics/pubsub
    // In all cases we want to have a handler to report errors
    void send(final Command command) {
        switch (state) {
            case CONNECTED:
                // The order read must match the order written, vertx guarantees
                // that this is only called from a single thread.
                command.writeTo(netSocket);
                for (int i = 0; i < command.getExpectedReplies(); ++i) {
                    repliesQueue.offer(command.getHandler());
                }
                break;
            case DISCONNECTED:
                logger.info("Got request when disconnected. Trying to connect.");
                connect(new AsyncResultHandler<Void>() {
                    public void handle(AsyncResult<Void> connection) {
                        if (connection.succeeded()) {
                            send(command);
                        } else {
                            command.getHandler().handle(new ErrorReply("Unable to connect"));
                        }
                    }
                });
                break;
            case CONNECTING:
                logger.debug("Got send request while connecting. Will try again in a while.");
                connectingQueue.offer(command);
        }
    }

    public void handleReply(Reply reply) {

        // Important to have this first - 'message' and 'pmessage' can be pushed at any moment, 
        // so they must be filtered out before checking repliesQueue queue
        if (handlePushedPubSubMessage(reply)) {
            return;
        }
        
        Handler<Reply> handler = repliesQueue.poll();
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
