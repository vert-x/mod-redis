package com.jetdrone.vertx.mods.redis

import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.EventBus
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import org.vertx.java.testframework.TestClientBase

@SuppressWarnings("GroovyUnusedDeclaration")
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

    void testAppend() {
        def key = makeKey()

        redis([command: "del", key: key]) { reply0 ->

            redis([command: "append", key: key, value: "Hello"]) { reply1 ->
                assertNumber(5, reply1)

                redis([command: "append", key: key, value: " World"]) { reply2 ->
                    assertNumber(11, reply2)

                    redis([command: "get", key: key]) { reply3 ->
                        assertString("Hello World", reply3)
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
        def key = makeKey()

        redis([command: "set", key: key, value: "foobar"]) { reply0 ->

            redis([command: "bitcount", key: key]) { reply1 ->
                assertNumber(26, reply1)

                redis([command: "bitcount", key: key, start: 0, end: 0]) { reply2 ->
                    assertNumber(4, reply2)

                    redis([command: "bitcount", key: key, start: 1, end: 1]) { reply3 ->
                        assertNumber(6, reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testBitOp() {
        def key1 = makeKey()
        def key2 = makeKey()
        def destkey = makeKey()

        redis([command: "set", key: key1, value: "foobar"]) { reply0 ->

            redis([command: "set", key: key2, value: "abcdef"]) { reply1 ->

                redis([command: "bitop", and: true, destkey: destkey, key: [key1, key2]]) { reply2 ->

                    redis([command: "get", key: destkey]) { reply3 ->
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testBlpop() {
        def list1 = makeKey()
        def list2 = makeKey()

        redis([command: "del", key: [list1, list2]]) { reply0 ->

            redis([command: "rpush", key: list1, value: ["a", "b", "c"]]) { reply1 ->
                assertNumber(3, reply1)

                redis([command: "blpop", key: [list1, list2], timeout: 0]) { reply2 ->
                    assertArray([list1, "a"], reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testBrpop() {
        def list1 = makeKey()
        def list2 = makeKey()

        redis([command: "del", key: [list1, list2]]) { reply0 ->

            redis([command: "rpush", key: list1, value: ["a", "b", "c"]]) { reply1 ->
                assertNumber(3, reply1)

                redis([command: "brpop", key: [list1, list2], timeout: 0]) { reply2 ->
                    assertArray([list1, "c"], reply2)
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
        def mykey = makeKey()

        redis([command: "set", key: mykey, value: "10"]) { reply0 ->
            redis([command: "decr", key: mykey]) { reply1 ->
                assertNumber(9, reply1)
                tu.testComplete()
            }
        }
    }

    void testDecrby() {
        def mykey = makeKey()

        redis([command: "set", key: mykey, value: "10"]) { reply0 ->
            redis([command: "decrby", key: mykey, decrement: 5]) { reply1 ->
                assertNumber(5, reply1)
                tu.testComplete()
            }
        }
    }

    void testDel() {
        def key1 = makeKey()
        def key2 = makeKey()
        def key3 = makeKey()

        redis([command: "set", key: key1, value: "Hello"]) { reply0 ->
            redis([command: "set", key: key2, value: "World"]) { reply1 ->
                redis([command: "del", key: [key1, key2, key3]]) { reply2 ->
                    assertNumber(2, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testDiscard() {
        tu.testComplete()
    }

    void testDump() {
        def mykey = makeKey()

        redis([command: "set", key: mykey, value: 10]) { reply0 ->
            redis([command: "dump", key: mykey]) { reply1 ->
                assertString("\\u0000\\xC0\\n\\u0006\\u0000\\xF8r?\\xC5\\xFB\\xFB_(", reply1)
                tu.testComplete()
            }
        }
    }

    void testEcho() {
        redis([command: "echo", message: "Hello World!"]) { reply0 ->
            assertString("Hello World!", reply0)
            tu.testComplete()
        }
    }

    void testEval() {
        def key1 = makeKey()
        def key2 = makeKey()

        redis([command: "eval", script: "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}", numkeys: 2, key: [key1, key2], arg: ["first", "second"]]) { reply0 ->
            def array = reply0.body.getArray("value")

            tu.azzert(4 == array.size())
            tu.azzert(key1.equals(array.get(0)))
            tu.azzert(key2.equals(array.get(1)))
            tu.azzert("first".equals(array.get(2)))
            tu.azzert("second".equals(array.get(2)))
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
        def key1 = makeKey()
        def key2 = makeKey()

        redis([command: "set", key: key1, value: "Hello"]) {reply0 ->
            redis([command: "exists", key: key1]) {reply1 ->
                assertNumber(1, reply1)

                redis([command: "exists", key: key2]) {reply2 ->
                    assertNumber(0, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testExpire() {
        def mykey = makeKey()

        redis([command: "set", key: mykey, value: "Hello"]) { reply0 ->
            redis([command: "expire", key: mykey, seconds: 10]) { reply1 ->
                assertNumber(1, reply1)

                redis([command: "ttl", key: mykey]) { reply2 ->
                    assertNumber(10, reply2)

                    redis([command: "set", key: mykey, value: "Hello World"]) { reply3 ->
                        redis([command: "ttl", key: mykey]) { reply4 ->
                            assertNumber(-1, reply4)
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testExpireat() {
        def mykey = makeKey()

        redis([command: "set", key: mykey, value: "Hello"]) { reply0 ->
            redis([command: "exists", key: mykey]) { reply1 ->
                assertNumber(1, reply1)

                redis([command: "expireat", key: mykey, timestamp: 1293840000]) { reply2 ->
                    assertNumber(1, reply2)

                    redis([command: "exists", key: mykey]) { reply3 ->
                        assertNumber(0, reply3)
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
        def nonexisting = makeKey()
        def mykey = makeKey()

        redis([command: "get", key: nonexisting]) { reply0 ->
            assertNull(reply0)

            redis([command: "set", key: mykey, value: "Hello"]) { reply1 ->
                redis([command: "get", key: mykey]) { reply2 ->
                    assertString("Hello", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testGetbit() {
        def mykey = makeKey()

        redis([command: "setbit", key: mykey, offset: 7, value: 1]) { reply0 ->
            assertNumber(0, reply0)

            redis([command: "getbit", key: mykey, offset: 0]) { reply1 ->
                assertNumber(0, reply1)

                redis([command: "getbit", key: mykey, offset: 7]) { reply2 ->
                    assertNumber(1, reply2)

                    redis([command: "getbit", key: mykey, offset: 100]) { reply3 ->
                        assertNumber(0, reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testGetrange() {
        def mykey = makeKey()

        redis([command: "set", key: mykey, value: "This is a string"]) { reply0 ->
            redis([command: "getrange", key: mykey, start:  0, end: 3]) { reply1 ->
                assertString("This", reply1)

                redis([command: "getrange", key: mykey, start:  -3, end: -1]) { reply2 ->
                    assertString("ing", reply2)

                    redis([command: "getrange", key: mykey, start:  0, end: -1]) { reply3 ->
                        assertString("This is a string", reply3)

                        redis([command: "getrange", key: mykey, start: 10, end: 100]) { reply4 ->
                            assertString("string", reply4)
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testGetset() {
        def mycounter = makeKey()

        redis([command: "incr", key: mycounter]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "getset", key: mycounter, value: "0"]) { reply1 ->
                assertString("1", reply1)

                redis([command: "get", key: mycounter]) { reply2 ->
                    assertString("0", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testHdel() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field1", value: "foo"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hdel", key: myhash, field: "field1"]) { reply1 ->
                assertNumber(1, reply1)

                redis([command: "hdel", key: myhash, field: "field2"]) { reply2 ->
                    assertNumber(0, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testHexists() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field1", value: "foo"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hexists", key: myhash, field: "field1"]) { reply1 ->
                assertNumber(1, reply1)

                redis([command: "hexists", key: myhash, field: "field2"]) { reply2 ->
                    assertNumber(0, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testHget() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field1", value: "foo"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hget", key: myhash, field: "field1"]) { reply1 ->
                assertString("foo", reply1)

                redis([command: "hget", key: myhash, field: "field2"]) { reply2 ->
                    assertNull(reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testHgetall() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field1", value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hset", key: myhash, field: "field2", value: "World"]) { reply1 ->
                assertNumber(1, reply1)

                redis([command: "hgetall", key: myhash]) { reply2 ->
                    assertArray(["field1", "Hello", "field2", "World"], reply2)
                    tu.testComplete()
               }
            }
        }
    }

    void testHincrby() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field", value: 5]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hincrby", key: myhash, field: "field", increment: 1]) { reply1 ->
                assertNumber(6, reply1)

                redis([command: "hincrby", key: myhash, field: "field", increment: -1]) { reply2 ->
                    assertNumber(5, reply2)

                    redis([command: "hincrby", key: myhash, field: "field", increment: -10]) { reply3 ->
                        assertNumber(-5, reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testHIncrbyfloat() {
        def mykey = makeKey()

        redis([command: "hset", key: mykey, field: "field", value: 10.50]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hincrbyfloat", key: mykey, field: "field", increment: 0.1]) { reply1 ->
                assertNumber(10.6, reply1)

                redis([command: "hset", key: mykey, field: "field", value: 5.0e3]) { reply2 ->
                    assertNumber(0, reply2)

                    redis([command: "hincrbyfloat", key: mykey, field: "field", increment: 2.0e2]) { reply3 ->
                        assertNumber(5200, reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testHkeys() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field1", value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hset", key: myhash, field: "field2", value: "World"]) { reply1 ->
                assertNumber(1, reply1)

                redis([command: "hkeys", key: myhash]) { reply2 ->
                    assertArray(["field1", "field2"], reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testHlen() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field1", value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hset", key: myhash, field: "field2", value: "World"]) { reply1 ->
                assertNumber(1, reply1)

                redis([command: "hlen", key: myhash]) { reply2 ->
                    assertNumber(2, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testHmget() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field1", value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hset", key: myhash, field: "field2", value: "World"]) { reply1 ->
                assertNumber(1, reply1)

                redis([command: "hmget", key: myhash, field: ["field1", "field2", "nofield"]]) { reply2 ->
                    assertArray(["Hello", "World", null], reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testHmset() {
        def myhash = makeKey()

        redis([command: "hmset", key: myhash, fieldvalues: [[field: "field1", value: "Hello"],[field: "field2", value: "World"]]]) { reply0 ->
            redis([command: "hget", key: myhash, field: "field1"]) { reply1 ->
                assertString("Hello", reply1)
                redis([command: "hget", key: myhash, field: "field2"]) { reply2 ->
                    assertString("World", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testHset() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field1", value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hget", key: myhash, field: "field1"]) { reply1 ->
                assertString("Hello", reply1)
                tu.testComplete()
            }
        }
    }

    void testHsetnx() {
        def myhash = makeKey()

        redis([command: "hsetnx", key: myhash, field: "field", value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hsetnx", key: myhash, field: "field", value: "World"]) { reply1 ->
                assertNumber(0, reply1)

                redis([command: "hget", key: myhash, field: "field"]) { reply2 ->
                    assertString("Hello", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testHvals() {
        def myhash = makeKey()

        redis([command: "hset", key: myhash, field: "field1", value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "hset", key: myhash, field: "field2", value: "World"]) { reply1 ->
                assertNumber(1, reply1)

                redis([command: "hvals", key: myhash]) { reply2 ->
                    assertArray(["Hello", "World"], reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testIncr() {
        def mykey = makeKey()

        redis([command: "set", key: mykey, value: "10"]) { reply0 ->
            redis([command: "incr", key: mykey]) { reply1 ->
                assertNumber(11, reply1)

                redis([command: "get", key: mykey]) { reply2 ->
                    assertString("11", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testIncrby() {
        def mykey = makeKey()

        redis([command: "set", key: mykey, value: "10"]) { reply0 ->
            redis([command: "incrby", key: mykey, increment: 5]) { reply1 ->
                assertNumber(15, reply1)
                tu.testComplete()
            }
        }
    }

    void testIncrbyfloat() {
        def mykey = makeKey()

        redis([command: "set", key: mykey, value: 10.50]) { reply0 ->
            redis([command: "incrbyfloat", key: mykey, increment: 0.1]) { reply1 ->
                assertString("10.6", reply1)

                redis([command: "set", key: mykey, value: 5.0e3]) { reply2 ->
                    redis([command: "incrbyfloat", key: mykey, increment: 2.0e2]) { reply3 ->
                        assertString("5200", reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testInfo() {
        redis([command: "info" /*, section: "server"*/]) { reply0 ->
            tu.azzert(reply0.body.getString("value").indexOf("redis_version") != -1)
            tu.testComplete()
        }
    }

    void testKeys() {
        redis([command: "mset", keyvalues: [[key: "one", value: 1], [key: "two", value: 2], [key: "three", value: 3], [key: "four", value: 4]]]) { reply0 ->
            redis([command: "keys", pattern: "*o*"]) { reply1 ->
                def array = reply1.body.getArray("value")
                println array
                // this is because there are leftovers from previous tests
                tu.azzert(3 <= array.size())

                redis([command: "keys", pattern: "t??"]) { reply2 ->
                    def array2 = reply2.body.getArray("value")
                    tu.azzert(1 == array2.size())

                    redis([command: "keys", pattern: "*"]) { reply3 ->
                        def array3 = reply3.body.getArray("value")
                        tu.azzert(4 <= array3.size())
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testLastsave() {
        redis([command: "lastsave"]) { reply0 ->
            tu.testComplete()
        }
    }

    void testLindex() {
        def mykey = makeKey()

        redis([command: "lpush", key: mykey, value: "World"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "lpush", key: mykey, value: "Hello"]) { reply1 ->
                assertNumber(2, reply1)

                redis([command: "lindex", key: mykey, index: 0]) { reply2 ->
                    assertString("Hello", reply2)

                    redis([command: "lindex", key: mykey, index: -1]) { reply3 ->
                        assertString("World", reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testLinsert() {
        def mykey = makeKey()

        redis([command: "rpush", key: mykey, value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)

            redis([command: "rpush", key: mykey, value: "World"]) { reply1 ->
                assertNumber(2, reply1)

                redis([command: "linsert", key: mykey, before: true, pivot: "World", value: "There"]) { reply2 ->
                    assertNumber(3, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testLlen() {
        def mykey = makeKey()
        redis([command: "lpush", key: mykey, value: "World"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "lpush", key: mykey, value: "Hello"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "llen", key: mykey]) { reply2 ->
                    assertNumber(2, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testLpop() {
        def mykey = makeKey()
        redis([command: "rpush", key: mykey, value: "one"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "rpush", key: mykey, value: "two"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "rpush", key: mykey, value: "three"]) { reply2 ->
                    assertNumber(3, reply2)
                    redis([command: "lpop", key: mykey]) { reply3 ->
                        assertString("one", reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testLpush() {
        def mykey = makeKey()
        redis([command: "lpush", key: mykey, value: "world"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "lpush", key: mykey, value: "hello"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "lrange", key: mykey, start: 0, stop: -1]) { reply2 ->
                    assertArray(["hello", "world"], reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testLpushx() {
        def mykey = makeKey()
        def myotherkey = makeKey()

        redis([command: "lpush", key: mykey, value: "World"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "lpushx", key: mykey, value: "Hello"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "lpushx", key: myotherkey, value: "Hello"]) { reply2 ->
                    assertNumber(0, reply2)
                    redis([command: "lrange", key: mykey, start: 0, stop: -1]) { reply3 ->
                        def array3 = reply3.body.getArray("value")
                        tu.azzert(2 == array3.size())

                        tu.azzert("Hello".equals(array3.get(0)))
                        tu.azzert("World".equals(array3.get(1)))
                        redis([command: "lrange", key: myotherkey, start: 0, stop: -1]) { reply4 ->
                            def array4 = reply4.body.getArray("value")
                            tu.azzert(0 == array4.size())
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testLrange() {
        def mykey = makeKey()
        redis([command: "rpush", key: mykey, value: "one"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "rpush", key: mykey, value: "two"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "rpush", key: mykey, value: "three"]) { reply2 ->
                    assertNumber(3, reply2)
                    redis([command: "lrange", key: mykey, start: 0, stop: 0]) { reply3 ->
                        assertArray(["one"], reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testLrem() {
        def mykey = makeKey()
        redis([command: "rpush", key: mykey, value: "hello"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "rpush", key: mykey, value: "hello"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "rpush", key: mykey, value: "foo"]) { reply2 ->
                    assertNumber(3, reply2)
                    redis([command: "rpush", key: mykey, value: "hello"]) { reply3 ->
                        assertNumber(4, reply3)
                        redis([command: "lrem", key: mykey, count: -2, value: "hello"]) { reply4 ->
                            assertNumber(2, reply4)
                            redis([command: "lrange", key: mykey, start: 0, stop: -1]) { reply5 ->
                                assertArray(["hello", "foo"], reply5)
                                tu.testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    void testLset() {
        def mykey = makeKey()
        redis([command: "rpush", key: mykey, value: "one"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "rpush", key: mykey, value: "two"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "rpush", key: mykey, value: "three"]) { reply2 ->
                    assertNumber(3, reply2)
                    redis([command: "lset", key: mykey, index: 0, value: "four"]) { reply3 ->
                        redis([command: "lset", key: mykey, index: -2, value: "five"]) { reply4 ->
                            redis([command: "lrange", key: mykey, start: 0, stop: -1]) { reply5 ->
                                assertArray(["four", "five", "three"], reply5)
                                tu.testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    void testLtrim() {
        def mykey = makeKey()
        redis([command: "rpush", key: mykey, value: "one"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "rpush", key: mykey, value: "two"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "rpush", key: mykey, value: "three"]) { reply2 ->
                    assertNumber(3, reply2)
                    redis([command: "ltrim", key: mykey, start: 0, stop: -1]) { reply3 ->
                        redis([command: "lrange", key: mykey, start: 0, stop: -1]) { reply5 ->
                            assertArray(["two", "three"], reply5)
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testMget() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()
        redis([command: "set", key: mykey1, value: "Hello"]) { reply0 ->
            redis([command: "set", key: mykey2, value: "World"]) { reply1 ->
                redis([command: "mget", key: [mykey1, mykey2, "nonexisting"]]) { reply2 ->
                    assertArray(["Hello", "World", null], reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testMigrate() {
        tu.testComplete()
    }

    void testMonitor() {
        tu.testComplete()
    }

    void testMove() {
        tu.testComplete()
    }

    void testMset() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()
        redis([command: "mset", keyvalues: [[key: mykey1, value: "Hello"], [key: mykey2, value: "World"]]]) { reply0 ->
            redis([command: "get", key: mykey1]) { reply1 ->
                assertString("Hello", reply1)
                redis([command: "get", key: mykey2]) { reply2 ->
                    assertString("World", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testMsetnx() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()
        def mykey3 = makeKey()

        redis([command: "msetnx", keyvalues: [[key: mykey1, value: "Hello"], [key: mykey2, value: "there"]]]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "msetnx", keyvalues: [[key: mykey2, value: "there"], [key: mykey3, value: "world"]]]) { reply1 ->
                assertNumber(0, reply1)
                redis([command: "mget", key: [mykey1, mykey2, mykey3]]) { reply2 ->
                    assertArray(["Hello", "there", null], reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testMulti() {
        tu.testComplete()
    }

    void testObject() {
        tu.testComplete()
    }

    void testPersist() {
        def mykey = makeKey()
        redis([command: "set", key: mykey, value: "Hello"]) { reply0 ->
            redis([command: "expire", key: mykey, seconds: 10]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "ttl", key: mykey]) { reply2 ->
                    assertNumber(10, reply2)
                    redis([command: "persist", key: mykey]) { reply3 ->
                        assertNumber(1, reply3)
                        redis([command: "ttl", key: mykey]) { reply4 ->
                            assertNumber(-1, reply4)
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testPexpire() {
        def mykey = makeKey()
        redis([command: "set", key: mykey, value: "Hello"]) { reply0 ->
            redis([command: "pexpire", key: mykey, milliseconds: 1500]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "ttl", key: mykey]) { reply2 ->
                    assertNumber(1, reply2)
                    redis([command: "pttl", key: mykey]) { reply3 ->
                        tu.azzert(1500 > reply3.body.getNumber("value") && reply3.body.getNumber("value") > 0)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testPing() {
        redis([command: "ping"]) { reply0 ->
            assertString("PONG", reply0)
            tu.testComplete()
        }
    }

    void testPsetex() {
        def mykey = makeKey()
        redis([command: "psetex", key: mykey, milliseconds: 1000, value: "Hello"]) { reply0 ->
            redis([command: "pttl", key: mykey]) { reply1 ->
                tu.azzert(1500 > reply1.body.getNumber("value") && reply1.body.getNumber("value") > 0)
                redis([command: "get", key: mykey]) { reply2 ->
                    assertString("Hello", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testPsubscribe() {
        tu.testComplete()
    }

    void testPttl() {
        def mykey = makeKey()
        redis([command: "set", key: mykey, value: "Hello"]) { reply0 ->
            redis([command: "expire", key: mykey, seconds: 1]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "pttl", key: mykey]) { reply2 ->
                    tu.azzert(1000 > reply2.body.getNumber("value") && reply2.body.getNumber("value") > 0)
                    tu.testComplete()
                }
            }
        }
    }

    void testPublish() {
        tu.testComplete()
    }

    void testPunsubscribe() {
        tu.testComplete()
    }

    void testQuit() {
        tu.testComplete()
    }

    void testRandomkey() {
        tu.testComplete()
    }

    void testRename() {
        def mykey = makeKey()
        def myotherkey = makeKey()

        redis([command: "set", key: mykey, value: "Hello"]) { reply0 ->
            redis([command: "rename", key: mykey, newkey: myotherkey]) { reply1 ->
                redis([command: "get", key: myotherkey]) { reply2 ->
                    assertString("Hello", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testRenamenx() {
        def mykey = makeKey()
        def myotherkey = makeKey()

        redis([command: "set", key: mykey, value: "Hello"]) { reply0 ->
            redis([command: "set", key: myotherkey, value: "World"]) { reply1 ->
                redis([command: "renamenx", key: mykey, newkey: myotherkey]) { reply2 ->
                    assertNumber(0, reply2)
                    redis([command: "get", key: myotherkey]) { reply3 ->
                        assertString("World", reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testRestore() {
        tu.testComplete()
    }

    void testRpop() {
        def mykey = makeKey()
        redis([command: "rpush", key: mykey, value: "one"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "rpush", key: mykey, value: "two"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "rpush", key: mykey, value: "three"]) { reply2 ->
                    assertNumber(3, reply2)
                    redis([command: "rpop", key: mykey]) { reply3 ->
                        assertString("three", reply3)
                        redis([command: "lrange", key: mykey, start: 0, stop: -1]) { reply5 ->
                            assertArray(["one", "two"], reply5)
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testRpoplpush() {
        def mykey = makeKey()
        def myotherkey = makeKey()
        redis([command: "rpush", key: mykey, value: "one"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "rpush", key: mykey, value: "two"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "rpush", key: mykey, value: "three"]) { reply2 ->
                    assertNumber(3, reply2)
                    redis([command: "rpoplpush", source: mykey, destination: myotherkey]) { reply3 ->
                        assertString("three", reply3)
                        redis([command: "lrange", key: mykey, start: 0, stop: -1]) { reply5 ->
                            assertArray(["one", "two"], reply5)
                            redis([command: "lrange", key: myotherkey, start: 0, stop: -1]) { reply6 ->
                                assertArray(["three"], reply6)
                                tu.testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    void testRpush() {
        def mykey = makeKey()
        redis([command: "rpush", key: mykey, value: "hello"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "rpush", key: mykey, value: "world"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "lrange", key: mykey, start: 0, stop: -1]) { reply2 ->
                    assertArray(["hello", "world"], reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testRpushx() {
        def mykey = makeKey()
        def myotherkey = makeKey()
        redis([command: "rpush", key: mykey, value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "rpushx", key: mykey, value: "World"]) { reply1 ->
                assertNumber(2, reply1)
                redis([command: "rpushx", key: myotherkey, value: "World"]) { reply2 ->
                    assertNumber(0, reply2)
                    redis([command: "lrange", key: mykey, start: 0, stop: -1]) { reply3 ->
                        assertArray(["Hello", "World"], reply3)
                        redis([command: "lrange", key: myotherkey, start: 0, stop: -1]) { reply4 ->
                            assertArray([], reply4)
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testSadd() {
        def mykey = makeKey()
        redis([command: "sadd", key: mykey, member: "Hello"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "sadd", key: mykey, member: "World"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "sadd", key: mykey, member: "World"]) { reply2 ->
                    assertNumber(0, reply2)
                    redis([command: "smembers", key: mykey]) { reply3 ->
                        assertArray(["World", "Hello"], reply3)
                        tu.testComplete()
                    }
                }
            }
        }
    }

    void testSave() {
        tu.testComplete()
    }

    void testScard() {
        def mykey = makeKey()
        redis([command: "sadd", key: mykey, member: "Hello"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "sadd", key: mykey, member: "World"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "scard", key: mykey]) { reply2 ->
                    assertNumber(2, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testScriptexists() {
        tu.testComplete()
    }

    void testScriptflush() {
        tu.testComplete()
    }

    void testScriptkill() {
        tu.testComplete()
    }

    void testScriptload() {
        tu.testComplete()
    }

    void testSdiff() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()

        redis([command: "sadd", key: mykey1, member: "a"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "sadd", key: mykey1, member: "b"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "sadd", key: mykey1, member: "c"]) { reply2 ->
                    assertNumber(1, reply2)
                    redis([command: "sadd", key: mykey2, member: "c"]) { reply3 ->
                        assertNumber(1, reply3)
                        redis([command: "sadd", key: mykey2, member: "d"]) { reply4 ->
                            assertNumber(1, reply4)
                            redis([command: "sadd", key: mykey2, member: "e"]) { reply5 ->
                                assertNumber(1, reply5)
                                redis([command: "sdiff", key: [mykey1, mykey2]]) { reply6 ->
                                    assertArray(["a", "b"], reply6)
                                    tu.testComplete()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    void testSdiffstore() {
        tu.testComplete()
    }

    void testSelect() {
        tu.testComplete()
    }

    void testSet() {
        def mykey = makeKey()
        redis([command: "set", key: mykey, value: "Hello"]) { reply0 ->
            redis([command: "get", key: mykey]) { reply1 ->
                assertString("Hello", reply1)
                tu.testComplete()
            }
        }
    }

    void testSetbit() {
        def mykey = makeKey()
        redis([command: "setbit", key: mykey, offset: 7, value: 1]) { reply0 ->
            assertNumber(0, reply0)
            redis([command: "setbit", key: mykey, offset: 7, value: 0]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "get", key: mykey]) { reply2 ->
                    assertString("\u0000", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testSetex() {
        def mykey = makeKey()
        redis([command: "setex", key: mykey, seconds: 10, value: "Hello"]) { reply0 ->
            redis([command: "ttl", key: mykey]) { reply1 ->
                assertNumber(10, reply1)
                redis([command: "get", key: mykey]) { reply2 ->
                    assertString("Hello", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testSetnx() {
        def mykey = makeKey()
        redis([command: "setnx", key: mykey, value: "Hello"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "setnx", key: mykey, value: "World"]) { reply1 ->
                assertNumber(0, reply1)
                redis([command: "get", key: mykey]) { reply2 ->
                    assertString("Hello", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testSetrange() {
        def mykey = makeKey()
        redis([command: "set", key: mykey, value: "Hello World"]) { reply0 ->
            redis([command: "setrange", key: mykey, offset: 6, value: "Redis"]) { reply1 ->
                assertNumber(11, reply1)
                redis([command: "get", key: mykey]) { reply2 ->
                    assertString("Hello Redis", reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testShutdown() {
        tu.testComplete()
    }

    void testSinter() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()

        redis([command: "sadd", key: mykey1, member: "a"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "sadd", key: mykey1, member: "b"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "sadd", key: mykey1, member: "c"]) { reply2 ->
                    assertNumber(1, reply2)
                    redis([command: "sadd", key: mykey2, member: "c"]) { reply3 ->
                        assertNumber(1, reply3)
                        redis([command: "sadd", key: mykey2, member: "d"]) { reply4 ->
                            assertNumber(1, reply4)
                            redis([command: "sadd", key: mykey2, member: "e"]) { reply5 ->
                                assertNumber(1, reply5)
                                redis([command: "sinter", key: [mykey1, mykey2]]) { reply6 ->
                                    assertArray(["c"], reply6)
                                    tu.testComplete()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    void testSinterstore() {
        tu.testComplete()
    }

    void testSismember() {
        def mykey = makeKey()
        redis([command: "sadd", key: mykey, member: "one"]) { reply0 ->
            redis([command: "sismember", key: mykey, member: "one"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "sismember", key: mykey, member: "two"]) { reply2 ->
                    assertNumber(0, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testSlaveof() {
        tu.testComplete()
    }

    void testSlowlog() {
        tu.testComplete()
    }

    void testSmembers() {
        def mykey = makeKey()
        redis([command: "sadd", key: mykey, member: "Hello"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "sadd", key: mykey, member: "World"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "smembers", key: mykey]) { reply2 ->
                    assertArray(["World", "Hello"], reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testSmove() {
        def mykey = makeKey()
        def myotherkey = makeKey()
        redis([command: "sadd", key: mykey, member: "one"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "sadd", key: mykey, member: "two"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "sadd", key: myotherkey, member: "three"]) { reply2 ->
                    assertNumber(1, reply2)
                    redis([command: "smove", source: mykey, destination: myotherkey, member: "two"]) { reply3 ->
                        assertNumber(1, reply3)
                        redis([command: "smembers", key: mykey]) { reply4 ->
                            assertArray(["one"], reply4)
                            redis([command: "smembers", key: myotherkey]) { reply5 ->
                                assertArray(["two", "three"], reply5)
                                tu.testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    void testSort() {
        tu.testComplete()
    }

    void testSpop() {
        def mykey = makeKey()
        redis([command: "sadd", key: mykey, member: "one"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "sadd", key: mykey, member: "two"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "sadd", key: mykey, member: "three"]) { reply2 ->
                    assertNumber(1, reply2)
                    redis([command: "spop", key: mykey]) { reply3 ->
                        assertString("one", reply3)
                        redis([command: "smembers", key: mykey]) { reply4 ->
                            assertArray(["three", "two"], reply4)
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testSrandmember() {
        def mykey = makeKey()
        redis([command: "sadd", key: mykey, member: ["one", "two", "three"]]) { reply0 ->
            assertNumber(3, reply0)
            redis([command: "srandmember", key: mykey]) { reply1 ->
                def randmember = reply1.body.getString("value");
                tu.azzert(randmember.equals("one") || randmember.equals("two") || randmember.equals("three"))
                tu.testComplete()
            }
        }
    }

    void testSrem() {
        def mykey = makeKey()
        redis([command: "sadd", key: mykey, member: "one"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "sadd", key: mykey, member: "two"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "sadd", key: mykey, member: "three"]) { reply2 ->
                    assertNumber(1, reply2)
                    redis([command: "srem", key: mykey, member: "one"]) { reply3 ->
                        assertNumber(1, reply3)
                        redis([command: "srem", key: mykey, member: "four"]) { reply4 ->
                            assertNumber(0, reply4)
                            tu.testComplete()
                        }
                    }
                }
            }
        }
    }

    void testStrlen() {
        def mykey = makeKey()
        redis([command: "set", key: mykey, value: "Hello world"]) { reply0 ->
            redis([command: "strlen", key: mykey]) { reply1 ->
                assertNumber(11, reply1)
                redis([command: "strlen", key: "nonexisting"]) { reply2 ->
                    assertNumber(0, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testSubscribe() {
        tu.testComplete()
    }

    void testSunion() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()

        redis([command: "sadd", key: mykey1, member: "a"]) { reply0 ->
            assertNumber(1, reply0)
            redis([command: "sadd", key: mykey1, member: "b"]) { reply1 ->
                assertNumber(1, reply1)
            }
            redis([command: "sadd", key: mykey1, member: "c"]) { reply2 ->
                assertNumber(1, reply2)
            }
            redis([command: "sadd", key: mykey2, member: "c"]) { reply3 ->
                assertNumber(1, reply3)
            }
            redis([command: "sadd", key: mykey2, member: "d"]) { reply4 ->
                assertNumber(1, reply4)
            }
            redis([command: "sadd", key: mykey2, member: "e"]) { reply5 ->
                assertNumber(1, reply5)
                redis([command: "sunion", key: [mykey1, mykey2]]) { reply6 ->
                    assertArray(["a", "b", "c", "d", "e"], reply6)
                    tu.testComplete()
                }
            }
        }
    }

    void testSuionstore() {
        tu.testComplete()
    }

    void testSync() {
        tu.testComplete()
    }

    void testTime() {
        redis([command: "time"]) { reply0 ->
            tu.azzert(reply0.body.getArray("value").size() == 2)
            tu.testComplete()
        }
    }

    void testTtl() {
        def mykey = makeKey()
        redis([command: "set", key: mykey, value: "Hello"]) { reply0 ->
            redis([command: "expire", key: mykey, seconds: 10]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "ttl", key: mykey]) { reply2 ->
                    assertNumber(10, reply2)
                    tu.testComplete()
                }
            }
        }
    }

    void testType() {
        def key1 = makeKey()
        def key2 = makeKey()
        def key3 = makeKey()

        redis([command: "set", key: key1, value: "value"]) { reply0 ->
            redis([command: "lpush", key: key2, value: "value"]) { reply1 ->
                assertNumber(1, reply1)
                redis([command: "sadd", key: key3, member: "value"]) { reply2 ->
                    assertNumber(1, reply2)
                    redis([command: "type", key: key1]) { reply3 ->
                        assertString("string", reply3)
                        redis([command: "type", key: key2]) { reply4 ->
                            assertString("list", reply4)
                            redis([command: "type", key: key3]) { reply5 ->
                                assertString("set", reply5)
                                tu.testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    void testUnsubscribe() {
        tu.testComplete()
    }

    void testUnwatch() {
        tu.testComplete()
    }

    void testWatch() {
        tu.testComplete()
    }


}