package com.jetdrone.vertx.mods.redis;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.testframework.TestClientBase;

public class TestClient extends TestClientBase {

    private final String address = "test.my_redisclient";
    private EventBus eb;

    @Override
    public void start() {
        super.start();
        eb = vertx.eventBus();
        JsonObject config = new JsonObject();

        config.putString("address", address);
        config.putString("host", "localhost");
        config.putNumber("port", 6379);

        container.deployModule("vertx.mods.redis-DEV", config, 1, new Handler<String>() {
            public void handle(String res) {
                tu.appReady();
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
    }

    @SuppressWarnings("unused")
    public void testPersistor() throws Exception {

        final String random = Long.toHexString(System.nanoTime());

        JsonObject message = new JsonObject();
        message.putString("command", "set");
        message.putString("key", "myKey");
        message.putString("value", random);

        eb.send(address, message, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                tu.azzert("ok".equals(reply.body.getString("status")));

                JsonObject message = new JsonObject();
                message.putString("command", "get");
                message.putString("key", "myKey");

                eb.send(address, message, new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> reply) {
                        tu.azzert("ok".equals(reply.body.getString("status")));
                        System.out.println(reply.body.getField("value"));
                        tu.azzert(random.equals(reply.body.getString("value")));
                        tu.testComplete();
                    }
                });
            }
        });
    }
}