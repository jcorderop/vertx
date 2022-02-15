package com.example.vertx_websockets;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.createHttpServer()
      .webSocketHandler(new WebSocketHandler(vertx))
      .listen(8900, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port 8900");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }
}
