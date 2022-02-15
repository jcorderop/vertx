package com.broker.vertx_stock_broker;

import com.broker.vertx_stock_broker.config.ConfigLoader;
import com.broker.vertx_stock_broker.db.migration.FlywayMigration;
import io.vertx.core.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  public static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  //public static int PORT= 8888;

  public static void main(String[] args) {
   /* var vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
      new MicrometerMetricsOptions()
        .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true)
          .setStartEmbeddedServer(true)
          .setEmbeddedServerOptions(new HttpServerOptions().setPort(8080))
          .setEmbeddedServerEndpoint("/metrics/vertx"))
        .setEnabled(true)));*/
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      logger.error("Unhandled exception {}", error);
    });

    vertx.deployVerticle(new MainVerticle(), ar -> {
      if (ar.failed()) {
        logger.error("Failed to be deployed", ar.cause());
      }
      logger.info("Main Start Deployed {}!",MainVerticle.class.getName());
    });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(VersionInfoVertical.class.getName())
            .onFailure(startPromise::fail)
            .onSuccess(id -> logger.info("Deployed {} with id {}",VersionInfoVertical.class.getName(), id))
            .compose(next -> migrateDataBase())
            .onFailure(startPromise::fail)
            .onSuccess(id -> logger.info("Migrated Database latest version success!!!"))
            .compose(next -> deployRestApiVerticle(startPromise));
  }

  private Future<Object> migrateDataBase() {
    return ConfigLoader.load(vertx)
                .compose(config -> {
                    return FlywayMigration.migrate(vertx, config.getDbConfig());
                  });

  }

  private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
    return vertx.deployVerticle(RestApiVerticle.class.getName(),
        new DeploymentOptions().setInstances(processors()))
              .onFailure(startPromise::fail)
              .onSuccess(id -> {
                      logger.info("DeployRestApiVerticle {} with id {}",RestApiVerticle.class.getName(), id);
                      startPromise.complete();
                  });
  }

  private int processors() {
    int numOfProcessors = Math.max(1, Runtime.getRuntime().availableProcessors()/2);
    //numOfProcessors = 1;
    logger.info("Number of processors: {}", numOfProcessors);
    return numOfProcessors;
  }
}
