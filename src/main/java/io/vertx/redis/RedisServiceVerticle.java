package io.vertx.redis;

import io.vertx.core.AbstractVerticle;

public class RedisServiceVerticle extends AbstractVerticle {

  private RedisService service;

  @Override
  public void start() throws Exception {
    // Create the service
    service = RedisService.create(vertx, config);
    service.start();
    String address = config.getString("address", "vertx.redis");
    vertx.eventBus().registerService(service, address);
  }

  @Override
  public void stop() throws Exception {
    service.stop();
  }
}
