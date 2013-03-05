package com.jetdrone.vertx.mods.redis

import org.junit.Test

import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import org.vertx.java.testframework.TestClientBase

class GRedisPubSubTester extends TestClientBase {

    private final String pubAddress = "test.redis.pub"
    private final String subAddress = "test.redis.sub"
    private EventBus eb

    void start() {
        super.start()
        eb = vertx.eventBus()
        JsonObject pubConfig = new JsonObject()
        pubConfig.putString("address", pubAddress)
        JsonObject subConfig = new JsonObject()
        subConfig.putString("address", subAddress)

        // deploy 1 module for publishing
        container.deployModule("mod-redis-io-vTEST", pubConfig, 1, new Handler<String>() {
            public void handle(final String pubDeploymentId) {
                // deploy 1 module for subscribing
                container.deployModule("mod-redis-io-vTEST", subConfig, 1, new Handler<String>() {
                    public void handle(final String subDeploymentId) {
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
                    tu.azzert("error".equals(reply.body.getString("status")))
                } else {
                    tu.azzert("ok".equals(reply.body.getString("status")))
                }

                closure.call(reply)
            }
        })
    }

    static String makeKey() {
        return UUID.randomUUID().toString()
    }

    void assertNumber(expected, value) {
        tu.azzert(expected == value.body.getNumber("value"), "Expected: <" + expected + "> but got: <" + value.body.getNumber("value") + ">")
    }

    void assertArray(expected, value) {
        def array = value.body.getArray("value")
        tu.azzert(null != array, "Expected: <!null> but got: <" + array + ">")
        tu.azzert(expected.size() == array.size(), "Expected Length: <" + expected.size() + "> but got: <" + array.size() + ">")

        for (int i = 0; i < array.size(); i++) {
            if (expected[i] == null) {
                tu.azzert(null == array.get(i), "Expected: <null> but got: <" + array.get(i) + ">")
            } else {
                tu.azzert(expected[i].equals(array.get(i)), "Expected at " + i + ": <" + expected[i] + "> but got: <" + array.get(i) + ">")
            }
        }
    }

    void appReady() {
        tu.appReady();
    }

    void testComplete() {
        tu.testComplete()
    }

    @Test
    void testPubSub() {
        def message = makeKey()

        // register a handler for the incoming message
        eb.registerHandler("${subAddress}.ch1", new Handler<Message<JsonObject>>() {
            @Override
            void handle(Message<JsonObject> received) {
                def value = received.body.getField('value')
                tu.azzert('ch1'.equals(value.getField('channel')))
                tu.azzert(message.equals(value.getField('message')))
                testComplete()
            }
        });

        // on sub address subscribe to channel ch1
        redis(subAddress, [command: 'subscribe', channel: 'ch1']) { subscribe ->
            assertArray(['subscribe', 'ch1', 1], subscribe)

            // on pub address publish a message
            redis(pubAddress, [command: 'publish', channel: 'ch1', message: message]) { publish ->
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
        redis(subAddress, [command: 'psubscribe', pattern: 'news.*']) { subscribe ->
            assertArray(['psubscribe', 'news.*', 1], subscribe)

            // on pub address publish a message to news.wold
            redis(pubAddress, [command: 'publish', channel: 'news.world', message: worldNews]) { publish ->
                assertNumber(1, publish)
            }
            // on pub address publish a message to news.technology
            redis(pubAddress, [command: 'publish', channel: 'news.technology', message: technologyNews]) { publish ->
                assertNumber(1, publish)
            }
        }
    }
}
