package com.example.vertx_websockets;

import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PriceBroadcast {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
  private final Map<String, ServerWebSocket> connections = new HashMap<>();

  public PriceBroadcast(Vertx vertx) {
    pediodicUpdate(vertx);
  }

  private void pediodicUpdate(Vertx vertx) {
    vertx.setPeriodic(Duration.ofSeconds(1).toMillis(), id -> {
      logger.info("Pushing prices to client {}", connections.size());
      pushPrice();
    });
  }

  private void pushPrice() {
    var price = createPrice();
    for (ServerWebSocket ws : connections.values()) {
      ws.writeTextMessage(price);
    }
  }

  private String createPrice() {
    var price = new JsonObject()
      .put("symbol", "GOOG")
      .put("price", new Random().nextInt(100))
      .toString();
    return price;
  }


  public void register(ServerWebSocket ws) {
    connections.put(ws.textHandlerID(), ws);
  }

  public void unregister(ServerWebSocket ws) {
    connections.remove(ws.textHandlerID());
  }
}
