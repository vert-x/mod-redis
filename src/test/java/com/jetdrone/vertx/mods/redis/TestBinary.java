package com.jetdrone.vertx.mods.redis;

import org.vertx.java.testframework.TestBase;

public class TestBinary extends TestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startApp(GTestBinary.class.getName());
    }

    public void testBinary() {
        startTest(getMethodName());
    }
}
