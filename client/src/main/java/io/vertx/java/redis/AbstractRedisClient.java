package io.vertx.java.redis;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;

abstract class AbstractRedisClient {

    final EventBus eventBus;
    final String redisAddress;

    AbstractRedisClient(EventBus eventBus, String redisAddress) {
        this.eventBus = eventBus;
        this.redisAddress = redisAddress;
    }

    @SuppressWarnings("unchecked")
    final void send(String command, Object... args) {

        JsonObject json = new JsonObject();
        JsonArray redisArgs = new JsonArray();

        int totalArgs = 0;
        boolean expectResult = false;
        Handler<Message<JsonObject>> messageHandler = null;

        // verify if there are args
        if (args != null) {
            // verify if the last one is a Handler
            Object last = args[args.length - 1];
            totalArgs = args.length;
            if (last instanceof Handler) {
                // the caller expects a result
                expectResult = true;
                totalArgs--;
                messageHandler = (Handler<Message<JsonObject>>) last;
            }
        }

        // serialize the request
        json.putString("command", command);

        // serialize arguments
        for (int i = 0; i < totalArgs; i++) {
            if (args[i] == null) {
                redisArgs.add(null);
            } else {
                if (args[i] instanceof String) {
                    redisArgs.addString((String) args[i]);
                } else if (args[i] instanceof JsonObject) {
                    redisArgs.addObject((JsonObject) args[i]);
                } else if (args[i] instanceof JsonArray) {
                    redisArgs.addArray((JsonArray) args[i]);
                } else if (args[i] instanceof JsonElement) {
                    redisArgs.addElement((JsonElement) args[i]);
                } else if (args[i] instanceof Number) {
                    redisArgs.addNumber((Number) args[i]);
                } else if (args[i] instanceof Boolean) {
                    redisArgs.addBoolean((Boolean) args[i]);
                } else if (args[i] instanceof byte[]) {
                    redisArgs.addBinary((byte[]) args[i]);
                } else {
                    throw new RuntimeException("Unsupported type: " + args[i].getClass().getName());
                }
            }
        }

        json.putArray("args", redisArgs);

        if (expectResult) {
            eventBus.send(redisAddress, json, messageHandler);
        } else {
            eventBus.send(redisAddress, json);
        }
    }
}
