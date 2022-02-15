package com.example.vertx_starter.bus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishSubscribeExample {
  public static final Logger LOGGER = LoggerFactory.getLogger(PublishSubscribeExample.class);
  public static final String MY_REQUEST_ADDRESS = "my.request.address";

  public static void main(String[] args) {
    LOGGER.info("Starting project...");
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new Publisher());
    vertx.deployVerticle(new Subscriber1());
    vertx.deployVerticle(new Subscriber2());
    LOGGER.debug("Has been started...");
  }

  static class Publisher extends AbstractVerticle {
    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.setPeriodic(1, id -> {
        vertx.eventBus().publish(Publisher.class.getName(), "Publishing sending something ..."+ id);
      });
    }
  }

  static class Subscriber1 extends AbstractVerticle {
    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().<String>consumer(Publisher.class.getName(), message -> {
        LOGGER.debug("New: {} message {}", Subscriber1.class.getName(), message.body());
      });
    }
  }

  static class Subscriber2 extends AbstractVerticle {
    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().<String>consumer(Publisher.class.getName(), message -> {
        LOGGER.debug("New: {} message {}", Subscriber2.class.getName(), message.body());
      });
    }
  }

}
