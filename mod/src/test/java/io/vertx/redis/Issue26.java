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

import static org.vertx.testtools.VertxAssert.*;

public class Issue26 extends TestVerticle {

    private final String address = "test.redis.load";
    private EventBus eb;

    private void appReady() {
        super.start();
    }

    @Override
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

    @Test
    public void testLongMessages() {

        // setup
        final String testKey = "testKey";
        JsonObject command = new JsonObject();
        command.putString("command", "hmset");

        JsonArray args = new JsonArray();
        args.add(testKey);
        for (int i=0; i<1000; i++) {
            args.add("field"+i);
            args.add("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        }
        command.putArray("args", args);

        eb.send(address, command, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> result) {
                //key should be stored

                //now hgetall the key
                final JsonObject command = new JsonObject();
                JsonArray args = new JsonArray();
                args.add(testKey);
                command.putString("command", "hgetall");
                command.putArray("args", args);

                eb.send(address, command, new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> result) {
                        System.out.println(result.body().getObject("value").size());

                        eb.send(address, command, new Handler<Message<JsonObject>>() {
                            @Override
                            public void handle(Message<JsonObject> result) {
                                System.out.println(result.body().getObject("value").size());
                                testComplete();
                                //never get here - lots of exceptions output instead!!!
                            }
                        });
                    }
                });
            }
        });
    }
}
