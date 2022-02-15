package com.broker.vertx_stock_broker;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestAssetRestApi extends AbstractRestApiTest {

  private static final Logger logger = LoggerFactory.getLogger(TestAssetRestApi.class);

  @Test
  void return_all_assets(Vertx vertx, VertxTestContext testContext) throws Throwable {
    //given
    var webClient = createWebClient(vertx);

    //when
    webClient.get("/assets").send().onComplete(testContext.succeeding(response -> {
      var json = response.bodyAsJsonArray();
      logger.info("Response {}", json);

      //then
      assertEquals("[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"FB\"},{\"name\":\"GOOG\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"}]", json.encode());
      assertEquals(200, response.statusCode());

      assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
      assertEquals("my-value", response.getHeader("my-header"));

      testContext.completeNow();
    }));
  }

}
