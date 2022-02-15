package com.example.vertx_starter.workers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WorkerExample extends AbstractVerticle {

  public static final Logger LOGGER = LoggerFactory.getLogger(WorkerExample.class);

  public static void main(String[] args) {
    LOGGER.info("Starting project...");
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new WorkerExample());
    LOGGER.debug("WorkerExample has been started...");
  }

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    LOGGER.info("Start: {}", getClass().getName());
    vertx.deployVerticle(new WorkerVertical()
      , new DeploymentOptions()
        .setWorker(true)
        .setWorkerPoolSize(1)
        .setWorkerPoolName("my-vertx-thread"));
    startPromise.complete();
    executeBlockingCode();
  }

  private void executeBlockingCode() {
    vertx.executeBlocking(event -> {
      //executed on the thread
      LOGGER.debug("Executed on the blocked thread...");
      try {
        TimeUnit.MILLISECONDS.sleep(5000);
        //event.fail("Failed...");
        event.complete();
      } catch (InterruptedException e) {
        LOGGER.error("Failed: {}",e);
        event.fail(e);
      }
    }, result -> {
      //executed on the event loop
      if (result.succeeded()) {
        LOGGER.debug("Blocking call done...");
      } else {
        LOGGER.debug("Blocking call failed...", result.cause());
      }
    });
  }
}
