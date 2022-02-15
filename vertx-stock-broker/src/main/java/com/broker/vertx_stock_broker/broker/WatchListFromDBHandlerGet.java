package com.broker.vertx_stock_broker.broker;


import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class WatchListFromDBHandlerGet implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(WatchListFromDBHandlerGet.class);

  private final Pool pgPool;

  public WatchListFromDBHandlerGet(Pool pgPool) {
    this.pgPool = pgPool;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String accountId = WatchListRestApi.getAccountId(routingContext);
    logger.info("Account Id parameter: {}", accountId);

    SqlTemplate.forQuery(pgPool,
        "SELECT w.asset" +
          " FROM broker.watchlist w " +
          " WHERE w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(DBHandlerCommon.failureHandler(routingContext,
        "Could not fetch the AccountId for: " + accountId))
      .onSuccess(createDBSuccessResponse(routingContext, accountId));
  }

  private Handler<RowSet<JsonObject>> createDBSuccessResponse(RoutingContext routingContext, String accountId) {
    return assets -> {
      if (!assets.iterator().hasNext()) {
        //no entity
        DBHandlerCommon.notFoundHandler(routingContext, "quote "+ accountId +" not found.");
        return;
      }

      var response =  new JsonArray();
      assets.forEach(response::add);
      DBHandlerCommon.sucessResponseHandler(routingContext, new JsonObject()
                                                              .put(accountId, response));
    };
  }
}
