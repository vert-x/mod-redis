var container = require("vertx/container");
var vertx = require("vertx");

module.exports = function (redisAddress) {
    this.eventBus = vertx.eventBus;
};

module.exports.prototype.init = function (redisAddress) {
    this.redisAddress = redisAddress;
};

module.exports.prototype.deployModule = function (options, instances, handler) {

    var config = {
        hostname: options.hostname || "localhost",
        port: options.port || 6379,
        address: this.redisAddress,
        encoding: options.encoding || "UTF-8",
        binary: options.binary || false,
        auth: options.auth || null,
        select: options.select || 0
    };

    if (arguments.length === 2) {
        if (typeof instances === "function") {
            handler = instances;
            instances = 1;
        }
    }

    var mod = "io.vertx~mod-redis~1.1.3";

    if (handler) {
      container.deployModule(mod, config, instances, handler);
    } else {
      container.deployModule(mod, config, instances);
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

    // handle special hash commands
    if ("MSET" === command || "MSETNX" === command || "HMSET" === command || "ZADD" === command) {
        if (totalArgs == 2 && args[1] instanceof Object) {
            // there are only 2 arguments and the last  is a json object, convert the hash into a redis command
            json.args.push(args[0]);
            for (var key in args[1]) {
                if (args[1].hasOwnProperty(key)) {
                    json.args.push(key);
                    json.args.push(args[1][key]);
                }
            }
            // remove these 2 since they are already added to the args array
            totalArgs = 0;
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
