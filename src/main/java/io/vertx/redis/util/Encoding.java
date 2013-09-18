package io.vertx.redis.util;

/**
 * Shared encoding code.
 */
public class Encoding {
    private static final byte[] NEG_ONE = convert(-1, false);
    private static final byte[] NEG_ONE_WITH_CRLF = convert(-1, true);
    private static final char LF = '\n';
    private static final char CR = '\r';

    // Cache 256 number conversions. That should cover a huge
    // percentage of numbers passed over the wire.
    private static final int NUM_MAP_LENGTH = 256;
    private static final byte[][] numMap = new byte[NUM_MAP_LENGTH][];

    static {
        for (int i = 0; i < NUM_MAP_LENGTH; i++) {
            numMap[i] = convert(i, false);
        }
    }

    private static final byte[][] numMapWithCRLF = new byte[NUM_MAP_LENGTH][];

    static {
        for (int i = 0; i < NUM_MAP_LENGTH; i++) {
            numMapWithCRLF[i] = convert(i, true);
        }
    }

    public static byte[] numToBytes(long value) {
        return numToBytes(value, false);
    }

    // Optimized for the direct to ASCII bytes case
    // About 5x faster than using Long.toString.getBytes
    public static byte[] numToBytes(long value, boolean withCRLF) {
        if (value >= 0 && value < NUM_MAP_LENGTH) {
            int index = (int) value;
            return withCRLF ? numMapWithCRLF[index] : numMap[index];
        } else if (value == -1) {
            return withCRLF ? NEG_ONE_WITH_CRLF : NEG_ONE;
        }
        return convert(value, withCRLF);
    }

    private static byte[] convert(long value, boolean withCRLF) {
        boolean negative = value < 0;
        // Checked javadoc: If the argument is equal to 10^n for integer n, then the result is n.
        // Also, if negative, leave another slot for the sign.
        long abs = Math.abs(value);
        int index = (value == 0 ? 0 : (int) Math.log10(abs)) + (negative ? 2 : 1);
        // Append the CRLF if necessary
        byte[] bytes = new byte[withCRLF ? index + 2 : index];
        if (withCRLF) {
            bytes[index] = CR;
            bytes[index + 1] = LF;
        }
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
}
