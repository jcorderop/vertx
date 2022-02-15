package com.example.vertx_starter;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
public class TestFuturePromiseExample {

  public static final Logger LOGGER = LoggerFactory.getLogger(TestFuturePromiseExample.class);

  @Test
  void promiseSuccess(Vertx vertx, VertxTestContext context) {
    final Promise<String> promise = Promise.promise();
    LOGGER.debug("Started...");
    vertx.setTimer(500, id -> {
      promise.complete("OK");
      LOGGER.debug("Done!");
      context.completeNow();
    });
    LOGGER.debug("Finished...");
  }

  @Test
  void promiseFailure(Vertx vertx, VertxTestContext context) {
    final Promise<String> promise = Promise.promise();
    LOGGER.debug("Started...");
    vertx.setTimer(500, id -> {
      promise.fail(new RuntimeException("we do not what was going on..."));
      LOGGER.debug("Fail!");
      context.completeNow();
    });
    LOGGER.debug("Finished...");
  }


  @Test
  void futureFailure(Vertx vertx, VertxTestContext context) {
    final Promise<String> promise = Promise.promise();
    LOGGER.debug("Started...");
    vertx.setTimer(500, id -> {
      promise.fail(new RuntimeException("It just blowup"));
      LOGGER.warn("NOT Done!");
      //context.completeNow();
    });

    final Future<String> future = promise.future();
    future.onSuccess(result -> {
      LOGGER.debug("Future done!");
      context.completeNow();
    }).onFailure(error -> {
      LOGGER.error("Inside the Fail Future: [{}]",error);
      context.completeNow();
    });

    LOGGER.debug("Finished...");
  }

  @Test
  void futureSuccess(Vertx vertx, VertxTestContext context) {
    final Promise<String> promise = Promise.promise();
    LOGGER.debug("Started...");
    vertx.setTimer(500, id -> {
      promise.complete("Totally fine!!!");
      LOGGER.debug("Done!");
      //context.completeNow();
    });

    final Future<String> future = promise.future();
    future.onSuccess(result -> {
      LOGGER.debug("Future done!");
      context.completeNow();
    }).onFailure(context::failNow);

    LOGGER.debug("Finished...");
  }

  @Test
  void futureSuccessMap(Vertx vertx, VertxTestContext context) {
    final Promise<String> promise = Promise.promise();
    LOGGER.debug("Started...");
    vertx.setTimer(500, id -> {
      promise.complete("Totally fine!!!");
      LOGGER.debug("Done!");
      //context.completeNow();
    });

    final Future<String> future = promise.future();
    future.map(asString -> {
      LOGGER.debug("Converting to JsonObject: [{}]", asString);
      return new JsonObject().put("wrapper", asString);
    }).map(jsonObject -> new JsonArray().add(jsonObject))
      .onSuccess(result -> {
      LOGGER.debug("Future done! [{}] of type [{}]", result, result.getClass().getSimpleName());
      context.completeNow();
    }).onFailure(context::failNow);

    LOGGER.debug("Finished...");
  }

  @Test
  void futureCoordinator(Vertx vertx, VertxTestContext context) {
    vertx.createHttpServer()
      .requestHandler(request -> LOGGER.debug("Request: {}", request))
      .listen(10_000)
      .compose(server -> {
        LOGGER.debug("Create some other task...");
        return Future.succeededFuture(server);
      })
      .compose(server -> {
        LOGGER.debug("Event more task...");
        return Future.succeededFuture(server);
      })
      .onFailure(context::failNow)
      .onSuccess(server -> {
        LOGGER.debug("Server started on port {}", server.actualPort());
        context.completeNow();
      });
  }

  @Test
  void futureComposite (Vertx vertx, VertxTestContext context) {
    vertx.exceptionHandler(e -> System.out.println("********* here we are *************"+e.getMessage()));

    var one = Promise.promise();
    var two = Promise.promise();
    var three = Promise.promise();

    var oneFuture = one.future();
    var twoFuture = two.future();
    var threeFuture = three.future();

    CompositeFuture.all(oneFuture, twoFuture, threeFuture)
      .onFailure(context::failNow)
      .onSuccess(result -> {
        LOGGER.debug("waiting to complete...");
        context.completeNow();
      });

    try {
      vertx.setTimer(500, id -> {
        one.complete();
        two.complete();
        //three.fail("sorry");
        three.complete();
      });
    } catch (Exception e) {
      // this is never reached
      System.out.println("**********************"+e.getMessage());
    }

  }
}
