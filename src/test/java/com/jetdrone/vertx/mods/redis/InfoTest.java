package com.jetdrone.vertx.mods.redis;

import org.vertx.java.testframework.TestBase;

public class InfoTest extends TestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startApp(GRedisInfoTester.class.getName());
    }

    public void testInfo() {
        startTest(getMethodName());
    }
}
