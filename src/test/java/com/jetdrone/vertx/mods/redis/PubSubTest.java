package com.jetdrone.vertx.mods.redis;

import org.vertx.java.testframework.TestBase;

public class PubSubTest extends TestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startApp(GRedisPubSubTester.class.getName());
    }

    public void testPubSub() {
        startTest(getMethodName());
    }

    public void testPubSubPattern() {
        startTest(getMethodName());
    }

    public void testLateJoin() {
        startTest(getMethodName());
    }
}
