package com.example.vertx_starter.bus;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonObjectRequestResponseExample {
  public static final Logger LOGGER = LoggerFactory.getLogger(JsonObjectRequestResponseExample.class);
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
      final var message = new JsonObject();
      message.put("name","vertx-test");
      message.put("version",1);

      LOGGER.debug("Sending: {}", message.encode());
      EventBus request = eventBus.<JsonArray>request(MY_REQUEST_ADDRESS, message, reply -> {
        LOGGER.debug("Response: {}", reply.result().body());
      });
    }
  }

  static class ResponseVertical extends AbstractVerticle {
    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().<JsonObject>consumer(MY_REQUEST_ADDRESS
        , message -> {
              LOGGER.debug("Message Received: {}", message.body());
              message.reply(new JsonArray().add(message.body()).add(new JsonObject().put("status","successful")));
            });
    }
  }


}
