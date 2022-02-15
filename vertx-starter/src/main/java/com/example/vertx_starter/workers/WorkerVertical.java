package com.example.vertx_starter.workers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WorkerVertical extends AbstractVerticle {

  public static final Logger LOGGER = LoggerFactory.getLogger(WorkerVertical.class);

  public static void main(String[] args) {
    LOGGER.info("Starting project...");
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new WorkerExample());
    LOGGER.debug("WorkerExample has been started...");
  }

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    LOGGER.info("Deployed as Worker Vertical: {}", getClass().getName());
    startPromise.complete();
    TimeUnit.MILLISECONDS.sleep(5000);
    LOGGER.debug("Worker Vertical Blocking queue done...");
  }
}
