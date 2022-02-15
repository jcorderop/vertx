package com.example.vertx_mutiny;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxMunityWeb extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(VertxMunityWeb.class);

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new VertxMunityWeb())
      .subscribe().with(
        id -> logger.info("Verticle deployed Successfully, ID: {}", id),
        failure -> logger.error("Verticle could not be deployed, check following exception:", failure)
      );
  }

  @Override
  public Uni<Void> asyncStart() {
    logger.info("Async start-up and configuration...");

    var router = Router.router(vertx);
    router.route().failureHandler(this::failureHandler);
    router.get("/user").respond(this::getUser);

    Uni<Void> listen = vertx.createHttpServer()
      //.requestHandler(req -> req.response().endAndForget("Hallo!"))
      .requestHandler(router)
      .listen(8111)
      .replaceWithVoid();
    return listen;
  }

  private void failureHandler(RoutingContext routingContext) {
    routingContext.response().setStatusCode(500).endAndForget("Something went wrong...");
  }

  private <T> Uni<JsonArray> getUser(RoutingContext routingContext) {
    final var response = new JsonArray();
    response.add(new JsonObject().put("name", "Alicia"));
    response.add(new JsonObject().put("name", "Vidal"));
    return Uni.createFrom().item(response);
  }


}
