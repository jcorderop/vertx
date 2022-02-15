package com.example.vertx_starter.bus;


import com.example.vertx_starter.bus.model.*;
import com.example.vertx_starter.workers.bus.model.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingPongRequestResponseExample {
  public static final Logger LOGGER = LoggerFactory.getLogger(PingPongRequestResponseExample.class);
  public static final String MY_REQUEST_ADDRESS = PingVertical.class.getName();

  public static void main(String[] args) {
    LOGGER.info("Starting project...");
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new PingVertical(), PingPongRequestResponseExample::logOnError);
    vertx.deployVerticle(new PongVertical(), PingPongRequestResponseExample::logOnError);
    LOGGER.debug("Has been started...");
  }

  private static void logOnError(AsyncResult<String> ar) {
    if (ar.failed()) {
      LOGGER.error("What is going on? {}", ar);
    }
  }

  static class PingVertical extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      var eventBus = vertx.eventBus();
      eventBus.registerDefaultCodec(Ping.class, new LocalMessageCodec<>(Ping.class));

      Ping message = new Ping("vertx", true);
      LOGGER.debug("Sending: {}", message);
      EventBus request = eventBus.<Pong>request(MY_REQUEST_ADDRESS, message, reply -> {
        if (reply.failed()) {
          LOGGER.error("Message could not be processed...");
        }
        LOGGER.debug("Response: {}", reply.result().body());
      });

      startPromise.complete();
    }
  }

  static class PongVertical extends AbstractVerticle {
    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      var eventBus = vertx.eventBus();
      eventBus.registerDefaultCodec(Pong.class, new LocalMessageCodec<>(Pong.class));

      eventBus.<Ping>consumer(MY_REQUEST_ADDRESS
            , message -> {
              LOGGER.debug("Message Received: {}", message.body());
              message.reply(new Pong(Type.CONFIRMATION, Status.OK));
            }).exceptionHandler(error -> {
              LOGGER.error("Message could not be read: {}", error);
            });

      startPromise.complete();
    }
  }


}
