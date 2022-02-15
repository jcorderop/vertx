package com.broker.vertx_stock_broker.broker;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class QuoteFromDBHandler implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(AssetsFromDBHandler.class);

  private final Pool pgPool;

  public QuoteFromDBHandler(Pool pgPool) {
    this.pgPool = pgPool;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final String assetParam = routingContext.pathParam("asset");
    logger.info("Asset parameter: {}", assetParam);

    SqlTemplate.forQuery(pgPool,
      "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume" +
        " FROM broker.quotes q " +
        " WHERE asset=#{asset}")
      .mapTo(QuoteEntity.class)
      .execute(Collections.singletonMap("asset", assetParam))
      .onFailure(DBHandlerCommon.failureHandler(routingContext,
        "Could not fetch the Quotes for: " + assetParam))
      .onSuccess(createDBSuccessResponse(routingContext, assetParam));
  }

  private Handler<RowSet<QuoteEntity>> createDBSuccessResponse(final RoutingContext routingContext, final String assetParam) {
    return quotes -> {
      if (!quotes.iterator().hasNext()) {
        //no entity
        DBHandlerCommon.notFoundHandler(routingContext, "quote "+ assetParam +" not found.");
        return;
      }

      var response =  quotes.iterator().next().toJsonObject();
      DBHandlerCommon.sucessResponseHandler(routingContext, response);
    };
  }
}
