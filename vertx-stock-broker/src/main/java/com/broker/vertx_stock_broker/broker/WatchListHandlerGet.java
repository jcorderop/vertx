package com.broker.vertx_stock_broker.broker;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WatchListHandlerGet implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(WatchListHandlerGet.class);

  private final Map<UUID, WatchList> watchListPerAccount;

  public WatchListHandlerGet(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListRestApi.getAccountId(context);

    var maybeAWatchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
    if (maybeAWatchList.isEmpty()) {
      final String warnMessage = "WatchList for "+accountId+" not found.";
      logger.warn(warnMessage);
      context.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(new JsonObject()
          .put("message", warnMessage)
          .put("path",context.normalizedPath())
          .toBuffer()
        );
      return;
    }

    final JsonObject response = maybeAWatchList.get().toJsonObject();
    logger.info("Path {} responds with {}.", context.normalizedPath(), response.encode());
    context.response().end(response.toBuffer());
  }
}
