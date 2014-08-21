var fs = require('fs');

var prefix = '../main/java/io/vertx/redis/';

var retTypes = {
    EVAL: 'Void',
    EVALSHA: 'Void',
    MONITOR: 'Void',
    OBJECT: 'Void',
    PSETEX: 'Void',
    PSUBSCRIBE: 'Void',
    PUNSUBSCRIBE: 'Void',
    SLOWLOG: 'Void',
    SUBSCRIBE: 'Void',
    SYNC: 'Void',
    UNSUBSCRIBE: 'Void',
    SCAN: 'Void',
    SSCAN: 'Void',
    HSCAN: 'Void',
    ZSCAN: 'Void'
};

function loadReturns() {
    var lines = fs.readFileSync('returns.txt');
    lines = lines.toString().split(/\n/);

    var conv = {
        'integer': 'Long',
        'simple-string': 'String',
        'bulk-string': 'String',
        'array': 'JsonArray',
        'nil': 'Void'
    };

    for (var i = 0; i < lines.length; i++) {
        var l = lines[i];
        var command = l.substring(0, l.indexOf('.'));

        var retregex = /@(.*)-reply/g;
        var match = retregex.exec(l);

        if (!match) {
            console.error(l);
        } else {
            retTypes[command.toUpperCase()] = conv[match[1]];
        }
    }
}

function generateAPI(json) {
    fs.truncateSync(prefix + 'RedisService.java');

    write('RedisService', 'package io.vertx.redis;');
    write('RedisService', '');
    write('RedisService', 'import io.vertx.codegen.annotations.VertxGen;');
    write('RedisService', 'import io.vertx.core.*;');
    write('RedisService', '');
    write('RedisService', 'import io.vertx.core.json.*;');
    write('RedisService', '');
    write('RedisService', '@VertxGen');
    write('RedisService', 'public interface RedisService {');
    write('RedisService', '  static RedisService create(Vertx vertx, JsonObject config) {');
    write('RedisService', '    return factory.create(vertx, config);');
    write('RedisService', '  }');
    write('RedisService', '');
    write('RedisService', '  static RedisService createEventBusProxy(Vertx vertx, String address) {');
    write('RedisService', '    return factory.createEventBusProxy(vertx, address);');
    write('RedisService', '  }');
    write('RedisService', '');
    write('RedisService', '  void start();');
    write('RedisService', '  void stop();');
    write('RedisService', '');
    write('RedisService', '  static final RedisServiceFactory factory = ServiceHelper.loadFactory(RedisServiceFactory.class);');
    write('RedisService', '');

    for (var fn in json) {
        if (json.hasOwnProperty(fn)) {
            generateAPICall(fn, json[fn]);
        }
    }

    write('RedisService', '}');
}

function generateAPIImpl(json) {
    fs.truncateSync(prefix + 'impl/RedisServiceImpl.java');

    write('impl/RedisServiceImpl', 'package io.vertx.redis.impl;');
    write('impl/RedisServiceImpl', '');
    write('impl/RedisServiceImpl', 'import io.vertx.core.*;');
    write('impl/RedisServiceImpl', 'import io.vertx.core.json.*;');
    write('impl/RedisServiceImpl', '');
    write('impl/RedisServiceImpl', 'public final class RedisServiceImpl extends AbstractRedisService {');
    write('impl/RedisServiceImpl', '');
    write('impl/RedisServiceImpl', '  RedisServiceImpl(Vertx vertx, JsonObject config) { super(vertx, config); }');
    write('impl/RedisServiceImpl', '');

    for (var fn in json) {
        if (json.hasOwnProperty(fn)) {
            generateApiCallImpl(fn, json[fn]);
        }
    }

    write('impl/RedisServiceImpl', '}');
}

function write(file, line) {
    fs.appendFileSync(prefix + file + '.java', line + '\n');
}

/**
 * Given a name, generate two interface methods with and without Handler
 */
function generateAPICall(name, json) {
    var fnName = niceFnName(name);

    var hasOptions = json.arguments && json.arguments.length > 0;
    var returnType = retTypes[name];

    if (returnType === undefined) {
        returnType = '?'
    }

    var code = [
        '  /**',
        '   * ' + json.summary,
        '   * @since ' + json.since,
        '   * group: ' + json.group,
        '   *'
    ];

    // generate java doc
    if (hasOptions) {
        code.push('   * @param args JsonArray ' + JSON.stringify(json.arguments));
    }

    if (hasOptions) {
        code.push('   * @param handler Handler for the result of this call.');
        code.push('   */');
        code.push('  void ' + fnName + '(JsonArray args, Handler<AsyncResult<' + returnType + '>> handler);');
    } else {
        code.push('   */');
        code.push('  void ' + fnName + '(Handler<AsyncResult<' + returnType + '>> handler);');
    }

    code.push('');

    write('RedisService', code.join('\n'));
}

function niceFnName(name) {
    var tok = name.toLowerCase().split(' ');
    for (var i = 1; i < tok.length; i++) {
        var tmp = tok[i];
        tok[i] = tmp.charAt(0).toUpperCase() + tmp.substring(1);
    }

    return tok.join('');
}

function generateApiCallImpl(name, json) {
    var fnName = niceFnName(name);

    var hasOptions = json.arguments && json.arguments.length > 0;
    var returnType = retTypes[name];

    if (returnType === undefined) {
        returnType = '?'
    }

    var code = [];

    if (hasOptions) {
        code.push('  public void ' + fnName + '(JsonArray args, Handler<AsyncResult<' + returnType + '>> handler) { send' + returnType + '("' + name + '", args, handler); }');
    } else {
        code.push('  public void ' + fnName + '(Handler<AsyncResult<' + returnType + '>> handler) { send' + returnType + '("' + name + '",null, handler); }');
    }

    code.push('');

    write('impl/RedisServiceImpl', code.join('\n'));
}

var api = require('./api.json');

loadReturns();

generateAPI(api);
generateAPIImpl(api);