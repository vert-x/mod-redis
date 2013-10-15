package io.vertx.redis;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import java.util.List;
import java.util.UUID;

import static org.vertx.testtools.VertxAssert.*;
import static org.vertx.testtools.VertxAssert.assertEquals;

public class RedisClientTester extends TestVerticle {

    private final String address = "test.redis.client";
    private EventBus eb;

    private void appReady() {
        super.start();
    }

    public void start() {
        VertxAssert.initialize(vertx);
        eb = vertx.eventBus();
        JsonObject config = new JsonObject();

        config.putString("address", address);
        config.putString("host", "localhost");
        config.putNumber("port", 6379);
        config.putString("encoding", "ISO-8859-1");

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
    void redis(final JsonObject json, final boolean fail, final Handler<Message<JsonObject>> handler) {
        eb.send(address, json, new Handler<Message<JsonObject>>() {
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

    private static String makeKey() {
        return UUID.randomUUID().toString();
    }

    private static void assertNumberValue(Number expected, Message<JsonObject> value) {
        assertEquals(expected, value.body().getNumber("value"));
    }

    private static void assertStringValue(String expected, Message<JsonObject> value) {
        assertEquals(expected, value.body().getString("value"));
    }

    private static void assertNullValue(Message<JsonObject> value) {
        assertEquals(null, value.body().getField("value"));
    }

    private static void assertNotNullValue(Message<JsonObject> value) {
        assertNotSame(null, value.body().getField("value"));
    }

    void assertArrayValue(List<?> expected, Message<JsonObject> value) {
        JsonArray array = value.body().getArray("value");
        assertNotNull(array);
        assertEquals(expected.size(), array.size());

        for (int i = 0; i < array.size(); i++) {
            if (expected.get(i) == null) {
                assertNull(array.get(i));
            } else {
                assertEquals(expected.get(i), array.get(i));
            }
        }
    }

    private static void assertUnorderedArrayValue(List<?> expected, Message<JsonObject> value) {
        JsonArray array = value.body().getArray("value");
        assertNotNull(array);
        assertEquals(expected.size(), array.size());

        for (int i = 0; i < array.size(); i++) {
            boolean found = false;
            for (int j = 0; j < expected.size(); j++) {
                if (expected.get(j) == null && array.get(i) == null) {
                    found = true;
                    expected.remove(j);
                    break;
                }
                if (expected.get(j).equals(array.get(i))) {
                    found = true;
                    expected.remove(j);
                    break;
                }
            }

            assertTrue("Expected one of: <" + expected + "> but got: <" + array.get(i) + ">", found);
        }
    }

    private static JsonObject json(String command, Object... args) {
        JsonObject json = new JsonObject();
        json.putString("command", command);
        if (args != null) {
            JsonArray jsonArgs = new JsonArray();
            for (Object o : args) {
                jsonArgs.add(o);
            }
            json.putArray("args", jsonArgs);
        }
        return json;
    }

    @Test
    public void testGet() {
        final String nonexisting = makeKey();
        final String mykey = makeKey();

        redis(json("get", nonexisting), false, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                assertNullValue(reply);

                redis(json("set", mykey, "Hello"), false, new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> reply) {

                        redis(json("get", mykey), false, new Handler<Message<JsonObject>>() {
                            @Override
                            public void handle(Message<JsonObject> reply) {

                                assertStringValue("Hello", reply);
                                testComplete();
                            }
                        });
                    }
                });
            }
        });
    }
}
