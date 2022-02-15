package com.example.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleA extends AbstractVerticle {

  public static final Logger logger = LoggerFactory.getLogger(VerticleA.class);

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    logger.info("Start: {}", getClass().getName());
    vertx.deployVerticle(new VerticleAA(), whenDeployed -> {
      logger.info("Stopping");
      vertx.undeploy(whenDeployed.result());
    });
    vertx.deployVerticle(new VerticleAB(), whenDeployed -> {
      logger.info("Stopping...");
      vertx.undeploy(whenDeployed.result());
    });
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    logger.info("Start: {}...", getClass().getName());
    stopPromise.complete();
  }
}
