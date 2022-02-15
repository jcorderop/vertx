package com.broker.vertx_stock_broker;

import com.broker.vertx_stock_broker.broker.AssetsRestApi;
import com.broker.vertx_stock_broker.broker.QuotesRestAPI;
import com.broker.vertx_stock_broker.broker.WatchListRestApi;
import com.broker.vertx_stock_broker.config.BrokerConfig;
import com.broker.vertx_stock_broker.config.ConfigLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(config -> {
        logger.info("Retrieved Configuration {}", config);
      })
      .onComplete(brokerConfigAsyncResult -> {
        startServer(startPromise, brokerConfigAsyncResult.result());
        startPromise.complete();
      });
  }

  private void startServer(Promise<Void> startPromise, BrokerConfig brokerConfig) {
    logger.info("Preparing to start http server...");
    final Pool pgPool = createPgPool(brokerConfig);
    final Router restApi = attachtRoutes(brokerConfig, pgPool);
    startHttpServer(startPromise, restApi, brokerConfig);
  }

  private Router attachtRoutes(final BrokerConfig brokerConfig, final Pool pgPool) {
    final Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create()
        //.setBodyLimit(1024)
        //.setHandleFileUploads(true)
      )
      .failureHandler(RestApiVerticle::routeErrorHandler);

    AssetsRestApi.attach(restApi, pgPool);
    QuotesRestAPI.attach(restApi, pgPool);
    WatchListRestApi.attach(restApi, pgPool);
    return restApi;
  }

  private PgPool createPgPool(final BrokerConfig brokerConfig) {
    // Create DB Pool
    return PgPool.pool(vertx
      , getPgConnectOptions(brokerConfig)
      , getOptions());
  }

  private PoolOptions getOptions() {
    return new PoolOptions()
      .setMaxSize(4);
  }

  private PgConnectOptions getPgConnectOptions(final BrokerConfig brokerConfig) {
    PgConnectOptions pgConnectOptions = new PgConnectOptions()
      .setHost(brokerConfig.getDbConfig().getHost())
      .setPort(brokerConfig.getDbConfig().getPort())
      .setDatabase(brokerConfig.getDbConfig().getDatabase())
      .setUser(brokerConfig.getDbConfig().getUser())
      .setPassword(brokerConfig.getDbConfig().getPassword());
    return pgConnectOptions;
  }

  private void startHttpServer(Promise<Void> startPromise
                              , Router restApi
                              , BrokerConfig brokerConfig) {
    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error -> {
        logger.error("HTTP error: ", error);
      })
      .listen(brokerConfig.getServerPort(), http -> {
        if (http.succeeded()) {
          //startPromise.complete();
          logger.info("HTTP server started on port {}", brokerConfig.getServerPort());
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  public static void routeErrorHandler(RoutingContext errorContext) {
    if (errorContext.response().ended()) {
      //the user stop the request
      return;
    }
    logger.error("Router Error: ", errorContext.failed());
    errorContext.response()
      .setStatusCode(500)
      .end(new JsonObject().put("message", "something went wrong...").toBuffer());
  }
}
