package io.vertx.redis

import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine

class Generate {

    public static void main(String[] args) {
        def slurper = new JsonSlurper()
        def result = slurper.parse(new InputStreamReader(new URL('http://redis.io/commands.json').openStream()))

        generateJava(result)
        generateGroovy(result)
        generateJS(result)
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

    static void generateJS(commands) {
        def engine = new SimpleTemplateEngine()

        def text = '''var container = require("vertx/container");
var vertx = require("vertx");

module.exports = function (redisAddress) {
  this.eventBus = vertx.eventBus;
  this.redisAddress = redisAddress;
};

module.exports.prototype.deployModule = function (options, instances, handler) {
  var config = {
    hostname: options.hostname || "localhost",
    port: options.port || 6379,
    address: this.redisAddress,
    encoding: options.encoding || "UTF-8",
    binary: options.binary || false,
    auth: options.auth || null
  };

  if (handler) {
    container.deployModule("io.vertx~mod-redis~1.1.3", config, instances, handler);
  } else {
      container.deployModule("io.vertx~mod-redis~1.1.3", instances, config);
  }
};

module.exports.prototype.send = function(command, args) {
  var json = {command: command, args: []};
  var totalArgs = 0;
  var messageHandler = null;

  // verify if there are args
  if (args != null) {
    // verify if the last one is a Handler
    var last = args[args.length - 1];
    totalArgs = args.length;
    if (last instanceof Function) {
      // the caller expects a result
      totalArgs--;
      messageHandler = last;
    }
  }

  // serialize arguments
  for (var i = 0; i < totalArgs; i++) {
    json.args.push(args[i]);
  }

  if (messageHandler) {
    this.eventBus.send(this.redisAddress, json, messageHandler);
  } else {
    this.eventBus.send(this.redisAddress, json);
  }
};

<%for (cmd in json) {%>/**
 * <% print cmd.value.summary %>
 * @since <% print cmd.value.since %>
 */
module.exports.prototype.<% print cmd.key.toLowerCase().replace(' ', '_') %> = function() {this.send("<% print cmd.key %>", arguments);};

<%}%>
'''
        engine.createTemplate(text).make([json: commands]).writeTo(new OutputStreamWriter(new FileOutputStream("client/src/main/resources/redisClient.js")))
    }
}
