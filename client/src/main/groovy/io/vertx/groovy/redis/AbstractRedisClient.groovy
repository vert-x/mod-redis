package io.vertx.groovy.redis

import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.groovy.platform.Container

class AbstractRedisClient {
    final EventBus eventBus
    final String redisAddress

    AbstractRedisClient(EventBus eventBus, String redisAddress) {
        this.eventBus = eventBus
        this.redisAddress = redisAddress
    }

    final void deployModule(Container container, String hostname = "localhost", int port = 6379, String encoding = "UTF-8", boolean binary = false, String auth = null, int database = 0, int instances = 1, Closure handler) {
        def config = [
           hostname: hostname,
           port: port,
           address: redisAddress,
           encoding: encoding,
           binary: binary,
           auth: auth,
           database : database
        ]

        if (handler != null) {
            container.deployModule("io.vertx~mod-redis~1.1.2-SNAPSHOT", config, instances, handler)
        } else {
            container.deployModule("io.vertx~mod-redis~1.1.2-SNAPSHOT", config, instances)
        }
    }

    @SuppressWarnings("unchecked")
    final void send(String command, Object... args) {

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
        json.put("command", command)

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
