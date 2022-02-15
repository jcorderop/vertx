package com.broker.vertx_stock_broker.broker;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class QuoteHandler implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(QuoteHandler.class);

  private final Map<String, JsonObject> cachedQuotes;

  public QuoteHandler(Map<String, JsonObject> cachedQuotes) {
    this.cachedQuotes = cachedQuotes;
  }

  @Override
  public void handle(RoutingContext routingContext) {

    final String assetParam = routingContext.pathParam("asset");
    logger.info("Asset parameter: {}", assetParam);

    var maybeQuote = Optional.ofNullable(this.cachedQuotes.get(assetParam));

    if (maybeQuote.isEmpty()) {
      DBHandlerCommon.notFoundHandler(routingContext, "quote "+ assetParam +" not found.");
      return;
    }

    final JsonObject response = maybeQuote.get();
    DBHandlerCommon.sucessResponseHandler(routingContext, response);
  }
}
