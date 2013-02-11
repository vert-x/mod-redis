package com.jetdrone.vertx.mods.redis;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.testframework.TestClientBase;

public class TestClient extends TestClientBase {

    private final String address = "test.my_redisclient";
    private EventBus eb;

    @Override
    public void start() {
        super.start();
        eb = vertx.eventBus();
        JsonObject config = new JsonObject();

        config.putString("address", address);
        config.putString("host", "localhost");
        config.putNumber("port", 6379);

        container.deployModule("vertx.mods.redis-DEV", config, 1, new Handler<String>() {
            public void handle(String res) {
                tu.appReady();
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
    }

//    @SuppressWarnings("unused")
//    public void testPersistor() throws Exception {
//
//        final String random = Long.toHexString(System.nanoTime());
//
//        JsonObject message = new JsonObject();
//        message.putString("command", "set");
//        message.putString("key", "myKey");
//        message.putString("value", random);
//
//        eb.send(address, message, new Handler<Message<JsonObject>>() {
//            @Override
//            public void handle(Message<JsonObject> reply) {
//                tu.azzert("ok".equals(reply.body.getString("status")));
//
//                JsonObject message = new JsonObject();
//                message.putString("command", "get");
//                message.putString("key", "myKey");
//
//                eb.send(address, message, new Handler<Message<JsonObject>>() {
//                    @Override
//                    public void handle(Message<JsonObject> reply) {
//                        tu.azzert("ok".equals(reply.body.getString("status")));
//                        System.out.println(reply.body.getField("value"));
//                        tu.azzert(random.equals(reply.body.getString("value")));
//                        tu.testComplete();
//                    }
//                });
//            }
//        });
//    }

    @SuppressWarnings("unused")
    public void testAppend() {
        eb.send(address, new JsonObject().putString("command", "exists").putString("key", "mykey"), new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply0) {
                tu.azzert("ok".equals(reply0.body.getString("status")));
                tu.azzert(0 == reply0.body.getNumber("value"));

                eb.send(address, new JsonObject().putString("command", "append").putString("key", "mykey").putString("value", "Hello"), new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> reply1) {
                        tu.azzert("ok".equals(reply1.body.getString("status")));
                        tu.azzert(5 == reply1.body.getNumber("value"));

                        eb.send(address, new JsonObject().putString("command", "append").putString("key", "mykey").putString("value", " World"), new Handler<Message<JsonObject>>() {
                            @Override
                            public void handle(Message<JsonObject> reply2) {
                                tu.azzert("ok".equals(reply2.body.getString("status")));
                                tu.azzert(11 == reply2.body.getNumber("value"));

                                eb.send(address, new JsonObject().putString("command", "get").putString("key", "mykey"), new Handler<Message<JsonObject>>() {
                                    @Override
                                    public void handle(Message<JsonObject> reply3) {
                                        tu.azzert("ok".equals(reply3.body.getString("status")));
                                        tu.azzert("Hello World".equals(reply3.body.getString("value")));
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void testAuth() {

    }

    public void testBgrewriteaof() {

    }

    public void testBgsave() {

    }

    public void testBitcount() {
        eb.send(address, new JsonObject().putString("command", "set").putString("key", "mykey").putString("value", "foobar"), new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> reply1) {
                tu.azzert("ok".equals(reply1.body.getString("status")));

                eb.send(address, new JsonObject().putString("command", "bitcount").putString("key", "mykey"), new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> reply2) {
                        tu.azzert("ok".equals(reply2.body.getString("status")));
                        tu.azzert(26 == reply2.body.getNumber("value"));

                        eb.send(address, new JsonObject().putString("command", "bitcount").putString("key", "mykey").putNumber("start", 0).putNumber("end", 0), new Handler<Message<JsonObject>>() {
                            @Override
                            public void handle(Message<JsonObject> reply3) {
                                tu.azzert("ok".equals(reply3.body.getString("status")));
                                tu.azzert(4 == reply3.body.getNumber("value"));

                                eb.send(address, new JsonObject().putString("command", "bitcount").putString("key", "mykey").putNumber("start", 1).putNumber("end", 1), new Handler<Message<JsonObject>>() {
                                    @Override
                                    public void handle(Message<JsonObject> reply4) {
                                        tu.azzert("ok".equals(reply4.body.getString("status")));
                                        tu.azzert(6 == reply4.body.getNumber("value"));
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}