package io.vertx.redis

import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine

class Generate {

    public static void main(String[] args) {
        def slurper = new JsonSlurper()
        def result = slurper.parse(new InputStreamReader(new URL('http://redis.io/commands.json').openStream()))

        generateJava(result)
    }

    static void generateJava(commands) {
        def engine = new SimpleTemplateEngine()

        def text = '''package io.vertx.redis;

import org.vertx.java.core.eventbus.EventBus;

public class RedisClient extends AbstractRedisClient {

    public RedisClient(EventBus eventBus, String redisAddress) {
        super(eventBus, redisAddress);
    }

<%for (cmd in json) {%>  /**
   * <% print cmd.value.summary %>
   * @since <% print cmd.value.since %>
   */
  public void <% print cmd.key.toLowerCase().replace(' ', '_') %>(Object... args) {send("<% print cmd.key.toLowerCase().replace(' ', '_') %>", args);}

<%}%>
}
'''
        println engine.createTemplate(text).make([json: commands])
    }
}
