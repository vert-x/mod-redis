package io.vertx.redis;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public final class Reply {

    private final byte type;
    private final Object data;

    public Reply(byte type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Reply(byte type, int size) {
        this.type = type;
        this.data = new Reply[size];
    }

    public Reply(char type, Object data) {
        this.type = (byte) type;
        this.data = data;
    }

    public Reply(char type, int size) {
        this.type = (byte) type;
        this.data = new Reply[size];
    }

    void set(int pos, Reply reply) {
        ((Reply[]) data)[pos] = reply;
    }

    public boolean is(byte b) {
        return type == b;
    }

    public boolean is(char b) {
        return type == (byte) b;
    }

    /**
     * Return the type of instance of this Reply. Useful to avoid checks against instanceof
     * @return enum
     */
    byte type() {
        return type;
    }

    public Object data() {
        return data;
    }

    public String toString(String encoding) {
        if (data == null) return null;
        if (data instanceof String) {
            return (String) data;
        }
        if (data instanceof Buffer) {
            return ((Buffer) data).toString(encoding);
        }
        return data.toString();
    }

    @Override
    public String toString() {
        if (data == null) return null;
        if (data instanceof String) {
            return (String) data;
        }
        return data.toString();
    }

    public Number toNumber() {
        if (data == null) return null;
        return (Number) data;
    }

    public JsonArray toJsonArray() {
        return toJsonArray("UTF-8");
    }

    public JsonArray toJsonArray(String encoding) {
        final JsonArray multi = new JsonArray();

        for (Reply r : (Reply[]) data) {
            switch (r.type()) {
                case '$':   // Bulk
                    multi.addString(r.toString(encoding));
                    break;
                case ':':   // Integer
                    multi.addNumber(r.toNumber());
                    break;
                case '*':   // Multi
                    multi.addArray(r.toJsonArray());
                    break;
                default:
                    throw new RuntimeException("Unknown sub message type in multi: " + r.type());
            }
        }

        return multi;
    }

    public JsonObject toJsonObject() {
        return toJsonObject("UTF-8");
    }

    public JsonObject toJsonObject(String encoding) {
        final JsonObject multi = new JsonObject();

        for (int i = 0; i < ((Reply[]) data).length; i+=2) {
            if (((Reply[]) data)[i].type() != '$') {
                throw new RuntimeException("Expected String as key type in multi: " + ((Reply[]) data)[i].type());
            }

            Reply brKey = ((Reply[]) data)[i];
            Reply brValue = ((Reply[]) data)[i+1];

            switch (brValue.type()) {
                case '$':   // Bulk
                    multi.putString(brKey.toString(encoding), brValue.toString(encoding));
                    break;
                case ':':   // Integer
                    multi.putNumber(brKey.toString(encoding), brValue.toNumber());
                    break;
                case '*':   // Multi
                    multi.putArray(brKey.toString(encoding), brValue.toJsonArray());
                    break;
                default:
                    throw new RuntimeException("Unknown sub message type in multi: " + ((Reply[]) data)[i+1].type());
            }

        }
        return multi;
    }
}
