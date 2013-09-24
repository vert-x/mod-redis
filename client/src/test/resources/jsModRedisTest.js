var container = require("vertx/container");
var vertx = require("vertx");
var vertxTests = require("vertx_tests");
var vassert = require("vertx_assert");


var RedisClient = require("redisClient");

function testClient() {
  var redis = new RedisClient("test.redis.api.client");

  redis.deployModule({}, 1, function (err) {
    if (err) {
      return vassert.testFail(err);
    }
    redis.set("key", "value", function (reply) {

      redis.get("key", function (reply) {

        vassert.assertEquals("value", reply.value);
        vassert.testComplete();
      });
    });
  });
}

vertxTests.startTests(this);