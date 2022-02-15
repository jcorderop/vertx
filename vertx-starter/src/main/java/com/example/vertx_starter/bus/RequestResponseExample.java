package com.example.vertx_starter.bus;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseExample {
  public static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseExample.class);
  public static final String MY_REQUEST_ADDRESS = "my.request.address";

  public static void main(String[] args) {
    LOGGER.info("Starting project...");
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new RequestVertical());
    vertx.deployVerticle(new ResponseVertical());
    LOGGER.debug("Has been started...");
  }

  static class RequestVertical extends AbstractVerticle {
    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      var eventBus = vertx.<String>eventBus();
      String message = "Hello world!";
      LOGGER.debug("Sending: {}", message);
      EventBus request = eventBus.request(MY_REQUEST_ADDRESS, message, reply -> {
        LOGGER.debug("Response: {}", reply.result().body());
      });
    }
  }

  static class ResponseVertical extends AbstractVerticle {
    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().<String>consumer(MY_REQUEST_ADDRESS
        , message -> {
              LOGGER.debug("Message Received: {}", message.body());
              message.reply("Received your message, it will be processed...");
            });
    }
  }


}
