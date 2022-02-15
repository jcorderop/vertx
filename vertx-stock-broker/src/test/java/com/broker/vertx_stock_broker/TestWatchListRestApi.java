package com.broker.vertx_stock_broker;

import com.broker.vertx_stock_broker.broker.Asset;
import com.broker.vertx_stock_broker.broker.WatchList;
import com.broker.vertx_stock_broker.broker.WatchListRestApi;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi extends AbstractRestApiTest {

  private static final Logger logger = LoggerFactory.getLogger(TestWatchListRestApi.class);

  @Test
  void add_and_return_watchlist_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    //given
    var webClient = createWebClient(vertx);
    var accountId = UUID.randomUUID();

    //when
    webClient.put(WatchListRestApi.PATH_ACCOUNT_WATCHLIST + accountId.toString())
      .sendJsonObject(getBody())
      .onComplete(testContext.succeeding(response -> {
      var json = response.bodyAsJsonObject();
      logger.info("Response PUT {}", json);

      //then
      assertEquals("{\"assetList\":[{\"name\":\"AAPL\"},{\"name\":\"TSLA\"}]}", json.encode());
      assertEquals(200, response.statusCode());
      testContext.completeNow();

    }))
      .compose(next -> {
      webClient.get(WatchListRestApi.PATH_ACCOUNT_WATCHLIST+ accountId.toString())
        .send()
        .onComplete(testContext.succeeding(response -> {
          var json = response.bodyAsJsonObject();
          logger.info("Response GET {}", json);

          //then
          assertEquals("{\"assetList\":[{\"name\":\"AAPL\"},{\"name\":\"TSLA\"}]}", json.encode());
          assertEquals(200, response.statusCode());
        }));
        return Future.succeededFuture();
      });
  }

  @Test
  void add_and_remove_watchlist(Vertx vertx, VertxTestContext testContext) throws Throwable {
    //given
    var webClient = createWebClient(vertx);
    var accountId = UUID.randomUUID();
    //when
    webClient.put(WatchListRestApi.PATH_ACCOUNT_WATCHLIST + accountId.toString())
      .sendJsonObject(getBody())
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        logger.info("Response PUT {}", json);

        //then
        assertEquals("{\"assetList\":[{\"name\":\"AAPL\"},{\"name\":\"TSLA\"}]}", json.encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();

      }))
      .compose(next -> {
        webClient.delete(WatchListRestApi.PATH_ACCOUNT_WATCHLIST+ accountId.toString())
          .send()
          .onComplete(testContext.succeeding(response -> {
            var json = response.bodyAsJsonObject();
            logger.info("Response DELETE {}", json);

            //then
            assertEquals("{\"assetList\":[{\"name\":\"AAPL\"},{\"name\":\"TSLA\"}]}", json.encode());
            assertEquals(200, response.statusCode());
          }));
        return Future.succeededFuture();
      });
  }

  private JsonObject getBody() {
    return new WatchList(Arrays.asList(
      new Asset("AAPL"),
      new Asset("TSLA"))
    ).toJsonObject();
  }
}
