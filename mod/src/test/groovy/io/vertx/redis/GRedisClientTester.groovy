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

class GRedisClientTester extends TestVerticle {

    private final String address = 'test.my_redisclient'
    private EventBus eb

    private void appReady() {
        super.start()
    }

    void start() {
        eb = vertx.eventBus()
        JsonObject config = new JsonObject()

        config.putString('address', address)
        config.putString('host', 'localhost')
        config.putNumber('port', 6379)
        config.putString('encoding', 'ISO-8859-1')

        container.deployModule(System.getProperty('vertx.modulename'), config, 1, new AsyncResultHandler<String>() {
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
                    println reply.body()
                    assertEquals('error', reply.body.getString('status'))
                } else {
                    assertEquals('ok', reply.body.getString('status'))
                }

                closure.call(reply)
            }
        })
    }

    private static String makeKey() {
        return UUID.randomUUID().toString()
    }

    private static void assertNumberValue(expected, value) {
        assertEquals(expected, value.body.getNumber('value'))
    }

    private static void assertStringValue(expected, value) {
        assertEquals(expected, value.body.getString('value'))
    }

    private static void assertNullValue(value) {
        assertEquals(null, value.body.getField('value'))
    }

    private static void assertNotNullValue(value) {
        assertNotSame(null, value.body.getField('value'))
    }

    void assertArrayValue(expected, value) {
        def array = value.body.getArray('value')
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

    private static void assertUnorderedArrayValue(expected, value) {
        def array = value.body.getArray('value')
        assertNotNull(array)
        assertEquals(expected.size(), array.size())

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

            assertTrue('Expected one of: <' + expected + '> but got: <' + array.get(i) + '>', found)
        }
    }

    @Test
    void testAppend() {
        def key = makeKey()

        redis([command: 'del', args: [key]]) { reply0 ->

            redis([command: 'append', args: [key, 'Hello']]) { reply1 ->
                assertNumberValue(5, reply1)

                redis([command: 'append', args: [key, ' World']]) { reply2 ->
                    assertNumberValue(11, reply2)

                    redis([command: 'get', args: [key]]) { reply3 ->
                        assertStringValue('Hello World', reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testAuth() {
        testComplete()
    }

    @Test
    void testBgrewriteaof() {
        testComplete()
    }

    @Test
    void testBgsave() {
        testComplete()
    }

    @Test
    void testBitcount() {
        def key = makeKey()

        redis([command: 'set', args: [key, 'foobar']]) { reply0 ->

            redis([command: 'bitcount', args: [key]]) { reply1 ->
                assertNumberValue(26, reply1)

                redis([command: 'bitcount', args: [key, 0, 0]]) { reply2 ->
                    assertNumberValue(4, reply2)

                    redis([command: 'bitcount', args: [key, 1, 1]]) { reply3 ->
                        assertNumberValue(6, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testBitop() {
        def key1 = makeKey()
        def key2 = makeKey()
        def destkey = makeKey()

        redis([command: 'set', args: [key1, 'foobar']]) { reply0 ->
            redis([command: 'set', args: [key2, 'abcdef']]) { reply1 ->
                redis([command: 'bitop', args: ['and', destkey, key1, key2]]) { reply2 ->
                    redis([command: 'get', args: [destkey]]) { reply3 ->
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testBlpop() {
        def list1 = makeKey()
        def list2 = makeKey()

        redis([command: 'del', args: [list1, list2]]) { reply0 ->

            redis([command: 'rpush', args: [list1, 'a', 'b', 'c']]) { reply1 ->
                assertNumberValue(3, reply1)

                redis([command: 'blpop', args: [list1, list2, 0]]) { reply2 ->
                    assertArrayValue([list1, 'a'], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testBrpop() {
        def list1 = makeKey()
        def list2 = makeKey()

        redis([command: 'del', args: [list1, list2]]) { reply0 ->

            redis([command: 'rpush', args: [list1, 'a', 'b', 'c']]) { reply1 ->
                assertNumberValue(3, reply1)

                redis([command: 'brpop', args: [list1, list2, 0]]) { reply2 ->
                    assertArrayValue([list1, 'c'], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testBrpoplpush() {
        testComplete()
    }

    @Test
    void testClientKill() {
        testComplete()
    }

    @Test
    void testClientList() {
        testComplete()
    }

    @Test
    void testClientGetname() {
        testComplete()
    }

    @Test
    void testClientSetname() {
        testComplete()
    }

    @Test
    void testConfigGet() {
        redis([command: 'config get', args: ['*max-*-entries*']]) { reply0 ->
            // 1) 'hash-max-zipmap-entries'
            // 2) '512'
            // 3) 'list-max-ziplist-entries'
            // 4) '512'
            // 5) 'set-max-intset-entries'
            // 6) '512'
            testComplete()
        }
    }

    @Test
    void testConfigSet() {
        testComplete()
    }

    @Test
    void testConfigResetstat() {
        testComplete()
    }

    @Test
    void testDbsize() {
        testComplete()
    }

    @Test
    void testDebugObject() {
        testComplete()
    }

    @Test
    void testDebugSegfault() {
        testComplete()
    }

    @Test
    void testDecr() {
        def mykey = makeKey()

        redis([command: 'set', args: [mykey, '10']]) { reply0 ->
            redis([command: 'decr', args: [mykey]]) { reply1 ->
                assertNumberValue(9, reply1)
                testComplete()
            }
        }
    }

    @Test
    void testDecrby() {
        def mykey = makeKey()

        redis([command: 'set', args: [mykey, '10']]) { reply0 ->
            redis([command: 'decrby', args: [mykey, 5]]) { reply1 ->
                assertNumberValue(5, reply1)
                testComplete()
            }
        }
    }

    @Test
    void testDel() {
        def key1 = makeKey()
        def key2 = makeKey()
        def key3 = makeKey()

        redis([command: 'set', args: [key1, 'Hello']]) { reply0 ->
            redis([command: 'set', args: [key2, 'World']]) { reply1 ->
                redis([command: 'del', args: [key1, key2, key3]]) { reply2 ->
                    assertNumberValue(2, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testDiscard() {
        testComplete()
    }

    @Test
    void testDump() {
        def mykey = makeKey()

        redis([command: 'set', args: [mykey, 10]]) { reply0 ->
            redis([command: 'dump', args: [mykey]]) { reply1 ->
                byte[] data = reply1.body.getString('value').getBytes('ISO-8859-1')

                assertEquals((byte) data[0], (byte) 0)
                assertEquals((byte) data[1], (byte) 0xc0)
                assertEquals((byte) data[2], (byte) '\n')
                assertEquals((byte) data[3], (byte) 6)
                assertEquals((byte) data[4], (byte) 0)
                assertEquals((byte) data[5], (byte) 0xf8)
                assertEquals((byte) data[6], (byte) 'r')
                assertEquals((byte) data[7], (byte) '?')
                assertEquals((byte) data[8], (byte) 0xc5)
                assertEquals((byte) data[9], (byte) 0xfb)
                assertEquals((byte) data[10], (byte) 0xfb)
                assertEquals((byte) data[11], (byte) '_')
                assertEquals((byte) data[12], (byte) '(')
                testComplete()
            }
        }
    }

    @Test
    void testEcho() {
        redis([command: 'echo', args: ['Hello World!']]) { reply0 ->
            assertStringValue('Hello World!', reply0)
            testComplete()
        }
    }

    @Test
    void testEval() {
        def key1 = makeKey()
        def key2 = makeKey()

        redis([command: 'eval', args: ['return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}', 2, key1, key2, 'first', 'second']]) { reply0 ->
            assertArrayValue([key1, key2, 'first', 'second'], reply0)
            testComplete()
        }
    }

    @Test
    void testEvalsha() {
        testComplete()
    }

    @Test
    void testExec() {
        testComplete()
    }

    @Test
    void testExists() {
        def key1 = makeKey()
        def key2 = makeKey()

        redis([command: 'set', args: [key1, 'Hello']]) {reply0 ->
            redis([command: 'exists', args: [key1]]) {reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'exists', args: [key2]]) {reply2 ->
                    assertNumberValue(0, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testExpire() {
        def mykey = makeKey()

        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'expire', args: [mykey, 10]]) { reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'ttl', args: [mykey]]) { reply2 ->
                    assertNumberValue(10, reply2)

                    redis([command: 'set', args: [mykey, 'Hello World']]) { reply3 ->
                        redis([command: 'ttl', args: [mykey]]) { reply4 ->
                            assertNumberValue(-1, reply4)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testExpireat() {
        def mykey = makeKey()

        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'exists', args: [mykey]]) { reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'expireat', args: [mykey, 1293840000]]) { reply2 ->
                    assertNumberValue(1, reply2)

                    redis([command: 'exists', args: [mykey]]) { reply3 ->
                        assertNumberValue(0, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testFlushall() {
        testComplete()
    }

    @Test
    void testFlushdb() {
        testComplete()
    }

    @Test
    void testGet() {
        def nonexisting = makeKey()
        def mykey = makeKey()

        redis([command: 'get', args: [nonexisting]]) { reply0 ->
            assertNullValue(reply0)

            redis([command: 'set', args: [mykey, 'Hello']]) { reply1 ->
                redis([command: 'get', args: [mykey]]) { reply2 ->
                    assertStringValue('Hello', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testGetbit() {
        def mykey = makeKey()

        redis([command: 'setbit', args: [mykey, 7, 1]]) { reply0 ->
            assertNumberValue(0, reply0)

            redis([command: 'getbit', args: [mykey, 0]]) { reply1 ->
                assertNumberValue(0, reply1)

                redis([command: 'getbit', args: [mykey, 7]]) { reply2 ->
                    assertNumberValue(1, reply2)

                    redis([command: 'getbit', args: [mykey, 100]]) { reply3 ->
                        assertNumberValue(0, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testGetrange() {
        def mykey = makeKey()

        redis([command: 'set', args: [mykey, 'This is a string']]) { reply0 ->
            redis([command: 'getrange', args: [mykey, 0, 3]]) { reply1 ->
                assertStringValue('This', reply1)

                redis([command: 'getrange', args: [mykey, -3, -1]]) { reply2 ->
                    assertStringValue('ing', reply2)

                    redis([command: 'getrange', args: [mykey, 0, -1]]) { reply3 ->
                        assertStringValue('This is a string', reply3)

                        redis([command: 'getrange', args: [mykey, 10, 100]]) { reply4 ->
                            assertStringValue('string', reply4)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testGetset() {
        def mycounter = makeKey()

        redis([command: 'incr', args: [mycounter]]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'getset', args: [mycounter, '0']]) { reply1 ->
                assertStringValue('1', reply1)

                redis([command: 'get', args: [mycounter]]) { reply2 ->
                    assertStringValue('0', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testHdel() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field1', 'foo']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hdel', args: [myhash, 'field1']]) { reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'hdel', args: [myhash, 'field2']]) { reply2 ->
                    assertNumberValue(0, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testHexists() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field1', 'foo']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hexists', args: [myhash, 'field1']]) { reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'hexists', args: [myhash, 'field2']]) { reply2 ->
                    assertNumberValue(0, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testHget() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field1', 'foo']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hget', args: [myhash, 'field1']]) { reply1 ->
                assertStringValue('foo', reply1)

                redis([command: 'hget', args: [myhash, 'field2']]) { reply2 ->
                    assertNullValue(reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testHgetall() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field1', 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hset', args: [myhash, 'field2', 'World']]) { reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'hgetall', args: [myhash]]) { reply2 ->
                    JsonObject obj = reply2.body.getObject('value')
                    assertEquals('Hello', obj.getField('field1'))
                    assertEquals('World', obj.getField('field2'))
                    testComplete()
               }
            }
        }
    }

    @Test
    void testHincrby() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field', 5]]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hincrby', args: [myhash, 'field', 1]]) { reply1 ->
                assertNumberValue(6, reply1)

                redis([command: 'hincrby', args: [myhash, 'field', -1]]) { reply2 ->
                    assertNumberValue(5, reply2)

                    redis([command: 'hincrby', args: [myhash, 'field', -10]]) { reply3 ->
                        assertNumberValue(-5, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testHIncrbyfloat() {
        def mykey = makeKey()

        redis([command: 'hset', args: [mykey, 'field', 10.50]]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hincrbyfloat', args: [mykey, 'field', 0.1]]) { reply1 ->
                assertStringValue('10.6', reply1)

                redis([command: 'hset', args: [mykey, 'field', 5.0e3]]) { reply2 ->
                    assertNumberValue(0, reply2)

                    redis([command: 'hincrbyfloat', args: [mykey, 'field', 2.0e2]]) { reply3 ->
                        assertStringValue('5200', reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testHkeys() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field1', 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hset', args: [myhash, 'field2', 'World']]) { reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'hkeys', args: [myhash]]) { reply2 ->
                    assertArrayValue(['field1', 'field2'], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testHlen() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field1', 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hset', args: [myhash, 'field2', 'World']]) { reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'hlen', args: [myhash]]) { reply2 ->
                    assertNumberValue(2, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testHmget() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field1', 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hset', args: [myhash, 'field2', 'World']]) { reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'hmget', args: [myhash, 'field1', 'field2', 'nofield']]) { reply2 ->
                    assertArrayValue(['Hello', 'World', null], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testHmset() {
        def myhash = makeKey()

        redis([command: 'hmset', args: [myhash, 'field1', 'Hello', 'field2', 'World']]) { reply0 ->
            redis([command: 'hget', args: [myhash, 'field1']]) { reply1 ->
                assertStringValue('Hello', reply1)
                redis([command: 'hget', args: [myhash, 'field2']]) { reply2 ->
                    assertStringValue('World', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testHset() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field1', 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hget', args: [myhash, 'field1']]) { reply1 ->
                assertStringValue('Hello', reply1)
                testComplete()
            }
        }
    }

    @Test
    void testHsetnx() {
        def myhash = makeKey()

        redis([command: 'hsetnx', args: [myhash, 'field', 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hsetnx', args: [myhash, 'field', 'World']]) { reply1 ->
                assertNumberValue(0, reply1)

                redis([command: 'hget', args: [myhash, 'field']]) { reply2 ->
                    assertStringValue('Hello', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testHvals() {
        def myhash = makeKey()

        redis([command: 'hset', args: [myhash, 'field1', 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'hset', args: [myhash, 'field2', 'World']]) { reply1 ->
                assertNumberValue(1, reply1)

                redis([command: 'hvals', args: [myhash]]) { reply2 ->
                    assertArrayValue(['Hello', 'World'], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testIncr() {
        def mykey = makeKey()

        redis([command: 'set', args: [mykey, '10']]) { reply0 ->
            redis([command: 'incr', args: [mykey]]) { reply1 ->
                assertNumberValue(11, reply1)

                redis([command: 'get', args: [mykey]]) { reply2 ->
                    assertStringValue('11', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testIncrby() {
        def mykey = makeKey()

        redis([command: 'set', args: [mykey, '10']]) { reply0 ->
            redis([command: 'incrby', args: [mykey, 5]]) { reply1 ->
                assertNumberValue(15, reply1)
                testComplete()
            }
        }
    }

    @Test
    void testIncrbyfloat() {
        def mykey = makeKey()

        redis([command: 'set', args: [mykey, 10.50]]) { reply0 ->
            redis([command: 'incrbyfloat', args: [mykey, 0.1]]) { reply1 ->
                assertStringValue('10.6', reply1)

                redis([command: 'set', args: [mykey, 5.0e3]]) { reply2 ->
                    redis([command: 'incrbyfloat', args: [mykey, 2.0e2]]) { reply3 ->
                        assertStringValue('5200', reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testInfo() {
        redis([command: 'info' /*, section: 'server'*/]) { reply0 ->
            assertNotNullValue(reply0)
            testComplete()
        }
    }

    @Test
    void testKeys() {
        redis([command: 'mset', args: ['one', 1, 'two', 2, 'three', 3, 'four', 4]]) { reply0 ->
            redis([command: 'keys', args: ['*o*']]) { reply1 ->
                def array = reply1.body.getArray('value')
                // this is because there are leftovers from previous tests
                assertTrue(3 <= array.size())

                redis([command: 'keys', args: ['t??']]) { reply2 ->
                    def array2 = reply2.body.getArray('value')
                    assertTrue(1 == array2.size())

                    redis([command: 'keys', args: ['*']]) { reply3 ->
                        def array3 = reply3.body.getArray('value')
                        assertTrue(4 <= array3.size())
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testLastsave() {
        redis([command: 'lastsave']) { reply0 ->
            testComplete()
        }
    }

    @Test
    void testLindex() {
        def mykey = makeKey()

        redis([command: 'lpush', args: [mykey, 'World']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'lpush', args: [mykey, 'Hello']]) { reply1 ->
                assertNumberValue(2, reply1)

                redis([command: 'lindex', args: [mykey, 0]]) { reply2 ->
                    assertStringValue('Hello', reply2)

                    redis([command: 'lindex', args: [mykey, -1]]) { reply3 ->
                        assertStringValue('World', reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testLinsert() {
        def mykey = makeKey()

        redis([command: 'rpush', args: [mykey, 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)

            redis([command: 'rpush', args: [mykey, 'World']]) { reply1 ->
                assertNumberValue(2, reply1)

                redis([command: 'linsert', args: [mykey, 'before', 'World', 'There']]) { reply2 ->
                    assertNumberValue(3, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testLlen() {
        def mykey = makeKey()
        redis([command: 'lpush', args: [mykey, 'World']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'lpush', args: [mykey, 'Hello']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'llen', args: [mykey]]) { reply2 ->
                    assertNumberValue(2, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testLpop() {
        def mykey = makeKey()
        redis([command: 'rpush', args: [mykey, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'rpush', args: [mykey, 'two']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'rpush', args: [mykey, 'three']]) { reply2 ->
                    assertNumberValue(3, reply2)
                    redis([command: 'lpop', args: [mykey]]) { reply3 ->
                        assertStringValue('one', reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testLpush() {
        def mykey = makeKey()
        redis([command: 'lpush', args: [mykey, 'world']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'lpush', args: [mykey, 'hello']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'lrange', args: [mykey, 0, -1]]) { reply2 ->
                    assertArrayValue(['hello', 'world'], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testLpushx() {
        def mykey = makeKey()
        def myotherkey = makeKey()

        redis([command: 'lpush', args: [mykey, 'World']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'lpushx', args: [mykey, 'Hello']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'lpushx', args: [myotherkey, 'Hello']]) { reply2 ->
                    assertNumberValue(0, reply2)
                    redis([command: 'lrange', args: [mykey, 0, -1]]) { reply3 ->
                        def array3 = reply3.body.getArray('value')
                        assertTrue(2 == array3.size())

                        assertTrue('Hello'.equals(array3.get(0)))
                        assertTrue('World'.equals(array3.get(1)))
                        redis([command: 'lrange',args: [myotherkey, 0, -1]]) { reply4 ->
                            def array4 = reply4.body.getArray('value')
                            assertTrue(0 == array4.size())
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testLrange() {
        def mykey = makeKey()
        redis([command: 'rpush', args: [mykey, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'rpush', args: [mykey, 'two']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'rpush', args: [mykey, 'three']]) { reply2 ->
                    assertNumberValue(3, reply2)
                    redis([command: 'lrange', args: [mykey, 0, 0]]) { reply3 ->
                        assertArrayValue(['one'], reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testLrem() {
        def mykey = makeKey()
        redis([command: 'rpush', args: [mykey, 'hello']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'rpush', args: [mykey, 'hello']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'rpush', args: [mykey, 'foo']]) { reply2 ->
                    assertNumberValue(3, reply2)
                    redis([command: 'rpush', args: [mykey, 'hello']]) { reply3 ->
                        assertNumberValue(4, reply3)
                        redis([command: 'lrem', args: [mykey, -2, 'hello']]) { reply4 ->
                            assertNumberValue(2, reply4)
                            redis([command: 'lrange', args: [mykey, 0, -1]]) { reply5 ->
                                assertArrayValue(['hello', 'foo'], reply5)
                                testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testLset() {
        def mykey = makeKey()
        redis([command: 'rpush', args: [mykey, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'rpush', args: [mykey, 'two']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'rpush', args: [mykey, 'three']]) { reply2 ->
                    assertNumberValue(3, reply2)
                    redis([command: 'lset', args: [mykey, 0, 'four']]) { reply3 ->
                        redis([command: 'lset', args: [mykey, -2, 'five']]) { reply4 ->
                            redis([command: 'lrange', args: [mykey, 0, -1]]) { reply5 ->
                                assertArrayValue(['four', 'five', 'three'], reply5)
                                testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testLtrim() {
        def mykey = makeKey()
        redis([command: 'rpush', args: [mykey, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'rpush', args: [mykey, 'two']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'rpush', args: [mykey, 'three']]) { reply2 ->
                    assertNumberValue(3, reply2)
                    redis([command: 'ltrim', args: [mykey, 1, -1]]) { reply3 ->
                        redis([command: 'lrange', args: [mykey, 0, -1]]) { reply5 ->
                            assertArrayValue(['two', 'three'], reply5)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testMget() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()
        redis([command: 'set', args: [mykey1, 'Hello']]) { reply0 ->
            redis([command: 'set', args: [mykey2, 'World']]) { reply1 ->
                redis([command: 'mget', args: [mykey1, mykey2, 'nonexisting']]) { reply2 ->
                    assertArrayValue(['Hello', 'World', null], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testMigrate() {
        testComplete()
    }

    @Test
    void testMonitor() {
        testComplete()
    }

    @Test
    void testMove() {
        testComplete()
    }

    @Test
    void testMset() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()
        redis([command: 'mset', args: [mykey1, 'Hello', mykey2, 'World']]) { reply0 ->
            redis([command: 'get', args: [mykey1]]) { reply1 ->
                assertStringValue('Hello', reply1)
                redis([command: 'get', args: [mykey2]]) { reply2 ->
                    assertStringValue('World', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testMsetnx() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()
        def mykey3 = makeKey()

        redis([command: 'msetnx', args: [mykey1, 'Hello', mykey2, 'there']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'msetnx', args: [mykey2, 'there', mykey3, 'world']]) { reply1 ->
                assertNumberValue(0, reply1)
                redis([command: 'mget', args: [mykey1, mykey2, mykey3]]) { reply2 ->
                    assertArrayValue(['Hello', 'there', null], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testMulti() {
        testComplete()
    }

    @Test
    void testObject() {
        testComplete()
    }

    @Test
    void testPersist() {
        def mykey = makeKey()
        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'expire', args: [mykey, 10]]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'ttl', args: [mykey]]) { reply2 ->
                    assertNumberValue(10, reply2)
                    redis([command: 'persist', args: [mykey]]) { reply3 ->
                        assertNumberValue(1, reply3)
                        redis([command: 'ttl', args: [mykey]]) { reply4 ->
                            assertNumberValue(-1, reply4)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testPexpire() {
        def mykey = makeKey()
        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'pexpire', args: [mykey, 1500]]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'ttl', args: [mykey]]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'pttl', args: [mykey]]) { reply3 ->
                        assertTrue(1500 > reply3.body.getNumber('value') && reply3.body.getNumber('value') > 0)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testPexpireat() {
        def mykey = makeKey()
        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'pexpireat', args: [mykey, 1555555555005]]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'ttl', args: [mykey]]) { reply2 ->
                    assertTrue(200000000 > reply2.body.getNumber('value') && reply2.body.getNumber('value') > 0)
                    redis([command: 'pttl', args: [mykey]]) { reply3 ->
                        assertTrue(1555555555005 > reply3.body.getNumber('value') && reply3.body.getNumber('value') > 0)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testPing() {
        redis([command: 'ping']) { reply0 ->
            assertStringValue('PONG', reply0)
            testComplete()
        }
    }

    @Test
    void testPsetex() {
        def mykey = makeKey()
        redis([command: 'psetex', args: [mykey, 1000, 'Hello']]) { reply0 ->
            redis([command: 'pttl', args: [mykey]]) { reply1 ->
                assertTrue(1500 > reply1.body.getNumber('value') && reply1.body.getNumber('value') > 0)
                redis([command: 'get', args: [mykey]]) { reply2 ->
                    assertStringValue('Hello', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testPsubscribe() {
        testComplete()
    }

    @Test
    void testPttl() {
        def mykey = makeKey()
        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'expire', args: [mykey, 1]]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'pttl', args: [mykey]]) { reply2 ->
                    assertTrue(1000 > reply2.body.getNumber('value') && reply2.body.getNumber('value') > 0)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testPublish() {
        testComplete()
    }

    @Test
    void testPunsubscribe() {
        testComplete()
    }

    @Test
    void testQuit() {
        testComplete()
    }

    @Test
    void testRandomkey() {
        testComplete()
    }

    @Test
    void testRename() {
        def mykey = makeKey()
        def myotherkey = makeKey()

        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'rename', args: [mykey, myotherkey]]) { reply1 ->
                redis([command: 'get', args: [myotherkey]]) { reply2 ->
                    assertStringValue('Hello', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testRenamenx() {
        def mykey = makeKey()
        def myotherkey = makeKey()

        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'set', args: [myotherkey, 'World']]) { reply1 ->
                redis([command: 'renamenx', args: [mykey, myotherkey]]) { reply2 ->
                    assertNumberValue(0, reply2)
                    redis([command: 'get', args: [myotherkey]]) { reply3 ->
                        assertStringValue('World', reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testRestore() {
        testComplete()
    }

    @Test
    void testRpop() {
        def mykey = makeKey()
        redis([command: 'rpush', args: [mykey, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'rpush', args: [mykey, 'two']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'rpush', args: [mykey, 'three']]) { reply2 ->
                    assertNumberValue(3, reply2)
                    redis([command: 'rpop', args: [mykey]]) { reply3 ->
                        assertStringValue('three', reply3)
                        redis([command: 'lrange', args: [mykey, 0, -1]]) { reply5 ->
                            assertArrayValue(['one', 'two'], reply5)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testRpoplpush() {
        def mykey = makeKey()
        def myotherkey = makeKey()
        redis([command: 'rpush', args: [mykey, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'rpush', args: [mykey, 'two']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'rpush', args: [mykey, 'three']]) { reply2 ->
                    assertNumberValue(3, reply2)
                    redis([command: 'rpoplpush', args: [mykey, myotherkey]]) { reply3 ->
                        assertStringValue('three', reply3)
                        redis([command: 'lrange', args: [mykey, 0, -1]]) { reply5 ->
                            assertArrayValue(['one', 'two'], reply5)
                            redis([command: 'lrange',args: [myotherkey, 0, -1]]) { reply6 ->
                                assertArrayValue(['three'], reply6)
                                testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testRpush() {
        def mykey = makeKey()
        redis([command: 'rpush', args: [mykey, 'hello']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'rpush', args: [mykey, 'world']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'lrange', args: [mykey, 0, -1]]) { reply2 ->
                    assertArrayValue(['hello', 'world'], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testRpushx() {
        def mykey = makeKey()
        def myotherkey = makeKey()
        redis([command: 'rpush', args: [mykey, 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'rpushx', args: [mykey, 'World']]) { reply1 ->
                assertNumberValue(2, reply1)
                redis([command: 'rpushx', args: [myotherkey, 'World']]) { reply2 ->
                    assertNumberValue(0, reply2)
                    redis([command: 'lrange', args: [mykey, 0, -1]]) { reply3 ->
                        assertArrayValue(['Hello', 'World'], reply3)
                        redis([command: 'lrange',args: [myotherkey, 0, -1]]) { reply4 ->
                            assertArrayValue([], reply4)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testSadd() {
        def mykey = makeKey()
        redis([command: 'sadd', args: [mykey, 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'sadd', args: [mykey, 'World']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'sadd', args: [mykey, 'World']]) { reply2 ->
                    assertNumberValue(0, reply2)
                    redis([command: 'smembers', args: [mykey]]) { reply3 ->
                        assertUnorderedArrayValue(['World', 'Hello'], reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testSave() {
        testComplete()
    }

    @Test
    void testScard() {
        def mykey = makeKey()
        redis([command: 'sadd', args: [mykey, 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'sadd', args: [mykey, 'World']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'scard', args: [mykey]]) { reply2 ->
                    assertNumberValue(2, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testScriptexists() {
        testComplete()
    }

    @Test
    void testScriptflush() {
        testComplete()
    }

    @Test
    void testScriptkill() {
        testComplete()
    }

    @Test
    void testScriptload() {
        testComplete()
    }

    @Test
    void testSdiff() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()

        redis([command: 'sadd', args: [mykey1, 'a']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'sadd', args: [mykey1, 'b']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'sadd', args: [mykey1, 'c']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'sadd', args: [mykey2, 'c']]) { reply3 ->
                        assertNumberValue(1, reply3)
                        redis([command: 'sadd', args: [mykey2, 'd']]) { reply4 ->
                            assertNumberValue(1, reply4)
                            redis([command: 'sadd', args: [mykey2, 'e']]) { reply5 ->
                                assertNumberValue(1, reply5)
                                redis([command: 'sdiff', args: [mykey1, mykey2]]) { reply6 ->
                                    assertUnorderedArrayValue(['a', 'b'], reply6)
                                    testComplete()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testSdiffstore() {
        testComplete()
    }

    @Test
    void testSelect() {
        testComplete()
    }

    @Test
    void testSet() {
        def mykey = makeKey()
        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'get', args: [mykey]]) { reply1 ->
                assertStringValue('Hello', reply1)
                testComplete()
            }
        }
    }

    @Test
    void testSetbit() {
        def mykey = makeKey()
        redis([command: 'setbit', args: [mykey, 7, 1]]) { reply0 ->
            assertNumberValue(0, reply0)
            redis([command: 'setbit', args: [mykey, 7, 0]]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'get', args: [mykey]]) { reply2 ->
                    assertStringValue('\u0000', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testSetex() {
        def mykey = makeKey()
        redis([command: 'setex', args: [mykey, 10, 'Hello']]) { reply0 ->
            redis([command: 'ttl', args: [mykey]]) { reply1 ->
                assertNumberValue(10, reply1)
                redis([command: 'get', args: [mykey]]) { reply2 ->
                    assertStringValue('Hello', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testSetnx() {
        def mykey = makeKey()
        redis([command: 'setnx', args: [mykey, 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'setnx', args: [mykey, 'World']]) { reply1 ->
                assertNumberValue(0, reply1)
                redis([command: 'get', args: [mykey]]) { reply2 ->
                    assertStringValue('Hello', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testSetrange() {
        def mykey = makeKey()
        redis([command: 'set', args: [mykey, 'Hello World']]) { reply0 ->
            redis([command: 'setrange', args: [mykey, 6, 'Redis']]) { reply1 ->
                assertNumberValue(11, reply1)
                redis([command: 'get', args: [mykey]]) { reply2 ->
                    assertStringValue('Hello Redis', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testShutdown() {
        testComplete()
    }

    @Test
    void testSinter() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()

        redis([command: 'sadd', args: [mykey1, 'a']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'sadd', args: [mykey1, 'b']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'sadd', args: [mykey1, 'c']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'sadd', args: [mykey2, 'c']]) { reply3 ->
                        assertNumberValue(1, reply3)
                        redis([command: 'sadd', args: [mykey2, 'd']]) { reply4 ->
                            assertNumberValue(1, reply4)
                            redis([command: 'sadd', args: [mykey2, 'e']]) { reply5 ->
                                assertNumberValue(1, reply5)
                                redis([command: 'sinter', args: [mykey1, mykey2]]) { reply6 ->
                                    assertArrayValue(['c'], reply6)
                                    testComplete()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testSinterstore() {
        testComplete()
    }

    @Test
    void testSismember() {
        def mykey = makeKey()
        redis([command: 'sadd', args: [mykey, 'one']]) { reply0 ->
            redis([command: 'sismember', args: [mykey, 'one']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'sismember', args: [mykey, 'two']]) { reply2 ->
                    assertNumberValue(0, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testSlaveof() {
        testComplete()
    }

    @Test
    void testSlowlog() {
        testComplete()
    }

    @Test
    void testSmembers() {
        def mykey = makeKey()
        redis([command: 'sadd', args: [mykey, 'Hello']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'sadd', args: [mykey, 'World']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'smembers', args: [mykey]]) { reply2 ->
                    assertUnorderedArrayValue(['World', 'Hello'], reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testSmove() {
        def mykey = makeKey()
        def myotherkey = makeKey()
        redis([command: 'sadd', args: [mykey, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'sadd', args: [mykey, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'sadd', args: [myotherkey, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'smove', args: [mykey, myotherkey, 'two']]) { reply3 ->
                        assertNumberValue(1, reply3)
                        redis([command: 'smembers', args: [mykey]]) { reply4 ->
                            assertUnorderedArrayValue(['one'], reply4)
                            redis([command: 'smembers', args: [myotherkey]]) { reply5 ->
                                assertUnorderedArrayValue(['two', 'three'], reply5)
                                testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testSort() {
        def mykey = makeKey()

        def k1 = "${mykey}:1".toString()
        def k2 = "${mykey}:2".toString()
        def k3 = "${mykey}:3".toString()
        def kx = "${mykey}:*".toString()

        redis([command: 'sadd', args: [mykey, '1', '2', '3']]) { reply0 ->
            assertNumberValue(3, reply0)
            redis([command: 'set', args: [k1, 'one']]) { reply1 ->
                redis([command: 'set', args: [k2, 'two']]) { reply2 ->
                    redis([command: 'set', args: [k3, 'three']]) { reply3 ->
                        redis([command: 'sort', args: [mykey, 'desc', 'get', kx]]) { reply4 ->
                            println reply4.body().encode()
                            assertArrayValue(['three', 'two', 'one'], reply4)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testSpop() {
        def mykey = makeKey()
        redis([command: 'sadd', args: [mykey, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'sadd', args: [mykey, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'sadd', args: [mykey, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'spop', args: [mykey]]) { reply3 ->
                        def ret = reply3.body.getString('value')
                        assertTrue(ret.equals('one') || ret.equals('two') || ret.equals('three'))
                        def expected = []
                        if (!ret.equals('one')) {
                            expected.add('one')
                        }
                        if (!ret.equals('two')) {
                            expected.add('two')
                        }
                        if (!ret.equals('three')) {
                            expected.add('three')
                        }
                        redis([command: 'smembers', args: [mykey]]) { reply4 ->
                            assertUnorderedArrayValue(expected, reply4)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testSrandmember() {
        def mykey = makeKey()
        redis([command: 'sadd', args: [mykey, 'one', 'two', 'three']]) { reply0 ->
            assertNumberValue(3, reply0)
            redis([command: 'srandmember', args: [mykey]]) { reply1 ->
                def randmember = reply1.body.getString('value');
                assertTrue(randmember.equals('one') || randmember.equals('two') || randmember.equals('three'))
                testComplete()
            }
        }
    }

    @Test
    void testSrem() {
        def mykey = makeKey()
        redis([command: 'sadd', args: [mykey, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'sadd', args: [mykey, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'sadd', args: [mykey, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'srem', args: [mykey, 'one']]) { reply3 ->
                        assertNumberValue(1, reply3)
                        redis([command: 'srem', args: [mykey, 'four']]) { reply4 ->
                            assertNumberValue(0, reply4)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testStrlen() {
        def mykey = makeKey()
        redis([command: 'set', args: [mykey, 'Hello world']]) { reply0 ->
            redis([command: 'strlen', args: [mykey]]) { reply1 ->
                assertNumberValue(11, reply1)
                redis([command: 'strlen', args: ['nonexisting']]) { reply2 ->
                    assertNumberValue(0, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testSubscribe() {
        testComplete()
    }

    @Test
    void testSunion() {
        def mykey1 = makeKey()
        def mykey2 = makeKey()

        redis([command: 'sadd', args: [mykey1, 'a']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'sadd', args: [mykey1, 'b']]) { reply1 ->
                assertNumberValue(1, reply1)
            }
            redis([command: 'sadd', args: [mykey1, 'c']]) { reply2 ->
                assertNumberValue(1, reply2)
            }
            redis([command: 'sadd', args: [mykey2, 'c']]) { reply3 ->
                assertNumberValue(1, reply3)
            }
            redis([command: 'sadd', args: [mykey2, 'd']]) { reply4 ->
                assertNumberValue(1, reply4)
            }
            redis([command: 'sadd', args: [mykey2, 'e']]) { reply5 ->
                assertNumberValue(1, reply5)
                redis([command: 'sunion', args: [mykey1, mykey2]]) { reply6 ->
                    def arr = reply6.body.getArray('value')
                    assertTrue(arr.size() == 5)
//                    assertArrayValue(['a', 'b', 'c', 'd', 'e'], reply6)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testSunionstore() {
        testComplete()
    }

    @Test
    void testSync() {
        testComplete()
    }

    @Test
    void testTime() {
        redis([command: 'time']) { reply0 ->
            assertTrue(reply0.body.getArray('value').size() == 2)
            testComplete()
        }
    }

    @Test
    void testTtl() {
        def mykey = makeKey()
        redis([command: 'set', args: [mykey, 'Hello']]) { reply0 ->
            redis([command: 'expire', args: [mykey, 10]]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'ttl', args: [mykey]]) { reply2 ->
                    assertNumberValue(10, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testType() {
        def key1 = makeKey()
        def key2 = makeKey()
        def key3 = makeKey()

        redis([command: 'set', args: [key1, 'value']]) { reply0 ->
            redis([command: 'lpush', args: [key2, 'value']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'sadd', args: [key3, 'value']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'type', args: [key1]]) { reply3 ->
                        assertStringValue('string', reply3)
                        redis([command: 'type', args: [key2]]) { reply4 ->
                            assertStringValue('list', reply4)
                            redis([command: 'type', args: [key3]]) { reply5 ->
                                assertStringValue('set', reply5)
                                testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testUnsubscribe() {
        testComplete()
    }

    @Test
    void testUnwatch() {
        testComplete()
    }

    @Test
    void testWatch() {
        testComplete()
    }

    @Test
    void testZadd() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 1, 'uno']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 2, 'two']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zadd', args: [key, 3, 'two']]) { reply3 ->
                        assertNumberValue(0, reply3)
                        redis([command: 'zrange', args: [key, 0, -1, 'withscores']]) { reply4 ->
                            assertArrayValue(['one', '1', 'uno', '1', 'two', '3'], reply4)
                            testComplete()
                        }
                    }
                }
            }
        }
    }

    @Test
    void testZcard() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zcard', args: [key]]) { reply2 ->
                    assertNumberValue(2, reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testZcount() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zcount', args: [key, '-inf', '+inf']]) { reply3 ->
                        assertNumberValue(3, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZincrby() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zincrby', args: [key, 2, 'one']]) { reply2 ->
                    assertStringValue('3', reply2)
                    testComplete()
                }
            }
        }
    }

    @Test
    void testZinterstore() {
        def key1 = makeKey()
        def key2 = makeKey()
        def key3 = makeKey()

        redis([command: 'zadd', args: [key1, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key1, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key2, 1, 'one']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zadd', args: [key2, 2, 'two']]) { reply3 ->
                        assertNumberValue(1, reply3)
                        redis([command: 'zadd', args: [key2, 3, 'three']]) { reply4 ->
                            assertNumberValue(1, reply4)
                            redis([command: 'zinterstore', args: [key3, 2, key1, key2, 'weights', 2, 3]]) { reply5 ->
                                assertNumberValue(2, reply5)
                                testComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testZrange() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zrange', args: [key, 0, -1]]) { reply3 ->
                        assertArrayValue(['one', 'two', 'three'], reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZrangebyscore() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zrangebyscore', args: [key, '-inf', '+inf']]) { reply3 ->
                        assertArrayValue(['one', 'two', 'three'], reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZrank() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zrank', args: [key, 'three']]) { reply3 ->
                        assertNumberValue(2, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZrem() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zrem', args: [key, 'two']]) { reply3 ->
                        assertNumberValue(1, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZremrangebyrank() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zremrangebyrank', args: [key, 0, 1]]) { reply3 ->
                        assertNumberValue(2, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZremrangebyscore() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zremrangebyscore', args: [key, '-inf', '(2']]) { reply3 ->
                        assertNumberValue(1, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZrevrange() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zrevrange', args: [key, 0, -1]]) { reply3 ->
                        assertArrayValue(['three', 'two', 'one'], reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZrevrangebyscore() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zrevrangebyscore', args: [key, '+inf', '-inf']]) { reply3 ->
                        assertArrayValue(['three', 'two', 'one'], reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZrevrank() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key, 3, 'three']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zrevrank', args: [key, 'one']]) { reply3 ->
                        assertNumberValue(2, reply3)
                        testComplete()
                    }
                }
            }
        }
    }

    @Test
    void testZscore() {
        def key = makeKey()
        redis([command: 'zadd', args: [key, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zscore', args: [key, 'one']]) { reply1 ->
                assertStringValue('1', reply1)
                testComplete()
            }
        }
    }

    @Test
    void testZunionstore() {
        def key1 = makeKey()
        def key2 = makeKey()
        def key3 = makeKey()

        redis([command: 'zadd', args: [key1, 1, 'one']]) { reply0 ->
            assertNumberValue(1, reply0)
            redis([command: 'zadd', args: [key1, 2, 'two']]) { reply1 ->
                assertNumberValue(1, reply1)
                redis([command: 'zadd', args: [key2, 1, 'one']]) { reply2 ->
                    assertNumberValue(1, reply2)
                    redis([command: 'zadd', args: [key2, 2, 'two']]) { reply3 ->
                        assertNumberValue(1, reply3)
                        redis([command: 'zadd', args: [key2, 3, 'three']]) { reply4 ->
                            assertNumberValue(1, reply4)
                            redis([command: 'zunionstore', args: [key3, 2, key1, key2, 'weights', 2, 3]]) { reply5 ->
                                assertNumberValue(3, reply5)
                                testComplete()
                            }
                        }
                    }
                }
            }
        }
    }
}