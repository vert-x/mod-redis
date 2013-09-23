package io.vertx.groovy.redis

import org.vertx.groovy.core.eventbus.EventBus

class AbstractRedisClient {
    final EventBus eventBus;
    final String redisAddress;

    AbstractRedisClient(EventBus eventBus, String redisAddress) {
        this.eventBus = eventBus;
        this.redisAddress = redisAddress;
    }

    @SuppressWarnings("unchecked")
    final void send(String command, Object... args) {

        Map<String, Object> json = new HashMap<>();
        List<Object> redisArgs = new ArrayList<>();

        int totalArgs = 0;
        boolean expectResult = false;
        Closure messageHandler = null;

        // verify if there are args
        if (args != null) {
            // verify if the last one is a Handler
            Object last = args[args.length - 1];
            totalArgs = args.length;
            if (last instanceof Closure) {
                // the caller expects a result
                expectResult = true;
                totalArgs--;
                messageHandler = last;
            }
        }

        // serialize the request
        json.put("command", command);

        // serialize arguments
        for (int i = 0; i < totalArgs; i++) {
            redisArgs.add(null);
        }

        json.put("args", redisArgs);

        if (expectResult) {
            eventBus.send(redisAddress, json, messageHandler);
        } else {
            eventBus.send(redisAddress, json);
        }
    }
}
