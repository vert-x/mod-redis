package io.vertx.redis

import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine

class Generate {

    public static void main(String[] args) {
        def slurper = new JsonSlurper()
        def result = slurper.parse(new InputStreamReader(new URL('http://redis.io/commands.json').openStream()))

        generateJava(result)
        generateGroovy(result)
    }

    static void generateJava(commands) {
        def engine = new SimpleTemplateEngine()

        def text = '''package io.vertx.java.redis;

import org.vertx.java.core.eventbus.EventBus;

public class RedisClient extends AbstractRedisClient {

    public RedisClient(EventBus eventBus, String redisAddress) {
        super(eventBus, redisAddress);
    }

<%for (cmd in json) {%>  /**
   * <% print cmd.value.summary %>
   * @since <% print cmd.value.since %>
   */
  public void <% print cmd.key.toLowerCase().replace(' ', '_') %>(Object... args) {send("<% print cmd.key %>", args);}

<%}%>
}
'''
        engine.createTemplate(text).make([json: commands]).writeTo(new OutputStreamWriter(new FileOutputStream("client/src/main/java/io/vertx/java/redis/RedisClient.java")))
    }

    static void generateGroovy(commands) {
        def engine = new SimpleTemplateEngine()

        def text = '''package io.vertx.groovy.redis;

import org.vertx.groovy.core.eventbus.EventBus;

public class RedisClient extends AbstractRedisClient {

    public RedisClient(EventBus eventBus, String redisAddress) {
        super(eventBus, redisAddress);
    }

<%for (cmd in json) {%>  /**
   * <% print cmd.value.summary %>
   * @since <% print cmd.value.since %>
   */
  public void <% print cmd.key.toLowerCase().replace(' ', '_') %>(Object... args) {send("<% print cmd.key %>", args);}

<%}%>
}
'''
        engine.createTemplate(text).make([json: commands]).writeTo(new OutputStreamWriter(new FileOutputStream("client/src/main/groovy/io/vertx/groovy/redis/RedisClient.java")))
    }
}
