package io.vertx.redis.command;

public final class KeyValue {

    public final String keyName;
    public final String valueName;
    public final String pairName;

    public KeyValue(String keyName, String valueName, String pairName) {
        this.keyName = keyName;
        this.valueName = valueName;
        this.pairName = pairName;
    }
}
