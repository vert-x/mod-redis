package com.jetdrone.vertx.mods.redis

import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonArray
import org.vertx.java.core.json.JsonObject
import org.vertx.java.testframework.TestClientBase

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
        tu.testComplete()
    }

    void testBgrewriteaof() {
        tu.testComplete()
    }

    void testBgsave() {
        tu.testComplete()
    }

    void testBitcount() {
        redis([command: "set", key: "mykey", value: "foobar"]) { reply0 ->

            redis([command: "bitcount", key: "mykey"]) { reply1 ->
                tu.azzert(26 == reply1.body.getNumber("value"))

                redis([command: "bitcount", key: "mykey", start: 0, end: 0]) { reply2 ->
                    tu.azzert(4 == reply2.body.getNumber("value"))

                    redis([command: "bitcount", key: "mykey", start: 1, end: 1]) { reply3 ->
                        tu.azzert(6 == reply3.body.getNumber("value"))
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testBitOp() {
        redis([command: "set", key: "key1", value: "foobar"]) { reply0 ->

            redis([command: "set", key: "key2", value: "abcdef"]) { reply1 ->

                redis([command: "bitop", and: true, destkey: "dest", key: ["key1", "key2"]]) { reply2 ->

                    redis([command: "get", key: "dest"]) { reply3 ->
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testBlpop() {
        redis([command: "del", key: ["list1", "list2"]]) { reply0 ->

            redis([command: "rpush", key: "list1", "value": ["a", "b", "c"]]) { reply1 ->
                println(reply1.body.getNumber("value"))
                tu.azzert(3 == reply1.body.getNumber("value"))

                redis([command: "blpop", key: ["list1", "list2"], timeout: 0]) { reply2 ->
                    // TODO: handle multibulk
                    //"list1", "a"
                    tu.testComplete()
                }
            }
        }
    }

    void testBrpop() {
        redis([command: "del", key: ["list1", "list2"]]) { reply0 ->

            redis([command: "rpush", key: "list1", "value": ["a", "b", "c"]]) { reply1 ->
                println(reply1.body.getNumber("value"))
                tu.azzert(3 == reply1.body.getNumber("value"))

                redis([command: "brpop", key: ["list1", "list2"], timeout: 0]) { reply2 ->
                    // TODO: handle multibulk
                    //"list1", "c"
                    tu.testComplete()
                }
            }
        }
    }
    
    void testBrpoplpush() {
        tu.testComplete()
    }

    void testClientKill() {
        tu.testComplete()
    }

    void testClientList() {
        tu.testComplete()
    }

    void testClientGetname() {
        tu.testComplete()
    }

    void testClientSetname() {
        tu.testComplete()
    }

    void testConfigGet() {
        redis([command: "config get", parameter: "*max-*-entries*"]) { reply0 ->
            // TODO: handle multibulk
            // 1) "hash-max-zipmap-entries"
            // 2) "512"
            // 3) "list-max-ziplist-entries"
            // 4) "512"
            // 5) "set-max-intset-entries"
            // 6) "512"
            tu.testComplete()
        }
    }

    void testConfigSet() {
        tu.testComplete()
    }

    void testConfigResetstat() {
        tu.testComplete()
    }

    void testDbsize() {
        tu.testComplete()
    }

    void testDebugObject() {
        tu.testComplete()
    }

    void testDebugSegfault() {
        tu.testComplete()
    }

    void testDecr() {
        redis([command: "set", key: "mykey", value: "10"]) { reply0 ->
            redis([command: "decr", key: "mykey"]) { reply1 ->
                tu.azzert(9 == reply1.body.getNumber("value"))
                tu.testComplete()
            }
        }
    }

    void testDecrby() {
        redis([command: "set", key: "mykey", value: "10"]) { reply0 ->
            redis([command: "decrby", key: "mykey", decrement: 5]) { reply1 ->
                tu.azzert(5 == reply1.body.getNumber("value"))
                tu.testComplete()
            }
        }
    }

    void testDel() {
        redis([command: "set", key: "key1", value: "Hello"]) { reply0 ->
            redis([command: "set", key: "key2", value: "World"]) { reply1 ->
                redis([command: "del", key: ["key1", "key2", "key3"]]) { reply2 ->
                    tu.azzert(2 == reply2.body.getNumber("value"))
                    tu.testComplete()
                }
            }
        }
    }

    void testDiscard() {
        tu.testComplete()
    }

    void testDump() {
        redis([command: "set", key: "mykey", value: 10]) { reply0 ->
            redis([command: "dump", key: "mykey"]) { reply1 ->
                tu.azzert("\\u0000\\xC0\\n\\u0006\\u0000\\xF8r?\\xC5\\xFB\\xFB_(".equals(reply1.body.getString("value")))
                tu.testComplete()
            }
        }
    }

    void testEcho() {
        redis([command: "echo", message: "Hello World!"]) { reply0 ->
            tu.azzert("Hello World!".equals(reply0.body.getString("value")))
            tu.testComplete()
        }
    }

    void testEval() {
        redis([command: "eval", script: "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}", numkeys: 2, key: ["key1", "key2"], arg: ["first", "second"]]) { reply0 ->
            // TODO: handle multibulk
            // 1) "key1"
            // 2) "key2"
            // 3) "first"
            // 4) "second"
            tu.testComplete()
        }
    }

    void testEvalsha() {
        tu.testComplete()
    }

    void testExec() {
        tu.testComplete()
    }

    void testExists() {
        redis([command: "set", key: "key1", value: "Hello"]) {reply0 ->
            redis([command: "exists", key: "key1"]) {reply1 ->
                tu.azzert(1 == reply1.body.getNumber("value"))

                redis([command: "exists", key: "key2"]) {reply2 ->
                    tu.azzert(0 == reply2.body.getNumber("value"))
                    tu.testComplete()
                }
            }
        }
    }

    void testExpire() {
        redis([command: "set", key: "mykey", value: "Hello"]) { reply0 ->
            redis([command: "expire", key: "mykey", seconds: 10]) { reply1 ->
                tu.azzert(1 == reply1.body.getNumber("value"))

                redis([command: "ttl", key: "mykey"]) { reply2 ->
                    tu.azzert(10 == reply2.body.getNumber("value"))

                    redis([command: "set", key: "mykey", value: "Hello World"]) { reply3 ->
                        redis([command: "ttl", key: "mykey"]) { reply4 ->
                            tu.azzert(-1 == reply4.body.getNumber("value"))
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testExpireat() {
        redis([command: "set", key: "mykey", value: "Hello"]) { reply0 ->
            redis([command: "exists", key: "mykey"]) { reply1 ->
                tu.azzert(1 == reply1.body.getNumber("value"))

                redis([command: "expireat", key: "mykey", timestamp: 1293840000]) { reply2 ->
                    tu.azzert(1 == reply2.body.getNumber("value"))

                    redis([command: "exists", key: "mykey"]) { reply3 ->
                        tu.azzert(0 == reply3.body.getNumber("value"))
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testFlushall() {
        tu.testComplete()
    }

    void testFlushdb() {
        tu.testComplete()
    }

    void testGet() {
        redis([command: "get", key: "nonexisting"]) { reply0 ->
            // TODO: verify nil
            redis([command: "set", key: "mykey", value: "Hello"]) { reply1 ->
                redis([command: "get", key: "mykey"]) { reply2 ->
                    tu.azzert("Hello".equals(reply2.body.getString("value")))
                    tu.testComplete()
                }
            }
        }
    }

    void testGetbit() {
        redis([command: "setbit", key: "mykey", offset: 7, value: 1]) { reply0 ->
            tu.azzert(0 == reply0.body.getNumber("value"))

            redis([command: "getbit", key: "mykey", offset: 0]) { reply1 ->
                tu.azzert(0 == reply1.body.getNumber("value"))

                redis([command: "getbit", key: "mykey", offset: 7]) { reply2 ->
                    tu.azzert(1 == reply2.body.getNumber("value"))

                    redis([command: "getbit", key: "mykey", offset: 100]) { reply3 ->
                        tu.azzert(0 == reply3.body.getNumber("value"))
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testGetrange() {
        redis([command: "set", key: "mykey", value: "This is a string"]) { reply0 ->
            redis([command: "getrange", key: "mykey", start:  0, end: 3]) { reply1 ->
                tu.azzert("This".equals(reply1.body.getString("value")))

                redis([command: "getrange", key: "mykey", start:  -3, end: -1]) { reply2 ->
                    tu.azzert("ing".equals(reply2.body.getString("value")))

                    redis([command: "getrange", key: "mykey", start:  0, end: -1]) { reply3 ->
                        tu.azzert("This is a string".equals(reply3.body.getString("value")))

                        redis([command: "getrange", key: "mykey", start: 10, end: 100]) { reply4 ->
                            tu.azzert("string".equals(reply4.body.getString("value")))
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testGetset() {
        redis([command: "incr", key: "mycounter"]) { reply0 ->
            tu.azzert(1 == reply0.body.getNumber("value"))

            redis([command: "getset", key: "mycounter", value: "0"]) { reply1 ->
                tu.azzert("1".equals(reply1.body.getString("value")))

                redis([command: "get", key: "mycounter"]) { reply2 ->
                    tu.azzert("0".equals(reply2.body.getString("value")))
                    tu.testComplete()
                }
            }
        }
    }

    void testHdel() {
        redis([command: "hset", key: "myhash", field: "field1", value: "foo"]) { reply0 ->
//            tu.azzert(1 == reply0.body.getNumber("value"))

            redis([command: "hdel", key: "myhash", field: "field1"]) { reply1 ->
                tu.azzert(1 == reply1.body.getNumber("value"))

                redis([command: "hdel", key: "myhash", field: "field2"]) { reply2 ->
                    tu.azzert(0 == reply2.body.getNumber("value"))
                    tu.testComplete()
                }
            }
        }
    }

    void testHexists() {
        redis([command: "hset", key: "myhash", field: "field1", value: "foo"]) { reply0 ->
//            tu.azzert(1 == reply0.body.getNumber("value"))

            redis([command: "hexists", key: "myhash", field: "field1"]) { reply1 ->
                tu.azzert(1 == reply1.body.getNumber("value"))

                redis([command: "hexists", key: "myhash", field: "field2"]) { reply2 ->
                    tu.azzert(0 == reply2.body.getNumber("value"))
                    tu.testComplete()
                }
            }
        }
    }

    void testHget() {
        redis([command: "hset", key: "myhash", field: "field1", value: "foo"]) { reply0 ->
//            tu.azzert(1 == reply0.body.getNumber("value"))

            redis([command: "hget", key: "myhash", field: "field1"]) { reply1 ->
                tu.azzert("foo".equals(reply1.body.getString("value")))

                redis([command: "hget", key: "myhash", field: "field2"]) { reply2 ->
                    // TODO: assert nil
                    tu.testComplete()
                }
            }
        }
    }

    void testHgetall() {
        redis([command: "hset", key: "myhash", field: "field1", value: "Hello"]) { reply0 ->
//            tu.azzert(1 == reply0.body.getNumber("value"))

            redis([command: "hset", key: "myhash", field: "field2", value: "World"]) { reply1 ->
                tu.azzert(1 == reply1.body.getNumber("value"))

                redis([command: "hgetall", key: "myhash"]) { reply2 ->
                    // TODO: bulk reply
                    // 1) "field1"
                    // 2) "Hello"
                    // 3) "field2"
                    // 4) "World"
                    tu.testComplete()
                }
            }
        }
    }

    void testHincrby() {
        redis([command: "hset", key: "myhash", field: "field", value: 5]) { reply0 ->
            tu.azzert(1 == reply0.body.getNumber("value"))

            redis([command: "hincrby", key: "myhash", field: "field", increment: 1]) { reply1 ->
                tu.azzert(6 == reply1.body.getNumber("value"))

                redis([command: "hincrby", key: "myhash", field: "field", increment: -1]) { reply2 ->
                    tu.azzert(5 == reply2.body.getNumber("value"))

                    redis([command: "hincrby", key: "myhash", field: "field", increment: -10]) { reply3 ->
                        tu.azzert(-5 == reply3.body.getNumber("value"))
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testHIncrbyfloat() {
        redis([command: "hset", key: "mykey", field: "field", value: 10.50]) { reply0 ->
            tu.azzert(1 == reply0.body.getNumber("value"))

            redis([command: "hincrbyfloat", key: "mykey", field: "field", increment: 0.1]) { reply1 ->
                tu.azzert("10.6".equals(reply1.body.getNumber("value")))

                redis([command: "hset", key: "mykey", field: "field", value: 5.0e3]) { reply2 ->
                    tu.azzert(0 == reply2.body.getNumber("value"))

                    redis([command: "hincrbyfloat", key: "mykey", field: "field", increment: 2.0e2]) { reply3 ->
                        tu.azzert("5200".equals(reply3.body.getNumber("value")))
                        tu.testComplete()
                    }
                }
            }
        }
    }
}