package io.vertx.test.redis;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisService;
import io.vertx.test.core.VertxTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class RedisServiceTest extends VertxTestBase {

    RedisService redis;

    @Before
    public void before() throws Exception {
        setUp();
        JsonObject config = new JsonObject();
        redis = RedisService.create(vertx, config);
        redis.start();
    }

    @After
    public void after() {
        redis.stop();
    }

    private static JsonArray p(final Object... params) {
        JsonArray parameters = null;

        if (params != null) {
            parameters = new JsonArray();
            for (Object o : params) {
                parameters.add(o);
            }
        }

        return parameters;
    }

    private static Object[] a(final Object... params) {
        return params;
    }

    private static String makeKey() {
        return UUID.randomUUID().toString();
    }

    @Test
    public void testAppend() {
        final String key = makeKey();

        redis.del(p(key), reply0 -> {
            assertTrue(reply0.succeeded());

            redis.append(p(key, "Hello"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(5l, reply1.result().longValue());

                redis.append(p(key, " World"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(11l, reply2.result().longValue());

                    redis.get(p(key), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertTrue(reply3.succeeded());
                        assertEquals("Hello World", reply3.result());
                        testComplete();
                    });
                });
            });
        });

        await();
    }

    @Test
    @Ignore
    public void testAuth() {
        testComplete();
        await();
    }

    @Test
    @Ignore
    public void testBgrewriteaof() {
        testComplete();
        await();
    }

    @Test
    @Ignore
    public void testBgsave() {
        testComplete();
        await();
    }

    @Test
    public void testBitcount() {
        final String key = makeKey();

        redis.set(p(key, "foobar"), reply0 -> {
            assertTrue(reply0.succeeded());

            redis.bitcount(p(key), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(26, reply1.result().longValue());

                redis.bitcount(p(key, 0, 0), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(4, reply2.result().longValue());

                    redis.bitcount(p(key, 1, 1), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(6, reply3.result().longValue());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testBitop() {
        final String key1 = makeKey();
        final String key2 = makeKey();
        final String destkey = makeKey();

        redis.set(p(key1, "foobar"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.set(p(key2, "abcdef"), reply1 -> {
                assertTrue(reply1.succeeded());
                redis.bitop(p("and", destkey, key1, key2), reply2 -> {
                    assertTrue(reply2.succeeded());
                    redis.get(p(destkey), reply3 -> {
                        assertTrue(reply3.succeeded());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testBlpop() {
        final String list1 = makeKey();
        final String list2 = makeKey();

        redis.del(p(list1, list2), reply0 -> { assertTrue(reply0.succeeded());

            redis.rpush(p(list1, "a", "b", "c"), reply1 -> { assertTrue(reply1.succeeded());
                assertEquals(3, reply1.result().longValue());

                redis.blpop(p(list1, list2, 0), reply2 -> { assertTrue(reply2.succeeded());
                    assertArrayEquals(a(list1, "a"), reply2.result().toArray());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testBrpop() {
        final String list1 = makeKey();
        final String list2 = makeKey();

        redis.del(p(list1, list2), reply0 -> { assertTrue(reply0.succeeded());

            redis.rpush(p(list1, "a", "b", "c"), reply1 -> { assertTrue(reply1.succeeded());
                assertEquals(3, reply1.result().longValue());

                redis.brpop(p(list1, list2, 0), reply2 -> { assertTrue(reply2.succeeded());
                    assertArrayEquals(a(list1, "c"), reply2.result().toArray());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testBrpoplpush() {
        testComplete();
        await();
    }

    @Test
    public void testClientKill() {
        testComplete();
        await();
    }

    @Test
    public void testClientList() {
        testComplete();
        await();
    }

    @Test
    public void testClientGetname() {
        testComplete();
        await();
    }

    @Test
    public void testClientSetname() {
        testComplete();
        await();
    }

//    @Test
//    public void testConfigGet() {
//        redis([command:"config get", args:["*max-*-entries*"]]){
//            reply0 ->
//                    // 1) "hash-max-zipmap-entries"
//                    // 2) "512"
//                    // 3) "list-max-ziplist-entries"
//                    // 4) "512"
//                    // 5) "set-max-intset-entries"
//                    // 6) "512"
//                    testComplete();
//        });
//        await();
//    }

    @Test
    public void testConfigSet() {
        testComplete();
        await();
    }

    @Test
    public void testConfigResetstat() {
        testComplete();
        await();
    }

    @Test
    public void testDbsize() {
        testComplete();
        await();
    }

    @Test
    public void testDebugObject() {
        testComplete();
        await();
    }

    @Test
    public void testDebugSegfault() {
        testComplete();
        await();
    }

    @Test
    public void testDecr() {
        final String mykey = makeKey();

        redis.set(p(mykey, "10"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.decr(p(mykey), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(9, reply1.result().longValue());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testDecrby() {
        final String mykey = makeKey();

        redis.set(p(mykey, "10"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.decrby(p(mykey, 5), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(5, reply1.result().longValue());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testDel() {
        final String key1 = makeKey();
        final String key2 = makeKey();
        final String key3 = makeKey();

        redis.set(p(key1, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.set(p(key2, "World"), reply1 -> {
                assertTrue(reply1.succeeded());
                redis.del(p(key1, key2, key3), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(2, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testDiscard() {
        testComplete();
        await();
    }

//    @Test
//    public void testDump() {
//        final String mykey = makeKey();
//
//        redis.set(p(mykey, 10), reply0 -> { assertTrue(reply0.succeeded());
//            redis.dump(p(mykey), reply1 -> { assertTrue(reply1.succeeded());
//                try {
//                    byte[] data = reply1.result().getBytes("ISO-8859-1");
//
//                    assertEquals((byte) data[0], (byte) 0);
//                    assertEquals((byte) data[1], (byte) 0xc0);
//                    assertEquals((byte) data[2], (byte) '\n');
//                    assertEquals((byte) data[3], (byte) 6);
//                    assertEquals((byte) data[4], (byte) 0);
//                    assertEquals((byte) data[5], (byte) 0xf8);
//                    assertEquals((byte) data[6], (byte) 'r');
//                    assertEquals((byte) data[7], (byte) '?');
//                    assertEquals((byte) data[8], (byte) 0xc5);
//                    assertEquals((byte) data[9], (byte) 0xfb);
//                    assertEquals((byte) data[10], (byte) 0xfb);
//                    assertEquals((byte) data[11], (byte) '_');
//                    assertEquals((byte) data[12], (byte) '(');
//                    testComplete();
//                } catch (UnsupportedEncodingException e) {
//                    fail(e.getMessage());
//                }
//            });
//        });
//        await();
//    }

    @Test
    public void testEcho() {
        redis.echo(p("Hello World!"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals("Hello World!", reply0.result());
            testComplete();
        });
        await();
    }

//    @Test
//    public void testEval() {
//        final String key1 = makeKey();
//        final String key2 = makeKey();
//
//        redis.eval(p("return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}", 2, key1, key2, "first", "second"), reply0 -> { assertTrue(reply0.succeeded());
//            assertArrayEquals(a(key1, key2, "first", "second"), reply0.result().toArray());
//            testComplete();
//        });
//        await();
//    }

    @Test
    public void testEvalsha() {
        testComplete();
        await();
    }

    @Test
    public void testExec() {
        testComplete();
        await();
    }

//    @Test
//    public void testExists() {
//        final String key1 = makeKey();
//        final String key2 = makeKey();
//
//        redis([command:"set", args:[key1, "Hello"]]){
//            reply0 ->
//                    redis([command:"exists", args:[key1]]){
//                reply1 ->
//                        assertEquals(1, reply1.result().longValue());
//
//                redis([command:"exists", args:[key2]]){
//                    reply2 ->
//                            assertEquals(0, reply2.result().longValue());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testExpire() {
        final String mykey = makeKey();

        redis.set(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.expire(p(mykey, 10), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());

                redis.ttl(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(10, reply2.result().longValue());

                    redis.set(p(mykey, "Hello World"), reply3 -> {
                        assertTrue(reply3.succeeded());
                        redis.ttl(p(mykey), reply4 -> {
                            assertTrue(reply4.succeeded());
                            assertEquals(-1, reply4.result().longValue());
                            testComplete();
                        });
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testExpireat() {
        final String mykey = makeKey();

        redis.set(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.exists(p(mykey), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());

                redis.expireat(p(mykey, 1293840000), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(1, reply2.result().longValue());

                    redis.exists(p(mykey), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(0, reply3.result().longValue());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testFlushall() {
        testComplete();
        await();
    }

    @Test
    public void testFlushdb() {
        testComplete();
        await();
    }

    @Test
    public void testGet() {
        final String nonexisting = makeKey();
        final String mykey = makeKey();

        redis.get(p(nonexisting), reply0 -> {
            assertTrue(reply0.succeeded());
            assertNull(reply0.result());

            redis.set(p(mykey, "Hello"), reply1 -> {
                assertTrue(reply1.succeeded());
                redis.get(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("Hello", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testGetbit() {
        final String mykey = makeKey();

        redis.setbit(p(mykey, 7, 1), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(0, reply0.result().longValue());

            redis.getbit(p(mykey, 0), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(0, reply1.result().longValue());

                redis.getbit(p(mykey, 7), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(1, reply2.result().longValue());

                    redis.getbit(p(mykey, 100), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(0, reply3.result().longValue());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testGetrange() {
        final String mykey = makeKey();

        redis.set(p(mykey, "This is a string"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.getrange(p(mykey, 0, 3), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals("This", reply1.result());

                redis.getrange(p(mykey, -3, -1), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("ing", reply2.result());

                    redis.getrange(p(mykey, 0, -1), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals("This is a string", reply3.result());

                        redis.getrange(p(mykey, 10, 100), reply4 -> {
                            assertTrue(reply4.succeeded());
                            assertEquals("string", reply4.result());
                            testComplete();
                        });
                    });
                });
            });
        });
        await();
    }

//    @Test
//    public void testGetset() {
//        final String mycounter = makeKey();
//
//        redis.incr(p(mycounter), reply0 -> {
//            assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//
//            redis.getset(p(mycounter, "0"), reply1 -> {
//                assertTrue(reply1.succeeded());
//                assertEquals("1", reply1.result());
//
//                redis.get(p(mycounter), reply2 -> {
//                    assertTrue(reply2.succeeded());
//                    assertEquals("0", reply2.result());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testHdel() {
        final String myhash = makeKey();

        redis.hset(p(myhash, "field1", "foo"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.hdel(p(myhash, "field1"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());

                redis.hdel(p(myhash, "field2"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(0, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testHexists() {
        final String myhash = makeKey();

        redis.hset(p(myhash, "field1", "foo"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.hexists(p(myhash, "field1"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());

                redis.hexists(p(myhash, "field2"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(0, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testHget() {
        final String myhash = makeKey();

        redis.hset(p(myhash, "field1", "foo"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.hget(p(myhash, "field1"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals("foo", reply1.result());

                redis.hget(p(myhash, "field2"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertNull(reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

//    @Test
//    public void testHgetall() {
//        final String myhash = makeKey();
//
//        redis.hset(p(myhash, "field1", "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//
//            redis.hset(p(myhash, "field2", "World"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//
//                redis.hgetall(p(myhash), reply2 -> { assertTrue(reply2.succeeded());
//                    JsonObject obj = reply2.body.getObject("value")
//                    assertEquals("Hello", obj.getField("field1"))
//                    assertEquals("World", obj.getField("field2"))
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testHincrby() {
        final String myhash = makeKey();

        redis.hset(p(myhash, "field", 5), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.hincrby(p(myhash, "field", 1), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(6, reply1.result().longValue());

                redis.hincrby(p(myhash, "field", -1), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(5, reply2.result().longValue());

                    redis.hincrby(p(myhash, "field", -10), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(-5, reply3.result().longValue());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testHIncrbyfloat() {
        final String mykey = makeKey();

        redis.hset(p(mykey, "field", 10.50), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.hincrbyfloat(p(mykey, "field", 0.1), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals("10.6", reply1.result());

                redis.hset(p(mykey, "field", 5.0e3), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(0, reply2.result().longValue());

                    redis.hincrbyfloat(p(mykey, "field", 2.0e2), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals("5200", reply3.result());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

//    @Test
//    public void testHkeys() {
//        final String myhash = makeKey();
//
//        redis.hset(p(myhash, "field1", "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//
//            redis.hset(p(myhash, "field2", "World"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//
//                redis.hkeys(p(myhash), reply2 -> { assertTrue(reply2.succeeded());
//                    assertArrayEquals(a("field1", "field2"), reply2.result().toArray());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testHlen() {
        final String myhash = makeKey();

        redis.hset(p(myhash, "field1", "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.hset(p(myhash, "field2", "World"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());

                redis.hlen(p(myhash), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(2, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

//    @Test
//    public void testHmget() {
//        final String myhash = makeKey();
//
//        redis.hset(p(myhash, "field1", "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//
//            redis.hset(p(myhash, "field2", "World"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//
//                redis.hmget(p(myhash, "field1", "field2", "nofield"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertArrayEquals(a("Hello", "World", null), reply2.result().toArray());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testHmset() {
        final String myhash = makeKey();

        redis.hmset(p(myhash, "field1", "Hello", "field2", "World"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.hget(p(myhash, "field1"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals("Hello", reply1.result());
                redis.hget(p(myhash, "field2"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("World", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testHset() {
        final String myhash = makeKey();

        redis.hset(p(myhash, "field1", "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.hget(p(myhash, "field1"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals("Hello", reply1.result());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testHsetnx() {
        final String myhash = makeKey();

        redis.hsetnx(p(myhash, "field", "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.hsetnx(p(myhash, "field", "World"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(0, reply1.result().longValue());

                redis.hget(p(myhash, "field"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("Hello", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

//    @Test
//    public void testHvals() {
//        final String myhash = makeKey();
//
//        redis.hset(p(myhash, "field1", "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//
//            redis.hset(p(myhash, "field2", "World"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//
//                redis.hvals(p(myhash), reply2 -> { assertTrue(reply2.succeeded());
//                    assertArrayEquals(a("Hello", "World"), reply2.result().toArray());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testIncr() {
        final String mykey = makeKey();

        redis.set(p(mykey, "10"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.incr(p(mykey), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(11, reply1.result().longValue());

                redis.get(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("11", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testIncrby() {
        final String mykey = makeKey();

        redis.set(p(mykey, "10"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.incrby(p(mykey, 5), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(15, reply1.result().longValue());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testIncrbyfloat() {
        final String mykey = makeKey();

        redis.set(p(mykey, 10.50), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.incrbyfloat(p(mykey, 0.1), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals("10.6", reply1.result());

                redis.set(p(mykey, 5.0e3), reply2 -> {
                    assertTrue(reply2.succeeded());
                    redis.incrbyfloat(p(mykey, 2.0e2), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals("5200", reply3.result());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

//    @Test
//    public void testInfo() {
//        redis([command:"info" /*, section: "server"*/]){
//            reply0 ->
//                    assertNotNullValue(reply0)
//            testComplete();
//        });
//        await();
//    }

//    @Test
//    public void testKeys() {
//        redis.mset(p("one", 1, "two", 2, "three", 3, "four", 4), reply0 -> { assertTrue(reply0.succeeded());
//            redis.keys(p("*o*"), reply1 -> { assertTrue(reply1.succeeded());
//                def array = reply1.body.getArray("value")
//                // this is because there are leftovers from previous tests
//                assertTrue(3 <= array.size())
//
//                redis.keys(p("t??"), reply2 -> { assertTrue(reply2.succeeded());
//                    def array2 = reply2.body.getArray("value")
//                    assertTrue(1 == array2.size())
//
//                    redis.keys(p("*"), reply3 -> { assertTrue(reply3.succeeded());
//                        def array3 = reply3.body.getArray("value")
//                        assertTrue(4 <= array3.size())
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testLastsave() {
//        redis([command:"lastsave"]){
//            reply0 ->
//                    testComplete();
//        });
//        await();
//    }

    @Test
    public void testLindex() {
        final String mykey = makeKey();

        redis.lpush(p(mykey, "World"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.lpush(p(mykey, "Hello"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(2, reply1.result().longValue());

                redis.lindex(p(mykey, 0), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("Hello", reply2.result());

                    redis.lindex(p(mykey, -1), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals("World", reply3.result());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testLinsert() {
        final String mykey = makeKey();

        redis.rpush(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());

            redis.rpush(p(mykey, "World"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(2, reply1.result().longValue());

                redis.linsert(p(mykey, "before", "World", "There"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(3, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testLlen() {
        final String mykey = makeKey();
        redis.lpush(p(mykey, "World"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.lpush(p(mykey, "Hello"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(2, reply1.result().longValue());
                redis.llen(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(2, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testLpop() {
        final String mykey = makeKey();
        redis.rpush(p(mykey, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.rpush(p(mykey, "two"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(2, reply1.result().longValue());
                redis.rpush(p(mykey, "three"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(3, reply2.result().longValue());
                    redis.lpop(p(mykey), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals("one", reply3.result());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

//    @Test
//    public void testLpush() {
//        final String mykey = makeKey();
//        redis.lpush(p(mykey, "world"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.lpush(p(mykey, "hello"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.lrange(p(mykey, 0, -1), reply2 -> { assertTrue(reply2.succeeded());
//                    assertArrayEquals(a("hello", "world"), reply2.result().toArray());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testLpushx() {
//        final String mykey = makeKey();
//        final String myotherkey = makeKey();
//
//        redis.lpush(p(mykey, "World"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.lpushx(p(mykey, "Hello"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.lpushx(p(myotherkey, "Hello"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(0, reply2.result().longValue());
//                    redis.lrange(p(mykey, 0, -1), reply3 -> { assertTrue(reply3.succeeded());
//                        def array3 = reply3.body.getArray("value")
//                        assertTrue(2 == array3.size())
//
//                        assertTrue("Hello".equals(array3.get(0)))
//                        assertTrue("World".equals(array3.get(1)))
//                        redis([command:"lrange", args:[myotherkey, 0, -1]]){
//                            reply4 ->
//                                    def array4 = reply4.body.getArray("value")
//                            assertTrue(0 == array4.size())
//                            testComplete();
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testLrange() {
//        final String mykey = makeKey();
//        redis.rpush(p(mykey, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.rpush(p(mykey, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.rpush(p(mykey, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(3, reply2.result().longValue());
//                    redis.lrange(p(mykey, 0, 0), reply3 -> { assertTrue(reply3.succeeded());
//                        assertArrayValue(["one"], reply3)
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testLrem() {
//        final String mykey = makeKey();
//        redis.rpush(p(mykey, "hello"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.rpush(p(mykey, "hello"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.rpush(p(mykey, "foo"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(3, reply2.result().longValue());
//                    redis.rpush(p(mykey, "hello"), reply3 -> { assertTrue(reply3.succeeded());
//                        assertEquals(4, reply3.result().longValue());
//                        redis.lrem(p(mykey, -2, "hello"), reply4 -> { assertTrue(reply4.succeeded());
//                            assertEquals(2, reply4.result().longValue());
//                            redis.lrange(p(mykey, 0, -1), reply5 -> { assertTrue(reply5.succeeded());
//                                assertArrayEquals(a("hello", "foo"), reply5.result().toArray());
//                                testComplete();
//                            });
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testLset() {
//        final String mykey = makeKey();
//        redis.rpush(p(mykey, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.rpush(p(mykey, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.rpush(p(mykey, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(3, reply2.result().longValue());
//                    redis.lset(p(mykey, 0, "four"), reply3 -> { assertTrue(reply3.succeeded());
//                        redis.lset(p(mykey, -2, "five"), reply4 -> { assertTrue(reply4.succeeded());
//                            redis.lrange(p(mykey, 0, -1), reply5 -> { assertTrue(reply5.succeeded());
//                                assertArrayEquals(a("four", "five", "three"), reply5.result().toArray());
//                                testComplete();
//                            });
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testLtrim() {
//        final String mykey = makeKey();
//        redis.rpush(p(mykey, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.rpush(p(mykey, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.rpush(p(mykey, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(3, reply2.result().longValue());
//                    redis.ltrim(p(mykey, 1, -1), reply3 -> { assertTrue(reply3.succeeded());
//                        redis.lrange(p(mykey, 0, -1), reply5 -> { assertTrue(reply5.succeeded());
//                            assertArrayEquals(a("two", "three"), reply5.result().toArray());
//                            testComplete();
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testMget() {
//        final String mykey1 = makeKey();
//        final String mykey2 = makeKey();
//        redis.set(p(mykey1, "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            redis.set(p(mykey2, "World"), reply1 -> { assertTrue(reply1.succeeded());
//                redis.mget(p(mykey1, mykey2, "nonexisting"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertArrayEquals(a("Hello", "World", null), reply2.result().toArray());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testMigrate() {
        testComplete();
        await();
    }

    @Test
    public void testMonitor() {
        testComplete();
        await();
    }

    @Test
    public void testMove() {
        testComplete();
        await();
    }

    @Test
    public void testMset() {
        final String mykey1 = makeKey();
        final String mykey2 = makeKey();
        redis.mset(p(mykey1, "Hello", mykey2, "World"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.get(p(mykey1), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals("Hello", reply1.result());
                redis.get(p(mykey2), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("World", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

//    @Test
//    public void testMsetnx() {
//        final String mykey1 = makeKey();
//        final String mykey2 = makeKey();
//        final String mykey3 = makeKey();
//
//        redis.msetnx(p(mykey1, "Hello", mykey2, "there"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.msetnx(p(mykey2, "there", mykey3, "world"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(0, reply1.result().longValue());
//                redis.mget(p(mykey1, mykey2, mykey3), reply2 -> { assertTrue(reply2.succeeded());
//                    assertArrayEquals(a("Hello", "there", null), reply2.result().toArray());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testMulti() {
        testComplete();
        await();
    }

    @Test
    public void testObject() {
        testComplete();
        await();
    }

    @Test
    public void testPersist() {
        final String mykey = makeKey();
        redis.set(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.expire(p(mykey, 10), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.ttl(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(10, reply2.result().longValue());
                    redis.persist(p(mykey), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(1, reply3.result().longValue());
                        redis.ttl(p(mykey), reply4 -> {
                            assertTrue(reply4.succeeded());
                            assertEquals(-1, reply4.result().longValue());
                            testComplete();
                        });
                    });
                });
            });
        });
        await();
    }

//    @Test
//    public void testPexpire() {
//        final String mykey = makeKey();
//        redis.set(p(mykey, "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            redis.pexpire(p(mykey, 1500), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.ttl(p(mykey), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.pttl(p(mykey), reply3 -> { assertTrue(reply3.succeeded());
//                        assertTrue(1500 > reply3.body.getNumber("value") && reply3.body.getNumber("value") > 0)
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testPexpireat() {
//        final String mykey = makeKey();
//        redis.set(p(mykey, "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            redis.pexpireat(p(mykey, 1555555555005), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.ttl(p(mykey), reply2 -> { assertTrue(reply2.succeeded());
//                    assertTrue(200000000 > reply2.body.getNumber("value") && reply2.body.getNumber("value") > 0)
//                    redis.pttl(p(mykey), reply3 -> { assertTrue(reply3.succeeded());
//                        assertTrue(1555555555005 > reply3.body.getNumber("value") && reply3.body.getNumber("value") > 0)
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testPing() {
//        redis([command:"ping"]){
//            reply0 ->
//                    assertEquals("PONG", reply0.result());
//            testComplete();
//        });
//        await();
//    }

//    @Test
//    public void testPsetex() {
//        final String mykey = makeKey();
//        redis.psetex(p(mykey, 1000, "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            redis.pttl(p(mykey), reply1 -> { assertTrue(reply1.succeeded());
//                assertTrue(1500 > reply1.body.getNumber("value") && reply1.body.getNumber("value") > 0)
//                redis.get(p(mykey), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals("Hello", reply2.result());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testPsubscribe() {
        testComplete();
        await();
    }

//    @Test
//    public void testPttl() {
//        final String mykey = makeKey();
//        redis.set(p(mykey, "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            redis.expire(p(mykey, 1), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.pttl(p(mykey), reply2 -> { assertTrue(reply2.succeeded());
//                    assertTrue(1000 > reply2.body.getNumber("value") && reply2.body.getNumber("value") > 0)
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testPublish() {
        testComplete();
        await();
    }

    @Test
    public void testPunsubscribe() {
        testComplete();
        await();
    }

    @Test
    public void testQuit() {
        testComplete();
        await();
    }

    @Test
    public void testRandomkey() {
        testComplete();
        await();
    }

    @Test
    public void testRename() {
        final String mykey = makeKey();
        final String myotherkey = makeKey();

        redis.set(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.rename(p(mykey, myotherkey), reply1 -> {
                assertTrue(reply1.succeeded());
                redis.get(p(myotherkey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("Hello", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testRenamenx() {
        final String mykey = makeKey();
        final String myotherkey = makeKey();

        redis.set(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.set(p(myotherkey, "World"), reply1 -> {
                assertTrue(reply1.succeeded());
                redis.renamenx(p(mykey, myotherkey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(0, reply2.result().longValue());
                    redis.get(p(myotherkey), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals("World", reply3.result());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testRestore() {
        testComplete();
        await();
    }

//    @Test
//    public void testRpop() {
//        final String mykey = makeKey();
//        redis.rpush(p(mykey, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.rpush(p(mykey, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.rpush(p(mykey, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(3, reply2.result().longValue());
//                    redis.rpop(p(mykey), reply3 -> { assertTrue(reply3.succeeded());
//                        assertEquals("three", reply3.result());
//                        redis.lrange(p(mykey, 0, -1), reply5 -> { assertTrue(reply5.succeeded());
//                            assertArrayEquals(a("one", "two"), reply5.result().toArray());
//                            testComplete();
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testRpoplpush() {
//        final String mykey = makeKey();
//        final String myotherkey = makeKey();
//        redis.rpush(p(mykey, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.rpush(p(mykey, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.rpush(p(mykey, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(3, reply2.result().longValue());
//                    redis.rpoplpush(p(mykey, myotherkey), reply3 -> { assertTrue(reply3.succeeded());
//                        assertEquals("three", reply3.result());
//                        redis.lrange(p(mykey, 0, -1), reply5 -> { assertTrue(reply5.succeeded());
//                            assertArrayEquals(a("one", "two"), reply5.result().toArray());
//                            redis([command:"lrange", args:[myotherkey, 0, -1]]){
//                                reply6 ->
//                                        assertArrayValue(["three"], reply6)
//                                testComplete();
//                            });
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testRpush() {
//        final String mykey = makeKey();
//        redis.rpush(p(mykey, "hello"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.rpush(p(mykey, "world"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.lrange(p(mykey, 0, -1), reply2 -> { assertTrue(reply2.succeeded());
//                    assertArrayEquals(a("hello", "world"), reply2.result().toArray());
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testRpushx() {
//        final String mykey = makeKey();
//        final String myotherkey = makeKey();
//        redis.rpush(p(mykey, "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.rpushx(p(mykey, "World"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(2, reply1.result().longValue());
//                redis.rpushx(p(myotherkey, "World"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(0, reply2.result().longValue());
//                    redis.lrange(p(mykey, 0, -1), reply3 -> { assertTrue(reply3.succeeded());
//                        assertArrayEquals(a("Hello", "World"), reply3.result().toArray());
//                        redis([command:"lrange", args:[myotherkey, 0, -1]]){
//                            reply4 ->
//                                    assertArrayValue([],reply4)
//                            testComplete();
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testSadd() {
//        final String mykey = makeKey();
//        redis.sadd(p(mykey, "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.sadd(p(mykey, "World"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.sadd(p(mykey, "World"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(0, reply2.result().longValue());
//                    redis.smembers(p(mykey), reply3 -> { assertTrue(reply3.succeeded());
//                        assertUnorderedArrayValue(["World", "Hello"],reply3)
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testSave() {
        testComplete();
        await();
    }

    @Test
    public void testScard() {
        final String mykey = makeKey();
        redis.sadd(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.sadd(p(mykey, "World"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.scard(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(2, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testScriptexists() {
        testComplete();
        await();
    }

    @Test
    public void testScriptflush() {
        testComplete();
        await();
    }

    @Test
    public void testScriptkill() {
        testComplete();
        await();
    }

    @Test
    public void testScriptload() {
        testComplete();
        await();
    }

//    @Test
//    public void testSdiff() {
//        final String mykey1 = makeKey();
//        final String mykey2 = makeKey();
//
//        redis.sadd(p(mykey1, "a"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.sadd(p(mykey1, "b"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.sadd(p(mykey1, "c"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.sadd(p(mykey2, "c"), reply3 -> { assertTrue(reply3.succeeded());
//                        assertEquals(1, reply3.result().longValue());
//                        redis.sadd(p(mykey2, "d"), reply4 -> { assertTrue(reply4.succeeded());
//                            assertEquals(1, reply4.result().longValue());
//                            redis.sadd(p(mykey2, "e"), reply5 -> { assertTrue(reply5.succeeded());
//                                assertEquals(1, reply5.result().longValue());
//                                redis.sdiff(p(mykey1, mykey2), reply6 -> { assertTrue(reply6.succeeded());
//                                    assertUnorderedArrayValue(["a", "b"],reply6)
//                                    testComplete();
//                                });
//                            });
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testSdiffstore() {
        testComplete();
        await();
    }

    @Test
    public void testSelect() {
        testComplete();
        await();
    }

    @Test
    public void testSet() {
        final String mykey = makeKey();
        redis.set(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.get(p(mykey), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals("Hello", reply1.result());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testSetbit() {
        final String mykey = makeKey();
        redis.setbit(p(mykey, 7, 1), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(0, reply0.result().longValue());
            redis.setbit(p(mykey, 7, 0), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.get(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("\u0000", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testSetex() {
        final String mykey = makeKey();
        redis.setex(p(mykey, 10, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.ttl(p(mykey), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(10, reply1.result().longValue());
                redis.get(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("Hello", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testSetnx() {
        final String mykey = makeKey();
        redis.setnx(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.setnx(p(mykey, "World"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(0, reply1.result().longValue());
                redis.get(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("Hello", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testSetrange() {
        final String mykey = makeKey();
        redis.set(p(mykey, "Hello World"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.setrange(p(mykey, 6, "Redis"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(11, reply1.result().longValue());
                redis.get(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("Hello Redis", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testShutdown() {
        testComplete();
        await();
    }

//    @Test
//    public void testSinter() {
//        final String mykey1 = makeKey();
//        final String mykey2 = makeKey();
//
//        redis.sadd(p(mykey1, "a"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.sadd(p(mykey1, "b"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.sadd(p(mykey1, "c"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.sadd(p(mykey2, "c"), reply3 -> { assertTrue(reply3.succeeded());
//                        assertEquals(1, reply3.result().longValue());
//                        redis.sadd(p(mykey2, "d"), reply4 -> { assertTrue(reply4.succeeded());
//                            assertEquals(1, reply4.result().longValue());
//                            redis.sadd(p(mykey2, "e"), reply5 -> { assertTrue(reply5.succeeded());
//                                assertEquals(1, reply5.result().longValue());
//                                redis.sinter(p(mykey1, mykey2), reply6 -> { assertTrue(reply6.succeeded());
//                                    assertArrayValue(["c"], reply6)
//                                    testComplete();
//                                });
//                            });
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testSinterstore() {
        testComplete();
        await();
    }

    @Test
    public void testSismember() {
        final String mykey = makeKey();
        redis.sadd(p(mykey, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.sismember(p(mykey, "one"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.sismember(p(mykey, "two"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(0, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testSlaveof() {
        testComplete();
        await();
    }

    @Test
    public void testSlowlog() {
        testComplete();
        await();
    }

//    @Test
//    public void testSmembers() {
//        final String mykey = makeKey();
//        redis.sadd(p(mykey, "Hello"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.sadd(p(mykey, "World"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.smembers(p(mykey), reply2 -> { assertTrue(reply2.succeeded());
//                    assertUnorderedArrayValue(["World", "Hello"],reply2)
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testSmove() {
//        final String mykey = makeKey();
//        final String myotherkey = makeKey();
//        redis.sadd(p(mykey, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.sadd(p(mykey, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.sadd(p(myotherkey, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.smove(p(mykey, myotherkey, "two"), reply3 -> { assertTrue(reply3.succeeded());
//                        assertEquals(1, reply3.result().longValue());
//                        redis.smembers(p(mykey), reply4 -> { assertTrue(reply4.succeeded());
//                            assertUnorderedArrayValue(["one"], reply4)
//                            redis.smembers(p(myotherkey), reply5 -> { assertTrue(reply5.succeeded());
//                                assertUnorderedArrayValue(["two", "three"],reply5)
//                                testComplete();
//                            });
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testSort() {
//        final String mykey = makeKey();
//
//        def k1 = "${mykey}:1".toString()
//        def k2 = "${mykey}:2".toString()
//        def k3 = "${mykey}:3".toString()
//        def kx = "${mykey}:*".toString()
//
//        redis.sadd(p(mykey, "1", "2", "3"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(3, reply0.result().longValue());
//            redis.set(p(k1, "one"), reply1 -> { assertTrue(reply1.succeeded());
//                redis.set(p(k2, "two"), reply2 -> { assertTrue(reply2.succeeded());
//                    redis.set(p(k3, "three"), reply3 -> { assertTrue(reply3.succeeded());
//                        redis.sort(p(mykey, "desc", "get", kx), reply4 -> { assertTrue(reply4.succeeded());
//                            println reply4.body().encode()
//                            assertArrayEquals(a("three", "two", "one"), reply4.result().toArray());
//                            testComplete();
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testSpop() {
//        final String mykey = makeKey();
//        redis.sadd(p(mykey, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.sadd(p(mykey, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.sadd(p(mykey, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.spop(p(mykey), reply3 -> { assertTrue(reply3.succeeded());
//                        def ret = reply3.body.getString("value")
//                        assertTrue(ret.equals("one") || ret.equals("two") || ret.equals("three"))
//                        def expected =[]
//                        if (!ret.equals("one")) {
//                            expected.add("one")
//                        });
//                        if (!ret.equals("two")) {
//                            expected.add("two")
//                        });
//                        if (!ret.equals("three")) {
//                            expected.add("three")
//                        });
//                        redis.smembers(p(mykey), reply4 -> { assertTrue(reply4.succeeded());
//                            assertUnorderedArrayValue(expected, reply4)
//                            testComplete();
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testSrandmember() {
//        final String mykey = makeKey();
//        redis.sadd(p(mykey, "one", "two", "three"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(3, reply0.result().longValue());
//            redis.srandmember(p(mykey), reply1 -> { assertTrue(reply1.succeeded());
//                def randmember = reply1.body.getString("value");
//                assertTrue(randmember.equals("one") || randmember.equals("two") || randmember.equals("three"))
//                testComplete();
//            });
//        });
//        await();
//    }

    @Test
    public void testSrem() {
        final String mykey = makeKey();
        redis.sadd(p(mykey, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.sadd(p(mykey, "two"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.sadd(p(mykey, "three"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(1, reply2.result().longValue());
                    redis.srem(p(mykey, "one"), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(1, reply3.result().longValue());
                        redis.srem(p(mykey, "four"), reply4 -> {
                            assertTrue(reply4.succeeded());
                            assertEquals(0, reply4.result().longValue());
                            testComplete();
                        });
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testStrlen() {
        final String mykey = makeKey();
        redis.set(p(mykey, "Hello world"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.strlen(p(mykey), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(11, reply1.result().longValue());
                redis.strlen(p("nonexisting"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(0, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testSubscribe() {
        testComplete();
        await();
    }

//    @Test
//    public void testSunion() {
//        final String mykey1 = makeKey();
//        final String mykey2 = makeKey();
//
//        redis.sadd(p(mykey1, "a"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.sadd(p(mykey1, "b"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//            });
//            redis.sadd(p(mykey1, "c"), reply2 -> { assertTrue(reply2.succeeded());
//                assertEquals(1, reply2.result().longValue());
//            });
//            redis.sadd(p(mykey2, "c"), reply3 -> { assertTrue(reply3.succeeded());
//                assertEquals(1, reply3.result().longValue());
//            });
//            redis.sadd(p(mykey2, "d"), reply4 -> { assertTrue(reply4.succeeded());
//                assertEquals(1, reply4.result().longValue());
//            });
//            redis.sadd(p(mykey2, "e"), reply5 -> { assertTrue(reply5.succeeded());
//                assertEquals(1, reply5.result().longValue());
//                redis.sunion(p(mykey1, mykey2), reply6 -> { assertTrue(reply6.succeeded());
//                    def arr = reply6.body.getArray("value")
//                    assertTrue(arr.size() == 5)
////                    assertArrayValue(["a", "b", "c", "d", "e"], reply6)
//                    testComplete();
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testSunionstore() {
        testComplete();
        await();
    }

    @Test
    public void testSync() {
        testComplete();
        await();
    }

//    @Test
//    public void testTime() {
//        redis([command:"time"]){
//            reply0 ->
//                    assertTrue(reply0.body.getArray("value").size() == 2)
//            testComplete();
//        });
//        await();
//    }

    @Test
    public void testTtl() {
        final String mykey = makeKey();
        redis.set(p(mykey, "Hello"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.expire(p(mykey, 10), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.ttl(p(mykey), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(10, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testType() {
        final String key1 = makeKey();
        final String key2 = makeKey();
        final String key3 = makeKey();

        redis.set(p(key1, "value"), reply0 -> {
            assertTrue(reply0.succeeded());
            redis.lpush(p(key2, "value"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.sadd(p(key3, "value"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(1, reply2.result().longValue());
                    redis.type(p(key1), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals("string", reply3.result());
                        redis.type(p(key2), reply4 -> {
                            assertTrue(reply4.succeeded());
                            assertEquals("list", reply4.result());
                            redis.type(p(key3), reply5 -> {
                                assertTrue(reply5.succeeded());
                                assertEquals("set", reply5.result());
                                testComplete();
                            });
                        });
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testUnsubscribe() {
        testComplete();
        await();
    }

    @Test
    public void testUnwatch() {
        testComplete();
        await();
    }

    @Test
    public void testWatch() {
        testComplete();
        await();
    }

//    @Test
//    public void testZadd() {
//        final String key = makeKey();
//        redis.zadd(p(key, 1, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.zadd(p(key, 1, "uno"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.zadd(p(key, 2, "two"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.zadd(p(key, 3, "two"), reply3 -> { assertTrue(reply3.succeeded());
//                        assertEquals(0, reply3.result().longValue());
//                        redis.zrange(p(key, 0, -1, "withscores"), reply4 -> { assertTrue(reply4.succeeded());
//                            assertArrayEquals(a("one", "1", "uno", "1", "two", "3"), reply4.result().toArray());
//                            testComplete();
//                        });
//                    });
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testZcard() {
        final String key = makeKey();
        redis.zadd(p(key, 1, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.zadd(p(key, 2, "two"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.zcard(p(key), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(2, reply2.result().longValue());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testZcount() {
        final String key = makeKey();
        redis.zadd(p(key, 1, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.zadd(p(key, 2, "two"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.zadd(p(key, 3, "three"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(1, reply2.result().longValue());
                    redis.zcount(p(key, "-inf", "+inf"), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(3, reply3.result().longValue());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

    @Test
    public void testZincrby() {
        final String key = makeKey();
        redis.zadd(p(key, 1, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.zadd(p(key, 2, "two"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.zincrby(p(key, 2, "one"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals("3", reply2.result());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testZinterstore() {
        final String key1 = makeKey();
        final String key2 = makeKey();
        final String key3 = makeKey();

        redis.zadd(p(key1, 1, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.zadd(p(key1, 2, "two"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.zadd(p(key2, 1, "one"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(1, reply2.result().longValue());
                    redis.zadd(p(key2, 2, "two"), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(1, reply3.result().longValue());
                        redis.zadd(p(key2, 3, "three"), reply4 -> {
                            assertTrue(reply4.succeeded());
                            assertEquals(1, reply4.result().longValue());
                            redis.zinterstore(p(key3, 2, key1, key2, "weights", 2, 3), reply5 -> {
                                assertTrue(reply5.succeeded());
                                assertEquals(2, reply5.result().longValue());
                                testComplete();
                            });
                        });
                    });
                });
            });
        });
        await();
    }

//    @Test
//    public void testZrange() {
//        final String key = makeKey();
//        redis.zadd(p(key, 1, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.zadd(p(key, 2, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.zadd(p(key, 3, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.zrange(p(key, 0, -1), reply3 -> { assertTrue(reply3.succeeded());
//                        assertArrayEquals(a("one", "two", "three"), reply3.result().toArray());
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testZrangebyscore() {
//        final String key = makeKey();
//        redis.zadd(p(key, 1, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.zadd(p(key, 2, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.zadd(p(key, 3, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.zrangebyscore(p(key, "-inf", "+inf"), reply3 -> { assertTrue(reply3.succeeded());
//                        assertArrayEquals(a("one", "two", "three"), reply3.result().toArray());
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testZrank() {
//        final String key = makeKey();
//        redis.zadd(p(key, 1, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.zadd(p(key, 2, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.zadd(p(key, 3, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.zrank(p(key, "three"), reply3 -> { assertTrue(reply3.succeeded());
//                        assertEquals(2, reply3.result().longValue());
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testZrem() {
        final String key = makeKey();
        redis.zadd(p(key, 1, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.zadd(p(key, 2, "two"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.zadd(p(key, 3, "three"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(1, reply2.result().longValue());
                    redis.zrem(p(key, "two"), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(1, reply3.result().longValue());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

//    @Test
//    public void testZremrangebyrank() {
//        final String key = makeKey();
//        redis.zadd(p(key, 1, "one"), reply0 -> {
//            assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.zadd(p(key, 2, "two"), reply1 -> {
//                assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.zadd(p(key, 3, "three"), reply2 -> {
//                    assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.zremrangebyrank(p(key, 0, 1), reply3 -> {
//                        assertTrue(reply3.succeeded());
//                        assertEquals(2, reply3.result().longValue());
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testZremrangebyscore() {
        final String key = makeKey();
        redis.zadd(p(key, 1, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.zadd(p(key, 2, "two"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.zadd(p(key, 3, "three"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(1, reply2.result().longValue());
                    redis.zremrangebyscore(p(key, "-inf", "(2"), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(1, reply3.result().longValue());
                        testComplete();
                    });
                });
            });
        });
        await();
    }

//    @Test
//    public void testZrevrange() {
//        final String key = makeKey();
//        redis.zadd(p(key, 1, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.zadd(p(key, 2, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.zadd(p(key, 3, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.zrevrange(p(key, 0, -1), reply3 -> { assertTrue(reply3.succeeded());
//                        assertArrayEquals(a("three", "two", "one"), reply3.result().toArray());
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testZrevrangebyscore() {
//        final String key = makeKey();
//        redis.zadd(p(key, 1, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.zadd(p(key, 2, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.zadd(p(key, 3, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.zrevrangebyscore(p(key, "+inf", "-inf"), reply3 -> { assertTrue(reply3.succeeded());
//                        assertArrayEquals(a("three", "two", "one"), reply3.result().toArray());
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

//    @Test
//    public void testZrevrank() {
//        final String key = makeKey();
//        redis.zadd(p(key, 1, "one"), reply0 -> { assertTrue(reply0.succeeded());
//            assertEquals(1, reply0.result().longValue());
//            redis.zadd(p(key, 2, "two"), reply1 -> { assertTrue(reply1.succeeded());
//                assertEquals(1, reply1.result().longValue());
//                redis.zadd(p(key, 3, "three"), reply2 -> { assertTrue(reply2.succeeded());
//                    assertEquals(1, reply2.result().longValue());
//                    redis.zrevrank(p(key, "one"), reply3 -> { assertTrue(reply3.succeeded());
//                        assertEquals(2, reply3.result().longValue());
//                        testComplete();
//                    });
//                });
//            });
//        });
//        await();
//    }

    @Test
    public void testZscore() {
        final String key = makeKey();
        redis.zadd(p(key, 1, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.zscore(p(key, "one"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals("1", reply1.result());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testZunionstore() {
        final String key1 = makeKey();
        final String key2 = makeKey();
        final String key3 = makeKey();

        redis.zadd(p(key1, 1, "one"), reply0 -> {
            assertTrue(reply0.succeeded());
            assertEquals(1, reply0.result().longValue());
            redis.zadd(p(key1, 2, "two"), reply1 -> {
                assertTrue(reply1.succeeded());
                assertEquals(1, reply1.result().longValue());
                redis.zadd(p(key2, 1, "one"), reply2 -> {
                    assertTrue(reply2.succeeded());
                    assertEquals(1, reply2.result().longValue());
                    redis.zadd(p(key2, 2, "two"), reply3 -> {
                        assertTrue(reply3.succeeded());
                        assertEquals(1, reply3.result().longValue());
                        redis.zadd(p(key2, 3, "three"), reply4 -> {
                            assertTrue(reply4.succeeded());
                            assertEquals(1, reply4.result().longValue());
                            redis.zunionstore(p(key3, 2, key1, key2, "weights", 2, 3), reply5 -> {
                                assertTrue(reply5.succeeded());
                                assertEquals(3, reply5.result().longValue());
                                testComplete();
                            });
                        });
                    });
                });
            });
        });
        await();
    }
}