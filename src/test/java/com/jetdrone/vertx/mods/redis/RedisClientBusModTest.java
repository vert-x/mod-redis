package com.jetdrone.vertx.mods.redis;

import org.vertx.java.testframework.TestBase;

public class RedisClientBusModTest extends TestBase {

    boolean skip26 = false;

    public RedisClientBusModTest() {
        super();
        if ("1".equals(System.getenv("TRAVISCI"))) {
            skip26 = true;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startApp(GRedisClientTester.class.getName());
    }

    public void testAppend() throws Exception {
        startTest(getMethodName());
    }

    public void testAuth() {
        startTest(getMethodName());
    }

    public void testBgrewriteaof() {
        startTest(getMethodName());
    }

    public void testBgsave() {
        startTest(getMethodName());
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testBitcount() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testBitop() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    public void testBlpop() {
        startTest(getMethodName());
    }

    public void testBrpop() {
        startTest(getMethodName());
    }

    public void testBrpoplpush() {
        startTest(getMethodName());
    }

    public void testClientKill() {
        startTest(getMethodName());
    }

    public void testClientList() {
        startTest(getMethodName());
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testClientGetname() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testClientSetname() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testConfigGet() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    public void testConfigSet() {
        startTest(getMethodName());
    }

    public void testConfigResetstat() {
        startTest(getMethodName());
    }

    public void testDbsize() {
        startTest(getMethodName());
    }

    public void testDebugObject() {
        startTest(getMethodName());
    }

    public void testDebugSegfault() {
        startTest(getMethodName());
    }

    public void testDecr() {
        startTest(getMethodName());
    }

    public void testDecrby() {
        startTest(getMethodName());
    }

    public void testDel() {
        startTest(getMethodName());
    }

    public void testDiscard() {
        startTest(getMethodName());
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testDump() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    public void testEcho() {
        startTest(getMethodName());
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testEval() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testEvalsha() {
        startTest(getMethodName());
    }

    public void testExec() {
        startTest(getMethodName());
    }

    public void testExists() {
        startTest(getMethodName());
    }

    public void testExpire() {
        startTest(getMethodName());
    }

    public void testExpireat() {
        startTest(getMethodName());
    }

    public void testFlushall() {
        startTest(getMethodName());
    }

    public void testFlushdb() {
        startTest(getMethodName());
    }

    public void testGet() {
        startTest(getMethodName());
    }

    public void testGetbit() {
        startTest(getMethodName());
    }

    public void testGetrange() {
        startTest(getMethodName());
    }

    public void testGetset() {
        startTest(getMethodName());
    }

    public void testHdel() {
        startTest(getMethodName());
    }

    public void testHexists() {
        startTest(getMethodName());
    }

    public void testHget() {
        startTest(getMethodName());
    }

    public void testHgetall() {
        startTest(getMethodName());
    }

    public void testHincrby() {
        startTest(getMethodName());
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testHIncrbyfloat() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    public void testHkeys() {
        startTest(getMethodName());
    }

    public void testHlen() {
        startTest(getMethodName());
    }

    public void testHmget() {
        startTest(getMethodName());
    }

    public void testHmset() {
        startTest(getMethodName());
    }

    public void testHset() {
        startTest(getMethodName());
    }

    public void testHsetnx() {
        startTest(getMethodName());
    }

    public void testHvals() {
        startTest(getMethodName());
    }

    public void testIncr() {
        startTest(getMethodName());
    }

    public void testIncrby() {
        startTest(getMethodName());
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testIncrbyfloat() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    public void testInfo() {
        startTest(getMethodName());
    }

    public void testKeys() {
        startTest(getMethodName());
    }

    public void testLastsave() {
        startTest(getMethodName());
    }

    public void testLindex() {
        startTest(getMethodName());
    }

    public void testLinsert() {
        startTest(getMethodName());
    }

    public void testLlen() {
        startTest(getMethodName());
    }

    public void testLpop() {
        startTest(getMethodName());
    }

    public void testLpush() {
        startTest(getMethodName());
    }

    public void testLpushx() {
        startTest(getMethodName());
    }

    public void testLrange() {
        startTest(getMethodName());
    }

    public void testLrem() {
        startTest(getMethodName());
    }

    public void testLset() {
        startTest(getMethodName());
    }

    public void testLtrim() {
        startTest(getMethodName());
    }

    public void testMget() {
        startTest(getMethodName());
    }

    public void testMigrate() {
        startTest(getMethodName());
    }

    public void testMonitor() {
        startTest(getMethodName());
    }

    public void testMove() {
        startTest(getMethodName());
    }

    public void testMset() {
        startTest(getMethodName());
    }

    public void testMsetnx() {
        startTest(getMethodName());
    }

    public void testMulti() {
        startTest(getMethodName());
    }

    public void testObject() {
        startTest(getMethodName());
    }

    public void testPersist() {
        startTest(getMethodName());
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testPexpire() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testPexpireat() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    public void testPing() {
        startTest(getMethodName());
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testPsetex() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    public void testPsubscribe() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testPttl() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    public void testPublish() {
        startTest(getMethodName());
    }

    public void testPunsubscribe() {
        startTest(getMethodName());
    }

    public void testQuit() {
        startTest(getMethodName());
    }

    public void testRandomkey() {
        startTest(getMethodName());
    }

    public void testRename() {
        startTest(getMethodName());
    }

    public void testRenamenx() {
        startTest(getMethodName());
    }

    public void testRestore() {
        startTest(getMethodName());
    }

    public void testRpop() {
        startTest(getMethodName());
    }

    public void testRpoplpush() {
        startTest(getMethodName());
    }

    public void testRpush() {
        startTest(getMethodName());
    }

    public void testRpushx() {
        startTest(getMethodName());
    }

    public void testSadd() {
        startTest(getMethodName());
    }

    public void testSave() {
        startTest(getMethodName());
    }

    public void testScard() {
        startTest(getMethodName());
    }

    public void testScriptexists() {
        startTest(getMethodName());
    }

    public void testScriptflush() {
        startTest(getMethodName());
    }

    public void testScriptkill() {
        startTest(getMethodName());
    }

    public void testScriptload() {
        startTest(getMethodName());
    }

    public void testSdiff() {
        startTest(getMethodName());
    }

    public void testSdiffstore() {
        startTest(getMethodName());
    }

    public void testSelect() {
        startTest(getMethodName());
    }

    public void testSet() {
        startTest(getMethodName());
    }

    public void testSetbit() {
        startTest(getMethodName());
    }

    public void testSetex() {
        startTest(getMethodName());
    }

    public void testSetnx() {
        startTest(getMethodName());
    }

    public void testSetrange() {
        startTest(getMethodName());
    }

    public void testShutdown() {
        startTest(getMethodName());
    }

    public void testSinter() {
        startTest(getMethodName());
    }

    public void testSinterstore() {
        startTest(getMethodName());
    }

    public void testSismember() {
        startTest(getMethodName());
    }

    public void testSlaveof() {
        startTest(getMethodName());
    }

    public void testSlowlog() {
        startTest(getMethodName());
    }

    public void testSmembers() {
        startTest(getMethodName());
    }

    public void testSmove() {
        startTest(getMethodName());
    }

    public void testSort() {
        startTest(getMethodName());
    }

    public void testSpop() {
        startTest(getMethodName());
    }

    public void testSrandmember() {
        startTest(getMethodName());
    }

    public void testSrem() {
        startTest(getMethodName());
    }

    public void testStrlen() {
        startTest(getMethodName());
    }

    public void testSubscribe() {
        startTest(getMethodName());
    }

    public void testSunion() {
        startTest(getMethodName());
    }

    public void testSunionstore() {
        startTest(getMethodName());
    }

    public void testSync() {
        startTest(getMethodName());
    }

    // FIXME: ("Requires redis 2.6.0")
    public void testTime() {
        if (!skip26) {
            startTest(getMethodName());
        }
    }

    public void testTtl() {
        startTest(getMethodName());
    }

    public void testType() {
        startTest(getMethodName());
    }

    public void testUnsubscribe() {
        startTest(getMethodName());
    }

    public void testUnwatch() {
        startTest(getMethodName());
    }

    public void testZadd() {
        startTest(getMethodName());
    }

    public void testZcard() {
        startTest(getMethodName());
    }

    public void testZcount() {
        startTest(getMethodName());
    }

    public void testZincrby() {
        startTest(getMethodName());
    }

    public void testZinterstore() {
        startTest(getMethodName());
    }

    public void testZrange() {
        startTest(getMethodName());
    }

    public void testZrangebyscore() {
        startTest(getMethodName());
    }

    public void testZrank() {
        startTest(getMethodName());
    }

    public void testZrem() {
        startTest(getMethodName());
    }

    public void testZremrangebyrank() {
        startTest(getMethodName());
    }

    public void testZremrangebyscore() {
        startTest(getMethodName());
    }

    public void testZrevrange() {
        startTest(getMethodName());
    }

    public void testZrevrangebyscore() {
        startTest(getMethodName());
    }

    public void testZrevrank() {
        startTest(getMethodName());
    }

    public void testZscore() {
        startTest(getMethodName());
    }

    public void testZunionstore() {
        startTest(getMethodName());
    }
}