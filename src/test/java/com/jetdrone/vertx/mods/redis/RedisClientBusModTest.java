package com.jetdrone.vertx.mods.redis;

import org.junit.Test;
import org.vertx.java.testframework.TestBase;

public class RedisClientBusModTest extends TestBase {

    public RedisClientBusModTest() {
        super();
        new GTestClient().sayHello();
    }

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

    @Test
    public void testAuth() {
        startTest(getMethodName());
    }

    @Test
    public void testBgrewriteaof() {
        startTest(getMethodName());
    }

    @Test
    public void testBgsave() {
        startTest(getMethodName());
    }

    @Test
    // TODO: require 2.6.0
    public void testBitcount() {
        startTest(getMethodName());
    }

    @Test
    // TODO: require 2.6.0
    public void testBitop() {
        startTest(getMethodName());
    }

    @Test
    public void testBlpop() {
        startTest(getMethodName());
    }
}