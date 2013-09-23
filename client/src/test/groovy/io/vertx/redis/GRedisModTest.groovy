package io.vertx.redis;

import io.vertx.groovy.redis.RedisClient;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.groovy.core.eventbus.EventBus;

import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class GRedisModTest extends TestVerticle {

    private final String address = "test.redis.api.client";

    private RedisClient client;

    private void appReady() {
        super.start();
    }
    private static String makeKey() {
        return UUID.randomUUID().toString();
    }

    public void start() {
        final EventBus eb = new EventBus(vertx.eventBus());
        JsonObject config = new JsonObject();

        config.putString("address", address);

        container.deployModule("io.vertx~mod-redis~1.1.2-SNAPSHOT", config, 1, new AsyncResultHandler<String>() {
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
        client.set(key, "value1") { reply ->
            assertEquals("ok", reply.body["status"]);

            client.get(key) { reply1 ->
                assertEquals("ok", reply1.body["status"]);
                assertEquals("value1", reply1.body["value"]);
                testComplete();
            };
        };
    }
}
