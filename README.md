Redis busmod for Vert.x
=============================

This module allows data to be saved, retrieved, searched for, and deleted in a Redis. Redis is an open source, BSD
licensed, advanced key-value store. It is often referred to as a data structure server since keys can contain strings,
hashes, lists, sets and sorted sets. To use this module you must have a Redis server instance running on your network.

[![Build Status](https://travis-ci.org/pmlopes/mod-redis-io.png?branch=master)](https://travis-ci.org/pmlopes/mod-redis-io)

## Dependencies

This module requires a Redis server to be available on the network.

## Name

The module name is `com.jetdrone.mod-redis-io`.

## Configuration

The module takes the following configuration:

    {
        "address": <address>,
        "host": <host>,
        "port": <port>,
        "encoding": <charset>,
        "binary": <boolean>,
        "auth": <password>
    }

For example:

    {
        "address": "test.my_redis",
        "host": "localhost",
        "port": 6379
    }

Let's take a look at each field in turn:

* `address` The main address for the module. Every module has a main address. Defaults to `vertx.mod-redis-io`.
* `host` Host name or ip address of the Redis instance. Defaults to `localhost`.
* `port` Port at which the Redis instance is listening. Defaults to `6379`.
* `encoding` The character encoding for string conversions (e.g.: `UTF-8`, `ISO-8859-1`, `US-ASCII`). Defaults to the platform default.
* `binary` To be implemented. In this case messages are expected to be in binary format.
* `auth` The password to authenticate the client connection. Since the module manages the connection using the `AUTH` command could not have the desired result due to concurrency.
## Usage

Simple example:

```groovy
    def eb = vertx.eventBus()
    def config = new JsonObject()

    config.putString("address", address)
    config.putString("host", "localhost")
    config.putNumber("port", 6379)

    container.deployModule("com.jetdrone.mod-redis-io", config, 1)

    eb.send(address, [command: 'get', key: 'mykey']) { reply ->
        if (reply.body.status.equals('ok') {
            // do something with reply.body.value
        } else {
            println('Error #{reply.body.message}')
        }
    }
```

Simple example with pub/sub mode:

```groovy
    def eb = vertx.eventBus()
    def pubConfig = new JsonObject()
    pubConfig.putString("address", 'redis.pub')
    def subConfig = new JsonObject()
    subConfig.putString("address", 'redis.sub')

    container.deployModule("com.jetdrone.mod-redis-io", pubConfig, 1)
    container.deployModule("com.jetdrone.mod-redis-io", subConfig, 1)

    // register a handler for the incoming message the naming the Redis module will use is base address + '.' + redis channel
    eb.registerHandler("redis.sub.ch1", new Handler<Message<JsonObject>>() {
        @Override
        void handle(Message<JsonObject> received) {
            // do whatever you need to do with your message
            def value = received.body.getField('value')
            // the value is a JSON doc with the following properties
            // channel - The channel to which this message was sent
            // pattern - Pattern is present if you use psubscribe command and is the pattern that matched this message channel
            // message - The message payload
        }
    });

    // on sub address subscribe to channel ch1
    eb.send('redis.sub', [command: 'subscribe', channel: 'ch1']) { subscribe ->
    }

    // on pub address publish a message
    eb.send('redis.pub', [command: 'publish', channel: 'ch1', message: 'Hello World!']) { publish ->
    }
```


### Sending Commands

Each Redis command is exposed as a json document on the `EventBus`. All commands take a field `command` and an arbitrary
amount of extra fields as described on the main redis documentation site.

An example would be:

    {
        command: "get",
        key: "mykey"
    }

When the command completes successfuly the response would be:

    {
        status: "ok",
        "value": "the value stored on redis"
    }

If an error occurs a reply is returned:

    {
        "status": "error",
        "message": <message>
    }

Where `message` is an error message.

For a list of Redis commands, see [Redis Command Reference](http://redis.io/commands)

At the moment, commands can be specified only in lowercase. Minimal parsing is done on the replies.
Commands that return a single line reply return `java.lang.String`, integer replies return `java.lang.Number`,
"bulk" replies return an array of `java.lang.String` using the specified encoding, and "multi bulk" replies return a
array of `java.lang.String` again using the specified encoding. `hgetall` is returns a `JsonObject`.

## Friendlier hash commands

Most Redis commands take a single String or an Array of Strings as arguments, and replies are sent back as a single
String or an Array of Strings. When dealing with hash values, there are a couple of useful exceptions to this.

### command hgetall

The reply from an `hgetall` command will be converted into a JSON Object.  That way you can interact with the responses
using JSON syntax which is handy for the EventBus communication.

### command mset

Multiple values in a hash can be set by supplying an object. Note however that key and value will be coerced to strings.

    {
        command: "mset",
        key: "redis key",
        keys: {
            keyName: "value",
            otherKeyName: "other value"
        }
    }

### command msetnx

Multiple values in a hash can be set by supplying an object. Note however that key and value will be coerced to strings.

    {
        command: "msetnx",
        key: "redis key",
        keys: {
            keyName: "value",
            otherKeyName: "other value"
        }
    }

### command hmset

Multiple values in a hash can be set by supplying an object. Note however that key and value will be coerced to strings.

    {
        command: "hmset",
        key: "redis key",
        fields: {
            fieldName: "value",
            otherFieldName: "other value"
        }
    }

### command zadd

Multiple values in a hash can be set by supplying an object. Note however that key and value will be coerced to strings.

    {
        command: "zadd",
        key: "redis key",
        scores: {
            score: "member",
            otherScore: "other member"
        }
    }


## Pub/Sub

As demonstrated with the source code example above, the module can work in pub/sub mode too. The basic idea behind it is
that you need to register a new handler for the address: `mod-redis-io-address.your_real_redis_address` At this moment
all commands to `subscribe`, `psubscribe`, `unsubscribe` and `pusubscribe` will send the received messages to the right
address.

The destination address is defined as a concatenation of the module address plus the redis address. This choice was made
to keep the existing json to redis api as close as possible to the official documentation. For example lets say we have
a module deployed at `mod.redis.pub` if we want to call the `[p]subscribe` method then first we should register an handler
with the name of the redis channel/pattern we want to listen and then call the redis command.

Seting up a subscription for a specific channel:

```java
    container.deployModule("com.jetdrone.mod-redis-io", new JsonObject().putString("address", "redis.subscription"), 1);

    // register a handler for the incoming message
    eb.registerHandler("redis.subscription.mychannel", new Handler<Message<JsonObject>>() {
        @Override
        void handle(Message<JsonObject> received) {
            JsonObject value = received.body.getObject("value");
            // the value is a JSON doc with the following properties
            // channel - The channel to which this message was sent
            // message - The message payload
            ...
        }
    });

    // subscribe to channel mychannel
    eb.send("redis.sub", new JsonObject("{command: \"subscribe\", channel: \"mychannel\"}));
```

Setting up a subscription to a pattern:

```java
    container.deployModule("com.jetdrone.mod-redis-io", new JsonObject().putString("address", "redis.subscription"), 1);

    // register a handler for the incoming message
    eb.registerHandler("redis.subscription.news.*", new Handler<Message<JsonObject>>() {
        @Override
        void handle(Message<JsonObject> received) {
            JsonObject value = received.body.getObject("value");
            // the value is a JSON doc with the following properties
            // channel - The channel to which this message was sent
            // pattern - Pattern is present if you use psubscribe command and is the pattern that matched this message channel
            // message - The message payload
            ...
        }
    });

    // subscribe to channel mychannel
    eb.send("redis.sub", new JsonObject("{command: \"subscribe\", channel: \"news.*\"}));
```

Note that in the last example that you can receive messages published both to `news.sports` and `news.world`. In order to
know better wich destination the message had on the handler the received message contains the following format:

    {
        channel: "String with the absolute channel name",
        pattern: "String with the pattern that matched the channel and *only* present in psubscribe replies",
        message: "The actual message payload"
    }

The fields `channel` and `message` are always present, however the field `pattern` is only present if you subscribed a
channel with `psubscribe`.


## Authentication

Authentication is done using the `auth` command. However the connection lifecycle is managed by the module so it is not
trivial when to execute the command from the client side. The module will call this method internally at the right time.


## Monitor

TODO: The module will do monitoring

## Server info

The module converts the info response to a friendly Json

    {
        server: {
            redis_version: "2.5.13",
            redis_git_sha1: "2812b945",
            redis_git_dirty: "0",
            os: "Linux 2.6.32.16-linode28 i686",
            arch_bits: "32",
            multiplexing_api: "epoll",
            gcc_version: "4.4.1",
            process_id: "8107",
            ...
        },
        memory: {...},
        client: {...},
        ...
    }

In order to make it easier to work with the `info` response you don't need to parse the data yourself and the module will
return it in a easy to understand JSON format. The format is as follows: A JSON object for each section filled with
properties that belong to that section. If for some reason there is no section the properties will be visible at the top
level object.

## Binary

TODO: either using putBinary or other alternative...

## Transactions

TODO: love or hate they must be supported! :)