package com.broker.vertx_stock_broker.broker;


import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WatchListFromDBHandlerPut implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(WatchListFromDBHandlerPut.class);

  private final Pool pgPool;

  public WatchListFromDBHandlerPut(Pool pgPool) {
    this.pgPool = pgPool;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String accountId = WatchListRestApi.getAccountId(routingContext);
    logger.info("Account Id parameter: {}", accountId);

    //watchList.getAssets().forEach(insertAssetsByWatchList(routingContext, accountId));
    pgPool.withTransaction(sqlConnection -> DBHandlerCommon.
      deleteWatchListByAccoutId(sqlConnection, routingContext, accountId)
      .compose(jsonObjects -> insertAssetsByWatchList(sqlConnection, routingContext, accountId)));
    logger.info("Batch has finished...");
  }

  private List<Map<String, Object>> getParameterBatch(String accountId, WatchList watchList) {
     return watchList.getAssets().stream().map(asset -> {
      final HashMap<String, Object> parameters = new HashMap<>();
      parameters.put("account_id", accountId);
      parameters.put("asset", asset.getName());
      logger.info("Parameters: {}", parameters);
      return parameters;
    }).collect(Collectors.toList());
  }

  private Future<SqlResult<Void>> insertAssetsByWatchList(SqlConnection sqlConnection, RoutingContext routingContext, String accountId) {
      var jsonBody = routingContext.getBodyAsJson();
      var watchList = jsonBody.mapTo(WatchList.class);

      final List<Map<String, Object>> parameterBatch = getParameterBatch(accountId, watchList);
      logger.info("ParameterBatch: {}", parameterBatch);

    Future<SqlResult<Void>> sqlResultFuture = SqlTemplate.forUpdate(sqlConnection,
        "INSERT INTO broker.watchlist" +
          " VALUES (#{account_id}, #{asset})")
      .executeBatch(parameterBatch)
      .onFailure(DBHandlerCommon.failureHandler(routingContext,
        "Could not insert watchlist into account: " + accountId))
      .onSuccess(createDBSuccessResponse(routingContext));

    return sqlResultFuture;
  }

  private Handler<SqlResult<Void>> createDBSuccessResponse(RoutingContext routingContext) {
    return result -> {
      logger.info("Batch executed successful...");
      routingContext.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end();
    };
  }
}
