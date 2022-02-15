package com.broker.vertx_stock_broker.broker;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class WatchListHandlerPut implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(WatchListHandlerPut.class);

  private final Map<UUID, WatchList> watchListPerAccount;

  public WatchListHandlerPut(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListRestApi.getAccountId(context);

    var json = context.getBodyAsJson();
    var watchList = json.mapTo(WatchList.class);
    watchListPerAccount.put(UUID.fromString(accountId), watchList);
    logger.info("watchListPerAccount size: {}", watchListPerAccount.size());
    context.response().end(json.toBuffer());
  }
}
