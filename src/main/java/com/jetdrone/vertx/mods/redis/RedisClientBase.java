package com.jetdrone.vertx.mods.redis;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.net.NetSocket;
import com.jetdrone.vertx.mods.redis.netty.RedisDecoder;
import com.jetdrone.vertx.mods.redis.netty.Reply;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.jetdrone.vertx.mods.redis.util.Encoding.numToBytes;

/**
 * Base class for Redis Vertx client. Generated client would use the facilties
 * in this class to implement typed commands.
 */
class RedisClientBase {
    public static final byte[] ARGS_PREFIX = "*".getBytes();
    public static final byte[] CRLF = "\r\n".getBytes();
    public static final byte[] BYTES_PREFIX = "$".getBytes();

    private final Queue<Handler<Reply>> replies = new LinkedList<>();
    private final NetSocket netSocket;

    RedisClientBase(NetSocket netSocket) {
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
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException th) {
                    // Got to catch decoding fails and try it again
                    channelBuffer.resetReaderIndex();
                    read = ChannelBuffers.copiedBuffer(channelBuffer);
                }
            }
        });
    }

    void send(List<byte[]> command, Handler<Reply> replyHandler) {
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
    }
}
