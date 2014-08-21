package io.vertx.redis;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface RedisServiceFactory {

  RedisService create(Vertx vertx, JsonObject config);

  RedisService createEventBusProxy(Vertx vertx, String address);
}
