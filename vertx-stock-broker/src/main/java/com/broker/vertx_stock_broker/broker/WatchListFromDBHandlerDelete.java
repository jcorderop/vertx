package com.broker.vertx_stock_broker.broker;


import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.broker.vertx_stock_broker.broker.DBHandlerCommon.deleteWatchListByAccoutId;

public class WatchListFromDBHandlerDelete implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(WatchListFromDBHandlerDelete.class);

  private final Pool pgPool;

  public WatchListFromDBHandlerDelete(Pool pgPool) {
    this.pgPool = pgPool;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String accountId = WatchListRestApi.getAccountId(routingContext);
    logger.info("Account Id parameter: {}", accountId);

    pgPool.withTransaction(sqlConnection -> {
      return deleteWatchListByAccoutId(sqlConnection, routingContext, accountId);
    });

  }


}
