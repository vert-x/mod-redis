package com.jetdrone.vertx.mods.redis

import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonArray
import org.vertx.java.core.json.JsonObject
import org.vertx.java.testframework.TestClientBase;

class GRedisClientTester extends TestClientBase {

    private final String address = "test.my_redisclient"
    private EventBus eb

    void start() {
        super.start()
        eb = vertx.eventBus()
        JsonObject config = new JsonObject()

        config.putString("address", address)
        config.putString("host", "localhost")
        config.putNumber("port", 6379)

        container.deployModule("vertx.mods.redis-DEV", config, 1, new Handler<String>() {
            public void handle(String res) {
                tu.appReady()
            }
        });
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
        });
    }

    void testAppend() {
        redis([command: "del", key: "mykey"]) { reply0 ->

            redis([command: "append", key: "mykey", value: "Hello"]) { reply1 ->
                tu.azzert(5 == reply1.body.getNumber("value"))

                redis([command: "append", key: "mykey", value: " World"]) { reply2 ->
                    tu.azzert(11 == reply2.body.getNumber("value"))

                    redis([command: "get", key: "mykey"]) { reply3 ->
                        tu.azzert("Hello World".equals(reply3.body.getString("value")))
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testAuth() {
        tu.testComplete();
    }

    void testBgrewriteaof() {
        tu.testComplete();
    }

    void testBgsave() {
        tu.testComplete();
    }

    // requires redis 2.6
    void testBitcount() {
        redis([command: "set", key: "mykey", value: "foobar"]) { reply0 ->

            redis([command: "bitcount", key: "mykey"]) { reply1 ->
                tu.azzert(26 == reply1.body.getNumber("value"));

                redis([command: "bitcount", key: "mykey", start: 0, end: 0]) { reply2 ->
                    tu.azzert(4 == reply2.body.getNumber("value"));

                    redis([command: "bitcount", key: "mykey", start: 1, end: 1]) { reply3 ->
                        tu.azzert(6 == reply3.body.getNumber("value"));
                        tu.testComplete();
                    }
                }
            }
        }
    }


    // requires redis 2.6
    void testBitOp() {
        redis([command: "set", key: "key1", value: "foobar"]) { reply0 ->

            redis([command: "set", key: "key2", value: "abcdef"]) { reply1 ->

                redis([command: "bitop", and: true, destkey: "dest", key: ["key1", "key2"]]) { reply2 ->

                    redis([command: "get", key: "dest"]) { reply3 ->
                        tu.testComplete();
                    }
                }
            }
        }
    }

    void testBlpop() {
        redis([command: "del", key: ["list1", "list2"]]) { reply0 ->

            redis([command: "rpush", key: "list1", "value": ["a", "b", "c"]]) { reply1 ->
                println(reply1.body.getNumber("value"))
                tu.azzert(3 == reply1.body.getNumber("value"));

                redis([command: "blpop", key: ["list1", "list2"], timeout: 0]) { reply2 ->
                    // TODO: handle multibulk
                    tu.testComplete();
                }
            }
        }
    }

}