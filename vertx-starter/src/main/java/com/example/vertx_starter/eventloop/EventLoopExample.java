package com.example.vertx_starter.eventloop;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class EventLoopExample extends AbstractVerticle {

  public static final Logger LOGGER = LoggerFactory.getLogger(EventLoopExample.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx(
      new VertxOptions()
          .setMaxEventLoopExecuteTime(500)
          .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS)
          .setBlockedThreadCheckInterval(1)
          .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS)
          //.setEventLoopPoolSize(4)
    );
    vertx.deployVerticle(EventLoopExample.class.getName(), new DeploymentOptions().setInstances(2));
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOGGER.debug("Start {}", this.getClass().getName());
    startPromise.complete();

    TimeUnit.MILLISECONDS.sleep(5000);
  }
}
