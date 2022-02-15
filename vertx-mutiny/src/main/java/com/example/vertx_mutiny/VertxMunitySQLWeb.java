package com.example.vertx_mutiny;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.function.Function;

public class VertxMunitySQLWeb extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(VertxMunitySQLWeb.class);

  public static void main(String[] args) {
    final var port = EmbeddedPostgres.startPostgres();

    final var options = new DeploymentOptions()
      .setConfig(new JsonObject().put("port", port));

    Vertx.vertx().deployVerticle(VertxMunitySQLWeb::new, options)
      .subscribe().with(
        id -> logger.info("Verticle deployed Successfully, ID: {}", id),
        failure -> logger.error("Verticle could not be deployed, check following exception:", failure)
      );
  }

  @Override
  public Uni<Void> asyncStart() {
    vertx.periodicStream(5000L).toMulti()
        .subscribe().with(aLong -> logger.info("{}", LocalTime.now().getMinute()));

    logger.info("Async start-up and configuration...");

    var db = createPgPool(config());

    var router = Router.router(vertx);
    router.route().failureHandler(this::failureHandler);
    router.get("/user").respond(routingContext -> executeQuery(db));

    Uni<Void> listen = vertx.createHttpServer()
      //.requestHandler(req -> req.response().endAndForget("Hallo!"))
      .requestHandler(router)
      .listen(8111)
      .replaceWithVoid();
    return listen;
  }

  private Uni<JsonArray> executeQuery(Pool db) {
    logger.info("Executing query to get users...");
    Uni<JsonArray> result = db.query("SELECT * FROM users")
      .execute()
      .onItem().transform(rows -> {
          logger.info("Processing response... elements: {}", rows.size());
          var data = new JsonArray();
          for (Row row : rows) {
            data.add(row.toJson());
          }
          logger.info("Response: {}", data.encode());
          return data;
        })
      .onFailure().invoke(failure -> logger.error("SQL to get user failed, see below exception:", failure))
      .onFailure().recoverWithItem(new JsonArray());
      //.subscribe().with(objects -> logger.info("Number of Users: {}", objects.size()),
      //  failure -> logger.error("Could not handle the request..."));
    logger.info("Query has successfully finished... {}", result);
    return result;
  }

  private Function<RowSet<Row>, JsonArray> getUserFromDB() {
    return rows -> {
      logger.info("Processing response... elements: {}", rows.size());
      var data = new JsonArray();
      for (Row row : rows) {
        data.add(row.toJson());
      }
      logger.info("Response: {}", data.encode());
      return data;
    };
  }

  private Pool createPgPool(JsonObject config) {
    var port = config.getInteger("port");

    PgConnectOptions pgConnectOptions = new PgConnectOptions()
      .setHost("localhost")
      .setPort(port)
      .setDatabase(EmbeddedPostgres.DATABASE_NAME)
      .setUser(EmbeddedPostgres.USERNAME)
      .setPassword(EmbeddedPostgres.PASSWORD);

    var pgPoolOptions =  new PoolOptions().setMaxSize(5);
    final Pool pool = PgPool.pool(vertx, pgConnectOptions, pgPoolOptions);
    pool.getConnection()
        .onFailure().invoke(failure -> logger.error("Could not connect:", failure));
    logger.info("DB Connection created listening on port {}", port);
    return pool;
  }

  private void failureHandler(RoutingContext routingContext) {
    routingContext.response().setStatusCode(500).endAndForget("Something went wrong...");
  }

}
