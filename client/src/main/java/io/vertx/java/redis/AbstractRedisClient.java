package io.vertx.java.redis;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

abstract class AbstractRedisClient {

    final EventBus eventBus;
    final String redisAddress;

    AbstractRedisClient(EventBus eventBus, String redisAddress) {
        this.eventBus = eventBus;
        this.redisAddress = redisAddress;
    }

    public final void deployModule(Container container) {
        deployModule(container, "localhost", 6379, "UTF-8", false, null, 1, null);
    }

    public final void deployModule(Container container, AsyncResultHandler<String> handler) {
        deployModule(container, "localhost", 6379, "UTF-8", false, null, 1, handler);
    }

    public final void deployModule(Container container, String hostname, int port) {
        deployModule(container, hostname, port, "UTF-8", false, null, 1, null);
    }

    public final void deployModule(Container container, String hostname, int port, AsyncResultHandler<String> handler) {
        deployModule(container, hostname, port, "UTF-8", false, null, 1, handler);
    }

    public final void deployModule(Container container, String hostname, int port, int instances) {
        deployModule(container, hostname, port, "UTF-8", false, null, instances, null);
    }

    public final void deployModule(Container container, String hostname, int port, int instances, AsyncResultHandler<String> handler) {
        deployModule(container, hostname, port, "UTF-8", false, null, instances, handler);
    }

    private static AsyncResult<String> createAsyncResult(final boolean succeed, final String result, final Throwable cause) {
        return new AsyncResult<String>() {
            @Override
            public String result() {
                return result;
            }

            @Override
            public Throwable cause() {
                return cause;
            }

            @Override
            public boolean succeeded() {
                return succeed;
            }

            @Override
            public boolean failed() {
                return !succeed;
            }
        };
    }

    public final void deployModule(Container container, String hostname, int port, String encoding, boolean binary, final String auth, int instances, final AsyncResultHandler<String> handler) {
        JsonObject config = new JsonObject()
                .putString("hostname", hostname)
                .putNumber("port", port)
                .putString("address", redisAddress)
                .putString("encoding", encoding)
                .putBoolean("binary", binary);

        container.deployModule("io.vertx~mod-redis~1.1.2-SNAPSHOT", config, instances, new Handler<AsyncResult<String>>() {
            @Override
            public void handle(final AsyncResult<String> deploymentResult) {
                if (deploymentResult.succeeded()) {
                    if (auth != null) {
                        send("auth", new JsonArray().addString(auth), new Handler<Message<JsonObject>>() {
                            public void handle(Message<JsonObject> message) {
                                if ("ok".equals(message.body().getString("status"))) {
                                    if (handler != null) {
                                        handler.handle(deploymentResult);
                                    }
                                } else {
                                    if (handler != null) {
                                        handler.handle(createAsyncResult(false, deploymentResult.result(), new RuntimeException(message.body().getString("message"))));
                                    }
                                }
                            }
                        });
                    } else {
                        if (handler != null) {
                            handler.handle(deploymentResult);
                        }
                    }
                }
            }
        });
    }

    private static void serializeArg(JsonArray redisArgs, Object arg) {
        if (arg == null) {
            redisArgs.add(null);
        } else {
            if (arg instanceof String) {
                redisArgs.addString((String) arg);
            } else if (arg instanceof JsonObject) {
                redisArgs.addObject((JsonObject) arg);
            } else if (arg instanceof JsonArray) {
                redisArgs.addArray((JsonArray) arg);
            } else if (arg instanceof JsonElement) {
                redisArgs.addElement((JsonElement) arg);
            } else if (arg instanceof Number) {
                redisArgs.addNumber((Number) arg);
            } else if (arg instanceof Boolean) {
                redisArgs.addBoolean((Boolean) arg);
            } else if (arg instanceof byte[]) {
                redisArgs.addBinary((byte[]) arg);
            } else {
                throw new RuntimeException("Unsupported type: " + arg.getClass().getName());
            }
        }
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

        // handle special hash commands
        if ("MSET".equals(command) || "MSETNX".equals(command) || "HMSET".equals(command) || "ZADD".equals(command)) {
            if (totalArgs == 2 && args[1] instanceof JsonObject) {
                // there are only 2 arguments and the last  is a json object, convert the hash into a redis command
                serializeArg(redisArgs, args[0]);
                JsonObject hash = (JsonObject) args[1];
                for (String key : hash.getFieldNames()) {
                    serializeArg(redisArgs, key);
                    serializeArg(redisArgs, hash.getField(key));
                }

                // remove these 2 since they are already added to the args array
                totalArgs = 0;
            }
        }

        // serialize arguments
        for (int i = 0; i < totalArgs; i++) {
            serializeArg(redisArgs, args[i]);
        }

        json.putArray("args", redisArgs);

        if (expectResult) {
            eventBus.send(redisAddress, json, messageHandler);
        } else {
            eventBus.send(redisAddress, json);
        }
    }
}
