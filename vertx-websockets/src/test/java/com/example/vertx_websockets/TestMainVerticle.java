package com.example.vertx_websockets;

import io.vertx.core.Vertx;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(VertxExtension.class)
public class TestMainVerticle {


  private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
  public static final int MESSAGES_COUNTER = 5;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void can_connect_to_websocket_server(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var client = vertx.createHttpClient();
    client.webSocket(8900, "localhost", WebSocketHandler.PATH)
      .onFailure(testContext::failNow)
      .onComplete(testContext.succeeding(ws -> {
        ws.handler(data -> {
          final var receivedData = data.toString();
          logger.info("Received data: {}", receivedData);
          assertEquals("Connected!", receivedData);
          client.close();
          testContext.completeNow();
        });
      }));
  }

  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void can_receive_multiple_messages(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var client = vertx.createHttpClient();

    final AtomicInteger counter = new AtomicInteger(0);

    client.webSocket(new WebSocketConnectOptions()
        .setHost("localhost")
        .setPort(8900)
        .setURI(WebSocketHandler.PATH))
      .onFailure(testContext::failNow)
      .onComplete(testContext.succeeding(ws -> {
        ws.handler(data -> {
          final var receivedData = data.toString();
          logger.info("Received data: {}", receivedData);
          //assertEquals("Connected!", receivedData);
          var currentValue = counter.incrementAndGet();
          if (currentValue >= MESSAGES_COUNTER) {
            logger.info("Test Completed...");
            client.close();
            testContext.completeNow();
          } else {
            logger.error("Not enough messages... " + currentValue + "/" + MESSAGES_COUNTER);
          }
        });
      }));
  }
}
