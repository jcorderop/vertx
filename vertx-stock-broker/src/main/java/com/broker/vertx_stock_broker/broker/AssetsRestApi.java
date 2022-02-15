package com.broker.vertx_stock_broker.broker;

import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  private static final Logger logger = LoggerFactory.getLogger(AssetsRestApi.class);

  public static final List<String> ASSETS = Arrays.asList("AAPL","AMZN","FB","GOOG","NFLX","TSLA");

  public static void attach(Router restApi, final Pool pgPool) {
    restApi.get("/assets").handler(new AssetsHandler());
    restApi.get("/pg/assets").handler(new AssetsFromDBHandler(pgPool));
  }


}
