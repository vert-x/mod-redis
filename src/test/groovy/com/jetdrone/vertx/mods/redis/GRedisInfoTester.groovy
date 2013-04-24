package com.jetdrone.vertx.mods.redis

import org.junit.Test
import org.vertx.java.core.AsyncResult
import org.vertx.java.core.AsyncResultHandler
import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import org.vertx.testtools.TestVerticle

import static org.vertx.testtools.VertxAssert.*

class GRedisInfoTester extends TestVerticle {

    private final String address = "test.redis.info"
    private EventBus eb

    private void appReady() {
        super.start()
    }

    void start() {
        eb = vertx.eventBus()
        JsonObject config = new JsonObject()

        config.putString("address", address)

        container.deployModule(System.getProperty("vertx.modulename"), config, 1, new AsyncResultHandler<String>() {
            @Override
            void handle(AsyncResult<String> event) {
                appReady()
            }
        })
    }

    /**
     * Helper method to allow simple Groovy closure calls and simplified maps as json messages
     * @param json message
     * @param closure response handler
     */
    void redis(Map json, boolean fail = false, Closure<Void> closure) {
        eb.send(address, new JsonObject(json), new Handler<Message<JsonObject>>() {
            public void handle(Message<JsonObject> reply) {
                if (fail) {
                    assertEquals("error", reply.body().getString("status"))
                } else {
                    assertEquals("ok", reply.body().getString("status"))
                }

                closure.call(reply)
            }
        })
    }

    @Test
    void testInfo() {
        redis([command: "info" /*, section: "server"*/]) { reply0 ->
            def server = reply0.body().getObject('value').getObject('server')
            if (server == null) {
                server = reply0.body().getObject('value')
            }
            assertTrue(server.getString('redis_version').startsWith('2.'))
            testComplete()
        }
    }
}
