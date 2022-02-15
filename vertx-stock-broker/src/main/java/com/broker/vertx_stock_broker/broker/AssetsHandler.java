package com.broker.vertx_stock_broker.broker;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetsHandler implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(AssetsHandler.class);


  @Override
  public void handle(RoutingContext routingContext) {
    final JsonArray response = new JsonArray();

    AssetsRestApi.ASSETS.stream().map(Asset::new).forEach(response::add);

    DBHandlerCommon.sucessResponseHandler(routingContext, new JsonObject()
                                                                  .put("assets", response));
  }
}
