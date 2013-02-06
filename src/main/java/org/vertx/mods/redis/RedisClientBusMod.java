package org.vertx.mods.redis;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.NetSocket;
import org.vertx.mods.redis.netty.Reply;

public class RedisClientBusMod extends BusModBase implements Handler<Message<JsonObject>> {

    private NetSocket socket;
    private RedisClientBase redisClient;

    @Override
    public void start() {
        super.start();

        String host = getOptionalStringConfig("host", "localhost");
        int port = getOptionalIntConfig("port", 6379);

        vertx.createNetClient().connect(port, host, new Handler<NetSocket>() {
            @Override
            public void handle(NetSocket netSocket) {
                socket = netSocket;
                redisClient = new RedisClientBase(netSocket);
            }
        });

        String address = getOptionalStringConfig("address", "redis-client");
        eb.registerHandler(address, this);
    }

    @Override
    public void stop() throws Exception {
        socket.close();
        super.stop();
    }

    @Override
    public void handle(Message<JsonObject> message) {

        String command = message.body.getString("command");

        if (command == null) {
            sendError(message, "command must be specified");
            return;
        }

        switch (command) {
            case "select":
                redisSelect(message);
                break;
            default:
                sendError(message, "Invalid command: " + command);
        }
    }

    /**
     * Select the DB with having the specified zero-based numeric index.
     * New connections always use DB 0.
     * @since 1.0.0
     *
     * @param message {index: Integer}
     */
    private void redisSelect(final Message<JsonObject> message) {
        Integer index = message.body.getInteger("index");

        if (index == null) {
            sendError(message, "index cannot be null");
        } else {
            redisClient.send(new Command("SELECT", index), new Handler<Reply>() {
                @Override
                public void handle(Reply reply) {
                    JsonObject replyMessage = new JsonObject();
                    replyMessage.putString("value", reply.toString());
                    sendOK(message, replyMessage);
                }
            });
        }
    }


}
