package com.broker.vertx_stock_broker.broker;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class DBHandlerCommon {

  private static final Logger logger = LoggerFactory.getLogger(DBHandlerCommon.class);

  public static Handler<Throwable> failureHandler(final RoutingContext routingContext, final String errorMessage) {
    return error -> {
      logger.error("DB Error: " + error);

      routingContext.response()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .end(new JsonObject()
          .put("message", errorMessage)
          .put("path", routingContext.normalizedPath())
          .toBuffer()
        );
    };
  }

  public static void notFoundHandler(RoutingContext context, String warnMessage) {
    logger.warn(warnMessage);
    context.response()
      .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
      .end(new JsonObject()
        .put("message", warnMessage)
        .put("path", context.normalizedPath())
        .toBuffer()
      );
  }

  public static void sucessResponseHandler(RoutingContext routingContext, JsonObject response) {
    logger.info("Path {} response with {}", routingContext.normalizedPath(), response.encode());
    routingContext.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .putHeader("my-header","my-value")
      .end(response.toBuffer());
  }

  public static Future<RowSet<JsonObject>> deleteWatchListByAccoutId(SqlConnection sqlConnection, RoutingContext routingContext, String accountId) {
    Future<RowSet<JsonObject>> result = SqlTemplate.forUpdate(sqlConnection,
        "DELETE FROM broker.watchlist w" +
          " WHERE w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(DBHandlerCommon.failureHandler(routingContext,
        "Could not delete the AccountId for: " + accountId))
      .onSuccess(createDBSuccessResponse(routingContext, accountId));
    logger.info("Batch has finished...");
    return result;
  }

  private static Handler<RowSet<JsonObject>> createDBSuccessResponse(RoutingContext routingContext, String accountId) {
    return result -> {
      logger.info("Delete executed successful... rows {} for account accountId {}", result.rowCount(), accountId);
      routingContext.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end();
    };
  }
}
