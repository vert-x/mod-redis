package io.vertx.test.redis;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisService;
import io.vertx.test.core.VertxTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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

    private static String makeKey() {
        return UUID.randomUUID().toString();
    }

    @Test
    public void testSetGet() {
        final String key = makeKey();
        redis.set(p(key, "value1"), reply -> {
            assertTrue(reply.succeeded());

            redis.get(p(key), reply2 -> {
                assertTrue(reply2.succeeded());
                assertEquals("value1", reply2.result());
                testComplete();
            });
        });
        await();
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
        await();
        testComplete();
    }

    @Test
    @Ignore
    public void testBgrewriteaof() {
        await();
        testComplete();
    }

    @Test
    @Ignore
    public void testBgsave() {
        await();
        testComplete();
    }

}
