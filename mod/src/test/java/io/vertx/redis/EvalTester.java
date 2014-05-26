package io.vertx.redis;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import static org.vertx.testtools.VertxAssert.*;

public class EvalTester extends TestVerticle {

    private final String address = "test.redis.eval";
    private EventBus eb;

    private void appReady() {
        super.start();
    }

    public void start() {
        VertxAssert.initialize(vertx);
        eb = vertx.eventBus();
        JsonObject config = new JsonObject();

        config.putString("address", address);

        container.deployModule(System.getProperty("vertx.modulename"), config, 1, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> event) {
                appReady();
            }
        });
    }

    /**
     * Helper method to allow simple Groovy closure calls and simplified maps as json messages
     * @param json message
     * @param handler response handler
     */
    void redis(final String json, final boolean fail, final Handler<Message<JsonObject>> handler) {
        eb.send(address, new JsonObject(json), new Handler<Message<JsonObject>>() {
            public void handle(Message<JsonObject> reply) {
                if (fail) {
                    assertEquals("error", reply.body().getString("status"));
                } else {
                    assertEquals("ok", reply.body().getString("status"));
                }

                handler.handle(reply);
            }
        });
    }

    @Test
    public void testEval() {

        String script = "\"return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}\"";

        redis("{\"command\": \"eval\", \"args\": [" + script + ", 2, \"key1\", \"key2\", \"first\", \"second\"]}", false, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                assertEquals(4, reply.body().getArray("value").size());
                testComplete();
            }
        });
    }

    @Test
    public void testEval2() {

        String script = "\"local items = {} for i = 1, #KEYS do items[#items + 1] = redis.call('hvals', KEYS[i]) end return items\"";

        redis("{\"command\": \"eval\", \"args\": [" + script + ", 1, \"a:1\"]}", false, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                System.out.println(reply.body().encodePrettily());
                testComplete();
            }
        });
    }
}
