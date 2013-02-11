package com.jetdrone.vertx.mods.redis;

import org.junit.Test;
import org.vertx.java.testframework.TestBase;

public class RedisClientBusModTest extends TestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startApp(TestClient.class.getName());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    @Test
//    public void testPersistor() throws Exception {
//        startTest(getMethodName());
//    }

    @Test
    public void testAppend() throws Exception {
        startTest(getMethodName());
    }
}