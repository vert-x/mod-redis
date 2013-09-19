package io.vertx.redis

import groovy.json.JsonSlurper

class Generate {

    public static void main(String[] args) {
        def slurper = new JsonSlurper()
        def result = slurper.parse(new InputStreamReader(new URL('http://redis.io/commands.json').openStream()))

        generateJava(result)
    }

    static void generateJava(commands) {
        for (cmd in commands) {
            println "$cmd.key - $cmd.value.summary"
        }
    }
}
