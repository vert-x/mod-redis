package io.vertx.redis.command;

import io.vertx.redis.RedisCommandError;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class JSONCommand {

    private static final byte[] EMPTY_BYTES = new byte[0];

    public final List<byte[]> args = new ArrayList<>();
    private final Message<JsonObject> message;
    private final Charset charset;

    public JSONCommand(String redisCommand, Message<JsonObject> message, Charset charset) {
        this.charset = charset;
        this.message = message;

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
        final Object arg = message.body().getField(argName);
        if (arg == null) {
            throw new RedisCommandError(argName + " cannot be null");
        } else {
            raw(arg);
        }
    }

    public void optArg(final String argName) {
        final Object arg = message.body().getField(argName);
        if (arg != null) {
            raw(arg);
        }
    }

    public void optArg(final NamedValue namedValue) {
        final Object arg = message.body().getField(namedValue.name);
        if (arg != null) {
            if (arg instanceof JsonArray) {
                for (Object item : (JsonArray) arg) {
                    raw(namedValue.name);
                    raw(item);
                }
            } else {
                raw(namedValue.name);
                raw(arg);
            }
        }
    }

    public void optArg(final Option option) {
        final Object arg = message.body().getField(option.name);

        if (arg != null) {
            raw(option.name);
        }
    }

    public void arg(final KeyValue keyValue) throws RedisCommandError {
        final Object arg = message.body().getField(keyValue.pairName);
        if (arg == null) {
            // process single pair
            arg(keyValue.keyName);
            arg(keyValue.valueName);
        } else {
            if (arg instanceof JsonObject) {
                // hash notation, needs to be flatten
                JsonObject hash = (JsonObject) arg;
                for (String fName : hash.getFieldNames()) {
                    Object value = hash.getField(fName);
                    // kv cannot be null
                    if (value == null) {
                        throw new RedisCommandError(fName + " cannot be null");
                    }

                    raw(fName);
                    raw(value);
                }
            } else {
                // error expected array of objects
                throw new RedisCommandError(keyValue.pairName + " must be an object");
            }
        }
    }

    public void arg(final OrOption options) throws RedisCommandError {
        final Object arg0 = message.body().getField(options.o1.name);
        if (arg0 == null) {
            // first field does not exist, try the second
            final Object arg1 = message.body().getField(options.o2.name);
            if (arg1 != null) {
                // secon field exists
                raw(options.o2.name);
            } else {
                throw new RedisCommandError(options.o1.name + " or " + options.o2.name + " must not be null");
            }
        } else {
            // first field exists
            raw(options.o1.name);
        }
    }

    public void optArg(final OrOption options) {
        final Object arg0 = message.body().getField(options.o1.name);
        if (arg0 == null) {
            // first field does not exist, try the second
            final Object arg1 = message.body().getField(options.o2.name);
            if (arg1 != null) {
                // secon field exists
                raw(options.o2.name);
            }
        } else {
            // first field exists
            raw(options.o1.name);
        }
    }

    public void optArg(final NamedKeyValue namedKeyValue) throws RedisCommandError {
        final Object arg = message.body().getField(namedKeyValue.name);
        if (arg != null) {
            if (arg instanceof JsonObject) {
                raw(namedKeyValue.name);
                final Object key = ((JsonObject) arg).getField(namedKeyValue.keyName);
                if (key == null) {
                    throw new RedisCommandError(namedKeyValue.keyName + " cannot be null");
                } else {
                    raw(key);
                }
                final Object value = ((JsonObject) arg).getField(namedKeyValue.valueName);
                if (value == null) {
                    throw new RedisCommandError(namedKeyValue.valueName + " cannot be null");
                } else {
                    raw(value);
                }
            } else {
                throw new RedisCommandError(namedKeyValue.name + " must be a JsonObject");
            }
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

    public String[] getArg(final String argName) {
        final Object arg = message.body().getField(argName);
        if (arg == null) {
            return null;
        } else {
            if (arg instanceof byte[]) {
                return new String[] {
                        new String((byte[]) arg)
                };
            }

            if (arg instanceof JsonArray) {
                JsonArray items = (JsonArray) arg;
                String[] args = new String[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    args[i] = items.get(i).toString();
                }
                return args;
            }

            if (arg instanceof String) {
                return new String[] {
                        (String) arg
                };
            }

            return new String[] {
                    arg.toString()
            };
        }
    }
}
