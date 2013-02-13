package com.jetdrone.vertx.mods.redis;

import org.junit.Test;
import org.vertx.java.testframework.TestBase;

public class RedisClientBusModTest extends TestBase {

    public RedisClientBusModTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startApp(GRedisClientTester.class.getName());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

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

//    @Test
//    // TODO: require 2.6.0
//    public void testBitcount() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    // TODO: require 2.6.0
//    public void testBitop() {
//        startTest(getMethodName());
//    }

    @Test
    public void testBlpop() {
        startTest(getMethodName());
    }

    @Test
    public void testBrpop() {
        startTest(getMethodName());
    }

    @Test
    public void testBrpoplpush() {
        startTest(getMethodName());
    }

    @Test
    public void testClientKill() {
        startTest(getMethodName());
    }

    @Test
    public void testClientList() {
        startTest(getMethodName());
    }

//    @Test
//    // TODO: requires 2.6.9
//    public void testClientGetname() {
//        startTest(getMethodName());
//    }

//    @Test
//    // TODO: requires 2.6.9
//    public void testClientSetname() {
//        startTest(getMethodName());
//    }

//    @Test
//    // TODO: broken
//    public void testConfigGet() {
//        startTest(getMethodName());
//    }

    @Test
    public void testConfigSet() {
        startTest(getMethodName());
    }

    @Test
    public void testConfigResetstat() {
        startTest(getMethodName());
    }

    @Test
    public void testDbsize() {
        startTest(getMethodName());
    }

    @Test
    public void testDebugObject() {
        startTest(getMethodName());
    }

    @Test
    public void testDebugSegfault() {
        startTest(getMethodName());
    }

    @Test
    public void testDecr() {
        startTest(getMethodName());
    }

    @Test
    public void testDecrby() {
        startTest(getMethodName());
    }

    @Test
    public void testDel() {
        startTest(getMethodName());
    }

    @Test
    public void testDiscard() {
        startTest(getMethodName());
    }

//    @Test
//    // TODO: require 2.6.0
//    public void testDump() {
//        startTest(getMethodName());
//    }

    @Test
    public void testEcho() {
        startTest(getMethodName());
    }

//    @Test
//    // TODO: require 2.6.0
//    public void testEval() {
//        startTest(getMethodName());
//    }

//    @Test
//    // TODO: require 2.6.0
//    public void testEvalsha() {
//        startTest(getMethodName());
//    }

    @Test
    public void testExec() {
        startTest(getMethodName());
    }

    @Test
    public void testExists() {
        startTest(getMethodName());
    }

    @Test
    public void testExpire() {
        startTest(getMethodName());
    }

    @Test
    public void testExpireat() {
        startTest(getMethodName());
    }

    @Test
    public void testFlushall() {
        startTest(getMethodName());
    }

    @Test
    public void testFlushdb() {
        startTest(getMethodName());
    }

    @Test
    public void testGet() {
        startTest(getMethodName());
    }

    @Test
    public void testGetbit() {
        startTest(getMethodName());
    }

    @Test
    public void testGetrange() {
        startTest(getMethodName());
    }

    @Test
    public void testGetset() {
        startTest(getMethodName());
    }

    @Test
    public void testHdel() {
        startTest(getMethodName());
    }

    @Test
    public void testHexists() {
        startTest(getMethodName());
    }

    @Test
    public void testHget() {
        startTest(getMethodName());
    }

    @Test
    public void testHgetall() {
        startTest(getMethodName());
    }

    @Test
    public void testHincrby() {
        startTest(getMethodName());
    }

//    @Test
//    // TODO: broken
//    public void testHIncrbyfloat() {
//        startTest(getMethodName());
//    }

    @Test
    public void testHkeys() {
        startTest(getMethodName());
    }

    @Test
    public void testHlen() {
        startTest(getMethodName());
    }

    @Test
    public void testHmget() {
        startTest(getMethodName());
    }

    @Test
    public void testHmset() {
        startTest(getMethodName());
    }

    @Test
    public void testHset() {
        startTest(getMethodName());
    }

    @Test
    public void testHsetnx() {
        startTest(getMethodName());
    }

    @Test
    public void testHvals() {
        startTest(getMethodName());
    }
}