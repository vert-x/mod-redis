package io.vertx.redis;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

import java.nio.charset.Charset;

abstract class AbstractRedisClient {
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

    private final Charset encoding;
    private final EventBus eventBus;
    private final String redisAddress;

    AbstractRedisClient(EventBus eventBus, String redisAddress) {
        this.encoding = Charset.defaultCharset();
        this.eventBus = eventBus;
        this.redisAddress = redisAddress;
    }

    private void appendToBuffer(final Object value, final Buffer buffer) {
        buffer.appendByte((byte) '$');
        if (value == null) {
            buffer.appendByte((byte) '0');
            buffer.appendByte((byte) '\r');
            buffer.appendByte((byte) '\n');
            buffer.appendByte((byte) '\r');
            buffer.appendByte((byte) '\n');
        } else {
            byte[] bytes;
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

            buffer.appendByte((byte) '\r');
            buffer.appendByte((byte) '\n');
            buffer.appendBytes(bytes);
            buffer.appendByte((byte) '\r');
            buffer.appendByte((byte) '\n');
        }
    }

    final void send(String command, Object... args) {
        int totalArgs = 0;
        boolean expectResult = false;
        Handler<Message> messageHandler = null;

        // verify if there are args
        if (args != null) {
            // verify if the last one is a Handler
            Object last = args[args.length - 1];
            totalArgs = args.length - 1;
            if (last instanceof Handler) {
                // the caller expects a result
                expectResult = true;
                totalArgs--;
                messageHandler = (Handler<Message>) last;
            }
        }

        // serialize the request
        Buffer buffer = new Buffer();
        buffer.appendByte((byte) '*');
        buffer.appendBytes(numToBytes(totalArgs + 1));
        buffer.appendByte((byte) '\r');
        buffer.appendByte((byte) '\n');
        // serialize the command
        appendToBuffer(command.getBytes(encoding), buffer);
        // serialize arguments
        for (int i = 0; i < totalArgs; i++) {
            appendToBuffer(args[i], buffer);
        }

        if (expectResult) {
            eventBus.send(redisAddress, buffer, messageHandler);
        } else {
            eventBus.send(redisAddress, buffer);
        }
    }
}
