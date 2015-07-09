package io.vertx.groovy.redis

import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.groovy.platform.Container
import org.vertx.java.core.AsyncResult

class AbstractRedisClient {
    final EventBus eventBus
    final String redisAddress

    AbstractRedisClient(EventBus eventBus, String redisAddress) {
        this.eventBus = eventBus
        this.redisAddress = redisAddress
    }

    private static AsyncResult<String> createAsyncResult(final boolean succeed, final String result, final Throwable cause) {
        return new AsyncResult<String>() {
            @Override
            public String result() {
                return result;
            }

            @Override
            public Throwable cause() {
                return cause;
            }

            @Override
            public boolean succeeded() {
                return succeed;
            }

            @Override
            public boolean failed() {
                return !succeed;
            }
        };
    }


    final void deployModule(Container container, String hostname = "localhost", int port = 6379, String encoding = "UTF-8", boolean binary = false, String auth = null, int select = 0, int instances = 1, Closure handler) {
        def config = [
           hostname: hostname,
           port: port,
           address: redisAddress,
           encoding: encoding,
           binary: binary,
           auth: auth,
           select: select
        ]

        def mod = "io.vertx~mod-redis~1.1.4";

        if (handler != null) {
            container.deployModule(mod, config, instances, handler)
        } else {
            container.deployModule(mod, config, instances)
        }
    }

    @SuppressWarnings("unchecked")
    final void send(RedisCommand command, Object... args) {

        Map<String, Object> json = new HashMap<>()
        List<Object> redisArgs = new ArrayList<>()

        int totalArgs = 0
        Closure messageHandler = null

        // verify if there are args
        if (args != null) {
            // verify if the last one is a Handler
            Object last = args[args.length - 1]
            totalArgs = args.length
            if (last instanceof Closure) {
                // the caller expects a result
                totalArgs--
                messageHandler = last
            }
        }

        // serialize the request
        json.put("command", command.getCommand())

        // handle special hash commands
        switch (command){
            case [RedisCommand.MSET, RedisCommand.MSETNX, RedisCommand.HMSET, RedisCommand.ZADD]:
                if (totalArgs == 2 && args[1] instanceof Map) {
                    // there are only 2 arguments and the last  is a json object, convert the hash into a redis command
                    redisArgs.add(args[0])
                    args[1].each() {key, value ->
                        redisArgs.add(key)
                        redisArgs.add(value)
                    }

                    // remove these 2 since they are already added to the args array
                    totalArgs = 0
                }
                break;
        }

        // serialize arguments
        for (int i = 0; i < totalArgs; i++) {
            redisArgs.add(args[i])
        }

        json.put("args", redisArgs)

        if (messageHandler != null) {
            eventBus.send(redisAddress, json, messageHandler)
        } else {
            eventBus.send(redisAddress, json)
        }
    }
}
