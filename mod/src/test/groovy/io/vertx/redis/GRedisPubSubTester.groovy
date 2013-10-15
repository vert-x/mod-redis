package io.vertx.redis

import org.junit.Test
import org.vertx.java.core.AsyncResult
import org.vertx.java.core.AsyncResultHandler
import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import org.vertx.testtools.TestVerticle
import static org.vertx.testtools.VertxAssert.*

class GRedisPubSubTester extends TestVerticle {

    private final String pubAddress = "test.redis.pub"
    private final String subAddress = "test.redis.sub"
    private EventBus eb

    private void appReady() {
        super.start()
    }

    void start() {
        initialize(vertx)
        eb = vertx.eventBus()
        JsonObject pubConfig = new JsonObject()
        pubConfig.putString("address", pubAddress)
        JsonObject subConfig = new JsonObject()
        subConfig.putString("address", subAddress)

        // deploy 1 module for publishing
        container.deployModule(System.getProperty("vertx.modulename"), pubConfig, 1, new AsyncResultHandler<String>() {
            @Override
            void handle(AsyncResult<String> event1) {
                // deploy 1 module for subscribing
                container.deployModule(System.getProperty("vertx.modulename"), subConfig, 1, new AsyncResultHandler<String>() {
                    @Override
                    void handle(AsyncResult<String> event2) {
                        appReady()
                    }
                })
            }
        })
    }

    /**
     * Helper method to allow simple Groovy closure calls and simplified maps as json messages
     * @param json message
     * @param closure response handler
     */
    void redis(String address, Map json, boolean fail = false, Closure<Void> closure) {
        eb.send(address, new JsonObject(json), new Handler<Message<JsonObject>>() {
            public void handle(Message<JsonObject> reply) {
                if (fail) {
                    assertEquals("error", reply.body.getString("status"))
                } else {
                    assertEquals("ok", reply.body.getString("status"))
                }

                closure.call(reply)
            }
        })
    }

    static String makeKey() {
        return UUID.randomUUID().toString()
    }

    void assertNumber(expected, value) {
        assertEquals(expected, value.body.getNumber("value"))
    }

    void assertArray(expected, value) {
        def array = value.body.getArray("value")
        assertNotNull(array)
        assertEquals(expected.size(), array.size())

        for (int i = 0; i < array.size(); i++) {
            if (expected[i] == null) {
                assertNull(array.get(i))
            } else {
                assertEquals(expected[i], array.get(i))
            }
        }
    }

    @Test
    void testPubSub() {
        def message = makeKey()

        // register a handler for the incoming message
        eb.registerHandler("${subAddress}.ch1", new Handler<Message<JsonObject>>() {
            @Override
            void handle(Message<JsonObject> received) {
                def value = received.body.getField('value')
                assertEquals('ch1', value.getField('channel'))
                assertEquals(message, value.getField('message'))
                testComplete()
            }
        });

        // on sub address subscribe to channel ch1
        redis(subAddress, [command: 'subscribe', args: ['ch1']]) { subscribe ->
            assertArray(['subscribe', 'ch1', 1], subscribe)

            // on pub address publish a message
            redis(pubAddress, [command: 'publish', args: ['ch1', message]]) { publish ->
                assertNumber(1, publish)
            }
        }
    }

    @Test
    void testPubSubPattern() {

        def worldNews = 'hello world'
        def technologyNews = 'hello vertx'
        def inbox = []

        // register a handler for all incoming messages
        eb.registerHandler("${subAddress}.news.*", new Handler<Message<JsonObject>>() {
            @Override
            void handle(Message<JsonObject> received) {
                inbox.add(received.body.getField('value'))

                if (inbox.size() == 2) {
                    if ((worldNews.equals(inbox[0].getField('message')) && technologyNews.equals(inbox[1].getField('message'))) || (worldNews.equals(inbox[1].getField('message')) && technologyNews.equals(inbox[0].getField('message')))) {
                        testComplete()
                    }
                }
            }
        });

        // on sub address subscribe to channels news.*
        redis(subAddress, [command: 'psubscribe', args: ['news.*']]) { subscribe ->
            assertArray(['psubscribe', 'news.*', 1], subscribe)

            // on pub address publish a message to news.wold
            redis(pubAddress, [command: 'publish', args: ['news.world', worldNews]]) { publish ->
                assertNumber(1, publish)
            }
            // on pub address publish a message to news.technology
            redis(pubAddress, [command: 'publish', args: ['news.technology', technologyNews]]) { publish ->
                assertNumber(1, publish)
            }
        }
    }

    private void lateJoinHelper(String message) {
        JsonObject subConfig2 = new JsonObject()
        subConfig2.putString("address", "test.redis.sub2")

        container.deployModule(System.getProperty("vertx.modulename"), subConfig2, 1, new AsyncResultHandler<String>() {
            public void handle(final AsyncResult<String> subDeploymentId) {
                // on sub address subscribe to channel ch2
                redis("test.redis.sub2", [command: 'subscribe', args: ['ch2']]) { subscribe ->
                    assertArray(['subscribe', 'ch2', 1], subscribe)

                    // on pub address publish a message
                    redis(pubAddress, [command: 'publish', args: ['ch2', message]]) { publish ->
                        assertNumber(2, publish)
                    }
                }
            }
        })
    }

    @Test
    void testLateJoin() {
        def message = makeKey()

        // register a handler for the incoming message
        eb.registerHandler("${subAddress}.ch2", new Handler<Message<JsonObject>>() {
            @Override
            void handle(Message<JsonObject> received) {
                def value = received.body.getField('value')
                assertEquals('ch2', value.getField('channel'))
                assertEquals(message, value.getField('message'))
                testComplete()
            }
        });

        // on sub address subscribe to channel ch2
        redis(subAddress, [command: 'subscribe', args: ['ch2']]) { subscribe ->
            assertArray(['subscribe', 'ch2', 1], subscribe)

            // deploy a new sub
            lateJoinHelper(message)
        }
    }
}
