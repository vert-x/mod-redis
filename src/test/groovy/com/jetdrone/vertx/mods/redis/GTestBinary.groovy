package com.jetdrone.vertx.mods.redis

class GTestBinary extends GRedisTest {

    public GTestBinary() {
        super("test.my_redisclient", [address: "test.my_redisclient", binary: true])
    }

    void testBinary() {
        def key = makeKey()

        redis([command: "del", key: key]) { reply0 ->

            redis([command: "append", key: key, value: "Hello"]) { reply1 ->
                assertNumber(5, reply1)

                redis([command: "append", key: key, value: " World"]) { reply2 ->
                    assertNumber(11, reply2)

                    redis([command: "get", key: key]) { reply3 ->
                        def expected = "Hello World".getBytes()
                        def result = reply3.body.getBinary("value")

                        tu.azzert(expected.length == result.length)

                        for (int i = 0; i < expected.length; i++) {
                            tu.azzert(expected[i] == result[i])
                        }
                        tu.testComplete()
                    }
                }
            }
        }
    }

}
