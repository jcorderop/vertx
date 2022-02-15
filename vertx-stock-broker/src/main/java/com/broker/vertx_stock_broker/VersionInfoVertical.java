package com.broker.vertx_stock_broker;

import com.broker.vertx_stock_broker.config.ConfigLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionInfoVertical extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(VersionInfoVertical.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(config -> {
        logger.info("Current Application Version: {}", config.getVersion());
        startPromise.complete();
      });
  }
}
