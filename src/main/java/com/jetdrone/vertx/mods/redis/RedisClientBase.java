package com.jetdrone.vertx.mods.redis;

import static com.jetdrone.vertx.mods.redis.util.Encoding.numToBytes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetClient;
import org.vertx.java.core.net.NetSocket;

import com.jetdrone.vertx.mods.redis.reply.ErrorReply;
import com.jetdrone.vertx.mods.redis.reply.Reply;

/**
 * Base class for Redis Vertx client. Generated client would use the facilties
 * in this class to implement typed commands.
 */
class RedisClientBase {
    public static final byte[] ARGS_PREFIX = "*".getBytes();
    public static final byte[] CRLF = "\r\n".getBytes();
    public static final byte[] BYTES_PREFIX = "$".getBytes();

    private Vertx vertx;
    private Logger logger;
    private final Queue<Handler<Reply>> replies = new LinkedList<>();
    private NetSocket netSocket;
    private String host;
    private int port;
    
    private enum State { DISCONNECTED, CONNECTING, CONNECTED };
    private State state = State.DISCONNECTED;
    
    public RedisClientBase(Vertx vertx, final Logger logger, String host, int port) {
        this.vertx = vertx;
        this.logger = logger;
        this.host = host;
        this.port = port;
    }
    
    void connect(final AsyncResultHandler<Void> resultHandler) {
        if(state == State.DISCONNECTED) {
            state = State.CONNECTING;
            NetClient client = vertx.createNetClient();
            client.exceptionHandler(new Handler<Exception>() {
                public void handle(Exception e) {
                    logger.error("Net client error",e);
                    if(resultHandler != null) {
                        resultHandler.handle(new AsyncResult<Void>(e));
                    }
                    disconnect();
                }
            });
            client.connect(port, host, new Handler<NetSocket>() {
                @Override
                public void handle(NetSocket socket) {
                    state = State.CONNECTED;
                    netSocket = socket;
                    init(netSocket);
                    socket.exceptionHandler(new Handler<Exception>() {
                        public void handle(Exception e) {
                            logger.error("Socket client error",e);
                            disconnect();
                        }
                    });                
                    socket.closedHandler(new Handler<Void>() {
                        public void handle(Void arg0) {
                            logger.info("Socket closed");
                            disconnect();
                        }
                    });
                    if(resultHandler != null) {
                        resultHandler.handle(new AsyncResult<Void>((Void)null));
                    }
                }
            });
        }
    }

    private void init(NetSocket netSocket) {
        this.netSocket = netSocket;
        final RedisDecoder redisDecoder = new RedisDecoder();
        netSocket.dataHandler(new Handler<Buffer>() {

            private ChannelBuffer read = null;

            @Override
            public void handle(Buffer buffer) {
                // Should only get one callback at a time, no sychronization necessary
                ChannelBuffer channelBuffer = buffer.getChannelBuffer();
                if (read != null) {
                    // Merge the new buffer with the previous buffer
                    channelBuffer = ChannelBuffers.copiedBuffer(read, channelBuffer);
                    read = null;
                }
                try {
                    // Attempt to decode a full reply from the channelbuffer
                    Reply receive = redisDecoder.receive(channelBuffer);
                    // If successful, grab the matching handler
                    replies.poll().handle(receive);
                    // May be more to read
                    if (channelBuffer.readable()) {
                        // More than one message in the buffer, need to be careful
                        handle(new Buffer(ChannelBuffers.copiedBuffer(channelBuffer)));
                    }
                } catch (IOException e) {
                    logger.error("Error receiving data from redis", e);
                    disconnect();
                } catch (IndexOutOfBoundsException th) {
                    // Got to catch decoding fails and try it again
                    channelBuffer.resetReaderIndex();
                    read = ChannelBuffers.copiedBuffer(channelBuffer);
                }
            }
        });
    }
        
    void send(final List<byte[]> command, final Handler<Reply> replyHandler) {
        switch(state) {
        case CONNECTED:
            // Serialize the buffer before writing it
            ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer();

            channelBuffer.writeBytes(ARGS_PREFIX);
            channelBuffer.writeBytes(numToBytes(command.size(), true));

            for (byte[] arg : command) {
                channelBuffer.writeBytes(BYTES_PREFIX);
                channelBuffer.writeBytes(numToBytes(arg.length, true));
                channelBuffer.writeBytes(arg);
                channelBuffer.writeBytes(CRLF);
            }

            Buffer buffer = new Buffer(channelBuffer);
            // The order read must match the order written, vertx guarantees
            // that this is only called from a single thread.
            netSocket.write(buffer);
            replies.offer(replyHandler);
            break;
        case DISCONNECTED:
            logger.info("Got request when disconnected. Trying to connect.");
            connect(new AsyncResultHandler<Void>() {
                public void handle(AsyncResult<Void> connection) {
                    if(connection.succeeded()) {
                        send(command, replyHandler);
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
                    send(command, replyHandler);
                }
            });
        }
    }
    
    void disconnect() {
        state = State.DISCONNECTED;
        while(!replies.isEmpty()) {
            replies.poll().handle(new ErrorReply("Connection closed"));
        }
        // make sure the socket is closed
        if(netSocket!=null) {
            netSocket.close();
        };
    }
}
