package com.broker.vertx_stock_broker.broker;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WatchListRestApi {

  private static final Logger logger = LoggerFactory.getLogger(WatchListRestApi.class);

  public static final String PATH_ACCOUNT_WATCHLIST = "/account/watchlist/";
  public static final String PATH_ACCOUNT_WATCHLIST_ACCOUNT_ID = PATH_ACCOUNT_WATCHLIST + ":accountId";

  public static void attach(Router restApi, final Pool pgPool) {

    final Map<UUID, WatchList> watchListPerAccount = new HashMap<>();

    restApi.get(PATH_ACCOUNT_WATCHLIST_ACCOUNT_ID).handler(new WatchListHandlerGet(watchListPerAccount));
    restApi.put(PATH_ACCOUNT_WATCHLIST_ACCOUNT_ID).handler(new WatchListHandlerPut(watchListPerAccount));
    restApi.delete(PATH_ACCOUNT_WATCHLIST_ACCOUNT_ID).handler(new WatchListHandlerDelete(watchListPerAccount));

    restApi.get("/pg"+PATH_ACCOUNT_WATCHLIST_ACCOUNT_ID).handler(new WatchListFromDBHandlerGet(pgPool));
    restApi.put("/pg"+PATH_ACCOUNT_WATCHLIST_ACCOUNT_ID).handler(new WatchListFromDBHandlerPut(pgPool));
    restApi.delete("/pg"+PATH_ACCOUNT_WATCHLIST_ACCOUNT_ID).handler(new WatchListFromDBHandlerDelete(pgPool));
  }

  public static String getAccountId(RoutingContext context) {
    var accountId =  context.pathParam("accountId");
    logger.info("Path {} request with account id {}.", context.normalizedPath(), accountId);
    return accountId;
  }
}
