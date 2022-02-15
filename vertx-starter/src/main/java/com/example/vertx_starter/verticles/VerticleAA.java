package com.example.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleAA extends AbstractVerticle {

  public static final Logger logger = LoggerFactory.getLogger(VerticleAA.class);

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    logger.info("Start: {}...", getClass().getName());
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    logger.info("Stop: {}", getClass().getName());
    stopPromise.complete();
  }
}
