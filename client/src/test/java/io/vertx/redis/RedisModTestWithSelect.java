package io.vertx.redis;

import io.vertx.java.redis.RedisClient;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import java.util.UUID;

import static org.vertx.testtools.VertxAssert.*;

public class RedisModTestWithSelect extends TestVerticle {

    private final String address = "test.redis.api.client";
    private EventBus eb;

    private RedisClient client;

    private void appReady() {
        super.start();
    }
    private static String makeKey() {
        return UUID.randomUUID().toString();
    }

    public void start() {
        VertxAssert.initialize(vertx);
        eb = vertx.eventBus();

        client = new RedisClient(eb, address);

        client.deployModule(container, "localhost", 6379, "UTF-8", false, null, 1, 1, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> event) {
                if (event.failed()) {
                    fail(event.cause().getMessage());
                } else {
                    client = new RedisClient(eb, address);
                    appReady();
                }
            }
        });
    }

    @Test
    public void testSetGet() {
        final String key = makeKey();
        client.set(key, "value1", new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                assertEquals("ok", reply.body().getString("status"));

                client.get(key, new Handler<Message<JsonObject>>() {

                    @Override
                    public void handle(Message<JsonObject> reply) {
                        assertEquals("ok", reply.body().getString("status"));
                        assertEquals("value1", reply.body().getString("value"));
                        testComplete();
                    }
                });
            }
        });
    }
}
