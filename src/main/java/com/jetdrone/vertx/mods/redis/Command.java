package com.jetdrone.vertx.mods.redis;

import java.io.IOException;
import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;

import static com.jetdrone.vertx.mods.redis.util.Encoding.numToBytes;

/**
 * Command serialization.  We special case when there are few 4 or fewer parameters
 * since most commands fall into that category. Passing bytes, channelbuffers and
 * strings / objects are all allowed. All strings are assumed to be UTF-8.
 */
public class Command {
    public static final byte[] ARGS_PREFIX = "*".getBytes();
    public static final byte[] CRLF = "\r\n".getBytes();
    public static final byte[] BYTES_PREFIX = "$".getBytes();
    public static final byte[] EMPTY_BYTES = new byte[0];

    private final Object name;
    private final Object[] objects;

    public Command(Object name, Object... objects) {
        this.name = name;
        this.objects = objects;
    }

    public void write(ChannelBuffer os) throws IOException {
        int length = objects == null ? 0 : objects.length;

        os.writeBytes(ARGS_PREFIX);
        os.writeBytes(numToBytes(length + + (name == null ? 0 : 1), true));

        if (name != null) writeObject(os, name);
        if (objects != null) {
            for (Object object : objects) {
                // verify if one of the arguments is an array of objects
                if (object instanceof Object[]) {
                    Object[] array = (Object[]) object;
                    for (Object innerObject : array) {
                        writeObject(os, innerObject);
                    }
                } else {
                    writeObject(os, object);
                }
            }
        }
    }

    private static void writeObject(ChannelBuffer os, Object object) throws IOException {
        byte[] argument;
        if (object == null) {
            argument = EMPTY_BYTES;
        } else if (object instanceof byte[]) {
            argument = (byte[]) object;
        } else if (object instanceof ChannelBuffer) {
            writeArgument(os, (ChannelBuffer) object);
            return;
        } else if (object instanceof String) {
            argument = ((String) object).getBytes(Charset.forName("UTF-8"));
        } else {
            argument = object.toString().getBytes(Charset.forName("UTF-8"));
        }
        writeArgument(os, argument);
    }

    private static void writeArgument(ChannelBuffer os, byte[] argument) throws IOException {
        os.writeBytes(BYTES_PREFIX);
        os.writeBytes(numToBytes(argument.length, true));
        os.writeBytes(argument);
        os.writeBytes(CRLF);
    }

    private static void writeArgument(ChannelBuffer os, ChannelBuffer argument) throws IOException {
        os.writeBytes(BYTES_PREFIX);
        os.writeBytes(numToBytes(argument.readableBytes(), true));
        os.writeBytes(argument);
        os.writeBytes(CRLF);
    }

}
