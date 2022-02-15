package com.broker.vertx_stock_broker.broker;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class WatchListHandlerDelete implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(WatchListHandlerDelete.class);

  private final Map<UUID, WatchList> watchListPerAccount;

  public WatchListHandlerDelete(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListRestApi.getAccountId(context);
    final WatchList removedWatchList = watchListPerAccount.remove(UUID.fromString(accountId));
    logger.info("watchListPerAccount size: {}", watchListPerAccount.size());
    context.response().end(removedWatchList.toJsonObject().toBuffer());
  }
}
