package com.jetdrone.vertx.mods.redis

import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import org.vertx.java.testframework.TestClientBase

class GRedisTest extends TestClientBase {

    String address
    JsonObject config
    EventBus eb

    GRedisTest() {
        this("test.my_redisclient", [address: "test.my_redisclient", host: "localhost", port: 6379, encoding: "ISO8859-1", binary: false])
    }

    GRedisTest(String address, Map config) {
        this.address = address
        this.config = new JsonObject(config)
    }

    void start() {
        super.start()
        eb = vertx.eventBus()
        container.deployModule("com.jetdrone.mod-redis-io-vTEST", config, 1, new Handler<String>() {
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

    static String makeKey() {
        return UUID.randomUUID().toString()
    }

    void assertNumber(expected, value) {
        tu.azzert(expected == value.body.getNumber("value"), "Expected: <" + expected + "> but got: <" + value.body.getNumber("value") + ">")
    }

    void assertString(expected, value) {
        tu.azzert(expected.equals(value.body.getString("value")), "Expected: <" + expected + "> but got: <" + value.body.getString("value") + ">")
    }

    void assertNull(value) {
        tu.azzert(null == value.body.getField("value"), "Expected: <null> but got: <" + value.body.getField("value") + ">")
    }

    void assertNotNull(value) {
        tu.azzert(null != value.body.getField("value"), "Expected: <!null> but got: <" + value.body.getField("value") + ">")
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

    void assertUnorderedArray(expected, value) {
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
}
