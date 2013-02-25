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

//    // TODO: require 2.6.0
//    @Test
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

    @Test
    public void testIncr() {
        startTest(getMethodName());
    }

    @Test
    public void testIncrby() {
        startTest(getMethodName());
    }

//    // TODO: require 2.6.0
//    @Test
//    public void testIncrbyfloat() {
//        startTest(getMethodName());
//    }

    @Test
    public void testInfo() {
        startTest(getMethodName());
    }

    @Test
    public void testKeys() {
        startTest(getMethodName());
    }

    @Test
    public void testLastsave() {
        startTest(getMethodName());
    }

    @Test
    public void testLindex() {
        startTest(getMethodName());
    }

    @Test
    public void testLinsert() {
        startTest(getMethodName());
    }

    @Test
    public void testLlen() {
        startTest(getMethodName());
    }

    @Test
    public void testLpop() {
        startTest(getMethodName());
    }

    @Test
    public void testLpush() {
        startTest(getMethodName());
    }

    @Test
    public void testLpushx() {
        startTest(getMethodName());
    }

    @Test
    public void testLrange() {
        startTest(getMethodName());
    }

    @Test
    public void testLrem() {
        startTest(getMethodName());
    }

    @Test
    public void testLset() {
        startTest(getMethodName());
    }

    @Test
    public void testLtrim() {
        startTest(getMethodName());
    }

    @Test
    public void testMget() {
        startTest(getMethodName());
    }

    @Test
    public void testMigrate() {
        startTest(getMethodName());
    }

    @Test
    public void testMonitor() {
        startTest(getMethodName());
    }

    @Test
    public void testMove() {
        startTest(getMethodName());
    }

    @Test
    public void testMset() {
        startTest(getMethodName());
    }

    @Test
    public void testMsetnx() {
        startTest(getMethodName());
    }

    @Test
    public void testMulti() {
        startTest(getMethodName());
    }

    @Test
    public void testObject() {
        startTest(getMethodName());
    }

    @Test
    public void testPersist() {
        startTest(getMethodName());
    }

    @Test
    public void testPexpire() {
        startTest(getMethodName());
    }

    @Test
    public void testPexpireat() {
        startTest(getMethodName());
    }

    @Test
    public void testPing() {
        startTest(getMethodName());
    }

    @Test
    public void testPsetex() {
        startTest(getMethodName());
    }

    @Test
    public void testPsubscribe() {
        startTest(getMethodName());
    }

    @Test
    public void testPttl() {
        startTest(getMethodName());
    }

    @Test
    public void testPublish() {
        startTest(getMethodName());
    }

    @Test
    public void testPunsubscribe() {
        startTest(getMethodName());
    }

    @Test
    public void testQuit() {
        startTest(getMethodName());
    }

    @Test
    public void testRandomkey() {
        startTest(getMethodName());
    }

    @Test
    public void testRename() {
        startTest(getMethodName());
    }

    @Test
    public void testRenamenx() {
        startTest(getMethodName());
    }

    @Test
    public void testRestore() {
        startTest(getMethodName());
    }

    @Test
    public void testRpop() {
        startTest(getMethodName());
    }

    @Test
    public void testRpoplpush() {
        startTest(getMethodName());
    }

    @Test
    public void testRpush() {
        startTest(getMethodName());
    }

    @Test
    public void testRpushx() {
        startTest(getMethodName());
    }

    @Test
    public void testSadd() {
        startTest(getMethodName());
    }

    @Test
    public void testSave() {
        startTest(getMethodName());
    }

    @Test
    public void testScard() {
        startTest(getMethodName());
    }

    @Test
    public void testScriptexists() {
        startTest(getMethodName());
    }

    @Test
    public void testScriptflush() {
        startTest(getMethodName());
    }

    @Test
    public void testScriptkill() {
        startTest(getMethodName());
    }

    @Test
    public void testScriptload() {
        startTest(getMethodName());
    }

    @Test
    public void testSdiff() {
        startTest(getMethodName());
    }

    @Test
    public void testSdiffstore() {
        startTest(getMethodName());
    }

    @Test
    public void testSelect() {
        startTest(getMethodName());
    }

    @Test
    public void testSet() {
        startTest(getMethodName());
    }

    @Test
    public void testSetbit() {
        startTest(getMethodName());
    }

    @Test
    public void testSetex() {
        startTest(getMethodName());
    }

    @Test
    public void testSetnx() {
        startTest(getMethodName());
    }

    @Test
    public void testSetrange() {
        startTest(getMethodName());
    }

    @Test
    public void testShutdown() {
        startTest(getMethodName());
    }

    @Test
    public void testSinter() {
        startTest(getMethodName());
    }

    @Test
    public void testSinterstore() {
        startTest(getMethodName());
    }

    @Test
    public void testSismember() {
        startTest(getMethodName());
    }

    @Test
    public void testSlaveof() {
        startTest(getMethodName());
    }

    @Test
    public void testSlowlog() {
        startTest(getMethodName());
    }

    @Test
    public void testSmembers() {
        startTest(getMethodName());
    }

    @Test
    public void testSmove() {
        startTest(getMethodName());
    }

    @Test
    public void testSort() {
        startTest(getMethodName());
    }

    @Test
    public void testSpop() {
        startTest(getMethodName());
    }

    @Test
    public void testSrandmember() {
        startTest(getMethodName());
    }

    @Test
    public void testSrem() {
        startTest(getMethodName());
    }

    @Test
    public void testStrlen() {
        startTest(getMethodName());
    }

    @Test
    public void testSubscribe() {
        startTest(getMethodName());
    }

    @Test
    public void testSunion() {
        startTest(getMethodName());
    }

    @Test
    public void testSunionstore() {
        startTest(getMethodName());
    }

    @Test
    public void testSync() {
        startTest(getMethodName());
    }

    @Test
    public void testTime() {
        startTest(getMethodName());
    }

    @Test
    public void testTtl() {
        startTest(getMethodName());
    }

    @Test
    public void testType() {
        startTest(getMethodName());
    }

    @Test
    public void testUnsubscribe() {
        startTest(getMethodName());
    }

    @Test
    public void testUnwatch() {
        startTest(getMethodName());
    }

    @Test
    public void testZadd() {
        startTest(getMethodName());
    }

    @Test
    public void testZcard() {
        startTest(getMethodName());
    }

    @Test
    public void testZcount() {
        startTest(getMethodName());
    }

    @Test
    public void testZincrby() {
        startTest(getMethodName());
    }

//    @Test
//    public void testZinterstore() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZrange() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZrangebyscore() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZrank() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZrem() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZremrangebyrank() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZremrangebyscore() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZrevrange() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZrevrangebyscore() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZrevrank() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZscore() {
//        startTest(getMethodName());
//    }
//
//    @Test
//    public void testZunionstore() {
//        startTest(getMethodName());
//    }
}