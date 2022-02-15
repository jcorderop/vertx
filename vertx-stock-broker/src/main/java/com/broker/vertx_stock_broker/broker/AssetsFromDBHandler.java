package com.broker.vertx_stock_broker.broker;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetsFromDBHandler implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(AssetsFromDBHandler.class);

  private final Pool pgPool;

  public AssetsFromDBHandler(Pool pgPool) {
    this.pgPool = pgPool;
  }

  @Override
  public void handle(RoutingContext routingContext) {

    pgPool.query("SELECT a.value FROM broker.assets a")
      .execute()
      .onFailure(DBHandlerCommon.failureHandler(routingContext, "Could not fetch asset!"))
      .onSuccess(createDBSuccessResponse(routingContext));
  }

  private Handler<RowSet<Row>> createDBSuccessResponse(RoutingContext routingContext) {
    return result -> {
      var response = new JsonArray();

      result.forEach(row -> {
        response.add(row.getValue("value"));
      });

      DBHandlerCommon.sucessResponseHandler(routingContext, new JsonObject()
                                                                    .put("assets", response));
    };
  }

}
