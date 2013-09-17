package io.vertx.redis.reply;

import io.vertx.redis.RedisClientBase;
import io.vertx.redis.reply.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;

public class ReplyParser implements Handler<Buffer> {

    private static class IncompleteReadBuffer extends Exception {
        public IncompleteReadBuffer(String message) {
            super(message);
        }
    }

    private Buffer _buffer;
    private int _offset;
    private final String _encoding = "utf-8";

    private final RedisClientBase client;

    public ReplyParser(RedisClientBase client) {
        this.client = client;
    }


    private Reply parseResult(byte type) throws IncompleteReadBuffer {
        int start, end, offset;
        int packetSize;

        if (type == 43 || type == 45) { // + or -
            // up to the delimiter
            end = packetEndOffset() - 1;
            start = _offset;

            // include the delimiter
            _offset = end + 2;

            if (end > _buffer.length()) {
                _offset = start;
                throw new IncompleteReadBuffer("Wait for more data.");
            }

            if (type == 43) {
                return new StatusReply(_buffer.getString(start, end, _encoding));
            } else {
                return new ErrorReply(_buffer.getString(start, end, _encoding));
            }
        } else if (type == 58) { // :
            // up to the delimiter
            end = packetEndOffset() - 1;
            start = _offset;

            // include the delimiter
            _offset = end + 2;

            if (end > _buffer.length()) {
                _offset = start;
                throw new IncompleteReadBuffer("Wait for more data.");
            }

            // return the coerced numeric value
            return new IntegerReply(Long.parseLong(_buffer.getString(start, end)));
        } else if (type == 36) { // $
            // set a rewind point, as the packet could be larger than the
            // buffer in memory
            offset = _offset - 1;

            packetSize = parsePacketSize();

            // packets with a size of -1 are considered null
            if (packetSize == -1) {
                return new BulkReply(null);
            }

            end = _offset + packetSize;
            start = _offset;

            // set the offset to after the delimiter
            _offset = end + 2;

            if (end > _buffer.length()) {
                _offset = offset;
                throw new IncompleteReadBuffer("Wait for more data.");
            }

            return new BulkReply(_buffer.getBytes(start, end));
        } else if (type == 42) { // *
            offset = _offset;
            packetSize = parsePacketSize();

            if (packetSize < 0) {
                return null;
            }

            if (packetSize > bytesRemaining()) {
                _offset = offset - 1;
                throw new IncompleteReadBuffer("Wait for more data.");
            }

            MultiBulkReply reply = new MultiBulkReply(packetSize);

            byte ntype;
            Reply res;

            for (int i = 0; i < packetSize; i++) {
                ntype = _buffer.getByte(_offset++);

                if (_offset > _buffer.length()) {
                    throw new IncompleteReadBuffer("Wait for more data.");
                }
                res = parseResult(ntype);
                reply.set(i, res);
            }

            return reply;
        }

        throw new RuntimeException("Unsupported message type");
    }

    public void handle(Buffer buffer) {
        append(buffer);

        byte type;
        Reply ret;
        int offset;

        while (true) {
            offset = _offset;
            try {
                // at least 4 bytes: :1\r\n
                if (bytesRemaining() < 4) {
                    break;
                }

                type = _buffer.getByte(_offset++);

                if (type == 43) { // +
                    ret = parseResult(type);

                    if (ret == null) {
                        break;
                    }

                    client.handleReply(ret);
                } else if (type == 45) { // -
                    ret = parseResult(type);

                    if (ret == null) {
                        break;
                    }

                    client.handleReply(ret);
                } else if (type == 58) { // :
                    ret = parseResult(type);

                    if (ret == null) {
                        break;
                    }

                    client.handleReply(ret);
                } else if (type == 36) { // $
                    ret = parseResult(type);

                    if (ret == null) {
                        break;
                    }

                    client.handleReply(ret);
                } else if (type == 42) { // *
                    // set a rewind point. if a failure occurs,
                    // wait for the next execute()/append() and try again
                    offset = _offset - 1;

                    ret = parseResult(type);

                    client.handleReply(ret);
                }
            } catch (IncompleteReadBuffer err) {
                // catch the error (not enough data), rewind, and wait
                // for the next packet to appear
                _offset = offset;
                break;
            }
        }
    }

    private void append(Buffer newBuffer) {
        if (newBuffer == null) {
            return;
        }

        // first run
        if (_buffer == null) {
            _buffer = newBuffer;

            return;
        }

        // out of data
        if (_offset >= _buffer.length()) {
            _buffer = newBuffer;
            _offset = 0;

            return;
        }

        // very large packet
        _buffer = _buffer.getBuffer(_offset, _buffer.length());
        _buffer.appendBuffer(newBuffer);

        _offset = 0;
    }

    private int parsePacketSize() throws IncompleteReadBuffer {
        int end = packetEndOffset();
        String value = _buffer.getString(_offset, end - 1, _encoding);

        _offset = end + 1;

        long size = Long.parseLong(value);

        if (size > Integer.MAX_VALUE) {
            throw new RuntimeException("Cannot allocate more than " + Integer.MAX_VALUE + " bytes");
        }

        if (size < Integer.MIN_VALUE) {
            throw new RuntimeException("Cannot allocate less than " + Integer.MIN_VALUE + " bytes");
        }
        return (int) size;
    }

    private int packetEndOffset() throws IncompleteReadBuffer {
        int offset = _offset;

        while (_buffer.getByte(offset) != 0x0d && _buffer.getByte(offset + 1) != 0x0a) {
            offset++;

            if (offset >= _buffer.length()) {
                throw new IncompleteReadBuffer("didn't see LF after NL reading multi bulk count (" + offset + " => " + _buffer.length() + ", " + _offset + ")");
            }
        }

        offset++;
        return offset;
    }

    private int bytesRemaining() {
        return (_buffer.length() - _offset) < 0 ? 0 : (_buffer.length() - _offset);
    }
}
