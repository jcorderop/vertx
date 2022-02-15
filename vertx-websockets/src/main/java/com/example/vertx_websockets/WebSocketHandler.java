package com.example.vertx_websockets;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketHandler implements Handler<ServerWebSocket> {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
  public static final String PATH = "/ws/simple/prices";

  private final PriceBroadcast broadcast;

  public WebSocketHandler(Vertx vertx) {
    this.broadcast = new PriceBroadcast(vertx);
  }


  @Override
  public void handle(ServerWebSocket ws) {
    if (!PATH.equalsIgnoreCase(ws.path())) {
      logger.warn("Connection rejected to this path: {}"
        , ws.path());
      ws.writeFinalTextFrame("Wrong path, only path "+PATH+" is accepted...");
      closeClient(ws);
      return;
    }

    logger.info("Opening web socket connection: {} - {}"
      , ws.path()
      , ws.textHandlerID());

    ws.accept();
    ws.frameHandler(frameHandler(ws));
    ws.endHandler(onClose -> {
      logger.info("Connection closed: {}", ws.textHandlerID());
      broadcast.unregister(ws);
    });
    ws.exceptionHandler(err -> logger.error("Client connection failed: ", err));

    broadcast.register(ws);
    ws.writeTextMessage("Connected!");

  }

  private Handler<WebSocketFrame> frameHandler(ServerWebSocket ws) {
    return received -> {
      final String message = received.textData();
      logger.info("Message Received: {} from client {}", message, ws.textHandlerID());

      if ("disconnect me".equalsIgnoreCase(message)) {
        logger.info("Client close request!");
        closeClient(ws);
      } else {
        ws.writeTextMessage("Not Supported => ("+message+")");
      }
    };
  }

  private void closeClient(ServerWebSocket ws) {
    ws.close((short)1000, "Normal Closure.");
  }
}
