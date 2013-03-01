package com.jetdrone.vertx.mods.redis.command;

import com.jetdrone.vertx.mods.redis.RedisCommandError;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class Command {

    private static final byte[] EMPTY_BYTES = new byte[0];

    public final List<byte[]> args = new ArrayList<>();
    final Message<JsonObject> message;
    final Charset charset;
    final boolean binary;

    public Command(String redisCommand, Message<JsonObject> message, Charset charset, boolean binary) {
        this.charset = charset;
        this.message = message;
        this.binary = binary;

        // add the command to the list
        if (redisCommand.indexOf(' ') == -1) {
            // single token
            args.add(redisCommand.getBytes(charset));
        } else {
            for (String token : redisCommand.split(" ")) {
                args.add(token.getBytes(charset));
            }
        }
    }

    public void arg(final String argName) throws RedisCommandError {
        final Object arg = message.body.getField(argName);
        if (arg == null) {
            throw new RedisCommandError(argName + " cannot be null");
        } else {
            raw(arg);
        }
    }

    public void optArg(final String argName) {
        final Object arg = message.body.getField(argName);
        if (arg != null) {
            raw(arg);
        }
    }

    public void binArg(final String argName) throws RedisCommandError {
        Object arg = message.body.getField(argName);
        if (arg == null) {
            throw new RedisCommandError(argName + " cannot be null");
        } else {
            if (arg instanceof String) {
                if (binary) {
                    arg = message.body.getBinary(argName);
                }
            }
            raw(arg);
        }
    }

    public void optBinArg(final String argName) {
        Object arg = message.body.getField(argName);
        if (arg != null) {
            if (arg instanceof String) {
                if (binary) {
                    arg = message.body.getBinary(argName);
                }
            }
            raw(arg);
        }
    }

    public void optArg(final Option option) {
        final Object arg = message.body.getField(option.name);

        if (arg != null) {
            raw(option.name);
        }
    }

    public void arg(final KeyValue keyValue) throws RedisCommandError {
        final Object arg = message.body.getField(keyValue.pairName);
        if (arg == null) {
            // process single pair
            arg(keyValue.keyName);
            arg(keyValue.valueName);
        } else {
            if (arg instanceof JsonArray) {
                // flatten the json array
                for (Object item : (JsonArray) arg) {
                    if (item instanceof JsonObject) {
                        JsonObject jsonEntry = (JsonObject) item;
                        Object key = jsonEntry.getField(keyValue.keyName);
                        Object value = jsonEntry.getField(keyValue.valueName);
                        // kv cannot be null
                        if (key == null || value == null) {
                            throw new RedisCommandError(keyValue.keyName + " or " + keyValue.valueName + " cannot be null");
                        }

                        if (value instanceof String) {
                            if (binary) {
                                value = jsonEntry.getBinary(keyValue.valueName);
                            }
                        }
                        raw(key);
                        raw(value);
                    } else {
                        throw new RedisCommandError(keyValue.pairName + " expected to have JsonObjects");
                    }
                }
            } else {
                // error expected array of objects
                throw new RedisCommandError(keyValue.pairName + " must be an array");
            }
        }
    }

    public void optArg(final OrOption options) {
        final Object arg0 = message.body.getField(options.o1.name);
        if (arg0 == null) {
            // first field does not exist, try the second
            final Object arg1 = message.body.getField(options.o2.name);
            if (arg1 != null) {
                // secon field exists
                raw(options.o2.name);
            }
        } else {
            // first field exists
            raw(options.o1.name);
        }
    }

    public void arg(final OrOption options) throws RedisCommandError {
        final Object arg0 = message.body.getField(options.o1.name);
        if (arg0 == null) {
            // first field does not exist, try the second
            final Object arg1 = message.body.getField(options.o2.name);
            if (arg1 == null) {
                // second field does not exist either
                throw new RedisCommandError("both " + options.o1.name + " and " + options.o2.name + " cannot be null");
            } else {
                // secon field exists
                raw(options.o2.name);
            }
        } else {
            // first field exists
            raw(options.o1.name);
        }
    }

    public void raw(final Object o) {
        if (o == null) {
            args.add(EMPTY_BYTES);
            return;
        }

        if (o instanceof byte[]) {
            args.add((byte[]) o);
            return;
        }

        if (o instanceof JsonArray) {
            for (Object item : (JsonArray) o) {
                raw(item);
            }
            return;
        }

        if (o instanceof String) {
            args.add(((String) o).getBytes(charset));
            return;
        }

        args.add(o.toString().getBytes(charset));
    }
}
