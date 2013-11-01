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

import java.util.concurrent.atomic.AtomicInteger;

import static org.vertx.testtools.VertxAssert.*;

public class LoadTester extends TestVerticle {

    private final String address = "test.redis.load";
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
    void redis(final String json, final Handler<Message<JsonObject>> handler) {
        eb.send(address, new JsonObject(json), handler);
    }

    void query(String json, final Handler<Object> handler) {
        eb.send(address, new JsonObject(json), new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                int queries = counter.decrementAndGet();

                String result = reply.body().getString("status");
                if ("error".equals(result)) {
                    fail(reply.body().getString("message"));
                } else {
                    assertEquals("ok", result);
                    handler.handle(reply.body().getField("value"));
                }

                if (queries == 0) {
                    testComplete();
                }
            }
        });
    }

    final AtomicInteger counter = new AtomicInteger();

    @Test
    public void testLoad() {

        // setup
        final Handler<Message<JsonObject>> okHandler = new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply) {
                String result = reply.body().getString("status");
                if ("error".equals(result)) {
                    fail(reply.body().getString("message"));
                } else {
                    assertEquals("ok", result);
                }
            }
        };

        redis("{\"command\":\"rpush\",\"args\":[\"list0\",0,10,12,5,3,4,8,9,13,1,6,4]}", okHandler);
        redis("{\"command\":\"sadd\",\"args\":[\"set0\",\"joe\",\"mark\",\"drew\",\"paul\",\"wario\"]}", okHandler);
        redis("{\"command\":\"sadd\",\"args\":[\"set1\",\"mark\",\"drew\",\"wario\",\"marie\"]}", okHandler);
        redis("{\"command\":\"set\",\"args\":[\"string0\",\"15\"]}", okHandler);
        redis("{\"command\":\"set\",\"args\":[\"string1\",\"7\"]}", okHandler);

        int repetitions = 1024;
        counter.set(repetitions * 7);

        // test
        for (int i = 0; i < repetitions; i++) {
            query("{\"command\":\"smembers\",\"args\":[\"set0\"]}", new Handler<Object>() {
                @Override
                public void handle(Object json) {
                    //System.out.println("smembers: " + json);
                }
            });

            query("{\"command\":\"lrange\",\"args\":[\"list0\",0,-1]}", new Handler<Object>() {
                @Override
                public void handle(Object json) {
                    //System.out.println("lrange: " + json);

                    query("{\"command\":\"mget\",\"args\":[\"string0\",\"string1\"]}", new Handler<Object>() {
                        @Override
                        public void handle(Object json) {
                            //System.out.println("mget: " + json);

                            query("{\"command\":\"scard\",\"args\":[\"set1\"]}", new Handler<Object>() {
                                @Override
                                public void handle(Object json) {
                                    //System.out.println("mget: " + json);
                                }
                            });
                        }
                    });
                }
            });

            query("{\"command\":\"lrange\",\"args\":[\"list0\",1,-3]}", new Handler<Object>() {
                @Override
                public void handle(Object json) {
                    //System.out.println("lrange: " + json);

                    query("{\"command\":\"scard\",\"args\":[\"set1\"]}", new Handler<Object>() {
                        @Override
                        public void handle(Object json) {
                            //System.out.println("scard: " + json);
                        }
                    });
                }
            });

            query("{\"command\":\"lrange\",\"args\":[\"list0\",0,-1]}", new Handler<Object>() {
                @Override
                public void handle(Object json) {
                    //System.out.println("lrange: " + json);
                }
            });
        }
    }
}
