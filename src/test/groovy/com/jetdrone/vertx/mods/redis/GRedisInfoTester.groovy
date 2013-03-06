package com.jetdrone.vertx.mods.redis

import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import org.vertx.java.testframework.TestClientBase

class GRedisInfoTester extends TestClientBase {

    private final String address = "test.redis.info"
    private EventBus eb

    void start() {
        super.start()
        eb = vertx.eventBus()
        JsonObject config = new JsonObject()

        config.putString("address", address)

        container.deployModule("mod-redis-io-vTEST", config, 1, new Handler<String>() {
            public void handle(String res) {
                tu.appReady()
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
                    tu.azzert("error".equals(reply.body.getString("status")))
                } else {
                    tu.azzert("ok".equals(reply.body.getString("status")))
                }

                closure.call(reply)
            }
        })
    }

    private static String makeKey() {
        return UUID.randomUUID().toString()
    }

    private void assertNumber(expected, value) {
        tu.azzert(expected == value.body.getNumber("value"), "Expected: <" + expected + "> but got: <" + value.body.getNumber("value") + ">")
    }

    private void assertString(expected, value) {
        tu.azzert(expected.equals(value.body.getString("value")), "Expected: <" + expected + "> but got: <" + value.body.getString("value") + ">")
    }

    private void assertNull(value) {
        tu.azzert(null == value.body.getField("value"), "Expected: <null> but got: <" + value.body.getField("value") + ">")
    }

    private void assertNotNull(value) {
        tu.azzert(null != value.body.getField("value"), "Expected: <!null> but got: <" + value.body.getField("value") + ">")
    }

    private void assertArray(expected, value) {
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

    private void assertUnorderedArray(expected, value) {
        def array = value.body.getArray("value")
        tu.azzert(null != array, "Expected: <!null> but got: <" + array + ">")
        tu.azzert(expected.size() == array.size(), "Expected Length: <" + expected.size() + "> but got: <" + array.size() + ">")

        for (int i = 0; i < array.size(); i++) {
            def found = false
            for (int j = 0; j < expected.size(); j++) {
                if (expected[j] == null && array.get(i) == null) {
                    found = true
                    expected.remove(j)
                    break
                }
                if (expected[j].equals(array.get(i))) {
                    found = true
                    expected.remove(j)
                    break
                }
            }

            tu.azzert(found, "Expected one of: <" + expected + "> but got: <" + array.get(i) + ">")
        }
    }

    void testInfo() {
        redis([command: "info" /*, section: "server"*/]) { reply0 ->
            def server = reply0.body.getObject('value').getObject('server')
            tu.azzert(server.getString('redis_version').startsWith('2.'))
            tu.testComplete()
        }
    }
}
