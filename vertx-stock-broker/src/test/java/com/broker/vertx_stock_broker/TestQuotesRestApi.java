package com.broker.vertx_stock_broker;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi extends AbstractRestApiTest {

  private static final Logger logger = LoggerFactory.getLogger(TestQuotesRestApi.class);

  @Test
  void return_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    //given
    var webClient = createWebClient(vertx);

    //when
    webClient.get("/quotes/AAPL").send().onComplete(testContext.succeeding(response -> {
      var json = response.bodyAsJsonObject();
      logger.info("Response {}", json);

      //then
      assertEquals("{\"name\":\"AAPL\"}", json.getJsonObject("asset").encode());
      assertEquals(200, response.statusCode());
      testContext.completeNow();
    }));
  }

  @Test
  void return_asset_not_found(Vertx vertx, VertxTestContext testContext) throws Throwable {
    //given
    var webClient = createWebClient(vertx);

    //when
    webClient.get("/quotes/IBM").send().onComplete(testContext.succeeding(response -> {
      var json = response.bodyAsJsonObject();
      logger.info("Response {}", json);

      //then
      assertEquals("{\"message\":\"quote IBM not found.\",\"path\":\"/quotes/IBM\"}", json.encode());
      assertEquals(404, response.statusCode());
      testContext.completeNow();
    }));
  }
}
