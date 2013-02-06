def config = [
        address:    'test.my_redisclient',
        host:       'localhost',
        port:       6379]

def eb = vertx.eventBus;

//container.deployModule('vert.x-busmod-redis-v1.0', config, 1) { res ->
//    println("redis: " + res)
//
//    def redis = new RedisHelper(eb, 'test.my_redisclient')
//
//    // assuming that we are connected
//    redis.select(1) { msg ->
//        println(msg.body)
//    }
//}

container.deployModule('vert.x-busmod-redis-v1.0', config, 1) { res ->
    println("redis: " + res)

    eb.send('test.my_redisclient', [command: 'set', key: 'mykey', value: 'myvalue']) { reply ->
        println(reply.body)

        eb.send('test.my_redisclient', [command: 'get', key: 'mykey']) { reply2 ->
            println(reply2.body)
        }
    }
}