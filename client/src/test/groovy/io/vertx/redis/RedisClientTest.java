package io.vertx.redis;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class RedisClientTest extends TestVerticle {

    private final String address = "test.redis.api.client";
    private EventBus eb;

    private RedisClient client;

    private void appReady() {
        super.start();
    }

    public void start() {
        eb = vertx.eventBus();
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
    public void testHmset() {
        testComplete();
//        client.hmset("myhash", "field1", "Hello", "field2", "World", new Handler<Message<Buffer>>() {
//            @Override
//            public void handle(Message<Buffer> event) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
    }
}
