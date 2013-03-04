package com.jetdrone.vertx.mods.redis.command;

public final class NamedKeyValue {
    public final String name;
    public final String keyName;
    public final String valueName;

    public NamedKeyValue(String name, String keyName, String valueName) {
        this.name = name;
        this.keyName = keyName;
        this.valueName = valueName;
    }
}
