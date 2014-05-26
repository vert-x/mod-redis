package io.vertx.redis;

import io.vertx.redis.impl.ReplyHandler;
import org.junit.Test;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class ReplyParserTester extends TestVerticle {

    @Test
    public void testArrayArrayParser() {
        Buffer b = new Buffer();
        b.appendString(
                "*2\r\n" +
                "*3\r\n" +
                ":1\r\n" +
                ":2\r\n" +
                ":3\r\n" +
                "*2\r\n" +
                "+Foo\r\n" +
                "-Bar\r\n");

        ReplyParser parser = new ReplyParser(new ReplyHandler() {
            @Override
            public void handleReply(Reply reply) {
                testComplete();
            }
        });

        parser.handle(b);
    }

    @Test
    public void testArrayArrayEmptyParser() {
        Buffer b = new Buffer();
        b.appendString(
                        "*1\r\n" +
                        "*0\r\n");

        ReplyParser parser = new ReplyParser(new ReplyHandler() {
            @Override
            public void handleReply(Reply reply) {
                testComplete();
            }
        });

        parser.handle(b);
    }
}
