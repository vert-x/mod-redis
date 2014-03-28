package io.vertx.redis;

import org.vertx.java.core.buffer.Buffer;

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
}
